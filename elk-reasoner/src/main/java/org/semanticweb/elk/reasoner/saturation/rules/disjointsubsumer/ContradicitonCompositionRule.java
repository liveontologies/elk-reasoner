package org.semanticweb.elk.reasoner.saturation.rules.disjointsubsumer;

import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * A {@link DisjointSubsumerRule} applied when processing a
 * {@link DisjointSubsumer} producing {@link Contradiction} caused by violation
 * of disjointness constrains of this {@link DisjointSubsumer}
 * 
 * @author "Yevgeny Kazakov"
 */
public class ContradicitonCompositionRule extends AbstractDisjointSubsumerRule {

	private static final String NAME_ = "Contradiction by Disjointness Axiom";

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public void apply(DisjointSubsumer premise, Context context,
			ConclusionProducer producer) {
		producer.produce(context, Contradiction.getInstance());
	}

	@Override
	public void accept(DisjointSubsumerRuleVisitor visitor,
			DisjointSubsumer premise, Context context,
			ConclusionProducer producer) {
		visitor.visit(this, premise, context, producer);
	}

}