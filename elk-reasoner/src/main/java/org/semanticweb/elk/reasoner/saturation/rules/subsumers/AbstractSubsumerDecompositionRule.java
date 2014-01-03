package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;

abstract class AbstractSubsumerDecompositionRule<P extends IndexedClassExpression>
		extends AbstractSubsumerRule<P> implements SubsumerDecompositionRule<P> {

	@Override
	public void accept(SubsumerRuleVisitor visitor, P premise, Context context,
			ConclusionProducer producer) {
		accept((SubsumerDecompositionRuleVisitor) visitor, premise, context,
				producer);

	}

}
