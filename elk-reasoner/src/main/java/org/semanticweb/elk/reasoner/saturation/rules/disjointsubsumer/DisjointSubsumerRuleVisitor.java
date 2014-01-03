package org.semanticweb.elk.reasoner.saturation.rules.disjointsubsumer;

import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.contradiction.ContradictionRule;

/**
 * A visitor pattern for {@link ContradictionRule}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface DisjointSubsumerRuleVisitor {

	void visit(ContradicitonCompositionRule rule, DisjointSubsumer premise,
			Context context, ConclusionProducer producer);

}
