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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import org.liveontologies.puli.Producer;
import org.liveontologies.puli.statistics.HasStats;
import org.liveontologies.puli.statistics.NestedStats;
import org.liveontologies.puli.statistics.ResetStats;
import org.liveontologies.puli.statistics.Stat;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
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
import org.semanticweb.elk.reasoner.tracing.factories.ClassInferenceBlockingFilter;
import org.semanticweb.elk.reasoner.tracing.factories.TracingJobListener;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Evictor;
import org.semanticweb.elk.util.collections.NQEvictor;
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

	public static final int DEFAULT_CONTEXT_CACHE_CAPACITY = 256;
	public static final String PROPERTY_PREFIX_CONTEXT_CACHE_CAPACITY = "org.semanticweb.elk.reasoner.tracing.contextcache.capacity.level";
	public static final double DEFAULT_CONTEXT_CACHE_LOAD_FACTOR = 0.75;
	public static final String PROPERTY_PREFIX_CONTEXT_CACHE_LOAD_FACTOR = "org.semanticweb.elk.reasoner.tracing.contextcache.loadfactor.level";

	public static final int DEFAULT_CONCLUSION_CACHE_CAPACITY = 65536;
	public static final String PROPERTY_PREFIX_CONCLUSION_CACHE_CAPACITY = "org.semanticweb.elk.reasoner.tracing.conclusioncache.capacity.level";
	public static final double DEFAULT_CONCLUSION_CACHE_LOAD_FACTOR = 0.75;
	public static final String PROPERTY_PREFIX_CONCLUSION_CACHE_LOAD_FACTOR = "org.semanticweb.elk.reasoner.tracing.conclusioncache.loadfactor.level";

	public static final int getCapacity(final String propName,
			final int defaultValue) {
		final int cacheCapacity = getProperty(propName, Integer.class,
				defaultValue);
		return cacheCapacity < 0 ? Integer.MAX_VALUE : cacheCapacity;
	}

	public static final int getEvictBeforeAddCount(final String propName,
			final int defaultValue) {
		final int value = getProperty(propName, Integer.class, defaultValue);
		return value < 0 ? defaultValue : value;
	}

	public static final double getLoadFactor(final String propName,
			final double defaultValue) {
		final double loadFactor = getProperty(propName, Double.class,
				defaultValue);
		return loadFactor < 0 || loadFactor > 1 ? 1 : loadFactor;
	}

	private final Queue<ClassConclusion> toTrace_ = new ConcurrentLinkedQueue<ClassConclusion>();
	private final Set<ClassConclusion> currentlyTraced_ = new ArrayHashSet<ClassConclusion>();
	private final Set<IndexedContextRoot> currentlyTracedContexts_ = new ArrayHashSet<IndexedContextRoot>();

	private final ConcurrentMap<IndexedContextRoot, ModifiableTracingProof<ClassInference>> tracedContexts_ = new ConcurrentHashMap<IndexedContextRoot, ModifiableTracingProof<ClassInference>>();

	private final Evictor<IndexedContextRoot> recentContexts_;

	private final ConcurrentMap<ClassConclusion, Collection<ClassInference>> tracedConclusions_ = new ConcurrentHashMap<ClassConclusion, Collection<ClassInference>>();

	private final Evictor<ClassConclusion> recentConclusions_;

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

	public <C extends Context> TraceState(
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

		this.recentContexts_ = buildCache(
				PROPERTY_PREFIX_CONTEXT_CACHE_CAPACITY,
				DEFAULT_CONTEXT_CACHE_CAPACITY,
				PROPERTY_PREFIX_CONTEXT_CACHE_LOAD_FACTOR,
				DEFAULT_CONTEXT_CACHE_LOAD_FACTOR);
		this.recentConclusions_ = buildCache(
				PROPERTY_PREFIX_CONCLUSION_CACHE_CAPACITY,
				DEFAULT_CONCLUSION_CACHE_CAPACITY,
				PROPERTY_PREFIX_CONCLUSION_CACHE_LOAD_FACTOR,
				DEFAULT_CONCLUSION_CACHE_LOAD_FACTOR);

	}

	private static <E> Evictor<E> buildCache(
			final String capacityPropertyPrefix, final int defaultCapacity,
			final String loadFactorPropertyPrefix,
			final double defaultLoadFactor) {
		final NQEvictor.Builder conclusionCacheBuilder = new NQEvictor.Builder();
		boolean noProperty = true;
		for (int level = 1;; level++) {
			final Integer capacityValue = getProperty(
					capacityPropertyPrefix + level, Integer.class, null);
			final Double loadFactorValue = getProperty(
					loadFactorPropertyPrefix + level, Double.class, null);
			if (capacityValue == null && loadFactorValue == null) {
				break;
			}
			noProperty = false;
			LOGGER_.info("{}{} = {}", capacityPropertyPrefix, level,
					capacityValue);
			LOGGER_.info("{}{} = {}", loadFactorPropertyPrefix, level,
					loadFactorValue);

			final int capacity = capacityValue == null ? defaultCapacity
					: (capacityValue < 0 ? Integer.MAX_VALUE : capacityValue);
			final double loadFactor = loadFactorValue == null
					? defaultLoadFactor
					: (loadFactorValue < 0 || loadFactorValue > 1 ? 1
							: loadFactorValue);

			conclusionCacheBuilder.addLevel(capacity, loadFactor);
		}
		if (noProperty) {
			LOGGER_.info("{}1 = {}", capacityPropertyPrefix, defaultCapacity);
			LOGGER_.info("{}1 = {}", loadFactorPropertyPrefix,
					defaultLoadFactor);
			conclusionCacheBuilder.addLevel(defaultCapacity, defaultLoadFactor);
		}
		return conclusionCacheBuilder.build();
	}

	/**
	 * @param conclusion
	 * @return Whether the queue changed.
	 */
	public synchronized boolean toTrace(ClassConclusion conclusion) {
		final IndexedContextRoot root = conclusion.getTraceRoot();
		final Iterator<IndexedContextRoot> evictedContexts = recentContexts_
				.addAndEvict(root, new Predicate<IndexedContextRoot>() {
					@Override
					public boolean apply(final IndexedContextRoot root) {
						return currentlyTracedContexts_.contains(root);
					}
				});
		final Iterator<ClassConclusion> evictedConclusions = recentConclusions_
				.addAndEvict(conclusion, new Predicate<ClassConclusion>() {
					@Override
					public boolean apply(final ClassConclusion conclusion) {
						return currentlyTraced_.contains(conclusion);
					}
				});
		// Evict. conclusion should not be evicted, due to retainCount above.
		while (evictedContexts.hasNext()) {
			tracedContexts_.remove(evictedContexts.next());
		}
		while (evictedConclusions.hasNext()) {
			tracedConclusions_.remove(evictedConclusions.next());
		}
		// Check cache.
		if (tracedConclusions_.get(conclusion) != null) {
			stats_.nConclusionCacheHits++;
			return false;
		}
		// else
		stats_.nConclusionCacheMisses++;
		final ModifiableTracingProof<ClassInference> contextInferences = tracedContexts_
				.get(root);
		if (contextInferences != null) {
			stats_.nContextCacheHits++;
			final Collection<ClassInference> newInfs = new ArrayList<ClassInference>();
			final Collection<ClassInference> infs = tracedConclusions_
					.putIfAbsent(conclusion, newInfs);
			if (infs == null) {
				// this thread adds the inferences as the first one
				newInfs.addAll(contextInferences.getInferences(conclusion));
			}
			return false;
		}
		// else
		stats_.nContextCacheMisses++;
		if (currentlyTraced_.add(conclusion)) {
			LOGGER_.trace("{}: to trace", conclusion);
			currentlyTracedContexts_.add(root);
			toTrace_.add(conclusion);
		}
		return true;
	}

	public ClassConclusion pollToTrace() {
		return toTrace_.poll();
	}

	private final TracingJobListener tracingListener_ = new TracingJobListener() {

		@Override
		public void notifyJobFinished(final ClassConclusion conclusion,
				final Iterable<? extends ClassInference> output) {

			final IndexedContextRoot root = conclusion.getTraceRoot();

			ModifiableTracingProof<ClassInference> proof = tracedContexts_
					.get(root);
			if (proof == null) {
				ModifiableTracingProof<ClassInference> newProof = new ModifiableTracingProofImpl<ClassInference>();
				proof = tracedContexts_.putIfAbsent(root, newProof);
				if (proof == null) {
					proof = newProof;
					final ClassInferenceBlockingFilter filter = new ClassInferenceBlockingFilter(
							proof);
					for (final ClassInference inference : output) {
						filter.produce(inference);
					}
				}
			}

			synchronized (proof) {
				/*
				 * Different threads may trace the same context for different
				 * conclusion, so while one is producing the inferences, the
				 * other should wait.
				 * 
				 * If different threads trace the same conclusion, they'll
				 * synchronize on the same proof, so no more synchronization is
				 * needed.
				 */
				if (!tracedConclusions_.containsKey(conclusion)) {
					tracedConclusions_.put(conclusion,
							new ArrayList<ClassInference>(
									proof.getInferences(conclusion)));
				}
			}

		}

		public void notifyComputationFinished() {
			currentlyTraced_.clear();
			currentlyTracedContexts_.clear();
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

	public static <T> T getProperty(final String property,
			final Class<T> valueClass, final T defaultValue) {
		final String value = System.getProperty(property);
		if (value == null) {
			return defaultValue;
		}
		// else
		if (valueClass == null) {
			return defaultValue;
		}
		try {
			final Method valueOf = valueClass.getMethod("valueOf",
					String.class);
			final int mod = valueOf.getModifiers();
			if (!valueClass.isAssignableFrom(valueOf.getReturnType())
					|| !Modifier.isStatic(mod) || !Modifier.isPublic(mod)) {
				return defaultValue;
			}
			// else
			@SuppressWarnings("unchecked")
			final T result = (T) valueOf.invoke(null, value);
			return result;
		} catch (final Exception e) {
			// Return defaultValue.
		}
		// else
		return defaultValue;
	}

	public static <T> T getProperty(final String property,
			final Class<T> valueClass) {
		return getProperty(property, valueClass, null);
	}

}
