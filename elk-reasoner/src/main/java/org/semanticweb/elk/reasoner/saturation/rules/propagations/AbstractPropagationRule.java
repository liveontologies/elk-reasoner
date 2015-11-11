package org.semanticweb.elk.reasoner.saturation.rules.propagations;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A skeleton implementation of {@link PropagationRule}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class AbstractPropagationRule implements PropagationRule {

	@Override
	public void accept(RuleVisitor<?> visitor, Propagation premise,
			ContextPremises premises, ClassConclusionProducer producer) {
		accept((PropagationRuleVisitor<?>) visitor, premise, premises, producer);
	}

}
