package org.semanticweb.elk.reasoner.saturation.rules.disjointsubsumer;

import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A skeleton implementation of {@link DisjointSubsumerRule}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
abstract class AbstractDisjointSubsumerRule implements DisjointSubsumerRule {

	@Override
	public void accept(RuleVisitor visitor, DisjointSubsumer premise,
			Context context, ConclusionProducer producer) {
		accept((DisjointSubsumerRuleVisitor) visitor, premise, context,
				producer);
	}

}
