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
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.liveontologies.puli.InferenceDerivabilityChecker;
import org.semanticweb.elk.loading.AbstractEntailmentQueryLoader;
import org.semanticweb.elk.loading.ElkLoadingException;
import org.semanticweb.elk.loading.EntailmentQueryLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.reasoner.consistency.ConsistencyCheckingState;
import org.semanticweb.elk.reasoner.entailments.EntailmentProofUnion;
import org.semanticweb.elk.reasoner.entailments.InconsistencyProofWrapper;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentProof;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
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
import org.semanticweb.elk.util.collections.Condition;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.RecencyQueue;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keeps track of axioms that were queried for entailment.
 * 
 * @author Peter Skocovsky
 */
public class EntailmentQueryState implements EntailmentQueryLoader.Factory {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ClassExpressionQueryState.class);

	public static final int CACHE_CAPACITY = 512;
	public static final float EVICTION_FACTOR = 0.5f;

	/**
	 * Maps axioms that were queried to the states of their query.
	 */
	private final Map<ElkAxiom, QueryState> queried_ = new ConcurrentHashMap<ElkAxiom, QueryState>();

	/**
	 * Contains axioms that were queried but not loaded.
	 */
	private final Queue<ElkAxiom> toLoad_ = new ConcurrentLinkedQueue<ElkAxiom>();

	/**
	 * Contains the same states as {@link #queried_} in the order in which they
	 * were queried.
	 */
	private final RecencyQueue<QueryState> recentlyQueried_ = new RecencyQueue<QueryState>();

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
			return new InferenceDerivabilityChecker<Entailment>(
					getEvidence(true)).isDerivable(indexed.getQuery());
		}

		@Override
		public EntailmentProof getEvidence(final boolean onlyOne)
				throws ElkQueryException {
			if (indexed == null) {
				throw new ElkQueryException(
						"Query was not indexed: " + getQuery());
			}
			// else

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

	/**
	 * The number of axioms that were registered by the last call.
	 */
	private int lastQuerySize_ = 0;

	private final SaturationState<? extends Context> saturationState_;

	private final ConsistencyCheckingState consistencyCheckingState_;

	private final SaturationConclusion.Factory conclusionFactory_;

	public <C extends Context> EntailmentQueryState(
			final SaturationState<C> saturationState,
			final ConsistencyCheckingState consistencyCheckingState,
			final SaturationConclusion.Factory factory) {
		this.saturationState_ = saturationState;
		this.consistencyCheckingState_ = consistencyCheckingState;
		this.conclusionFactory_ = factory;
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

		int axiomCount = 0;
		for (final ElkAxiom axiom : axioms) {

			LOGGER_.trace("entailment query registered {}", axiom);

			axiomCount++;

			QueryState state = queried_.get(axiom);
			if (state != null) {
				recentlyQueried_.offer(state);
				continue;
			}
			// Create query state.
			state = new QueryState(axiom);
			queried_.put(axiom, state);
			recentlyQueried_.offer(state);
			toLoad_.offer(axiom);

		}

		lastQuerySize_ = axiomCount;

	}

	@Override
	public EntailmentQueryLoader getQueryLoader(
			final InterruptMonitor interrupter) {
		return new Loader(interrupter);
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

			/*
			 * Load all registered queries that are not loaded and assign
			 * state.indexed if successful.
			 */
			ElkAxiom axiom;
			while ((axiom = toLoad_.poll()) != null) {
				final QueryState state = queried_.get(axiom);
				if (state == null) {
					continue;
				}

				state.isLoaded = true;
				state.indexed = axiom.accept(inserter);

				if (isInterrupted()) {
					return;
				}
			}

			/*
			 * If the cache size is exceeded, evict old entries.
			 */
			if (recentlyQueried_.size() > CACHE_CAPACITY) {

				// @formatter:off
				final int goalCapacity = Math.max(
						Math.min(
								(int) (recentlyQueried_.size() * EVICTION_FACTOR),
								CACHE_CAPACITY),
						lastQuerySize_);
				// @formatter:on

				final Iterator<QueryState> iter = recentlyQueried_.iterator();
				while (iter.hasNext()
						&& recentlyQueried_.size() > goalCapacity) {
					final QueryState state = iter.next();
					/*
					 * While goalCapacity is at least lastQuerySize_, at least
					 * the last lastQuerySize_ queries will remain cached.
					 */

					if (state.isLocked()) {
						// Do not evict.
						continue;
					}
					// else evict

					iter.remove();
					queried_.remove(state.getQuery());
					if (state.isLoaded) {
						state.getQuery().accept(deleter);
						state.indexed = null;
						state.isLoaded = false;
					}

					if (isInterrupted()) {
						return;
					}
				}

			}

		}

		@Override
		public boolean isLoadingFinished() {
			return toLoad_.isEmpty();
		}

	}

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
