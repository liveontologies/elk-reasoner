package org.semanticweb.elk.reasoner.saturation.rules.contradiction;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A skeleton implementation of {@link ContradictionRule}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
abstract class AbstractContradictionRule implements ContradictionRule {

	@Override
	public void accept(RuleVisitor<?> visitor, Contradiction premise,
			ContextPremises premises, ClassConclusionProducer producer) {
		accept((ContradictionRuleVisitor<?>) visitor, premise, premises,
				producer);
	}

}
