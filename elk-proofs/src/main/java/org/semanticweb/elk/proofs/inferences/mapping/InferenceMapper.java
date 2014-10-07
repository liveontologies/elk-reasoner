/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.mapping;
/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.proofs.inferences.Inference;
import org.semanticweb.elk.proofs.inferences.InferenceVisitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceUnwinder;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractObjectPropertyInferenceVisitor;

/**
 * Visits all used low level {@link ClassInference}s and
 * {@link ObjectPropertyInference}, maps them to higher level {@link Inference}s
 * and passes to the provided visitor.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class InferenceMapper {

	private final TraceUnwinder unwinder_;
	
	public InferenceMapper(TraceUnwinder unwinder) {
		unwinder_ = unwinder;
	}
	
	public void map(final Iterable<? extends TracingInput> inputs, final InferenceVisitor<?, ?> visitor) {
		for (TracingInput input : inputs) {
			//TODO visitor here (that would get rid of the exception at the bottom)?
			if (input instanceof ClassTracingInput) {
				ClassTracingInput ci = (ClassTracingInput) input;
				
				map(ci.root, ci.conclusion, visitor);
			}
			else if (input instanceof ObjectPropertyTracingInput) {
				ObjectPropertyTracingInput pi = (ObjectPropertyTracingInput) input;
				
				map(pi.conclusion, visitor);
			}
			else {
				throw new IllegalArgumentException("Unsupported tracing input " + input.getClass());
			}
		}
	}
	
	// class and object property inferences
	public void map(final IndexedClassExpression cxt, final Conclusion conclusion, final InferenceVisitor<?, ?> visitor) {
		final SingleInferenceMapper singleMapper = new SingleInferenceMapper();
		
		unwinder_.accept(cxt, conclusion, 
				new AbstractClassInferenceVisitor<IndexedClassExpression, Void>() {

					@Override
					protected Void defaultTracedVisit(ClassInference inference,
							IndexedClassExpression whereStored) {
						Inference mapped = singleMapper.map(inference, whereStored);
						
						if (mapped != null) {
							mapped.accept(visitor, null);
						}
						
						return null;
					}
				}, 
				new AbstractObjectPropertyInferenceVisitor<Void, Void>() {

					@Override
					protected Void defaultTracedVisit(
							ObjectPropertyInference inference, Void input) {
						Inference mapped = singleMapper.map(inference);
						
						if (mapped != null) {
							mapped.accept(visitor, null);
						}
						
						return null;
					}
				});
	}
	
	// only object property inferences
	public void map(final ObjectPropertyConclusion conclusion, final InferenceVisitor<?, ?> visitor) {
		final SingleInferenceMapper singleMapper = new SingleInferenceMapper();
		
		unwinder_.accept(
				conclusion,
				new AbstractObjectPropertyInferenceVisitor<Void, Void>() {

					@Override
					protected Void defaultTracedVisit(
							ObjectPropertyInference inference, Void input) {
						Inference mapped = singleMapper.map(inference);
						
						if (mapped != null) {
							mapped.accept(visitor, null);
						}
						
						return null;
					}
				});
	}	
}
