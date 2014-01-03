package org.semanticweb.elk.reasoner.saturation.rules.contradiction;

import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;

/**
 * A {@link Rule} applied when processing {@link Contradiction}s in a
 * {@link Context}
 * 
 * @author "Yevgeny Kazakov"
 */
public interface ContradictionRule extends Rule<Contradiction> {

	public void accept(ContradictionRuleVisitor visitor, Contradiction premise,
			Context context, ConclusionProducer producer);

}
