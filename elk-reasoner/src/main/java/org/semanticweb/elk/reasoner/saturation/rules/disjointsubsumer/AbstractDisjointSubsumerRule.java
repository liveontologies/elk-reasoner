package org.semanticweb.elk.reasoner.saturation.rules.disjointsubsumer;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A skeleton implementation of {@link DisjointSubsumerRule}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
abstract class AbstractDisjointSubsumerRule implements DisjointSubsumerRule {

	@Override
	public void accept(RuleVisitor<?> visitor, DisjointSubsumer premise,
			ContextPremises premises, ClassConclusionProducer producer) {
		accept((DisjointSubsumerRuleVisitor<?>) visitor, premise, premises,
				producer);
	}

}
