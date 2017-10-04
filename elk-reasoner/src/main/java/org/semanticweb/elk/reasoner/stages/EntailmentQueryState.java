/*-
 * #%L
 * ELK Reasoner Core
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
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.liveontologies.puli.InferenceDerivabilityChecker;
import org.semanticweb.elk.loading.AbstractEntailmentQueryLoader;
import org.semanticweb.elk.loading.ElkLoadingException;
import org.semanticweb.elk.loading.EntailmentQueryLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.reasoner.completeness.IncompletenessManager;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.consistency.ConsistencyCheckingState;
import org.semanticweb.elk.reasoner.entailments.EntailmentProofUnion;
import org.semanticweb.elk.reasoner.entailments.InconsistencyProofWrapper;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentProof;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexingListener;
import org.semanticweb.elk.reasoner.indexing.model.Occurrence;
import org.semanticweb.elk.reasoner.indexing.model.OccurrenceStore;
import org.semanticweb.elk.reasoner.query.AbstractProperEntailmentQueryResult;
import org.semanticweb.elk.reasoner.query.ElkQueryException;
import org.semanticweb.elk.reasoner.query.EntailmentQueryConverter;
import org.semanticweb.elk.reasoner.query.EntailmentQueryResult;
import org.semanticweb.elk.reasoner.query.IndexedEntailmentQuery;
import org.semanticweb.elk.reasoner.query.UnsupportedIndexingEntailmentQueryResultImpl;
import org.semanticweb.elk.reasoner.query.UnsupportedQueryTypeEntailmentQueryResultImpl;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Condition;
import org.semanticweb.elk.util.collections.Evictor;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

/**
 * Keeps track of axioms that were queried for entailment.
 * 
 * @author Peter Skocovsky
 */
public class EntailmentQueryState implements EntailmentQueryLoader.Factory {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(EntailmentQueryState.class);

	/**
	 * Maps axioms that were queried to the states of their query.
	 */
	private final Map<ElkAxiom, QueryState> queried_ = new ConcurrentHashMap<ElkAxiom, QueryState>();

	/**
	 * Contains axioms that were queried but not loaded.
	 */
	private final Queue<ElkAxiom> toLoad_ = new ConcurrentLinkedQueue<ElkAxiom>();

	/**
	 * Manages eviction from {@link #queried_}.
	 */
	private final Evictor<QueryState> queriedEvictor_;

	/**
	 * The axioms that were registered by the last call.
	 */
	private final Set<ElkAxiom> lastQueries_ = new ArrayHashSet<ElkAxiom>();

	/**
	 * State of the query of a particular axiom. There are two forbidden states:
	 * <ul>
	 * <li>when an axiom does not have a state, it must not be loaded,
	 * <li>when {@link #isLoaded} is {@code false} and {@link #indexed} is not
	 * {@code null},
	 * </ul>
	 * 
	 * @author Peter Skocovsky
	 */
	private class QueryState extends AbstractProperEntailmentQueryResult {
		/**
		 * Whether the queried axiom was loaded (whether it was attempted to
		 * index it). If this is {@code false}, then {@link #indexed} must be
		 * {@code null}.
		 */
		boolean isLoaded = false;
		/**
		 * Results of indexing of the entailment query. If this is {@code null},
		 * then {@link #isComputed} must be {@code false}.
		 */
		IndexedEntailmentQuery<? extends Entailment> indexed = null;
		/**
		 * Whether the query is locked. While this is true and {@link #indexed}
		 * is not {@code null}, the query results must be available.
		 */
		private int lockedCount_ = 0;
		/**
		 * How many times an {@link Occurrence} occurs in the query.
		 * {@code null} means no occurrences.
		 */
		Map<Occurrence, Integer> occurrences = null;

		public QueryState(final ElkAxiom query) {
			super(query);
		}

		@Override
		public Entailment getEntailment() throws ElkQueryException {
			if (indexed == null) {
				throw new ElkQueryException(
						"Query was not indexed: " + getQuery());
			}
			// else
			return indexed.getQuery();
		}

		@Override
		public boolean isEntailed() throws ElkQueryException {
			if (indexed == null) {
				throw new ElkQueryException(
						"Query was not indexed: " + getQuery());
			}
			// else
			final boolean result = new InferenceDerivabilityChecker<Entailment>(
					getEvidence(true)).isDerivable(indexed.getQuery());
			if (!result) {
				incompleteness_.log(incompleteness_
						.getIncompletenessMonitorForEntailmentQuery(
								getOccurrenceStore(this)));
			}
			// If the query is entailed, the result is complete.
			return result;
		}

		@Override
		public EntailmentProof getEvidence(final boolean onlyOne)
				throws ElkQueryException {
			if (indexed == null) {
				throw new ElkQueryException(
						"Query was not indexed: " + getQuery());
			}
			// else

			incompleteness_.log(
					incompleteness_.getIncompletenessMonitorForEntailmentQuery(
							getOccurrenceStore(this)));

			final EntailmentProof inconsistencyEvidence = new InconsistencyProofWrapper(
					consistencyCheckingState_.getEvidence(onlyOne));

			if (consistencyCheckingState_.isInconsistent() && onlyOne) {
				return inconsistencyEvidence;
			}
			// else

			final EntailmentProof entailmentEvidence = indexed
					.getEvidence(onlyOne, saturationState_, conclusionFactory_);

			return new EntailmentProofUnion(inconsistencyEvidence,
					entailmentEvidence);
		}

		public synchronized boolean lock() {
			final boolean wasLocked = isLocked();
			lockedCount_++;
			return wasLocked != isLocked();
		}

		@Override
		public synchronized boolean isLocked() {
			return lockedCount_ > 0;
		}

		@Override
		public synchronized boolean unlock() {
			if (!isLocked()) {
				return false;
			}
			// else
			lockedCount_--;
			return !isLocked();
		}

	}

	private final SaturationState<? extends Context> saturationState_;

	private final ConsistencyCheckingState consistencyCheckingState_;

	private final SaturationConclusion.Factory conclusionFactory_;

	private final IncompletenessManager incompleteness_;

	public <C extends Context> EntailmentQueryState(
			final ReasonerConfiguration config,
			final SaturationState<C> saturationState,
			final ConsistencyCheckingState consistencyCheckingState,
			final SaturationConclusion.Factory factory,
			final IncompletenessManager incompletenessManager) {
		this.saturationState_ = saturationState;
		this.consistencyCheckingState_ = consistencyCheckingState;
		this.conclusionFactory_ = factory;
		this.incompleteness_ = incompletenessManager;
		final Object builder = config
				.getParameter(ReasonerConfiguration.ENTAILMENT_QUERY_EVICTOR);
		LOGGER_.info("{} = {}", ReasonerConfiguration.ENTAILMENT_QUERY_EVICTOR,
				builder);
		this.queriedEvictor_ = ((Evictor.Builder) builder).build();
	}

	/**
	 * Registers the supplied axioms for querying. If all necessary stages are
	 * run after calling this method for some axioms, neither
	 * {@link #isEntailed(Iterable)} nor a
	 * {@link org.semanticweb.elk.reasoner.query.ProperEntailmentQueryResult
	 * ProperEntailmentQueryResult} for any of these axioms will throw
	 * {@link ElkQueryException}.
	 * 
	 * @param axioms
	 * @return {@code true} if this is a new query, {@code false} if this class
	 *         expression has already been registered.
	 */
	void registerQueries(final Iterable<? extends ElkAxiom> axioms) {

		lastQueries_.clear();
		for (final ElkAxiom axiom : axioms) {

			LOGGER_.trace("entailment query registered {}", axiom);

			lastQueries_.add(axiom);

			QueryState state = queried_.get(axiom);
			if (state != null) {
				queriedEvictor_.add(state);
				continue;
			}
			// Create query state.
			state = new QueryState(axiom);
			queried_.put(axiom, state);
			queriedEvictor_.add(state);
			toLoad_.offer(axiom);

		}

	}

	public IndexingListener getIndexingListener() {
		return indexingListener_;
	}

	@Override
	public EntailmentQueryLoader getQueryLoader(
			final InterruptMonitor interrupter) {
		return indexingListener_.new Loader(interrupter);
	}

	private final QueryIndexingListener indexingListener_ = new QueryIndexingListener();

	private class QueryIndexingListener implements IndexingListener {

		// Indexing state.
		private boolean isInsertion_;
		private QueryState beingIndexed_ = null;

		private void checkStateOnIndexing() throws IllegalStateException {
			if (beingIndexed_ == null) {
				throw new IllegalStateException(
						"Indexing listener notified while no query is being loaded!");
			}
		}

		private class Loader extends AbstractEntailmentQueryLoader {

			public Loader(final InterruptMonitor interrupter) {
				super(interrupter);
			}

			@Override
			public void load(
					final ElkAxiomVisitor<IndexedEntailmentQuery<? extends Entailment>> inserter,
					final ElkAxiomVisitor<IndexedEntailmentQuery<? extends Entailment>> deleter)
					throws ElkLoadingException {

				// First evict and unload.
				isInsertion_ = false;
				final Iterator<QueryState> evicted = queriedEvictor_
						.evict(doNotEvict_);
				while (evicted.hasNext()) {
					final QueryState state = evicted.next();
					queried_.remove(state.getQuery());
					if (state.isLoaded) {
						beingIndexed_ = state;
						state.getQuery().accept(deleter);
						state.indexed = null;
						state.isLoaded = false;
					}
				}

				/*
				 * Load all registered queries that are not loaded and assign
				 * state.indexed if successful.
				 */
				isInsertion_ = true;
				ElkAxiom axiom;
				while ((axiom = toLoad_.poll()) != null) {
					final QueryState state = queried_.get(axiom);
					if (state == null) {
						continue;
					}

					state.isLoaded = true;
					beingIndexed_ = state;
					state.indexed = axiom.accept(inserter);

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

		@Override
		public void onIndexing(final Occurrence occurrence) {
			checkStateOnIndexing();
			if (isInsertion_) {
				if (beingIndexed_.occurrences == null) {
					beingIndexed_.occurrences = new ArrayHashMap<Occurrence, Integer>(
							4);
				}
				Integer noOccurrences = beingIndexed_.occurrences
						.get(occurrence);
				if (noOccurrences == null) {
					noOccurrences = 0;
				}
				noOccurrences++;
				beingIndexed_.occurrences.put(occurrence, noOccurrences);
			} else {
				if (beingIndexed_.occurrences == null) {
					return;
				}
				Integer noOccurrences = beingIndexed_.occurrences
						.get(occurrence);
				if (noOccurrences == null) {
					return;
				}
				noOccurrences--;
				if (noOccurrences <= 0) {
					beingIndexed_.occurrences.remove(occurrence);
					if (beingIndexed_.occurrences.isEmpty()) {
						beingIndexed_.occurrences = null;
					}
				} else {
					beingIndexed_.occurrences.put(occurrence, noOccurrences);
				}
			}
		}

	}

	private final Predicate<QueryState> doNotEvict_ = new Predicate<QueryState>() {
		@Override
		public boolean apply(final QueryState state) {
			return state.isLocked() || lastQueries_.contains(state.getQuery());
		}
	};

	private final Condition<IndexedContextRoot> IS_NOT_SATURATED = new Condition<IndexedContextRoot>() {
		@Override
		public boolean holds(final IndexedContextRoot root) {
			final Context context = saturationState_.getContext(root);
			return context == null || !context.isInitialized()
					|| !context.isSaturated();
		}
	};

	private final Operations.Transformation<QueryState, Iterable<? extends IndexedContextRoot>> POSITIVELY_INDEXED = new Operations.Transformation<QueryState, Iterable<? extends IndexedContextRoot>>() {
		@Override
		public Iterable<? extends IndexedContextRoot> transform(
				final QueryState state) {
			if (state.indexed == null) {
				return null;
			}
			// else
			final Collection<? extends IndexedContextRoot> roots = state.indexed
					.getPositivelyIndexed();
			return Operations.filter(roots, IS_NOT_SATURATED);
		}
	};

	/**
	 * @return {@link IndexedContextRoot} that are needed to answer the
	 *         registered entailment queries and not saturated.
	 */
	Collection<IndexedContextRoot> getNotSaturatedPositivelyIndexedRoots() {
		int sizeUpper = 0;
		for (final QueryState state : queried_.values()) {
			if (state.indexed != null) {
				sizeUpper += state.indexed.getPositivelyIndexed().size();
			}
		}
		final Iterable<IndexedContextRoot> result = Operations
				.concat(Operations.map(queried_.values(), POSITIVELY_INDEXED));
		return Operations.getCollection(result, sizeUpper);
	}

	private OccurrenceStore getOccurrenceStore(final QueryState state) {
		return new OccurrenceStore() {

			@Override
			public Collection<? extends ElkObject> occursIn(
					final Occurrence occurrence) {
				if (state == null || state.occurrences == null
						|| !state.occurrences.containsKey(occurrence)) {
					return Collections.emptySet();
				}
				// else
				return Collections.singleton(state.getQuery());
			}

		};
	}

	/**
	 * Decides whether the supplied {@code axioms} are entailed. If some of the
	 * supplied axioms was not registered by {@link #registerQueries(Iterable)}.
	 * 
	 * @param axioms
	 *            Entailment of what axioms is queried.
	 * @return A map from each queried axiom to the result of entailment query
	 *         for that axiom.
	 * @param axioms
	 * @return
	 * @throws ElkQueryException
	 *             When some of the axioms was not registered by
	 *             {@link #registerQueries(Iterable)}.
	 */
	Map<ElkAxiom, EntailmentQueryResult> isEntailed(
			final Iterable<? extends ElkAxiom> axioms)
			throws ElkQueryException {

		final Map<ElkAxiom, EntailmentQueryResult> results = new ArrayHashMap<ElkAxiom, EntailmentQueryResult>();

		for (final ElkAxiom axiom : axioms) {
			if (!EntailmentQueryConverter
					.isEntailmentCheckingSupported(axiom.getClass())) {
				results.put(axiom,
						new UnsupportedQueryTypeEntailmentQueryResultImpl(
								axiom));
				continue;
			}
			// else
			final QueryState state = queried_.get(axiom);
			if (state == null) {
				throw new ElkQueryException(
						"Query was not registered: " + axiom);
			}
			// else
			if (state.indexed == null) {
				results.put(axiom,
						new UnsupportedIndexingEntailmentQueryResultImpl(
								axiom));
				continue;
			}
			// else
			state.lock();
			results.put(axiom, state);
		}

		return results;
	}

}
