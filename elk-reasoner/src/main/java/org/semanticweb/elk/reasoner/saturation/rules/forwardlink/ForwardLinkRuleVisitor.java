package org.semanticweb.elk.reasoner.saturation.rules.forwardlink;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;

/**
 * A visitor pattern for {@link ForwardLinkRule}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <O>
 *            the type of output parameter with which this visitor works
 */
public interface ForwardLinkRuleVisitor<O> {

	O visit(BackwardLinkFromForwardLinkRule rule, ForwardLink premise,
			ContextPremises premises, ClassConclusionProducer producer);

	O visit(ReflexiveBackwardLinkCompositionRule rule, ForwardLink premise,
			ContextPremises premises, ClassConclusionProducer producer);

	O visit(NonReflexiveBackwardLinkCompositionRule rule, ForwardLink premise,
			ContextPremises premises, ClassConclusionProducer producer);

}
