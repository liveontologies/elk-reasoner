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
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.DummyConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.DummyObjectPropertyConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.RecursiveTraceUnwinder;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceUnwinder;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractObjectPropertyInferenceVisitor;

/**
 * Visits all used low level {@link ClassInference}s and
 * {@link ObjectPropertyInference}, maps them to higher level {@link Inference}s
 * and passes the provided visitor.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class InferenceMapper {

	private final TraceStore.Reader traceReader_;
	
	public InferenceMapper(TraceStore.Reader reader) {
		traceReader_ = reader;
	}
	
	public void map(final IndexedClassExpression cxt, final Conclusion conclusion, final InferenceVisitor<?, ?> visitor) {
		final SingleInferenceMapper singleMapper = new SingleInferenceMapper();
		TraceUnwinder unwinder = new RecursiveTraceUnwinder(traceReader_);
		
		unwinder.accept(cxt, conclusion, 
				new DummyConclusionVisitor<IndexedClassExpression, Void>(), 
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
				new DummyObjectPropertyConclusionVisitor<IndexedClassExpression, Void>(), 
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
