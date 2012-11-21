package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.saturation.context.Context;

public class ContextSaturationCheckingConclusionVisitor implements
		ConclusionVisitor<Boolean> {

	@Override
	public Boolean visit(NegativeSuperClassExpression negSCE, Context context) {
		return context.isSaturated();
	}

	@Override
	public Boolean visit(PositiveSuperClassExpression posSCE, Context context) {
		return context.isSaturated();
	}

	@Override
	public Boolean visit(BackwardLink link, Context context) {
		return link.getSource().isSaturated();
	}

	@Override
	public Boolean visit(ForwardLink link, Context context) {
		return context.isSaturated();
	}

	@Override
	public Boolean visit(Bottom bot, Context context) {
		return context.isSaturated();
	}

	@Override
	public Boolean visit(Propagation propagation, Context context) {
		return context.isSaturated();
	}

	@Override
	public Boolean visit(DisjointnessAxiom disjointnessAxiom, Context context) {
		return context.isSaturated();
	}

}
