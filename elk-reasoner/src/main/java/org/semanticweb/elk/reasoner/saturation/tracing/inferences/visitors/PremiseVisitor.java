/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AbstractConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedConjunction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedConjunction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DecomposedExistential;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.InitializationSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.PropagatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ReflexiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ReversedBackwardLink;
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
public class PremiseVisitor<I, O> extends AbstractConclusionVisitor<I, O> implements InferenceVisitor<I, O> {
	
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
	public O visit(ReversedBackwardLink conclusion, I parameter) {
		conclusion.getSourceLink().accept(this, parameter);
		return null;
	}

	@Override
	public O visit(DecomposedExistential conclusion, I parameter) {
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
		//no-op
		return null;
	}

}
