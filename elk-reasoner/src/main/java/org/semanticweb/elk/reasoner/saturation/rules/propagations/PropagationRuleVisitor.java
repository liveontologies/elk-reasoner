package org.semanticweb.elk.reasoner.saturation.rules.propagations;

import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.contradiction.ContradictionRule;

/**
 * A visitor pattern for {@link ContradictionRule}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface PropagationRuleVisitor {

	void visit(ReflexivePropagationRule rule, Propagation premise,
			Context context, ConclusionProducer producer);

	void visit(NonReflexivePropagationRule rule, Propagation premise,
			Context context, ConclusionProducer producer);

}
