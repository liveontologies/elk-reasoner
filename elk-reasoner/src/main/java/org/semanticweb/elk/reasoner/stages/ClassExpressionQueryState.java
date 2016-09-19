/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.semanticweb.elk.reasoner.stages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataHasValue;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.owl.predefined.PredefinedElkClassFactory;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.visitors.DummyElkObjectVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.reasoner.indexing.classes.BaseModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.classes.DummyIndexedObjectVisitor;
import org.semanticweb.elk.reasoner.indexing.classes.UpdatingModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkIndexingUnsupportedException;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverterImpl;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceIncrement;
import org.semanticweb.elk.reasoner.query.ElkQueryException;
import org.semanticweb.elk.reasoner.query.QueryNode;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputEquivalent;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputEquivalentDirect;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputUnsatisfiable;
import org.semanticweb.elk.reasoner.reduction.TransitiveReductionOutputVisitor;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateDummyChangeListener;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.taxonomy.ElkClassKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.RecencyQueue;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keeps track of class expressions that were queried for satisfiability,
 * equivalent classes, super-classes or sub-classes.
 * 
 * @author Peter Skocovsky
 */
public class ClassExpressionQueryState {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ClassExpressionQueryState.class);

	public static final int CACHE_CAPACITY = 512;
	public static final float EVICTION_FACTOR = 0.5f;

	/**
	 * Maps class expressions that were queried and added to the index to the
	 * result of indexing.
	 */
	private final Map<ElkClassExpression, IndexedClassExpression> queried_ = new ConcurrentHashMap<ElkClassExpression, IndexedClassExpression>();

	/**
	 * Contains the same class expressions as {@link #queried_} in the order in
	 * which they were queried.
	 */
	private final RecencyQueue<ElkClassExpression> recentlyQueried_ = new RecencyQueue<ElkClassExpression>();

	/**
	 * Contains indexed class expressions that were queried and added to the
	 * index, but not computed.
	 * 
	 * Computed means that output of transitive reduction was received by
	 * {@link #transitiveReductionOutputProcessor_}.
	 * 
	 * @see #computed_
	 */
	private final Set<IndexedClassExpression> notComputed_ = Collections
			.newSetFromMap(
					new ConcurrentHashMap<IndexedClassExpression, Boolean>());

	/**
	 * Contains indexed class expressions that were queried, added to the index
	 * and computed.
	 * 
	 * @see #notComputed_
	 */
	private final Set<IndexedClassExpression> computed_ = Collections
			.newSetFromMap(
					new ConcurrentHashMap<IndexedClassExpression, Boolean>());

	/**
	 * Maps indexed class expressions that were queried, added to the index,
	 * computed and are satisfiable to the nodes with their equivalent classes.
	 * 
	 * @see #notComputed_
	 * @see #computed_
	 */
	private final Map<IndexedClassExpression, QueryNode<ElkClass>> satisfiable_ = new ConcurrentHashMap<IndexedClassExpression, QueryNode<ElkClass>>();

	/**
	 * Maps atomic classes to queried indexed class expressions to which they
	 * are related, i.e., the atomic classes are equivalent to them, or their
	 * direct super-classes.
	 */
	private final Map<ElkClass, Collection<IndexedClassExpression>> queriesByRelated_ = new ConcurrentHashMap<ElkClass, Collection<IndexedClassExpression>>();

	private final ModifiableOntologyIndex ontologyIndex_;

	private final SaturationState<? extends Context> saturationState_;

	private final ElkPolarityExpressionConverter updatingExpressionConverter_;

	private final ElkPolarityExpressionConverter removingExpressionConverter_;

	private final ElkPolarityExpressionConverter resolvingExpressionConverter_;

	private final ClassInconsistency.Factory conclusionFactory_;

	public <C extends Context> ClassExpressionQueryState(
			final SaturationState<C> saturationState,
			final PredefinedElkClassFactory elkFactory,
			final ModifiableOntologyIndex ontologyIndex,
			final ClassInconsistency.Factory conclusionFactory) {
		this.saturationState_ = saturationState;
		this.ontologyIndex_ = ontologyIndex;
		this.updatingExpressionConverter_ = new ElkPolarityExpressionConverterImpl(
				elkFactory,
				new UpdatingModifiableIndexedObjectFactory(
						new BaseModifiableIndexedObjectFactory(), ontologyIndex,
						OccurrenceIncrement.getDualIncrement(1)),
				ontologyIndex);
		this.removingExpressionConverter_ = new ElkPolarityExpressionConverterImpl(
				elkFactory,
				new UpdatingModifiableIndexedObjectFactory(
						new BaseModifiableIndexedObjectFactory(), ontologyIndex,
						OccurrenceIncrement.getDualIncrement(-1)),
				ontologyIndex);
		this.resolvingExpressionConverter_ = new ElkPolarityExpressionConverterImpl(
				elkFactory, ontologyIndex);
		this.conclusionFactory_ = conclusionFactory;
		saturationState
				.addListener(new SaturationStateDummyChangeListener<C>() {

					@Override
					public void contextMarkNonSaturated(final C context) {
						/*
						 * Saturation and context clean should not happen at the
						 * same time, so this should be thread-safe.
						 */
						final IndexedContextRoot root = context.getRoot();
						if (root instanceof IndexedClass) {
							final IndexedClass ic = (IndexedClass) root;

							synchronized (queriesByRelated_) {
								final Collection<IndexedClassExpression> queryClasses = queriesByRelated_
										.remove(ic.getElkEntity());
								if (queryClasses != null) {
									for (final IndexedClassExpression queryClass : queryClasses) {
										markNotComputed(queryClass);
									}
								}
							}

						} else if (root instanceof IndexedClassExpression) {
							final IndexedClassExpression ice = (IndexedClassExpression) root;
							markNotComputed(ice);
						}
					}

					@Override
					public void contextsClear() {
						notComputed_.addAll(computed_);
						computed_.clear();
						satisfiable_.clear();
						queriesByRelated_.clear();
					}

				});
	}

	/**
	 * Marks the specified query as computed. Does <strong>not</strong> modify
	 * the cached query results.
	 * 
	 * @param queryClass
	 * @return {@code true} iff the query was not-computed just before the call
	 *         and was marked computed by this call.
	 */
	private boolean markComputed(final IndexedClassExpression queryClass) {
		if (!notComputed_.remove(queryClass)) {
			return false;
		}
		computed_.add(queryClass);
		return true;
	}

	/**
	 * Marks the specified query as not-computed and deletes the query results.
	 * 
	 * @param queryClass
	 * @return {@code true} iff the query was computed just before the call and
	 *         was marked not-computed by this call.
	 */
	private boolean markNotComputed(final IndexedClassExpression queryClass) {
		if (!computed_.remove(queryClass)) {
			return false;
		}
		notComputed_.add(queryClass);
		final QueryNode<ElkClass> node = satisfiable_.remove(queryClass);
		if (node != null) {
			removeAllRelated(queryClass, node);
		}
		return true;
	}

	/**
	 * Registers the supplied class expression for querying. Methods
	 * {@link #isSatisfiable(ElkClassExpression)}, ... will throw
	 * {@link ElkQueryException} for class expressions that were not registered.
	 * 
	 * @param classExpression
	 * @return {@code true} when the query result is cashed, {@code false}
	 *         otherwise, i.e., the query result needs to be computed
	 * @throws ElkIndexingUnsupportedException
	 *             when the class expression is not supported
	 */
	boolean indexQuery(final ElkClassExpression classExpression) {
		/* @formatter:off
		 * 
		 * If it was already queried, do nothing. Otherwise:
		 * index it by the converter
		 * put it into the sets according to whether it is saturated
		 * 
		 * @formatter:on
		 */

		final List<ModifiableIndexedObject> unsupportedIndexed = new ArrayList<ModifiableIndexedObject>();
		final List<ElkObject> unsupportedElk = new ArrayList<ElkObject>();
		final ModifiableOntologyIndex.IndexingUnsupportedListener listener = new ModifiableOntologyIndex.IndexingUnsupportedListener() {
			@Override
			public void indexingUnsupported(
					final ModifiableIndexedObject indexedObject,
					final OccurrenceIncrement increment) {
				unsupportedIndexed.add(indexedObject);
			}

			@Override
			public void indexingUnsupported(final ElkObject elkObject) {
				unsupportedElk.add(elkObject);
			}
		};

		IndexedClassExpression ice;
		synchronized (queried_) {
			ice = queried_.get(classExpression);
			if (ice != null) {
				return computed_.contains(ice);
			}
			try {
				ontologyIndex_.addIndexingUnsupportedListener(listener);
				ice = classExpression.accept(updatingExpressionConverter_);
			} catch (final ElkIndexingUnsupportedException e) {
				if (LOGGER_.isWarnEnabled()) {
					LoggerWrap.log(LOGGER_, LogLevel.WARN,
							"reasoner.indexing.queryIgnored",
							e.getMessage()
									+ " Query results may be incomplete:\n"
									+ OwlFunctionalStylePrinter
											.toString(classExpression));
				}
				throw e;
			} finally {
				ontologyIndex_.removeIndexingUnsupportedListener(listener);
				for (final ModifiableIndexedObject obj : unsupportedIndexed) {
					obj.accept(INDEXING_UNSUPPORTED_INDEXED_OBJECT_VISITOR_);
				}
				for (final ElkObject obj : unsupportedElk) {
					obj.accept(INDEXING_UNSUPPORTED_ELK_OBJECT_VISITOR_);
				}
			}
			queried_.put(classExpression, ice);
			recentlyQueried_.offer(classExpression);
			evictIfExceeded();
		}

		final Context context = saturationState_.getContext(ice);
		if (context != null && context.isInitialized() && context.isSaturated()
				&& context.containsConclusion(
						conclusionFactory_.getContradiction(ice))) {
			// If the query is unsatisfiable, it is already computed ...
			computed_.add(ice);
			return true;
		} else {
			// ... otherwise we need to compute the equivalent and super-classes
			notComputed_.add(ice);
			return false;
		}

	}

	private void evictIfExceeded() {
		if (recentlyQueried_.size() <= CACHE_CAPACITY) {
			return;
		}

		// @formatter:off
		final int desiredCapacity = Math.max(
				Math.min(
						(int) (recentlyQueried_.size() * EVICTION_FACTOR),
						CACHE_CAPACITY),
				1);
		// @formatter:on

		while (recentlyQueried_.size() > desiredCapacity) {
			final ElkClassExpression classExpression = recentlyQueried_.poll();
			/*
			 * While desiredCapacity is at least 1, at least the last query will
			 * remain cached.
			 */

			classExpression.accept(removingExpressionConverter_);

			final IndexedClassExpression ice = queried_.remove(classExpression);
			if (computed_.remove(ice)) {
				final QueryNode<ElkClass> node = satisfiable_.remove(ice);
				if (node != null) {
					removeAllRelated(ice, node);
				}
			} else {
				notComputed_.remove(ice);
			}

		}

	}

	private final TransitiveReductionOutputVisitor<IndexedClassExpression> transitiveReductionOutputProcessor_ = new TransitiveReductionOutputVisitor<IndexedClassExpression>() {

		@Override
		public void visit(
				final TransitiveReductionOutputEquivalentDirect<IndexedClassExpression> output) {

			final IndexedClassExpression ice = output.getRoot();
			/*
			 * Saturation and "un-saturation" should not happen at the same
			 * time, so this should be thread-safe.
			 */
			if (!markComputed(ice)) {
				return;
			}

			final List<ElkClass> equivalent = output.getEquivalent();
			final Collection<? extends List<ElkClass>> directSubsumers = output
					.getDirectSubsumers();

			final QueryNode<ElkClass> node = new QueryNode<ElkClass>(equivalent,
					equivalent.size(), ElkClassKeyProvider.INSTANCE);

			for (final List<ElkClass> directSubs : directSubsumers) {
				// direct subsumers should not be empty
				final QueryNode<ElkClass> directSuperNode = new QueryNode<ElkClass>(
						directSubs, directSubs.size(),
						ElkClassKeyProvider.INSTANCE);
				node.addDirectSuperNode(directSuperNode);
			}

			addAllRelated(ice, node);
			satisfiable_.put(ice, node);
		}

		@Override
		public void visit(
				final TransitiveReductionOutputUnsatisfiable<IndexedClassExpression> output) {

			final IndexedClassExpression ice = output.getRoot();
			/*
			 * Saturation and "un-saturation" should not happen at the same
			 * time, so this should be thread-safe.
			 */
			markComputed(ice);
		}

		@Override
		public void visit(
				final TransitiveReductionOutputEquivalent<IndexedClassExpression> output) {
			// transitive reduction computation should not produce these
			throw new IllegalArgumentException(
					"Unexpected output of transitive reduction!");
		}

	};

	private void addAllRelated(final IndexedClassExpression queryClass,
			final QueryNode<ElkClass> queryNode) {
		synchronized (queriesByRelated_) {
			// equivalent
			for (final ElkClass related : queryNode) {
				addRelated(queryClass, related);
			}
			// superclasses
			for (final Node<ElkClass> superNode : queryNode
					.getDirectSuperNodes()) {
				for (final ElkClass related : superNode) {
					addRelated(queryClass, related);
				}
			}
		}
	}

	private void addRelated(final IndexedClassExpression queryClass,
			final ElkClass related) {
		// Synchronized on queriesByRelated_ by caller.
		Collection<IndexedClassExpression> queryClasses = queriesByRelated_
				.get(related);
		if (queryClasses == null) {
			queryClasses = new ArrayHashSet<IndexedClassExpression>();
			queriesByRelated_.put(related, queryClasses);
		}
		queryClasses.add(queryClass);
	}

	private void removeAllRelated(final IndexedClassExpression queryClass,
			final QueryNode<ElkClass> queryNode) {
		synchronized (queriesByRelated_) {
			// equivalent
			for (final ElkClass related : queryNode) {
				removeRelated(queryClass, related);
			}
			// superclasses
			for (final Node<ElkClass> superNode : queryNode
					.getDirectSuperNodes()) {
				for (final ElkClass related : superNode) {
					removeRelated(queryClass, related);
				}
			}
		}
	}

	private void removeRelated(final IndexedClassExpression queryClass,
			final ElkClass related) {
		// Synchronized on queriesByRelated_ by caller.
		Collection<IndexedClassExpression> queryClasses = queriesByRelated_
				.get(related);
		if (queryClasses != null) {
			queryClasses.remove(queryClass);
			if (queryClasses.isEmpty()) {
				queriesByRelated_.remove(related);
			}
		}
	}

	/**
	 * @return processor of output of transitive reduction of the queried class
	 *         expressions.
	 */
	TransitiveReductionOutputVisitor<IndexedClassExpression> getTransitiveReductionOutputProcessor() {
		return transitiveReductionOutputProcessor_;
	}

	/**
	 * @return indexed class expressions that were queried and added to the
	 *         index, but not saturated.
	 */
	public Collection<IndexedClassExpression> getNotSaturatedQueriedClassExpressions() {
		return notComputed_;
	}

	private IndexedClassExpression checkComputed(
			final ElkClassExpression classExpression) throws ElkQueryException {

		final IndexedClassExpression ice = queried_.get(classExpression);

		if (ice != null && computed_.contains(ice)) {
			return ice;
		}

		throw new ElkQueryException(
				"Query was not computed yet: " + classExpression);
	}

	/**
	 * Checks whether the supplied class expression is satisfiable, if the
	 * result was already computed. If the class expression was not registered
	 * by {@link #indexQuery(ElkClassExpression)} or the appropriate stage was
	 * not completed yet, throws {@link ElkQueryException}.
	 * 
	 * @param classExpression
	 * @return whether the supplied class expression is satisfiable.
	 * @throws ElkQueryException
	 *             if the result is not ready
	 */
	boolean isSatisfiable(final ElkClassExpression classExpression)
			throws ElkQueryException {
		final IndexedClassExpression ice = checkComputed(classExpression);
		return satisfiable_.containsKey(ice);
	}

	/**
	 * Returns {@link Node} containing all {@link ElkClass}es equivalent to the
	 * supplied class expression, if it is satisfiable. Returns
	 * <code>null</code> otherwise. If the class expression was not registered
	 * by {@link #indexQuery(ElkClassExpression)} or the appropriate stage was
	 * not completed yet, throws {@link ElkQueryException}.
	 * 
	 * @param classExpression
	 * @return atomic classes equivalent to the supplied class expression, if it
	 *         is satisfiable, otherwise <code>null</code>.
	 * @throws ElkQueryException
	 *             if the result is not ready
	 */
	Node<ElkClass> getEquivalentClasses(
			final ElkClassExpression classExpression) throws ElkQueryException {
		final IndexedClassExpression ice = checkComputed(classExpression);
		return satisfiable_.get(ice);
	}

	/**
	 * Returns set of {@link Node}s containing all {@link ElkClass}es that are
	 * direct strict super-classes of the supplied class expression, if it is
	 * satisfiable. Returns <code>null</code> otherwise. If the class expression
	 * was not registered by {@link #indexQuery(ElkClassExpression)} or the
	 * appropriate stage was not completed yet, throws {@link ElkQueryException}
	 * .
	 * 
	 * @param classExpression
	 * @return atomic direct strict super-classes of the supplied class
	 *         expression, if it is satisfiable, otherwise <code>null</code>.
	 * @throws ElkQueryException
	 *             if the result is not ready
	 */
	Set<? extends Node<ElkClass>> getDirectSuperClasses(
			final ElkClassExpression classExpression) throws ElkQueryException {
		final IndexedClassExpression ice = checkComputed(classExpression);
		final QueryNode<ElkClass> node = satisfiable_.get(ice);
		if (node == null) {
			return null;
		} else {
			return node.getDirectSuperNodes();
		}
	}

	/**
	 * Returns set of {@link Node}s containing all {@link ElkClass}es that are
	 * direct strict sub-classes of the supplied class expression, if it is
	 * satisfiable. Returns <code>null</code> otherwise. If the class expression
	 * was not registered by {@link #indexQuery(ElkClassExpression)} or the
	 * appropriate stage was not completed yet, throws {@link ElkQueryException}
	 * .
	 * 
	 * @param classExpression
	 * @param taxonomy
	 * @return atomic direct strict sub-classes of the supplied class
	 *         expression, if it is satisfiable, otherwise <code>null</code>.
	 * @throws ElkQueryException
	 *             if the result is not ready
	 */
	Set<? extends Node<ElkClass>> getDirectSubClasses(
			final ElkClassExpression classExpression,
			final Taxonomy<ElkClass> taxonomy) throws ElkQueryException {

		final IndexedClassExpression ice = checkComputed(classExpression);

		final QueryNode<ElkClass> node = satisfiable_.get(ice);
		if (node == null) {
			return null;
		}
		// else

		final Iterator<ElkClass> iter = node.iterator();
		if (iter.hasNext()) {
			final ElkClass cls = iter.next();
			return taxonomy.getNode(cls).getDirectSubNodes();
		}
		/*
		 * Else, if classExpression is not equivalent to any atomic class,
		 * direct atomic sub-classes of classExpression are atomic classes that
		 * have classExpression among their subsumers, but no other of their
		 * strict subsumers have classExpression among its subsumers.
		 */

		final Collection<? extends IndexedClass> allClasses = saturationState_
				.getOntologyIndex().getClasses();
		final Set<IndexedClass> strictSubclasses = new ArrayHashSet<IndexedClass>(
				allClasses.size());

		for (final IndexedClass ic : allClasses) {
			final Set<IndexedClassExpression> subsumers = ic.getContext()
					.getComposedSubsumers();
			if (subsumers.contains(ice) && ice.getContext()
					.getComposedSubsumers().size() != subsumers.size()) {
				// is subclass, but not equivalent
				strictSubclasses.add(ic);
			}
		}

		final Set<TaxonomyNode<ElkClass>> result = new ArrayHashSet<TaxonomyNode<ElkClass>>();

		for (final IndexedClass strictSubclass : strictSubclasses) {
			/*
			 * If some strict superclass of strictSubclass is a strict subclass
			 * of classExpression, strictSubclass is not direct.
			 * 
			 * It is sufficient to check only direct superclasses of
			 * strictSubclass.
			 */
			boolean isDirect = true;
			for (final TaxonomyNode<ElkClass> superNode : taxonomy
					.getNode(strictSubclass.getElkEntity())
					.getDirectSuperNodes()) {
				final IndexedClassExpression superClass = superNode
						.getCanonicalMember()
						.accept(resolvingExpressionConverter_);
				if (strictSubclasses.contains(superClass)) {
					isDirect = false;
					break;
				}
			}

			if (isDirect) {
				result.add(taxonomy.getNode(strictSubclass.getElkEntity()));
			}
		}

		if (result.isEmpty()) {
			/*
			 * No indexed class has classExpression among its subsumers and
			 * classExpression is not equivalent to any atomic class, so the
			 * only subclass of classExpression is Nothing and it is direct.
			 */
			result.add(taxonomy.getBottomNode());
		}
		return Collections.unmodifiableSet(result);
	}

	private static final ElkObjectVisitor<Void> INDEXING_UNSUPPORTED_ELK_OBJECT_VISITOR_ = new DummyElkObjectVisitor<Void>() {

		@Override
		public Void visit(final ElkDataHasValue obj) {
			if (LOGGER_.isWarnEnabled()) {
				LoggerWrap.log(LOGGER_, LogLevel.WARN,
						"reasoner.indexing.dataHasValue",
						"ELK supports DataHasValue only partially. Query results may be incomplete!");
			}
			return super.visit(obj);
		}

		@Override
		public Void visit(final ElkObjectOneOf obj) {
			if (LOGGER_.isWarnEnabled()) {
				LoggerWrap.log(LOGGER_, LogLevel.WARN,
						"reasoner.indexing.objectOneOf",
						"ELK supports ObjectOneOf only partially. Query results may be incomplete!");
			}
			return super.visit(obj);
		}

	};

	private static final IndexedObject.Visitor<Void> INDEXING_UNSUPPORTED_INDEXED_OBJECT_VISITOR_ = new DummyIndexedObjectVisitor<Void>() {

		@Override
		public Void visit(final IndexedObjectComplementOf element) {
			if (LOGGER_.isWarnEnabled()) {
				LoggerWrap.log(LOGGER_, LogLevel.WARN,
						"reasoner.indexing.IndexedObjectComplementOf",
						"ELK does not support querying equivalent classes and subclasses of ObjectComplementOf. Query results may be incomplete!");
			}
			return super.visit(element);
		}

		@Override
		public Void visit(final IndexedObjectUnionOf element) {
			if (LOGGER_.isWarnEnabled()) {
				LoggerWrap.log(LOGGER_, LogLevel.WARN,
						"reasoner.indexing.IndexedObjectUnionOf",
						"ELK does not support querying equivalent classes and superclasses of ObjectUnionOf or ObjectOneOf. Reasoning might be incomplete!");
			}
			return super.visit(element);
		}

	};

}
