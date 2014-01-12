package org.semanticweb.elk.reasoner.saturation.rules.contextinit;

import org.semanticweb.elk.reasoner.saturation.conclusions.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;

/**
 * A {@link Rule} applied when processing initializing {@link Context}s
 * 
 * @author "Yevgeny Kazakov"
 */
public interface ContextInitRule extends Rule<ContextInitialization> {

	public void accept(ContextInitRuleVisitor visitor,
			ContextInitialization premise, Context context,
			ConclusionProducer producer);

}
