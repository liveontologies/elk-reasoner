package org.semanticweb.elk.reasoner.saturation.rules.contextinit;

import org.semanticweb.elk.reasoner.saturation.conclusions.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ChainableSubsumerRule;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;

/**
 * A skeleton implementation of {@link ChainableSubsumerRule}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
abstract class AbstractChainableContextInitRule extends
		ModifiableLinkImpl<ChainableContextInitRule> implements
		ChainableContextInitRule {

	AbstractChainableContextInitRule(ChainableContextInitRule tail) {
		super(tail);
	}

	@Override
	public void accept(RuleVisitor visitor, ContextInitialization premise,
			Context context, ConclusionProducer producer) {
		accept((ContextInitRuleVisitor) visitor, premise, context, producer);
	}

	@Override
	public void accept(ContextInitRuleVisitor visitor,
			ContextInitialization premise, Context context,
			ConclusionProducer producer) {
		accept((LinkedContextInitRuleVisitor) visitor, premise, context,
				producer);
	}
}
