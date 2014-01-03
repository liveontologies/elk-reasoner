package org.semanticweb.elk.reasoner.saturation.rules.backwardlinks;

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * A visitor pattern for {@link BackwardLinkRule}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface BackwardLinkRuleVisitor extends LinkedBackwardLinkRuleVisitor {

	void visit(ForwardLinkFromBackwardLinkRule rule, BackwardLink premise,
			Context context, ConclusionProducer producer);	

	void visit(PropagationFromBackwardLinkRule rule, BackwardLink premise,
			Context context, ConclusionProducer producer);

}
