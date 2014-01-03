package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;

/**
 * A skeleton implementation of {@link ChainableSubsumerRule}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
abstract class AbstractChainableSubsumerRule extends
		ModifiableLinkImpl<ChainableSubsumerRule> implements
		ChainableSubsumerRule {

	AbstractChainableSubsumerRule(ChainableSubsumerRule tail) {
		super(tail);
	}

	@Override
	public void accept(RuleVisitor visitor, IndexedClassExpression premise,
			Context context, ConclusionProducer producer) {
		accept((SubsumerRuleVisitor) visitor, premise, context, producer);
	}

	@Override
	public void accept(SubsumerRuleVisitor visitor,
			IndexedClassExpression premise, Context context,
			ConclusionProducer producer) {
		accept((LinkedSubsumerRuleVisitor) visitor, premise, context, producer);
	}

}
