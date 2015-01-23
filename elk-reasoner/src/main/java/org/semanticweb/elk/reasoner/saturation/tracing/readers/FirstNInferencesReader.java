/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.readers;
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

import org.semanticweb.elk.MutableInteger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.DelegatingTraceReader;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;

/**
 * Visits only the first N inferences provided by the underlying
 * {@link TraceStore.Reader}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class FirstNInferencesReader extends DelegatingTraceReader {

	private final int numberOfInferencesToVisit_;
	
	public FirstNInferencesReader(TraceStore.Reader r, int n) {
		super(r);
		numberOfInferencesToVisit_ = n;
	}
	
	@Override
	public void accept(IndexedClassExpression root, Conclusion conclusion, final ClassInferenceVisitor<IndexedClassExpression, ?> visitor) {
		final MutableInteger counter = new MutableInteger(0);
		
		reader.accept(root, conclusion, new AbstractClassInferenceVisitor<IndexedClassExpression, Void>() {

			@Override
			protected Void defaultTracedVisit(ClassInference inference, IndexedClassExpression ignored) {
				if (counter.get() < numberOfInferencesToVisit_) {
					counter.increment();
					inference.acceptTraced(visitor, null);
				}
				
				return null;
			}
			
		});
	}

}
