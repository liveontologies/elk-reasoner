package org.semanticweb.elk.reasoner.saturation.rules.disjointsubsumer;

import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;

/**
 * A {@link Rule} applied when processing {@link DisjointSubsumer}s in a
 * {@link Context}
 * 
 * @author "Yevgeny Kazakov"
 */
public interface DisjointSubsumerRule extends Rule<DisjointSubsumer> {

	public void accept(DisjointSubsumerRuleVisitor visitor,
			DisjointSubsumer premise, Context context,
			ConclusionProducer producer);

}
