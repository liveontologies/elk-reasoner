/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.util;
/*
 * #%L
 * ELK Reasoner
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ContradictionImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractObjectPropertyInferenceVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TracingUtils {

	public static Collection<ClassInference> getClassInferences(TraceStore.Reader reader, IndexedClassExpression cxt, Conclusion conclusion) {
		final List<ClassInference> inferences = new LinkedList<ClassInference>();
		
		reader.accept(cxt, conclusion, new AbstractClassInferenceVisitor<IndexedClassExpression, Void>() {

			@Override
			protected Void defaultTracedVisit(ClassInference inf, IndexedClassExpression root) {
				inferences.add(inf);
				return null;
			}
			
		});
		
		return inferences;
	}
	
	public static Collection<ObjectPropertyInference> getObjectPropertyInferences(TraceStore.Reader reader, ObjectPropertyConclusion conclusion) {
		final List<ObjectPropertyInference> inferences = new LinkedList<ObjectPropertyInference>();
		
		reader.accept(conclusion, new AbstractObjectPropertyInferenceVisitor<Void, Void>() {

			@Override
			protected Void defaultTracedVisit(ObjectPropertyInference inf, Void _ignored) {
				inferences.add(inf);
				return null;
			}
			
		});
		
		return inferences;
	}
	
	public static Conclusion getConclusionToTrace(Context context, IndexedClassExpression subsumer) {
		if (context != null) {
			if (context.containsConclusion(ContradictionImpl.getInstance())) {
				return ContradictionImpl.getInstance();
			}
			
			return new DecomposedSubsumerImpl<IndexedClassExpression>(subsumer);
		}
		
		throw new IllegalArgumentException("Context may not be null");
	}		
}
