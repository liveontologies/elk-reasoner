package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.saturation.context.Context;

public class ConclusionDeletionVisitor implements ConclusionVisitor<Boolean> {

	@Override
	public Boolean visit(NegativeSuperClassExpression negSCE, Context context) {
		return context.removeSuperClassExpression(negSCE.getExpression());
	}

	@Override
	public Boolean visit(PositiveSuperClassExpression posSCE, Context context) {
		return context.removeSuperClassExpression(posSCE.getExpression());
	}

	@Override
	public Boolean visit(BackwardLink link, Context context) {
		return context.removeBackwardLink(link);
	}

	@Override
	public Boolean visit(ForwardLink link, Context context) {
		return link.removeFromContextBackwardLinkRule(context);
	}

	@Override
	public Boolean visit(Bottom bot, Context context) {
		return context.setInconsistent(false);
	}

	@Override
	public Boolean visit(Propagation propagation, Context context) {
		return propagation.removeFromContextBackwardLinkRule(context);
	}

	@Override
	public Boolean visit(DisjointnessAxiom disjointnessAxiom, Context context) {
		return context.removeDisjointnessAxiom(disjointnessAxiom.getAxiom()) == 1;
	}

}
