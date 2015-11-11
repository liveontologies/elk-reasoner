package org.semanticweb.elk.reasoner.saturation.rules.contradiction;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;

/**
 * A {@link Rule} applied when processing {@link Contradiction}s in a
 * {@link Context}
 * 
 * @author "Yevgeny Kazakov"
 */
public interface ContradictionRule extends Rule<Contradiction> {

	public void accept(ContradictionRuleVisitor<?> visitor,
			Contradiction premise, ContextPremises premises,
			ClassConclusionProducer producer);

}
