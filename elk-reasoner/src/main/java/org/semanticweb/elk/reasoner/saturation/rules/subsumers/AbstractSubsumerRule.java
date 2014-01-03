package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A skeleton implementation of {@link SubsumerRule}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
abstract class AbstractSubsumerRule<P extends IndexedClassExpression>
		implements SubsumerRule<P> {

	@Override
	public void accept(RuleVisitor visitor, P premise, Context context,
			ConclusionProducer producer) {
		accept((SubsumerRuleVisitor) visitor, premise, context, producer);
	}

}
