package org.semanticweb.elk.reasoner.saturation.rules.forwardlink;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;

/**
 * A {@link Rule} applied when processing {@link ForwardLink}s in a
 * {@link Context}
 * 
 * @author "Yevgeny Kazakov"
 */
public interface ForwardLinkRule extends Rule<ForwardLink> {

	public void accept(ForwardLinkRuleVisitor<?> visitor, ForwardLink premise,
			ContextPremises premises, ClassConclusionProducer producer);

}
