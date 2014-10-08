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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceUnwinder;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;

/**
 * Unwinds only one step back, i.e. just calls the underlying trace reader once.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class OneStepTraceUnwinder implements TraceUnwinder {

	private final TraceStore.Reader traceReader_;

	public OneStepTraceUnwinder(TraceStore.Reader reader) {
		traceReader_ = reader;
	}

	@Override
	public void accept(IndexedClassExpression context, Conclusion conclusion,
			ClassInferenceVisitor<IndexedClassExpression, ?> inferenceVisitor,
			ObjectPropertyInferenceVisitor<?, ?> propertyInferenceVisitor) {
		traceReader_.accept(context, conclusion, inferenceVisitor);
	}

	@Override
	public void accept(ObjectPropertyConclusion conclusion,
			ObjectPropertyInferenceVisitor<?, ?> inferenceVisitor) {
		traceReader_.accept(conclusion, inferenceVisitor);
	}

}
