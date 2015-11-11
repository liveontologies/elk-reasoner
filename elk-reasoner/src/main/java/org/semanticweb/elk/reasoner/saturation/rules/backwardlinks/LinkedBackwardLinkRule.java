package org.semanticweb.elk.reasoner.saturation.rules.backwardlinks;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.util.collections.chains.Link;

/**
 * A {@link BackwardLinkRule} that is linked to other such
 * {@link LinkedBackwardLinkRule}s, thus representing a chain of
 * {@link BackwardLinkRule}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface LinkedBackwardLinkRule extends BackwardLinkRule,
		Link<LinkedBackwardLinkRule> {

	public void accept(LinkedBackwardLinkRuleVisitor<?> visitor,
			BackwardLink premise, ContextPremises premises,
			ClassConclusionProducer producer);

}
