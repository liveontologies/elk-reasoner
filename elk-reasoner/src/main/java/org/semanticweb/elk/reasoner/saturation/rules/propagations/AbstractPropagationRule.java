package org.semanticweb.elk.reasoner.saturation.rules.propagations;

import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A skeleton implementation of {@link PropagationRule}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class AbstractPropagationRule implements PropagationRule {

	@Override
	public void accept(RuleVisitor visitor, Propagation premise,
			Context context, ConclusionProducer producer) {
		accept((PropagationRuleVisitor) visitor, premise, context, producer);
	}

}
