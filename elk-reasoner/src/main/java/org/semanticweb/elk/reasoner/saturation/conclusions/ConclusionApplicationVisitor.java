package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.context.Context;

public class ConclusionApplicationVisitor implements ConclusionVisitor<Boolean> {

	private final SaturationState.Writer engine_;

	public ConclusionApplicationVisitor(SaturationState.Writer engine) {
		this.engine_ = engine;
	}

	// TODO: move the contents of Conclusion#apply method here

	@Override
	public Boolean visit(NegativeSuperClassExpression negSCE, Context context) {
		negSCE.apply(engine_, context);
		return true;
	}

	@Override
	public Boolean visit(PositiveSuperClassExpression posSCE, Context context) {
		posSCE.apply(engine_, context);
		return true;
	}

	@Override
	public Boolean visit(BackwardLink link, Context context) {
		link.apply(engine_, context);
		return true;
	}

	@Override
	public Boolean visit(ForwardLink link, Context context) {
		link.apply(engine_, context);
		return true;
	}

	@Override
	public Boolean visit(Bottom bot, Context context) {
		bot.apply(engine_, context);
		return true;
	}

	@Override
	public Boolean visit(Propagation propagation, Context context) {
		propagation.apply(engine_, context);
		return true;
	}

	@Override
	public Boolean visit(DisjointnessAxiom disjointnessAxiom, Context context) {
		disjointnessAxiom.apply(engine_, context);
		return true;
	}

}
