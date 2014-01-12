/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class PremiseVisitor<R, C> implements TracedConclusionVisitor<R, C> {
	
	private final ConclusionVisitor<R, C> visitor_;
	
	public PremiseVisitor(ConclusionVisitor<R, C> v) {
		visitor_ = v;
	}
	
	@Override
	public R visit(InitializationSubsumer conclusion, C parameter) {
		return null;
	}

	@Override
	public R visit(SubClassOfSubsumer conclusion, C cxt) {
		conclusion.getPremise().accept(visitor_, cxt);
		return null;
	}

	@Override
	public R visit(ComposedConjunction conclusion, C parameter) {
		conclusion.getFirstConjunct().accept(visitor_, parameter);
		conclusion.getSecondConjunct().accept(visitor_, parameter);
		return null;
	}

	@Override
	public R visit(DecomposedConjunction conclusion, C parameter) {
		conclusion.getConjunction().accept(visitor_, parameter);
		return null;
	}

	@Override
	public R visit(PropagatedSubsumer conclusion, C parameter) {
		conclusion.getBackwardLink().accept(visitor_, parameter);
		conclusion.getPropagation().accept(visitor_, parameter);
		return null;
	}

	@Override
	public R visit(ReflexiveSubsumer conclusion, C parameter) {
		return null;
	}

	@Override
	public R visit(ComposedBackwardLink conclusion, C parameter) {
		conclusion.getBackwardLink().accept(visitor_, parameter);
		conclusion.getForwardLink().accept(visitor_, parameter);
		return null;
	}

	@Override
	public R visit(ReversedBackwardLink conclusion, C parameter) {
		conclusion.getSourceLink().accept(visitor_, parameter);
		return null;
	}

	@Override
	public R visit(DecomposedExistential conclusion, C parameter) {
		conclusion.getExistential().accept(visitor_, parameter);
		return null;
	}

	@Override
	public R visit(TracedPropagation conclusion, C parameter) {
		conclusion.getPremise().accept(visitor_, parameter);
		return null;
	}

}
