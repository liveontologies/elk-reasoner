package org.semanticweb.elk.reasoner.saturation.rules.propagations;

import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;

/**
 * A {@link Rule} applied when processing {@link Propagation}s in a
 * {@link Context}
 * 
 * @author "Yevgeny Kazakov"
 */
public interface PropagationRule extends Rule<Propagation> {

	public void accept(PropagationRuleVisitor visitor, Propagation premise,
			Context context, ConclusionProducer producer);

}
