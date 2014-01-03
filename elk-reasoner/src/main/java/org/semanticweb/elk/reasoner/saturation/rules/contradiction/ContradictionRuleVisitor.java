package org.semanticweb.elk.reasoner.saturation.rules.contradiction;

import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

/**
 * A visitor pattern for {@link ContradictionRule}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface ContradictionRuleVisitor {

	void visit(ContradictionPropagationRule rule, Contradiction premise,
			Context context, ConclusionProducer producer);

}
