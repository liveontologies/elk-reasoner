package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.saturation.context.Context;

public class ConclusionOccurranceCheckingVisitor implements
		ConclusionVisitor<Boolean> {

	@Override
	public Boolean visit(NegativeSuperClassExpression negSCE, Context context) {
		return context.containsSuperClassExpression(negSCE.getExpression());
	}

	@Override
	public Boolean visit(PositiveSuperClassExpression posSCE, Context context) {
		return context.containsSuperClassExpression(posSCE.getExpression());
	}

	@Override
	public Boolean visit(BackwardLink link, Context context) {
		return context.containsBackwardLink(link);
	}

	@Override
	public Boolean visit(ForwardLink link, Context context) {
		return link.containsBackwardLinkRule(context);
	}

	@Override
	public Boolean visit(Bottom bot, Context context) {
		return context.isInconsistent();
	}

	@Override
	public Boolean visit(Propagation propagation, Context context) {
		return propagation.containsBackwardLinkRule(context);
	}

	@Override
	public Boolean visit(DisjointnessAxiom disjointnessAxiom, Context context) {
		return context.containsDisjointnessAxiom(disjointnessAxiom.getAxiom()) > 0;
	}

}
