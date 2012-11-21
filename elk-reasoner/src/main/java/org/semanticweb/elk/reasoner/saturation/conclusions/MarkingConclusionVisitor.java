package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.context.Context;

public class MarkingConclusionVisitor implements ConclusionVisitor<Boolean> {

	private final SaturationState.Writer engine_;

	public MarkingConclusionVisitor(SaturationState.Writer engine) {
		this.engine_ = engine;
	}

	// TODO: move the contents of Conclusion#apply method here

	@Override
	public Boolean visit(NegativeSuperClassExpression negSCE, Context context) {
		engine_.markAsNotSaturated(context);
		return true;
	}

	@Override
	public Boolean visit(PositiveSuperClassExpression posSCE, Context context) {
		engine_.markAsNotSaturated(context);
		return true;
	}

	@Override
	public Boolean visit(BackwardLink link, Context context) {
		return true;
	}

	@Override
	public Boolean visit(ForwardLink link, Context context) {
		engine_.markAsNotSaturated(context);
		return true;
	}

	@Override
	public Boolean visit(Bottom bot, Context context) {
		return true;
	}

	@Override
	public Boolean visit(Propagation propagation, Context context) {
		return true;
	}

	@Override
	public Boolean visit(DisjointnessAxiom disjointnessAxiom, Context context) {
		return true;
	}

}
