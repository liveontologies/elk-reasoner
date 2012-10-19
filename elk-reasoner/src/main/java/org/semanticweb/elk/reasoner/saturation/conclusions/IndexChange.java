/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ContextRules;
import org.semanticweb.elk.reasoner.saturation.rules.RuleEngine;
import org.semanticweb.elk.util.collections.chains.AbstractChain;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IndexChange  extends AbstractChain<ContextRules> implements Conclusion {

	private ContextRules contextRules_;

	@Override
	public ContextRules next() {
		return contextRules_;
	}

	@Override
	public void setNext(ContextRules tail) {
		contextRules_ = tail;
	}	
	
	
	@Override
	public void deapply(RuleEngine ruleEngine, Context context) {
		apply(ruleEngine, context);
	}

	@Override
	public void apply(RuleEngine ruleEngine, Context context) {
		ContextRules compositionRule = contextRules_.next();

		for (;;) {
			if (compositionRule == null)
				return;
			compositionRule.apply(ruleEngine, context);
			compositionRule = compositionRule.next();
		}
	}

	@Override
	public <R> R accept(ConclusionVisitor<R> visitor, Context context) {
		return visitor.visit(this, context);
	}

}
