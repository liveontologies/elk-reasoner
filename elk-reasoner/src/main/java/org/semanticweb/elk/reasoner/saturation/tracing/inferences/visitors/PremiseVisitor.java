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

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AbstractConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedConjunction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedForwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedConjunction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedExistentialBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedExistentialForwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.InitializationSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.PropagatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ReflexiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ReversedForwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.SubClassOfSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.TracedPropagation;

/**
 * Visits all premises for the given {@link Inference}. Each premise implements
 * {@link Conclusion}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class PremiseVisitor<I, O> extends AbstractConclusionVisitor<I, O>
		implements InferenceVisitor<I, O> {

	@Override
	public O visit(InitializationSubsumer conclusion, I parameter) {
		return null;
	}

	@Override
	public O visit(SubClassOfSubsumer conclusion, I cxt) {
		conclusion.getPremise().accept(this, cxt);
		return null;
	}

	@Override
	public O visit(ComposedConjunction conclusion, I parameter) {
		conclusion.getFirstConjunct().accept(this, parameter);
		conclusion.getSecondConjunct().accept(this, parameter);
		return null;
	}

	@Override
	public O visit(DecomposedConjunction conclusion, I parameter) {
		conclusion.getConjunction().accept(this, parameter);
		return null;
	}

	@Override
	public O visit(PropagatedSubsumer conclusion, I parameter) {
		conclusion.getBackwardLink().accept(this, parameter);
		conclusion.getPropagation().accept(this, parameter);
		return null;
	}

	@Override
	public O visit(ReflexiveSubsumer conclusion, I parameter) {
		return null;
	}

	@Override
	public O visit(ComposedBackwardLink conclusion, I parameter) {
		conclusion.getBackwardLink().accept(this, parameter);
		conclusion.getForwardLink().accept(this, parameter);
		return null;
	}

	@Override
	public O visit(ComposedForwardLink conclusion, I parameter) {
		conclusion.getBackwardLink().accept(this, parameter);
		conclusion.getForwardLink().accept(this, parameter);
		return null;
	}

	@Override
	public O visit(ReversedForwardLink conclusion, I parameter) {
		conclusion.getSourceLink().accept(this, parameter);
		return null;
	}

	@Override
	public O visit(DecomposedExistentialBackwardLink conclusion, I parameter) {
		conclusion.getExistential().accept(this, parameter);
		return null;
	}

	@Override
	public O visit(DecomposedExistentialForwardLink conclusion, I parameter) {
		conclusion.getExistential().accept(this, parameter);
		return null;
	}

	@Override
	public O visit(TracedPropagation conclusion, I parameter) {
		conclusion.getPremise().accept(this, parameter);
		return null;
	}

	@Override
	protected O defaultVisit(Conclusion conclusion, I input) {
		// no-op
		return null;
	}

}
