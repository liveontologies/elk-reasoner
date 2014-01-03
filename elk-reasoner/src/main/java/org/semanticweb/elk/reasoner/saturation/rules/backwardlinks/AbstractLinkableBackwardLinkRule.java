package org.semanticweb.elk.reasoner.saturation.rules.backwardlinks;

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;

/**
 * A skeleton implementation of {@link LinkableBackwardLinkRule}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
abstract class AbstractLinkableBackwardLinkRule extends
		ModifiableLinkImpl<LinkableBackwardLinkRule> implements
		LinkableBackwardLinkRule {

	AbstractLinkableBackwardLinkRule(LinkableBackwardLinkRule tail) {
		super(tail);
	}

	@Override
	public void accept(RuleVisitor visitor, BackwardLink premise,
			Context context, ConclusionProducer producer) {
		accept((BackwardLinkRuleVisitor) visitor, premise, context, producer);
	}

	@Override
	public void accept(BackwardLinkRuleVisitor visitor, BackwardLink premise,
			Context context, ConclusionProducer producer) {
		accept((LinkedBackwardLinkRuleVisitor) visitor, premise, context,
				producer);
	}

}
