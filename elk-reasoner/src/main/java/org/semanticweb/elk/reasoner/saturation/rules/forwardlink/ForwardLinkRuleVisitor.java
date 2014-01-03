package org.semanticweb.elk.reasoner.saturation.rules.forwardlink;

import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.contradiction.ContradictionRule;

/**
 * A visitor pattern for {@link ContradictionRule}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ForwardLinkRuleVisitor {

	void visit(BackwardLinkCompositionRule rule, ForwardLink premise,
			Context context, ConclusionProducer producer);

}
