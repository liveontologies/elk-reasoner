/**
 * 
 */
package org.semanticweb.elk.reasoner.tracing;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.indexing.classes.ResolvingModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverterImpl;
import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectCache;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.SaturationInference;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.ObjectPropertyInference;
import org.semanticweb.elk.util.collections.ArrayHashSet;

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
		implements TracingInferenceProducer<SaturationInference>, TracingInferenceSet {

	private final Queue<ClassConclusion> toTrace_ = new LinkedList<ClassConclusion>();

	private final Set<ElkAxiom> indexedAxioms_ = new ArrayHashSet<ElkAxiom>();

	private final ModifiableTracingInferenceSet<ClassInference> classInferences_ = new SynchronizedModifiableTracingInferenceSet<ClassInference>();

	private final ModifiableTracingInferenceSet<ObjectPropertyInference> objectPropertyInferences_ = new SynchronizedModifiableTracingInferenceSet<ObjectPropertyInference>();

	private final ModifiableTracingInferenceSet<IndexedAxiomInference> indexedAxiomInferences_ = new SynchronizedModifiableTracingInferenceSet<IndexedAxiomInference>();

	private final SaturationInference.Visitor<Void> inferenceProducer_ = new InferenceProducer();

	private final Conclusion.Visitor<Iterable<? extends TracingInference>> inferenceGetter_ = new InferenceGetter();

	private final ElkAxiomConverter elkAxiomConverter_;

	public TraceState(ElkObject.Factory elkFactory, ModifiableIndexedObjectCache cache) {
		// the axiom converter that resolves indexed axioms from the given cache
		// and additionally saves the inferences that produced them
		this.elkAxiomConverter_ = new ElkAxiomConverterImpl(elkFactory,
				new ResolvingModifiableIndexedObjectFactory(cache) {
					@Override
					protected <T extends ModifiableIndexedAxiomInference> T filter(
							T input) {
						indexedAxiomInferences_.produce(input);
						return input;
					}
				});
	}

	public Collection<? extends ClassConclusion> getToTrace() {
		return toTrace_;
	}

	public void addToTrace(ClassConclusion conclusion) {
		toTrace_.add(conclusion);
	}

	public void clearToTrace() {
		toTrace_.clear();
	}

	public void clearClassInferences() {
		classInferences_.clear();
	}

	public void clearObjectPropertyInferences() {
		objectPropertyInferences_.clear();
	}

	public void clearIndexedAxiomInferences() {
		indexedAxioms_.clear();
		indexedAxiomInferences_.clear();
	}

	@Override
	public Iterable<? extends TracingInference> getInferences(Conclusion conclusion) {
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
	private class InferenceGetter
			extends DummyConclusionVisitor<Iterable<? extends TracingInference>> {

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
