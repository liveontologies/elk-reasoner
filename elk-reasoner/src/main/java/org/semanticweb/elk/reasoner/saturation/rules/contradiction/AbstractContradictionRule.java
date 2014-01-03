package org.semanticweb.elk.reasoner.saturation.rules.contradiction;

import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A skeleton implementation of {@link ContradictionRule}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
abstract class AbstractContradictionRule implements ContradictionRule {

	@Override
	public void accept(RuleVisitor visitor, Contradiction premise,
			Context context, ConclusionProducer producer) {
		accept((ContradictionRuleVisitor) visitor, premise, context, producer);
	}

}
