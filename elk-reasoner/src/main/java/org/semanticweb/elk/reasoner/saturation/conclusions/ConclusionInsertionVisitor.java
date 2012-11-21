package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.saturation.context.Context;

public class ConclusionInsertionVisitor implements ConclusionVisitor<Boolean> {

	@Override
	public Boolean visit(NegativeSuperClassExpression negSCE, Context context) {
		return context.addSuperClassExpression(negSCE.getExpression());
	}

	@Override
	public Boolean visit(PositiveSuperClassExpression posSCE, Context context) {
		return context.addSuperClassExpression(posSCE.getExpression());
	}

	@Override
	public Boolean visit(BackwardLink link, Context context) {
		return context.addBackwardLink(link);
	}

	@Override
	public Boolean visit(ForwardLink link, Context context) {
		return link.addToContextBackwardLinkRule(context);
	}

	@Override
	public Boolean visit(Bottom bot, Context context) {
		return !context.setInconsistent(true);
	}

	@Override
	public Boolean visit(Propagation propagation, Context context) {
		return propagation.addToContextBackwardLinkRule(context);
	}

	@Override
	public Boolean visit(DisjointnessAxiom disjointnessAxiom, Context context) {
		return context.addDisjointnessAxiom(disjointnessAxiom.getAxiom()) == 0;
	}

}
