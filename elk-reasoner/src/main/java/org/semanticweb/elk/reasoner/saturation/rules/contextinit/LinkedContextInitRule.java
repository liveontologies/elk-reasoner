package org.semanticweb.elk.reasoner.saturation.rules.contextinit;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.util.collections.chains.Link;

/**
 * A {@link ContextInitRule} that is linked to other such
 * {@link LinkedContextInitRule}s, thus representing a chain of
 * {@link ContextInitRule}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface LinkedContextInitRule extends ContextInitRule,
		Link<LinkedContextInitRule> {

	public void accept(LinkedContextInitRuleVisitor<?> visitor,
			ContextInitialization premise, ContextPremises premises,
			ClassConclusionProducer producer);

}
