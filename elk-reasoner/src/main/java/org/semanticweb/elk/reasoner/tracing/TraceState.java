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

import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.liveontologies.proof.util.Producer;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
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
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collections of objects for tracing contexts and keeping the relevant
 * information about the state of tracing.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 * 
 *         TODO: filter out cyclic inferences
 */
public class TraceState
		implements Producer<SaturationInference>, TracingInferenceSet {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(TraceState.class);

	private final Queue<ClassConclusion> toTrace_ = new ConcurrentLinkedQueue<ClassConclusion>();

	private final Set<ClassConclusion> traced_ = new HashSet<ClassConclusion>();

	private final Set<ElkAxiom> indexedAxioms_ = new ArrayHashSet<ElkAxiom>();

	private final ModifiableTracingInferenceSet<ClassInference> classInferences_ = new SynchronizedModifiableTracingInferenceSet<ClassInference>();

	private final ModifiableTracingInferenceSet<ObjectPropertyInference> objectPropertyInferences_ = new SynchronizedModifiableTracingInferenceSet<ObjectPropertyInference>();

	private final ModifiableTracingInferenceSet<IndexedAxiomInference> indexedAxiomInferences_ = new SynchronizedModifiableTracingInferenceSet<IndexedAxiomInference>();

	private final SaturationInference.Visitor<Void> inferenceProducer_ = new InferenceProducer();

	private final Conclusion.Visitor<Iterable<? extends TracingInference>> inferenceGetter_ = new InferenceGetter();

	private final ElkAxiomConverter elkAxiomConverter_;

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
	}

	public synchronized void toTrace(ClassConclusion conclusion) {
		if (traced_.add(conclusion)) {
			LOGGER_.trace("{}: to trace", conclusion);
			toTrace_.add(conclusion);
		}
	}

	public ClassConclusion pollToTrace() {
		return toTrace_.poll();
	}

	private void clearClassInferences() {
		classInferences_.clear();
		traced_.clear();
	}

	private void clearObjectPropertyInferences() {
		objectPropertyInferences_.clear();
	}

	private void clearIndexedAxiomInferences() {
		indexedAxiomInferences_.clear();
		indexedAxioms_.clear();
	}

	@Override
	public Iterable<? extends TracingInference> getInferences(
			Conclusion conclusion) {
		return conclusion.accept(inferenceGetter_);
	}

	@Override
	public void produce(SaturationInference inference) {
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
	 * Delegates getting inferences to the corresponding inference set
	 * 
	 * @author Yevgeny Kazakov
	 */
	private class InferenceGetter extends
			DummyConclusionVisitor<Iterable<? extends TracingInference>> {

		@Override
		protected Iterable<? extends ClassInference> defaultVisit(
				ClassConclusion conclusion) {
			return classInferences_.getInferences(conclusion);
		}

		@Override
		protected Iterable<? extends ObjectPropertyInference> defaultVisit(
				ObjectPropertyConclusion conclusion) {
			return objectPropertyInferences_.getInferences(conclusion);
		}

		@Override
		public Iterable<? extends TracingInference> visit(
				final SubPropertyChain conclusion) {
			/*
			 * Tautologies over trivial properties may not be recorded, so they
			 * should be added to the result.
			 */
			final Iterable<? extends TracingInference> infs = super.visit(
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
		protected Iterable<? extends IndexedAxiomInference> defaultVisit(
				IndexedAxiom conclusion) {
			// compute inferences on demand
			indexAxiom(conclusion.getOriginalAxiom());
			return indexedAxiomInferences_.getInferences(conclusion);
		}

	}

	/**
	 * Delegates saving inferences to the corresponding inference set
	 * 
	 * @author Yevgeny Kazakov
	 */
	private class InferenceProducer extends TracingInferenceDummyVisitor<Void> {

		@Override
		protected Void defaultVisit(ClassInference inference) {
			classInferences_.produce(inference);
			return null;
		}

		@Override
		protected Void defaultVisit(ObjectPropertyInference inference) {
			objectPropertyInferences_.produce(inference);
			return null;
		}

	}

}
