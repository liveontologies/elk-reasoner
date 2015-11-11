package org.semanticweb.elk.reasoner.saturation.rules.disjointsubsumer;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;

/**
 * A {@link Rule} applied when processing {@link DisjointSubsumer}s in a
 * {@link Context}
 * 
 * @author "Yevgeny Kazakov"
 */
public interface DisjointSubsumerRule extends Rule<DisjointSubsumer> {

	public void accept(DisjointSubsumerRuleVisitor<?> visitor,
			DisjointSubsumer premise, ContextPremises premises,
			ClassConclusionProducer producer);

}
