package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;

/**
 * A {@link RuleVisitor} thar returns {@code true} for local rules regardless of
 * all other parameters
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see Rule#isLocal()
 *
 */
public class RuleLocalityChecker extends AbstractRuleVisitor<Boolean> {

	@Override
	<P> Boolean defaultVisit(Rule<P> rule, P premise, ContextPremises premises,
			ConclusionProducer producer) {
		return rule.isLocal();
	}

}
