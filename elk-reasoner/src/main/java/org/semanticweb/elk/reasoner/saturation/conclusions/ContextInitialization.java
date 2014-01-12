package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.LinkedContextInitRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@code Conclusion} indicating that this {@link Context} should be
 * initialized.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ContextInitialization extends AbstractConclusion {
	
	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ContextInitialization.class);
	
	// actually we just need only context initialization rules,
	// but they can change after creating this object
	private final OntologyIndex ontologyIndex_;

	public ContextInitialization(OntologyIndex ontologyIndex) {
		this.ontologyIndex_ = ontologyIndex;
	}

	@Override
	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

	@Override
	public void applyNonRedundantRules(RuleVisitor ruleAppVisitor,
			Context context, ConclusionProducer producer) {
		LinkedContextInitRule rule = ontologyIndex_.getContextInitRuleHead();		
		LOGGER_.trace("applying init rules:");
		while (rule != null) {
			LOGGER_.trace("init rule: {}", rule.getName());
			rule.accept(ruleAppVisitor, this, context, producer);
			rule = rule.next();
		}
	}

	@Override
	public void applyRedundantRules(RuleVisitor ruleAppVisitor,
			Context context, ConclusionProducer producer) {
		// no redundant rules

	}

	@Override
	public String toString() {
		return "Init";
	}

}
