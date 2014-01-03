package org.semanticweb.elk.reasoner.saturation.rules.backwardlinks;

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * A visitor pattern for {@link LinkedBackwardLinkRule}s
 * 
 * @author "Yevgeny Kazakov"
 */
public interface LinkedBackwardLinkRuleVisitor {

	void visit(ContradictionOverBackwardLinkRule rule, BackwardLink premise,
			Context context, ConclusionProducer producer);

	void visit(BackwardLinkChainFromBackwardLinkRule rule,
			BackwardLink premise, Context context, ConclusionProducer producer);

	void visit(SubsumerBackwardLinkRule rule, BackwardLink premise,
			Context context, ConclusionProducer producer);

}
