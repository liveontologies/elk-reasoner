package org.semanticweb.elk.reasoner.saturation.rules.backwardlinks;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;

/**
 * A visitor pattern for {@link LinkedBackwardLinkRule}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <O>
 *            the type of output parameter with which this visitor works
 */
public interface LinkedBackwardLinkRuleVisitor<O> {

	O visit(ContradictionOverBackwardLinkRule rule, BackwardLink premise,
			ContextPremises premises, ClassConclusionProducer producer);

	O visit(BackwardLinkChainFromBackwardLinkRule rule, BackwardLink premise,
			ContextPremises premises, ClassConclusionProducer producer);

}
