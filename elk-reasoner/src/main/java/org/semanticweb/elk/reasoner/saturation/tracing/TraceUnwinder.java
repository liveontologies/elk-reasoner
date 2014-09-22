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
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ObjectPropertyConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;

/**
 * A generic interface for objects which recursively unwind previously stored
 * inferences and let the calling code visit conclusions and inferences.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface TraceUnwinder {

	/*
	 * class trace unwinding involves both class and property inferences.
	 */
	public void accept(
			IndexedClassExpression context,
			Conclusion conclusion,
			ConclusionVisitor<IndexedClassExpression, ?> premiseVisitor,
			ClassInferenceVisitor<IndexedClassExpression, ?> inferenceVisitor,
			ObjectPropertyConclusionVisitor<?, ?> propertyPremiseVisitor,
			ObjectPropertyInferenceVisitor<?, ?> propertyInferenceVisitor);

	/*
	 * unwinds only object property inferences.
	 */
	public void accept(ObjectPropertyConclusion conclusion,
			ObjectPropertyConclusionVisitor<?, ?> conclusionVisitor,
			ObjectPropertyInferenceVisitor<?, ?> inferenceVisitor);
}
