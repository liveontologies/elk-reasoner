/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors;
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

import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedConjunction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedConjunction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedExistential;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.InitializationSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.PropagatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ReflexiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ReversedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.SubClassOfSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.TracedPropagation;



/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class BaseInferenceVisitor<I, O> implements InferenceVisitor<I, O> {

	protected O defaultTracedVisit(Inference conclusion, I input) {
		return null;
	}

	@Override
	public O visit(InitializationSubsumer conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(SubClassOfSubsumer conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ComposedConjunction conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(DecomposedConjunction conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(PropagatedSubsumer conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ReflexiveSubsumer conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ComposedBackwardLink conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ReversedBackwardLink conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(DecomposedExistential conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(TracedPropagation conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}
}
