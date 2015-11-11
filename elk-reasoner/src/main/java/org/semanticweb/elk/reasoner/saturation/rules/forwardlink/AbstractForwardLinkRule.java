package org.semanticweb.elk.reasoner.saturation.rules.forwardlink;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A skeleton implementation of {@link ForwardLinkRule}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class AbstractForwardLinkRule implements ForwardLinkRule {

	@Override
	public void accept(RuleVisitor<?> visitor, ForwardLink premise,
			ContextPremises premises, ClassConclusionProducer producer) {
		accept((ForwardLinkRuleVisitor<?>) visitor, premise, premises, producer);
	}

}
