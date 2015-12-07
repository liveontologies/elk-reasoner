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
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.classes.ResolvingModifiableIndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverterImpl;
import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectCache;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
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
 */
public class TraceState
		implements
			InferenceSet,
			ModifiableClassInferenceTracingState,
			InferenceProducer<ObjectPropertyInference> {

	private final Queue<ClassConclusion> toTrace_ = new LinkedList<ClassConclusion>();

	private final ConcurrentHashMap<IndexedContextRoot, ClassInferenceSet> classInferenceMap_ = new ConcurrentHashMap<IndexedContextRoot, ClassInferenceSet>();

	private final ModifiableInferenceSet<ObjectPropertyInference> objectPropertyInferenceSet_ = new SynchronizedModifiableInferenceSet<ObjectPropertyInference>();

	private final ModifiableInferenceSet<IndexedAxiomInference> indexedAxiomInferenceSet_ = new SynchronizedModifiableInferenceSet<IndexedAxiomInference>();

	private final Set<ElkAxiom> indexedAxioms_ = new ArrayHashSet<ElkAxiom>();

	private final ModifiableIndexedObject.Factory indexedObjectFactory_;

	private final ElkAxiomConverter elkAxiomConverter_;

	public TraceState(ModifiableIndexedObjectCache cache) {
		this.indexedObjectFactory_ = new ResolvingModifiableIndexedObjectFactory(
				cache) {
			@Override
			protected <T extends ModifiableIndexedAxiomInference> T filter(
					T input) {
				indexedAxiomInferenceSet_.add(input);
				return input;
			}
		};
		this.elkAxiomConverter_ = new ElkAxiomConverterImpl(
				indexedObjectFactory_);
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
		classInferenceMap_.clear();
	}

	public void clearPropertyInferences() {
		objectPropertyInferenceSet_.clear();
	}

	@Override
	public ClassInferenceSet getInferencesForOrigin(
			IndexedContextRoot inferenceOriginRoot) {
		return classInferenceMap_.get(inferenceOriginRoot);
	}

	@Override
	public ClassInferenceSet setClassInferences(
			IndexedContextRoot inferenceOriginRoot,
			Iterable<? extends ClassInference> classInferences) {
		ClassInferenceSet result = classInferenceMap_.get(inferenceOriginRoot);
		if (result != null)
			return result;
		ModifiableClassInferenceSet newSet = new ModifiableClassInferenceSetImpl();
		// TODO: filter cyclic inferences
		for (ClassInference inference : classInferences) {
			newSet.add(inference);
		}
		result = classInferenceMap_.putIfAbsent(inferenceOriginRoot, newSet);
		if (result != null)
			return result;
		// else
		return newSet;
	}

	@Override
	public void produce(ObjectPropertyInference inference) {
		objectPropertyInferenceSet_.add(inference);
	}

	@Override
	public Iterable<? extends Inference> getInferences(Conclusion conclusion) {
		return conclusion.accept(
				new DummyConclusionVisitor<Iterable<? extends Inference>>() {

					@Override
					protected Iterable<? extends ClassInference> defaultVisit(
							ClassConclusion cncl) {
						IndexedContextRoot originRoot = cncl.getOriginRoot();
						ClassInferenceSet inferences = classInferenceMap_
								.get(originRoot);
						if (inferences == null)
							return Collections.emptyList();
						// else
						return inferences.getClassInferences(cncl);
					}

					@Override
					protected Iterable<? extends ObjectPropertyInference> defaultVisit(
							ObjectPropertyConclusion cncl) {
						return objectPropertyInferenceSet_.getInferences(cncl);
					}

					@Override
					protected Iterable<? extends IndexedAxiomInference> defaultVisit(
							IndexedAxiom cncl) {
						indexAxiom(cncl.getOriginalAxiom());
						return indexedAxiomInferenceSet_.getInferences(cncl);
					}

				});

	}

	synchronized void indexAxiom(ElkAxiom axiom) {
		if (!indexedAxioms_.add(axiom)) {
			// already done
			return;
		}
		// else index axiom
		axiom.accept(elkAxiomConverter_);
	}
	
}
