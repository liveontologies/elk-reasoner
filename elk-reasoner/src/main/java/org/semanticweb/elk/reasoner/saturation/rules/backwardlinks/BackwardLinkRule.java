package org.semanticweb.elk.reasoner.saturation.rules.backwardlinks;

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;

/**
 * A {@link Rule} applied when processing {@link BackwardLink}s in a
 * {@link Context}
 * 
 * @author "Yevgeny Kazakov"
 */
public interface BackwardLinkRule extends Rule<BackwardLink> {

	public void accept(BackwardLinkRuleVisitor visitor, BackwardLink premise,
			Context context, ConclusionProducer producer);

}
