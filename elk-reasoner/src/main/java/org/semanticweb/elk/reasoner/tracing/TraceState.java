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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
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
import org.semanticweb.elk.util.collections.Evictor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private IndexedContextRoot toTrace_ = null;

	/**
	 * Cache of {@link ClassInference}s.
	 */
	private final Map<Conclusion, Collection<? extends ClassInference>> classInferencesCache_ = new HashMap<Conclusion, Collection<? extends ClassInference>>();

	/**
	 * Manages eviction from {@link #classInferencesCache_}. Each key added to
	 * {@link #classInferencesCache_} must be added to this evictor, otherwise
	 * it may never be evicted.
	 */
	private final Evictor<Conclusion> classInferenceEvictor_;

	private final Set<ElkAxiom> indexedAxioms_ = new HashSet<ElkAxiom>();

	private final ModifiableTracingProof<ObjectPropertyInference> objectPropertyInferences_ = new SynchronizedModifiableTracingProof<ObjectPropertyInference>();

	private final ModifiableTracingProof<IndexedAxiomInference> indexedAxiomInferences_ = new SynchronizedModifiableTracingProof<IndexedAxiomInference>();

	private final SaturationInference.Visitor<Void> inferenceProducer_ = new InferenceProducer();

	private final Conclusion.Visitor<Collection<? extends TracingInference>> inferenceGetter_ = new InferenceGetter();

	private final ElkAxiomConverter elkAxiomConverter_;

	// Stats.
	public class Stats {
		@Stat
		public int nCacheHits = 0;
		@Stat
		public int nCacheMisses = 0;

		@ResetStats
		public void resetStats() {
			nCacheHits = 0;
			nCacheMisses = 0;
		}

		@NestedStats(name = "evictor")
		public Object getRecentConclusionsStats() {
			return classInferenceEvictor_.getStats();
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
				new ResolvingModifiableIndexedObjectFactory(index), index, 0,
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

		final Object builder = config
				.getParameter(ReasonerConfiguration.TRACING_EVICTOR);
		LOGGER_.info("{}={}", ReasonerConfiguration.TRACING_EVICTOR, builder);
		this.classInferenceEvictor_ = ((Evictor.Builder) builder).build();

	}

	/**
	 * Requests inferences of a {@link Conclusion}.
	 * 
	 * @param conclusion
	 * @return {@code true} if they are ready and can be retrieved by
	 *         {@link #getInferences(Conclusion)}, returns {@code false} if the
	 *         tracing stage needs to be run before the inferences can be
	 *         retrieved by {@link #getInferences(Conclusion)}.
	 */
	public synchronized boolean requestInferences(final Conclusion conclusion) {
		LOGGER_.trace("{}: request inferences", conclusion);
		return conclusion.accept(requestedConclusionVisitor_);
	}

	private final Conclusion.Visitor<Boolean> requestedConclusionVisitor_ = new DummyConclusionVisitor<Boolean>() {

		@Override
		protected Boolean defaultVisit(final Conclusion conclusion) {
			return true;
		}

		@Override
		protected Boolean defaultVisit(final ClassConclusion conclusion) {
			tracingListener_.lastRequestedConclusion_ = conclusion;
			classInferenceEvictor_.add(conclusion);
			// Check cache.
			final Collection<? extends ClassInference> infs = classInferencesCache_
					.get(conclusion);
			if (infs != null) {
				stats_.nCacheHits++;
				tracingListener_.inferencesOfLastRequestedConclusion_ = infs;
				return true;
			}
			// else
			stats_.nCacheMisses++;
			toTrace_ = conclusion.getTraceRoot();
			return false;
		}

	};

	public synchronized IndexedContextRoot pollToTrace() {
		final IndexedContextRoot result = toTrace_;
		toTrace_ = null;
		return result;
	}

	private class ThisTracingJobListener implements TracingJobListener {

		/**
		 * The {@link ClassConclusion} that was passed to
		 * {@link #requestInferences(ClassConclusion)} as the last one.
		 */
		private ClassConclusion lastRequestedConclusion_ = null;
		/**
		 * Inferences of the {@link ClassConclusion} that was passed to
		 * {@link #requestInferences(ClassConclusion)} as the last one, if they
		 * have been cached or computed.
		 */
		private Collection<? extends ClassInference> inferencesOfLastRequestedConclusion_ = null;

		@Override
		public synchronized void notifyJobFinished(
				final IndexedContextRoot root,
				final ModifiableTracingProof<ClassInference> proof) {
			for (final Conclusion concl : proof.getAllConclusions()) {
				final Collection<? extends ClassInference> tracedInfs = proof
						.getInferences(concl);
				if (concl.equals(lastRequestedConclusion_)) {
					inferencesOfLastRequestedConclusion_ = tracedInfs;
				}
				if (!tracedInfs.isEmpty()
						&& !classInferencesCache_.containsKey(concl)) {
					classInferencesCache_.put(concl, tracedInfs);
					final Iterator<Conclusion> evictedConclusions = classInferenceEvictor_
							.addAndEvict(concl);
					while (evictedConclusions.hasNext()) {
						classInferencesCache_.remove(evictedConclusions.next());
					}
				}
			}
		}

	}

	private final ThisTracingJobListener tracingListener_ = new ThisTracingJobListener();

	public TracingJobListener getTracingListener() {
		return tracingListener_;
	}

	private void clearClassInferences() {
		classInferencesCache_.clear();
		tracingListener_.inferencesOfLastRequestedConclusion_ = null;
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
			Object conclusion) {
		if (conclusion instanceof Conclusion) {
			return ((Conclusion) conclusion).accept(inferenceGetter_);
		}
		// else
		return Collections.emptySet();
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
			if (!conclusion.equals(tracingListener_.lastRequestedConclusion_)
					|| tracingListener_.inferencesOfLastRequestedConclusion_ == null) {
				throw new ElkRuntimeException(
						"Conclusion not traced: " + conclusion);
			}
			// else
			return tracingListener_.inferencesOfLastRequestedConclusion_;
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
