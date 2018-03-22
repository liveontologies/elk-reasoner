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
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.loading.AbstractClassQueryLoader;
import org.semanticweb.elk.loading.ClassQueryLoader;
import org.semanticweb.elk.loading.ElkLoadingException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.predefined.PredefinedElkClassFactory;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionProcessor;
import org.semanticweb.elk.reasoner.completeness.EmptyOccurrenceCounter;
import org.semanticweb.elk.reasoner.completeness.Feature;
import org.semanticweb.elk.reasoner.completeness.OccurrenceCounter;
import org.semanticweb.elk.reasoner.completeness.OccurrenceRegistry;
import org.semanticweb.elk.reasoner.completeness.OccurrenceListener;
import org.semanticweb.elk.reasoner.completeness.OccurrenceManager;
import org.semanticweb.elk.reasoner.completeness.OccurrencesInClassExpressionQuery;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkIndexingUnsupportedException;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverterImpl;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
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
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Evictor;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

/**
 * Keeps track of class expressions that were queried for satisfiability,
 * equivalent classes, super-classes or sub-classes.
 * 
 * @author Peter Skocovsky
 */
public class ClassExpressionQueryState implements ClassQueryLoader.Factory {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ClassExpressionQueryState.class);

	/**
	 * Maps class expressions that were queried to the states of their query.
	 * The values must be superset of values of {@link #indexed_}.
	 */
	private final Map<ElkClassExpression, QueryState> queried_ = new ConcurrentHashMap<ElkClassExpression, QueryState>();

	/**
	 * Contains class expressions that were queried but not loaded. This is an
	 * overestimation of class expressions that need to be loaded, because some
	 * of them may not have states.
	 */
	private final Queue<ElkClassExpression> toLoad_ = new ConcurrentLinkedQueue<ElkClassExpression>();

	/**
	 * Maps results of indexing of queried class expressions to the states of
	 * their query. The values must have {@link QueryState#indexed} set to the
	 * keys.
	 */
	private final Map<IndexedClassExpression, QueryState> indexed_ = new ConcurrentHashMap<IndexedClassExpression, QueryState>();

	/**
	 * Manages eviction from {@link #queried_}.
	 */
	private final Evictor<ElkClassExpression> queriedEvictor_;

	/**
	 * The class expressions that were registered by the last call.
	 */
	private final Set<ElkClassExpression> lastQueries_ = new ArrayHashSet<ElkClassExpression>();

	/**
	 * State of the query of a particular class expression. There are four
	 * forbidden states:
	 * <ul>
	 * <li>when a class expression does not have a state, it must not be loaded,
	 * <li>when {@link #isLoaded} is {@code false} and {@link #indexed} is not
	 * {@code null},
	 * <li>when {@link #indexed} is {@code null} and {@link #isComputed} is
	 * {@code true}, and
	 * <li>when {@link #isComputed} is {@code false} and {@link #node} is not
	 * {@code null}.
	 * </ul>
	 * 
	 * @author Peter Skocovsky
	 */
	private static class QueryState {
		/**
		 * Whether the queried class expression was loaded (whether it was
		 * attempted to index it). If this is {@code false}, then
		 * {@link #indexed} must be {@code null}.
		 */
		boolean isLoaded = false;
		/**
		 * Results of indexing of the queried class expression. If this is
		 * {@code null}, then {@link #isComputed} must be {@code false}.
		 */
		IndexedClassExpression indexed = null;
		/**
		 * Whether the query was computed. If this is {@code false}, then
		 * {@link #node} must be {@code null}.
		 */
		boolean isComputed = false;
		/**
		 * The result of the query. If {@link #isComputed} is {@code true} and
		 * this field is {@code null}, the query is unsatisfiable.
		 */
		QueryNode<ElkClass> node = null;
		/**
		 * Counts how many times each {@link Feature} occurs in the query.
		 * {@code null} means no occurrences.
		 */
		OccurrenceRegistry occurrences = null;

	}

	/**
	 * Maps atomic classes to queried indexed class expressions to which they
	 * are related, i.e., the atomic classes are equivalent to them, or their
	 * direct super-classes.
	 */
	private final Map<ElkClass, Collection<IndexedClassExpression>> queriesByRelated_ = new ConcurrentHashMap<ElkClass, Collection<IndexedClassExpression>>();

	private final SaturationState<? extends Context> saturationState_;

	private final ElkPolarityExpressionConverter resolvingExpressionConverter_;

	private final ClassInconsistency.Factory conclusionFactory_;

	public <C extends Context> ClassExpressionQueryState(
			final ReasonerConfiguration config,
			final SaturationState<C> saturationState,
			final PredefinedElkClassFactory elkFactory,
			final ModifiableOntologyIndex ontologyIndex,
			final ClassInconsistency.Factory conclusionFactory) {
		this.saturationState_ = saturationState;
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
						synchronized (queriesByRelated_) {
							if (root instanceof IndexedClass) {
								final IndexedClass ic = (IndexedClass) root;

								final Collection<IndexedClassExpression> queryClasses = queriesByRelated_
										.remove(ic.getElkEntity());
								if (queryClasses != null) {
									for (final IndexedClassExpression queryClass : queryClasses) {
										markNotComputed(queryClass);
									}
								}

							} else if (root instanceof IndexedClassExpression) {
								final IndexedClassExpression ice = (IndexedClassExpression) root;
								markNotComputed(ice);
							}
						}
					}

					@Override
					public void contextsClear() {
						for (final QueryState state : queried_.values()) {
							state.isComputed = false;
							state.node = null;
						}
						queriesByRelated_.clear();
					}

				});

		final Object builder = config.getParameter(
				ReasonerConfiguration.CLASS_EXPRESSION_QUERY_EVICTOR);
		LOGGER_.info("{} = {}",
				ReasonerConfiguration.CLASS_EXPRESSION_QUERY_EVICTOR, builder);
		this.queriedEvictor_ = ((Evictor.Builder) builder).build();
	}

	/**
	 * If the specified query was added to the index, this method marks it as
	 * computed. Does <strong>not</strong> modify the cached query results.
	 * 
	 * @param queryClass
	 * @return query state iff the parameter was queried, added to the index,
	 *         and the query was not-computed just before the call and was
	 *         marked computed by this call, otherwise {@code null}.
	 */
	private QueryState markComputed(final IndexedClassExpression queryClass) {
		final QueryState state = indexed_.get(queryClass);
		if (state == null || state.isComputed) {
			return null;
		}
		state.isComputed = true;
		LOGGER_.trace("query computed {}", queryClass);
		return state;
	}

	/**
	 * If the specified query was added to the index, this method marks it as
	 * not-computed and deletes the query results.
	 * 
	 * @param queryClass
	 * @return query state iff the parameter was queried, added to the index,
	 *         and the query was computed just before the call and was marked
	 *         not-computed by this call, otherwise {@code null}.
	 */
	private QueryState markNotComputed(
			final IndexedClassExpression queryClass) {
		final QueryState state = indexed_.get(queryClass);
		if (state == null || !state.isComputed) {
			return null;
		}
		state.isComputed = false;
		if (state.node != null) {
			removeAllRelated(queryClass, state.node);
			state.node = null;
		}
		return state;
	}

	/**
	 * Registers the supplied class expression for querying. If the expression
	 * has already been registered, returns {@code false}. Otherwise, if this
	 * state did not keep track of the expression yet, returns {@code true}. If
	 * all necessary stages are run after doing this, the result retrieval
	 * methods, e.g., {@link #isSatisfiable(ElkClassExpression)}, will not throw
	 * {@link ElkQueryException}.
	 * 
	 * @param classExpression
	 * @return {@code true} if this is a new query, {@code false} if this class
	 *         expression has already been registered.
	 */
	boolean registerQuery(final ElkClassExpression classExpression) {

		LOGGER_.trace("class expression query registered {}", classExpression);

		lastQueries_.clear();
		queriedEvictor_.add(classExpression);
		lastQueries_.add(classExpression);

		QueryState state = queried_.get(classExpression);
		if (state != null) {
			return false;
		}
		// Create query state.
		state = new QueryState();
		queried_.put(classExpression, state);
		toLoad_.add(classExpression);

		return true;
	}

	private final QueryOccurrenceListener occurrenceListener_ = new QueryOccurrenceListener();
	

	private class QueryOccurrenceListener implements OccurrenceListener {

		private QueryState beingLoaded_ = null;

		@Override
		public void occurrenceChanged(Feature feature, int increment) {
			OccurrenceRegistry occurrences = beingLoaded_.occurrences;
			if (occurrences == null) {
				occurrences = new OccurrenceRegistry();
				beingLoaded_.occurrences = occurrences;
			}
			occurrences.occurrenceChanged(feature, increment);
			if (occurrences.isEmpty()) {
				beingLoaded_.occurrences = null;
			}
		}

	}

	public OccurrenceListener getOccurrenceListener() {
		return occurrenceListener_;
	}

	@Override
	public ClassQueryLoader getQueryLoader(final InterruptMonitor interrupter) {
		return new Loader(interrupter);
	}

	
	private class Loader extends AbstractClassQueryLoader {
		public Loader(final InterruptMonitor interrupter) {
			super(interrupter);
		}

		@Override
		public void load(final ElkClassExpressionProcessor inserter,
				final ElkClassExpressionProcessor deleter)
				throws ElkLoadingException {

			// First evict and unload.
			final Iterator<ElkClassExpression> evicted = queriedEvictor_
					.evict(doNotEvict_);
			while (evicted.hasNext()) {
				final ElkClassExpression classExpression = evicted.next();
				final QueryState state = queried_.remove(classExpression);
				if (!state.isLoaded) {
					continue;
				}
				// else
				occurrenceListener_.beingLoaded_ = state;
				deleter.visit(classExpression);
				if (state.indexed != null) {
					if (state.isComputed) {
						if (state.node != null) {
							removeAllRelated(state.indexed, state.node);
							state.node = null;
						}
					}
					indexed_.remove(state.indexed);
					state.indexed = null;
				}
			}

			/*
			 * Load all registered queries that are not loaded and assign
			 * state.indexed if successful.
			 */
			ElkClassExpression classExpression;
			while ((classExpression = toLoad_.poll()) != null) {
				final QueryState state = queried_.get(classExpression);
				if (state == null) {
					continue;
				}
				// else

				occurrenceListener_.beingLoaded_ = state;
				inserter.visit(classExpression);
				state.isLoaded = true;

				try {
					final IndexedClassExpression ice = classExpression
							.accept(resolvingExpressionConverter_);
					state.indexed = ice;
					indexed_.put(state.indexed, state);
					LOGGER_.trace("query {} indexed as {}", classExpression,
							ice);

					// Check whether it is computed.
					final Context context = saturationState_
							.getContext(state.indexed);
					if (context != null && context.isInitialized()
							&& context.isSaturated()
							&& context.containsConclusion(conclusionFactory_
									.getContradiction(state.indexed))) {
						/*
						 * If the query is unsatisfiable, it is already computed
						 * ...
						 */
						state.isComputed = true;
						LOGGER_.trace("query computed {}", ice);
					} else {
						/*
						 * ... otherwise we need to compute the equivalent and
						 * super-classes
						 */
						state.isComputed = false;
					}
				} catch (final ElkIndexingUnsupportedException e) {
					state.indexed = null;
					LOGGER_.trace("query NOT indexed {}", classExpression);
				}

				if (isInterrupted()) {
					return;
				}
			}

		}

		@Override
		public boolean isLoadingFinished() {
			return toLoad_.isEmpty();
		}
	}

	private final Predicate<ElkClassExpression> doNotEvict_ = new Predicate<ElkClassExpression>() {
		@Override
		public boolean apply(final ElkClassExpression ce) {
			return lastQueries_.contains(ce);
		}
	};

	private final TransitiveReductionOutputVisitor<IndexedClassExpression> transitiveReductionOutputProcessor_ = new TransitiveReductionOutputVisitor<IndexedClassExpression>() {

		@Override
		public void visit(
				final TransitiveReductionOutputEquivalentDirect<IndexedClassExpression> output) {

			final IndexedClassExpression ice = output.getRoot();
			/*
			 * Saturation and "un-saturation" should not happen at the same
			 * time, so this should be thread-safe.
			 */
			final QueryState state = markComputed(ice);
			if (state == null) {
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
			state.node = node;
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

	private final static Operations.Transformation<QueryState, IndexedClassExpression> notComputedIces_ = new Operations.Transformation<QueryState, IndexedClassExpression>() {
		@Override
		public IndexedClassExpression transform(final QueryState element) {
			if (element.isComputed) {
				return null;
			}
			return element.indexed;
		}
	};

	/**
	 * @return indexed class expressions that were queried and added to the
	 *         index, but not saturated.
	 */
	public Collection<IndexedClassExpression> getNotSaturatedQueriedClassExpressions() {
		return Operations.map(indexed_.values(), notComputedIces_);
	}

	/**
	 * @param classExpression
	 * @return whether the supplied class expression was indexed as a query.
	 */
	public boolean isIndexed(final ElkClassExpression classExpression) {
		final QueryState state = queried_.get(classExpression);
		return state != null && state.indexed != null;
	}

	/**
	 * @param classExpression
	 * @return whether the query result for the supplied class expression was
	 *         already computed.
	 */
	public boolean isComputed(final ElkClassExpression classExpression) {
		final QueryState state = queried_.get(classExpression);
		return state != null && state.isComputed;
	}

	private QueryState checkComputed(final ElkClassExpression classExpression)
			throws ElkQueryException {

		final QueryState state = queried_.get(classExpression);

		if (state != null && state.isComputed) {
			return state;
		}

		throw new ElkQueryException(
				"Query was not computed yet: " + classExpression);
	}

	OccurrenceManager getOccurrenceManager(
			final ElkClassExpression classExpression) {
		OccurrenceCounter occurrences = queried_
				.get(classExpression).occurrences;
		if (occurrences == null) {
			occurrences = EmptyOccurrenceCounter.get();
		}

		return new OccurrencesInClassExpressionQuery(classExpression,
				occurrences);
	}

	/**
	 * Checks whether the supplied class expression is satisfiable, if the
	 * result was already computed. If the class expression was not registered
	 * by {@link #registerQuery(ElkClassExpression)} or the appropriate stage
	 * was not completed yet, throws {@link ElkQueryException}.
	 * 
	 * @param classExpression
	 * @return whether the supplied class expression is satisfiable.
	 * @throws ElkQueryException
	 *             if the result is not ready
	 */
	boolean isSatisfiable(final ElkClassExpression classExpression)
			throws ElkQueryException {
		final QueryState state = checkComputed(classExpression);
		return state.node != null;
	}

	/**
	 * Returns {@link Node} containing all {@link ElkClass}es equivalent to the
	 * supplied class expression, if it is satisfiable. Returns
	 * <code>null</code> otherwise. If the class expression was not registered
	 * by {@link #registerQuery(ElkClassExpression)} or the appropriate stage
	 * was not completed yet, throws {@link ElkQueryException}.
	 * 
	 * @param classExpression
	 * @return atomic classes equivalent to the supplied class expression, if it
	 *         is satisfiable, otherwise <code>null</code>.
	 * @throws ElkQueryException
	 *             if the result is not ready
	 */
	Node<ElkClass> getEquivalentClasses(
			final ElkClassExpression classExpression) throws ElkQueryException {
		final QueryState state = checkComputed(classExpression);
		return state.node;
	}

	/**
	 * Returns set of {@link Node}s containing all {@link ElkClass}es that are
	 * direct strict super-classes of the supplied class expression, if it is
	 * satisfiable. Returns <code>null</code> otherwise. If the class expression
	 * was not registered by {@link #registerQuery(ElkClassExpression)} or the
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
		final QueryState state = checkComputed(classExpression);
		if (state.node == null) {
			return null;
		} else {
			return state.node.getDirectSuperNodes();
		}
	}

	/**
	 * Returns set of {@link Node}s containing all {@link ElkClass}es that are
	 * direct strict sub-classes of the supplied class expression, if it is
	 * satisfiable. Returns <code>null</code> otherwise. If the class expression
	 * was not registered by {@link #registerQuery(ElkClassExpression)} or the
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

		final QueryState state = checkComputed(classExpression);
		if (state.node == null) {
			return null;
		}
		// else

		final Iterator<ElkClass> iter = state.node.iterator();
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
			if (subsumers.contains(state.indexed) && state.indexed.getContext()
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

	/**
	 * Returns set of {@link Node}s containing all {@link ElkNamedIndividual}s
	 * that are direct instances of the supplied class expression, if it is
	 * satisfiable. Returns <code>null</code> otherwise. If the class expression
	 * was not registered by {@link #registerQuery(ElkClassExpression)} or the
	 * appropriate stage was not completed yet, throws {@link ElkQueryException}
	 * .
	 * 
	 * @param classExpression
	 * @param taxonomy
	 * @return direct instances of the supplied class expression, if it is
	 *         satisfiable, otherwise <code>null</code>.
	 * @throws ElkQueryException
	 *             if the result is not ready
	 */
	Set<? extends Node<ElkNamedIndividual>> getDirectInstances(
			final ElkClassExpression classExpression,
			final InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy)
			throws ElkQueryException {

		final QueryState state = checkComputed(classExpression);
		if (state.node == null) {
			return null;
		}
		// else

		final Iterator<ElkClass> iter = state.node.iterator();
		if (iter.hasNext()) {
			final ElkClass cls = iter.next();
			return taxonomy.getNode(cls).getDirectInstanceNodes();
		}
		/*
		 * Else, if classExpression is not equivalent to any atomic class,
		 * direct instances of classExpression are instances that have
		 * classExpression among their subsumers, but no other of their strict
		 * subsumers have classExpression among its subsumers.
		 */

		final Collection<? extends IndexedIndividual> allIndividuals = saturationState_
				.getOntologyIndex().getIndividuals();
		final Set<IndexedIndividual> instances = new ArrayHashSet<IndexedIndividual>(
				allIndividuals.size());

		for (final IndexedIndividual ii : allIndividuals) {
			final Set<IndexedClassExpression> subsumers = ii.getContext()
					.getComposedSubsumers();
			if (subsumers.contains(state.indexed)) {
				instances.add(ii);
			}
		}

		final Set<InstanceNode<ElkClass, ElkNamedIndividual>> result = new ArrayHashSet<InstanceNode<ElkClass, ElkNamedIndividual>>();

		for (final IndexedIndividual instance : instances) {
			/*
			 * If some type of instance is a strict subclass of classExpression,
			 * instance is not direct.
			 * 
			 * It is sufficient to check only direct types of instance.
			 */
			boolean isDirect = true;
			for (final TypeNode<ElkClass, ElkNamedIndividual> typeNode : taxonomy
					.getInstanceNode(instance.getElkEntity())
					.getDirectTypeNodes()) {
				final IndexedClassExpression type = typeNode
						.getCanonicalMember()
						.accept(resolvingExpressionConverter_);
				final Set<IndexedClassExpression> subsumers = type.getContext()
						.getComposedSubsumers();
				if (subsumers.contains(state.indexed)
						&& state.indexed.getContext().getComposedSubsumers()
								.size() != subsumers.size()) {
					// is subclass, but not equivalent
					isDirect = false;
					break;
				}
			}

			if (isDirect) {
				result.add(taxonomy.getInstanceNode(instance.getElkEntity()));
			}
		}

		return Collections.unmodifiableSet(result);
	}

}
