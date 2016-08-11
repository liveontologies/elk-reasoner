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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.predefined.PredefinedElkClassFactory;
import org.semanticweb.elk.reasoner.indexing.classes.BaseModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.classes.UpdatingModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverterImpl;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
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

/**
 * Keeps track of class expressions that were queried for satisfiability,
 * equivalent classes, super-classes or sub-classes.
 * 
 * @author Peter Skocovsky
 */
public class ClassExpressionQueryState {

	/**
	 * Maps class expressions that were queried and added to the index to the
	 * result of indexing.
	 */
	private final Map<ElkClassExpression, IndexedClassExpression> queried_ = new ConcurrentHashMap<ElkClassExpression, IndexedClassExpression>();

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

	private final SaturationState<? extends Context> saturationState_;

	private final ElkPolarityExpressionConverter updatingExpressionConverter_;

	private final ElkPolarityExpressionConverter resolvingExpressionConverter_;

	private final ClassInconsistency.Factory conclusionFactory_;

	public <C extends Context> ClassExpressionQueryState(
			final SaturationState<C> saturationState,
			final PredefinedElkClassFactory elkFactory,
			final ModifiableOntologyIndex ontologyIndex,
			final ClassInconsistency.Factory conclusionFactory) {
		this.saturationState_ = saturationState;
		this.updatingExpressionConverter_ = new ElkPolarityExpressionConverterImpl(
				elkFactory,
				new UpdatingModifiableIndexedObjectFactory(
						new BaseModifiableIndexedObjectFactory(), ontologyIndex,
						OccurrenceIncrement.getDualIncrement(1)));
		this.resolvingExpressionConverter_ = new ElkPolarityExpressionConverterImpl(
				elkFactory, ontologyIndex);
		this.conclusionFactory_ = conclusionFactory;
		saturationState
				.addListener(new SaturationStateDummyChangeListener<C>() {

					@Override
					public void contextMarkNonSaturated(final C context) {
						final IndexedContextRoot root = context.getRoot();
						if (root instanceof IndexedClassExpression) {
							final IndexedClassExpression ice = (IndexedClassExpression) root;
							/*
							 * Saturation and "un-saturation" should not happen
							 * on the same time, so this should be thread-safe.
							 */
							if (computed_.remove(ice)) {
								notComputed_.add(ice);
								satisfiable_.remove(ice);
							}

							if (ice instanceof IndexedClass) {
								final IndexedClass ic = (IndexedClass) ice;

								final Collection<IndexedClassExpression> queryClasses = queriesByRelated_
										.remove(ic.getElkEntity());
								if (queryClasses != null) {
									for (final IndexedClassExpression queryClass : queryClasses) {
										if (computed_.remove(queryClass)) {
											notComputed_.add(queryClass);
											satisfiable_.remove(queryClass);
										}
									}
								}

							}
						}
					}

					@Override
					public void contextsClear() {
						// TODO: does this need to be more thread-safe ??
						notComputed_.addAll(computed_);
						computed_.clear();
						satisfiable_.clear();
						queriesByRelated_.clear();
					}

				});
	}

	/**
	 * Registers the supplied class expression for querying. Methods
	 * {@link #isSatisfiable(ElkClassExpression)}, ... will throw
	 * {@link ElkQueryException} for class expressions that were not registered.
	 * 
	 * @param classExpression
	 */
	void indexQuery(final ElkClassExpression classExpression) {
		/* @formatter:off
		 * 
		 * If it was already queried, do nothing. Otherwise:
		 * index it by the converter
		 * put it into the sets according to whether it is saturated
		 * 
		 * @formatter:on
		 */

		IndexedClassExpression ice;
		synchronized (queried_) {
			ice = queried_.get(classExpression);
			if (ice != null) {
				return;
			}
			ice = classExpression.accept(updatingExpressionConverter_);
			queried_.put(classExpression, ice);
		}

		final Context context = saturationState_.getContext(ice);
		if (context != null && context.isInitialized() && context.isSaturated()
				&& context.containsConclusion(
						conclusionFactory_.getContradiction(ice))) {
			// If the query is unsatisfiable, it is already computed ...
			computed_.add(ice);
		} else {
			// ... otherwise we need to compute the equivalent and super-classes
			notComputed_.add(ice);
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
			if (!notComputed_.remove(ice)) {
				return;
			}
			computed_.add(ice);

			final List<ElkClass> equivalent = output.getEquivalent();
			final Collection<? extends List<ElkClass>> directSubsumers = output
					.getDirectSubsumers();

			final QueryNode<ElkClass> node = new QueryNode<ElkClass>(equivalent,
					equivalent.size(), ElkClassKeyProvider.INSTANCE);
			for (final ElkClass elkClass : equivalent) {
				addRelated(ice, elkClass);
			}

			for (final List<ElkClass> directSubs : directSubsumers) {
				// direct subsumers should not be empty
				final QueryNode<ElkClass> directSuperNode = new QueryNode<ElkClass>(
						directSubs, directSubs.size(),
						ElkClassKeyProvider.INSTANCE);
				node.addDirectSuperNode(directSuperNode);
				for (final ElkClass elkClass : directSubs) {
					addRelated(ice, elkClass);
				}
			}

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
			if (!notComputed_.remove(ice)) {
				return;
			}
			computed_.add(ice);
		}

		@Override
		public void visit(
				final TransitiveReductionOutputEquivalent<IndexedClassExpression> output) {
			// transitive reduction computation should not produce these
			throw new IllegalArgumentException(
					"Unexpected output of transitive reduction!");
		}

	};

	private void addRelated(final IndexedClassExpression queryClass,
			final ElkClass related) {
		// More fine-grained synchronization is possible but not necessary.
		synchronized (queriesByRelated_) {
			Collection<IndexedClassExpression> queryClasses = queriesByRelated_
					.get(related);
			if (queryClasses == null) {
				queryClasses = new ArrayHashSet<IndexedClassExpression>();
				queriesByRelated_.put(related, queryClasses);
			}
			queryClasses.add(queryClass);
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
		 * 
		 * The search can be limited to all sub-classes of direct super-classes
		 * of classExpression.
		 */

		final Set<TaxonomyNode<ElkClass>> result = new ArrayHashSet<TaxonomyNode<ElkClass>>();

		final Queue<TaxonomyNode<ElkClass>> toDo = new LinkedList<TaxonomyNode<ElkClass>>();
		final Set<TaxonomyNode<ElkClass>> done = new ArrayHashSet<TaxonomyNode<ElkClass>>();

		for (final Node<ElkClass> superNode : node.getDirectSuperNodes()) {
			final Set<? extends TaxonomyNode<ElkClass>> subNodes = taxonomy
					.getNode(superNode.getCanonicalMember())
					.getDirectSubNodes();
			for (final TaxonomyNode<ElkClass> subNode : subNodes) {
				if (done.add(subNode)) {
					toDo.add(subNode);
				}
			}
		}

		while (!toDo.isEmpty()) {
			final TaxonomyNode<ElkClass> subNode = toDo.poll();

			// test direct subsumption

			final IndexedClassExpression canonical = subNode
					.getCanonicalMember().accept(resolvingExpressionConverter_);

			boolean isDirect = true;
			boolean isSubClass = false;
			for (final IndexedClassExpression subsumer : saturationState_
					.getContext(canonical).getComposedSubsumers()) {
				if (subsumer == ice) {
					isSubClass = true;
				} else {
					final Context context = saturationState_
							.getContext(subsumer);
					if (context != null) {
						final Set<IndexedClassExpression> subsumers = context
								.getComposedSubsumers();
						if (!subsumers.contains(canonical)
								&& subsumers.contains(ice)) {
							// not direct
							isDirect = false;
							break;
						}
					}
				}
			}

			if (isDirect && isSubClass) {
				result.add(subNode);
			}

			// queue up sub-nodes

			for (final TaxonomyNode<ElkClass> subSubNode : subNode
					.getDirectSubNodes()) {
				if (done.add(subSubNode)) {
					toDo.add(subSubNode);
				}
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

}
