package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.context.Context;

public class ConclusionDeapplicationVisitor implements ConclusionVisitor<Boolean> {

	private final SaturationState.Writer engine_;

	public ConclusionDeapplicationVisitor(SaturationState.Writer engine) {
		this.engine_ = engine;
	}

	// TODO: move the contents of Conclusion#deapply method here

	@Override
	public Boolean visit(NegativeSuperClassExpression negSCE, Context context) {
		negSCE.deapply(engine_, context);
		return true;
	}

	@Override
	public Boolean visit(PositiveSuperClassExpression posSCE, Context context) {
		posSCE.deapply(engine_, context);
		return true;
	}

	@Override
	public Boolean visit(BackwardLink link, Context context) {
		link.deapply(engine_, context);
		return true;
	}

	@Override
	public Boolean visit(ForwardLink link, Context context) {
		link.deapply(engine_, context);
		return true;
	}

	@Override
	public Boolean visit(Bottom bot, Context context) {
		bot.deapply(engine_, context);
		return true;
	}

	@Override
	public Boolean visit(Propagation propagation, Context context) {
		propagation.deapply(engine_, context);
		return true;
	}

	@Override
	public Boolean visit(DisjointnessAxiom disjointnessAxiom, Context context) {
		disjointnessAxiom.deapply(engine_, context);
		return true;
	}

}
