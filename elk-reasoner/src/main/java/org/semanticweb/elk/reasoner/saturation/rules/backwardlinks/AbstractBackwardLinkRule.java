package org.semanticweb.elk.reasoner.saturation.rules.backwardlinks;

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A skeleton implementation of {@link BackwardLinkRule}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
abstract class AbstractBackwardLinkRule implements BackwardLinkRule {

	@Override
	public void accept(RuleVisitor visitor, BackwardLink premise,
			Context context, ConclusionProducer producer) {
		accept((BackwardLinkRuleVisitor) visitor, premise, context, producer);
	}

}
