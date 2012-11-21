package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.saturation.context.Context;

public class CombinedConclusionVisitor implements ConclusionVisitor<Boolean> {

	final private ConclusionVisitor<Boolean> first_;
	final private ConclusionVisitor<Boolean> second_;

	public CombinedConclusionVisitor(ConclusionVisitor<Boolean> first,
			ConclusionVisitor<Boolean> second) {
		this.first_ = first;
		this.second_ = second;
	}

	@Override
	public Boolean visit(NegativeSuperClassExpression negSCE, Context context) {
		return first_.visit(negSCE, context) && second_.visit(negSCE, context);
	}

	@Override
	public Boolean visit(PositiveSuperClassExpression posSCE, Context context) {
		return first_.visit(posSCE, context) && second_.visit(posSCE, context);
	}

	@Override
	public Boolean visit(BackwardLink link, Context context) {
		return first_.visit(link, context) && second_.visit(link, context);
	}

	@Override
	public Boolean visit(ForwardLink link, Context context) {
		return first_.visit(link, context) && second_.visit(link, context);
	}

	@Override
	public Boolean visit(Bottom bot, Context context) {
		return first_.visit(bot, context) && second_.visit(bot, context);
	}

	@Override
	public Boolean visit(Propagation propagation, Context context) {
		return first_.visit(propagation, context)
				&& second_.visit(propagation, context);
	}

	@Override
	public Boolean visit(DisjointnessAxiom disjointnessAxiom, Context context) {
		return first_.visit(disjointnessAxiom, context)
				&& second_.visit(disjointnessAxiom, context);
	}

}
