package org.semanticweb.elk.reasoner.saturation.rules.propagations;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;

/**
 * A {@link Rule} applied when processing {@link Propagation}s in a
 * {@link Context}
 * 
 * @author "Yevgeny Kazakov"
 */
public interface PropagationRule extends Rule<Propagation> {

	public void accept(PropagationRuleVisitor<?> visitor, Propagation premise,
			ContextPremises premises, ClassConclusionProducer producer);

}
