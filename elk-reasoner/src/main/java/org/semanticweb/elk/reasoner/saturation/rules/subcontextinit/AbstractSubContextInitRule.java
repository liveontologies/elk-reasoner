package org.semanticweb.elk.reasoner.saturation.rules.subcontextinit;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A skeleton implementation of {@link SubContextInitRule}
 * 
 * @author "Yevgeny Kazakov"
 */
abstract class AbstractSubContextInitRule implements SubContextInitRule {

	@Override
	public void accept(RuleVisitor<?> visitor,
			SubContextInitialization premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		accept((SubContextInitRuleVisitor<?>) visitor, premise, premises,
				producer);
	}

}
