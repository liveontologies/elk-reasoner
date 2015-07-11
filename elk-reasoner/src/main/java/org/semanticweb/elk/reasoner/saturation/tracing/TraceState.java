/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

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
import java.util.concurrent.ConcurrentHashMap;

import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ObjectPropertyInference;

/**
 * A collections of objects for tracing contexts and keeping the relevant
 * information about the state of tracing.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TraceState implements InferenceSet,
		ModifiableClassInferenceTracingState, ObjectPropertyInferenceProducer {

	private final Queue<Conclusion> toTrace_ = new LinkedList<Conclusion>();

	private final ConcurrentHashMap<IndexedContextRoot, ClassInferenceSet> classInferenceMap_ = new ConcurrentHashMap<IndexedContextRoot, ClassInferenceSet>();

	private final ModifiableObjectPropertyInferenceSet objectPropertyInferenceSet_ = new ModifiableObjectPropertyInferenceSetImpl();

	public Collection<? extends Conclusion> getToTrace() {
		return toTrace_;
	}

	public void addToTrace(Conclusion conclusion) {
		toTrace_.add(conclusion);
	}

	public void clearToTrace() {
		toTrace_.clear();
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
		ModifiableClassInferenceSet newSet = new ModifiableClassInferenceSetImpl(
				inferenceOriginRoot);
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
	public Iterable<? extends ClassInference> getClassInferences(
			Conclusion conclusion) {
		IndexedContextRoot originRoot = conclusion.getOriginRoot();
		ClassInferenceSet inferences = classInferenceMap_.get(originRoot);
		if (inferences == null)
			return Collections.emptyList();
		// else
		return inferences.getClassInferences(conclusion);
	}

	@Override
	public void produce(ObjectPropertyInference inference) {
		objectPropertyInferenceSet_.add(inference);
	}

	@Override
	public Iterable<? extends ObjectPropertyInference> getObjectPropertyInferences(
			ObjectPropertyConclusion conclusion) {
		return objectPropertyInferenceSet_
				.getObjectPropertyInferences(conclusion);
	}

}
