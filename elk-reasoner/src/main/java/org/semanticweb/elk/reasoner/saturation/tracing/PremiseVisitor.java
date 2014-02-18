/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class PremiseVisitor<R, C> extends BaseConclusionVisitor<R, C> implements InferenceVisitor<R, C> {
	
	@Override
	public R visit(InitializationSubsumer conclusion, C parameter) {
		return null;
	}

	@Override
	public R visit(SubClassOfSubsumer conclusion, C cxt) {
		conclusion.getPremise().accept(this, cxt);
		return null;
	}

	@Override
	public R visit(ComposedConjunction conclusion, C parameter) {
		conclusion.getFirstConjunct().accept(this, parameter);
		conclusion.getSecondConjunct().accept(this, parameter);
		return null;
	}

	@Override
	public R visit(DecomposedConjunction conclusion, C parameter) {
		conclusion.getConjunction().accept(this, parameter);
		return null;
	}

	@Override
	public R visit(PropagatedSubsumer conclusion, C parameter) {
		conclusion.getBackwardLink().accept(this, parameter);
		conclusion.getPropagation().accept(this, parameter);
		return null;
	}

	@Override
	public R visit(ReflexiveSubsumer conclusion, C parameter) {
		return null;
	}

	@Override
	public R visit(ComposedBackwardLink conclusion, C parameter) {
		conclusion.getBackwardLink().accept(this, parameter);
		conclusion.getForwardLink().accept(this, parameter);
		return null;
	}

	@Override
	public R visit(ReversedBackwardLink conclusion, C parameter) {
		conclusion.getSourceLink().accept(this, parameter);
		return null;
	}

	@Override
	public R visit(DecomposedExistential conclusion, C parameter) {
		conclusion.getExistential().accept(this, parameter);
		return null;
	}

	@Override
	public R visit(TracedPropagation conclusion, C parameter) {
		conclusion.getPremise().accept(this, parameter);
		return null;
	}

}
