package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.LinkedBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.SubsumerBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.LinkedContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.contradiction.ContradictionPropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.disjointsubsumer.ContradictionCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.BackwardLinkFromForwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.NonReflexiveBackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.ReflexiveBackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.propagations.SubsumerPropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subcontextinit.PropagationInitializationRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ClassConclusion.Visitor} that applies inference rules for the visited
 * {@link ClassConclusion}s using the provided {@link RuleVisitor} to apply rules and
 * {@link ClassConclusionProducer} to output the {@link ClassConclusion}s of the applied
 * rules. The methods always return {@link true}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class RuleApplicationClassConclusionVisitor extends
		AbstractRuleApplicationClassConclusionVisitor {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(RuleApplicationClassConclusionVisitor.class);

	/**
	 * cached rules for frequent use
	 */
	private static ContradictionCompositionRule CONTRADICTION_COMPOSITION_RULE_ = new ContradictionCompositionRule();

	public RuleApplicationClassConclusionVisitor(RuleVisitor<?> ruleAppVisitor,
			ClassConclusionProducer producer) {
		super(ruleAppVisitor, producer);
	}

	@Override
	protected Boolean defaultVisit(ClassConclusion conclusion, ContextPremises input) {
		// all methods should be explicitly implemented
		throw new RuntimeException("Rules for " + conclusion
				+ " not implemented!");
	}

	@Override
	public Boolean visit(BackwardLink subConclusion, ContextPremises premises) {
		ruleAppVisitor.visit(SubsumerBackwardLinkRule.getInstance(),
				subConclusion, premises, producer);

		// apply all backward link rules of the context
		LinkedBackwardLinkRule backLinkRule = premises
				.getBackwardLinkRuleHead();
		while (backLinkRule != null) {
			backLinkRule.accept(ruleAppVisitor, subConclusion, premises,
					producer);
			backLinkRule = backLinkRule.next();
		}
		return true;
	}

	@Override
	public Boolean visit(Propagation subConclusion, ContextPremises premises) {
		// propagate over non-reflexive backward links
		ruleAppVisitor.visit(SubsumerPropagationRule.getInstance(),
				subConclusion, premises, producer);
		return true;
	}

	@Override
	public Boolean visit(SubContextInitialization subConclusion,
			ContextPremises premises) {
		LOGGER_.trace("{}::{} applying sub-concept init rules:",
				premises.getRoot(), subConclusion.getConclusionSubRoot());
		PropagationInitializationRule.getInstance().accept(ruleAppVisitor,
				subConclusion, premises, producer);
		return true;
	}

	@Override
	public Boolean visit(ComposedSubsumer conclusion, ContextPremises premises) {
		applyCompositionRules(conclusion, premises);
		return true;
	}

	@Override
	public Boolean visit(ContextInitialization conclusion,
			ContextPremises premises) {
		LinkedContextInitRule rule = conclusion.getContextInitRuleHead();
		LOGGER_.trace("applying init rules:");
		while (rule != null) {
			LOGGER_.trace("init rule: {}", rule);
			rule.accept(ruleAppVisitor, conclusion, premises, producer);
			rule = rule.next();
		}
		return true;
	}

	@Override
	public Boolean visit(Contradiction conclusion, ContextPremises premises) {
		ruleAppVisitor.visit(ContradictionPropagationRule.getInstance(),
				conclusion, premises, producer);
		return true;
	}

	@Override
	public Boolean visit(DecomposedSubsumer conclusion, ContextPremises premises) {
		applyDecompositionRules(conclusion, premises);
		return true;
	}

	@Override
	public Boolean visit(DisjointSubsumer conclusion, ContextPremises premises) {
		ruleAppVisitor.visit(CONTRADICTION_COMPOSITION_RULE_, conclusion,
				premises, producer);
		return true;
	}

	@Override
	public Boolean visit(ForwardLink conclusion, ContextPremises premises) {
		// generate backward links
		ruleAppVisitor.visit(BackwardLinkFromForwardLinkRule.getInstance(),
				conclusion, premises, producer);
		// compose with non-reflexive backward links
		ruleAppVisitor.visit(
				NonReflexiveBackwardLinkCompositionRule.getRuleFor(conclusion),
				conclusion, premises, producer);
		// compose with reflexive backward links
		ruleAppVisitor.visit(
				ReflexiveBackwardLinkCompositionRule.getRuleFor(conclusion),
				conclusion, premises, producer);
		return true;
	}

}
