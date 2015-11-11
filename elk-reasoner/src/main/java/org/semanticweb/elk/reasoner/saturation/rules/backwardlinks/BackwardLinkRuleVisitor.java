package org.semanticweb.elk.reasoner.saturation.rules.backwardlinks;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;

/**
 * A visitor pattern for {@link BackwardLinkRule}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <O>
 *            the type of output parameter with which this visitor works
 */
public interface BackwardLinkRuleVisitor<O> extends
		LinkedBackwardLinkRuleVisitor<O> {

	O visit(SubsumerBackwardLinkRule rule, BackwardLink premise,
			ContextPremises premises, ClassConclusionProducer producer);

}
