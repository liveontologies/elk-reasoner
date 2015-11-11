package org.semanticweb.elk.reasoner.saturation.rules.propagations;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.contradiction.ContradictionRule;

/**
 * A visitor pattern for {@link ContradictionRule}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <O>
 *            the type of output parameter with which this visitor works
 */
public interface PropagationRuleVisitor<O> {

	O visit(SubsumerPropagationRule rule, Propagation premise,
			ContextPremises premises, ClassConclusionProducer producer);

}
