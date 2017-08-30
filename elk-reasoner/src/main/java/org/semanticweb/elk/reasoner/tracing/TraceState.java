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
package org.semanticweb.elk.reasoner.tracing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.liveontologies.puli.Producer;
import org.liveontologies.puli.statistics.HasStats;
import org.liveontologies.puli.statistics.NestedStats;
import org.liveontologies.puli.statistics.ResetStats;
import org.liveontologies.puli.statistics.Stat;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.indexing.classes.ResolvingModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverterImpl;
import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateDummyChangeListener;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.SaturationInference;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainTautology;
import org.semanticweb.elk.reasoner.stages.PropertyHierarchyCompositionState;
import org.semanticweb.elk.reasoner.tracing.factories.TracingJobListener;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Evictor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

/**
 * A collections of objects for tracing contexts and keeping the relevant
 * information about the state of tracing.
 * 
 * TODO: filter out cyclic inferences
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 */
public class TraceState
		implements Producer<ObjectPropertyInference>, TracingProof, HasStats {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(TraceState.class);

	private final Queue<ClassConclusion> toTrace_ = new ConcurrentLinkedQueue<ClassConclusion>();
	private final Set<Conclusion> currentlyTraced_ = new ArrayHashSet<Conclusion>();

	private final Map<Conclusion, Collection<ClassInference>> tracedContexts_ = new ArrayHashMap<Conclusion, Collection<ClassInference>>();

	private final Evictor<Conclusion> recentContexts_;

	private final Map<Conclusion, Collection<ClassInference>> tracedConclusions_ = new ArrayHashMap<Conclusion, Collection<ClassInference>>();

	private final Evictor<Conclusion> recentConclusions_;

	private final Set<ElkAxiom> indexedAxioms_ = new ArrayHashSet<ElkAxiom>();

	private final ModifiableTracingProof<ObjectPropertyInference> objectPropertyInferences_ = new SynchronizedModifiableTracingProof<ObjectPropertyInference>();

	private final ModifiableTracingProof<IndexedAxiomInference> indexedAxiomInferences_ = new SynchronizedModifiableTracingProof<IndexedAxiomInference>();

	private final SaturationInference.Visitor<Void> inferenceProducer_ = new InferenceProducer();

	private final Conclusion.Visitor<Collection<? extends TracingInference>> inferenceGetter_ = new InferenceGetter();

	private final ElkAxiomConverter elkAxiomConverter_;

	// Stats.
	public class Stats {
		@Stat
		public int nContextCacheHits = 0;
		@Stat
		public int nContextCacheMisses = 0;
		@Stat
		public int nConclusionCacheHits = 0;
		@Stat
		public int nConclusionCacheMisses = 0;

		@ResetStats
		public void resetStats() {
			nContextCacheHits = 0;
			nContextCacheMisses = 0;
			nConclusionCacheHits = 0;
			nConclusionCacheMisses = 0;
		}

		@NestedStats(name = "recentContexts")
		public Object getRecentContextsStats() {
			return recentContexts_.getStats();
		}

		@NestedStats(name = "recentConclusions")
		public Object getRecentConclusionsStats() {
			return recentConclusions_.getStats();
		}
	}

	private final Stats stats_ = new Stats();

	@Override
	public Object getStats() {
		return stats_;
	}

	public <C extends Context> TraceState(final ReasonerConfiguration config,
			final SaturationState<C> saturationState,
			final PropertyHierarchyCompositionState propertySaturationState,
			ElkObject.Factory elkFactory, ModifiableOntologyIndex index) {
		// the axiom converter that resolves indexed axioms from the given cache
		// and additionally saves the inferences that produced them
		this.elkAxiomConverter_ = new ElkAxiomConverterImpl(elkFactory,
				new ResolvingModifiableIndexedObjectFactory(index), index,
				indexedAxiomInferences_);

		saturationState
				.addListener(new SaturationStateDummyChangeListener<C>() {

					@Override
					public void contextsClear() {
						clearClassInferences();
						clearIndexedAxiomInferences();
					}

					@Override
					public void contextMarkNonSaturated(final C context) {
						// TODO: remove only affected inferences
						clearClassInferences();
						clearIndexedAxiomInferences();
					}

				});
		propertySaturationState
				.addListener(new PropertyHierarchyCompositionState.Listener() {

					@Override
					public void propertyBecameSaturated(
							IndexedPropertyChain chain) {
						// no-op
					}

					@Override
					public void propertyBecameNotSaturated(
							IndexedPropertyChain chain) {
						clearObjectPropertyInferences();
						clearIndexedAxiomInferences();
					}
				});

		Object builder = config
				.getParameter(ReasonerConfiguration.TRACING_CONTEXT_EVICTOR);
		LOGGER_.info("{} = {}", ReasonerConfiguration.TRACING_CONTEXT_EVICTOR,
				builder);
		this.recentContexts_ = ((Evictor.Builder) builder).build();
		builder = config
				.getParameter(ReasonerConfiguration.TRACING_CONCLUSION_EVICTOR);
		LOGGER_.info("{} = {}",
				ReasonerConfiguration.TRACING_CONCLUSION_EVICTOR, builder);
		this.recentConclusions_ = ((Evictor.Builder) builder).build();

	}

	/**
	 * @param conclusion
	 * @return Whether the queue changed.
	 */
	public synchronized boolean toTrace(final ClassConclusion conclusion) {
		recentConclusions_.add(conclusion);
		// Check cache.
		if (tracedConclusions_.get(conclusion) != null) {
			stats_.nConclusionCacheHits++;
			return false;
		}
		// else
		stats_.nConclusionCacheMisses++;
		final Collection<ClassInference> infs = tracedContexts_.get(conclusion);
		if (infs != null) {
			stats_.nContextCacheHits++;
			recentContexts_.add(conclusion);
			tracedConclusions_.put(conclusion, infs);
			return false;
		}
		// else
		// Queue up.
		stats_.nContextCacheMisses++;
		if (currentlyTraced_.add(conclusion)) {
			LOGGER_.trace("{}: to trace", conclusion);
			toTrace_.add(conclusion);
		}
		// Evict.
		final Iterator<Conclusion> evictedContexts = recentContexts_.evict();
		final Iterator<Conclusion> evictedConclusions = recentConclusions_
				.evict(new Predicate<Conclusion>() {
					@Override
					public boolean apply(final Conclusion conclusion) {
						return currentlyTraced_.contains(conclusion);
					}
				});
		while (evictedContexts.hasNext()) {
			tracedContexts_.remove(evictedContexts.next());
		}
		while (evictedConclusions.hasNext()) {
			tracedConclusions_.remove(evictedConclusions.next());
		}
		return true;
	}

	public ClassConclusion pollToTrace() {
		return toTrace_.poll();
	}

	private final TracingJobListener tracingListener_ = new TracingJobListener() {

		@Override
		public synchronized void notifyJobFinished(
				final ClassConclusion conclusion,
				final ModifiableTracingProof<ClassInference> proof) {
			for (final Conclusion concl : proof.getAllConclusions()) {
				final Collection<? extends ClassInference> tracedInfs = proof
						.getInferences(concl);
				if (!tracedInfs.isEmpty()) {
					recentContexts_.add(concl);
					tracedContexts_.put(concl,
							new ArrayList<ClassInference>(tracedInfs));
				}
			}
			final Collection<? extends ClassInference> tracedInfs = proof
					.getInferences(conclusion);
			if (!tracedInfs.isEmpty()) {
				final Conclusion concl = new TracingInferenceConclusion(
						tracedInfs.iterator().next());
				tracedConclusions_.put(concl,
						new ArrayList<ClassInference>(tracedInfs));
			}

		}

		public void notifyComputationFinished() {
			currentlyTraced_.clear();
		};

	};

	public TracingJobListener getTracingListener() {
		return tracingListener_;
	}

	private void clearClassInferences() {
		tracedContexts_.clear();
		tracedConclusions_.clear();
	}

	private void clearObjectPropertyInferences() {
		objectPropertyInferences_.clear();
	}

	private void clearIndexedAxiomInferences() {
		indexedAxiomInferences_.clear();
		indexedAxioms_.clear();
	}

	@Override
	public Collection<? extends TracingInference> getInferences(
			Conclusion conclusion) {
		return conclusion.accept(inferenceGetter_);
	}

	@Override
	public void produce(ObjectPropertyInference inference) {
		inference.accept(inferenceProducer_);
	}

	synchronized void indexAxiom(ElkAxiom axiom) {
		if (!indexedAxioms_.add(axiom)) {
			// already done
			return;
		}
		// else index axiom
		axiom.accept(elkAxiomConverter_);
	}

	/**
	 * Delegates getting inferences to the corresponding proof
	 * 
	 * @author Yevgeny Kazakov
	 */
	private class InferenceGetter extends
			DummyConclusionVisitor<Collection<? extends TracingInference>> {

		@Override
		protected Collection<? extends ClassInference> defaultVisit(
				final ClassConclusion conclusion) {
			final Collection<ClassInference> inferences = tracedConclusions_
					.get(conclusion);
			if (inferences == null) {
				throw new ElkRuntimeException(
						"Conclusion not traced: " + conclusion);
			}
			// else
			return inferences;
		}

		@Override
		protected Collection<? extends ObjectPropertyInference> defaultVisit(
				ObjectPropertyConclusion conclusion) {
			return objectPropertyInferences_.getInferences(conclusion);
		}

		@Override
		public Collection<? extends TracingInference> visit(
				final SubPropertyChain conclusion) {
			/*
			 * Tautologies over trivial properties may not be recorded, so they
			 * should be added to the result.
			 */
			final Collection<? extends TracingInference> infs = super.visit(
					conclusion);
			if (infs.iterator().hasNext()) {
				// If some inferences are recorded, they should be complete.
				return infs;
			}
			// else
			final IndexedPropertyChain subChain = conclusion.getSubChain();
			if (conclusion.getSuperChain().equals(subChain)) {
				return Collections
						.singleton(new SubPropertyChainTautology(subChain));
			}
			// else
			return infs;
		}

		@Override
		protected Collection<? extends IndexedAxiomInference> defaultVisit(
				IndexedAxiom conclusion) {
			// compute inferences on demand
			indexAxiom(conclusion.getOriginalAxiom());
			return indexedAxiomInferences_.getInferences(conclusion);
		}

	}

	/**
	 * Delegates saving inferences to the corresponding proof
	 * 
	 * @author Yevgeny Kazakov
	 */
	private class InferenceProducer extends TracingInferenceDummyVisitor<Void> {

		@Override
		protected Void defaultVisit(ObjectPropertyInference inference) {
			objectPropertyInferences_.produce(inference);
			return null;
		}

	}

}
