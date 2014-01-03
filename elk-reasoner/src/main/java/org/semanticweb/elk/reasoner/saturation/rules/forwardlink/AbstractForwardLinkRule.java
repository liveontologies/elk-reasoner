package org.semanticweb.elk.reasoner.saturation.rules.forwardlink;

import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A skeleton implementation of {@link ForwardLinkRule}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class AbstractForwardLinkRule implements ForwardLinkRule {

	@Override
	public void accept(RuleVisitor visitor, ForwardLink premise,
			Context context, ConclusionProducer producer) {
		accept((ForwardLinkRuleVisitor) visitor, premise, context, producer);
	}

}
