package org.semanticweb.elk.reasoner.saturation.rules.subcontextinit;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.context.SubContext;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;

/**
 * A {@link Rule} applied when processing initializing {@link SubContext}s
 * 
 * @author "Yevgeny Kazakov"
 */
public interface SubContextInitRule extends Rule<SubContextInitialization> {

	public void accept(SubContextInitRuleVisitor<?> visitor,
			SubContextInitialization premise, ContextPremises premises,
			ClassConclusionProducer producer);

}
