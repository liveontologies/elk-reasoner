package org.semanticweb.elk.reasoner.saturation.rules.contextinit;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A skeleton implementation of {@link ContextInitRule}
 * 
 * @author "Yevgeny Kazakov"
 */
abstract class AbstractContextInitRule implements ContextInitRule {

	@Override
	public void accept(RuleVisitor<?> visitor, ContextInitialization premise,
			ContextPremises premises, ClassConclusionProducer producer) {
		accept((ContextInitRuleVisitor<?>) visitor, premise, premises, producer);
	}

}
