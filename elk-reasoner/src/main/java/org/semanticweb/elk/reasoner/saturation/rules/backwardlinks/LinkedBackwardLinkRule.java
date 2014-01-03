package org.semanticweb.elk.reasoner.saturation.rules.backwardlinks;

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
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

	public void accept(LinkedBackwardLinkRuleVisitor visitor,
			BackwardLink premise, Context context, ConclusionProducer producer);

}
