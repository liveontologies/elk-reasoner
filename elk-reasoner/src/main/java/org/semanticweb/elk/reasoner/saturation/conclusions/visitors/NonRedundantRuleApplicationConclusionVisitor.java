package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.LinkedBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.SubsumerBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.LinkedContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.contradiction.ContradictionPropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.disjointsubsumer.ContradicitonCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.BackwardLinkFromForwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.NonReflexiveBackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.ReflexiveBackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.propagations.NonReflexivePropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.propagations.ReflexivePropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subcontextinit.PropagationInitializationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.LinkedSubsumerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SubsumerDecompositionVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ConclusionVisitor} that applies non-redundant rules for the visited
 * {@link Conclusion}s using the provided {@link RuleVisitor} to track rule
 * applications and {@link ConclusionProducer} to output the {@link Conclusion}s
 * of the applied rules. The methods always return {@link true}.
 * 
 * @see AllRuleApplicationConclusionVisitor
 * @see HybridRuleApplicationConclusionVisitor
 * 
 * @author "Yevgeny Kazakov"
 */
public class NonRedundantRuleApplicationConclusionVisitor implements
		ConclusionVisitor<ContextPremises, Boolean> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(NonRedundantRuleApplicationConclusionVisitor.class);

	/**
	 * cached rules for frequent use
	 */
	private static ContradicitonCompositionRule CONTRADICTION_COMPOSITION_RULE_ = new ContradicitonCompositionRule();

	/**
	 * {@link RuleVisitor} to track rule applications
	 */
	private final RuleVisitor ruleAppVisitor_;

	/**
	 * {@link ConclusionProducer} to produce the {@link Conclusion}s of the
	 * applied rules
	 */
	private final ConclusionProducer producer_;

	public NonRedundantRuleApplicationConclusionVisitor(
			RuleVisitor ruleAppVisitor, ConclusionProducer producer) {
		this.producer_ = producer;
		this.ruleAppVisitor_ = ruleAppVisitor;
	}

	@Override
	public Boolean visit(BackwardLink subConclusion, ContextPremises premises) {
		ruleAppVisitor_.visit(SubsumerBackwardLinkRule.getInstance(),
				subConclusion, premises, producer_);

		// apply all backward link rules of the context
		LinkedBackwardLinkRule backLinkRule = premises
				.getBackwardLinkRuleHead();
		while (backLinkRule != null) {
			backLinkRule.accept(ruleAppVisitor_, subConclusion, premises,
					producer_);
			backLinkRule = backLinkRule.next();
		}
		return true;
	}

	@Override
	public Boolean visit(Propagation subConclusion, ContextPremises premises) {
		// propagate over all backward links
		ruleAppVisitor_.visit(ReflexivePropagationRule.getInstance(),
				subConclusion, premises, producer_);
		ruleAppVisitor_.visit(NonReflexivePropagationRule.getInstance(),
				subConclusion, premises, producer_);
		return true;
	}

	@Override
	public Boolean visit(SubContextInitialization subConclusion,
			ContextPremises premises) {
		LOGGER_.trace("{}::{} applying sub-concept init rules:",
				premises.getRoot(), subConclusion.getSubRoot());
		PropagationInitializationRule.getInstance().accept(ruleAppVisitor_,
				subConclusion, premises, producer_);
		return true;
	}

	@Override
	public Boolean visit(ComposedSubsumer<?> conclusion,
			ContextPremises premises) {
		applyCompositionRules(conclusion, premises);
		return true;
	}

	@Override
	public Boolean visit(ContextInitialization conclusion,
			ContextPremises premises) {
		LinkedContextInitRule rule = conclusion.getContextInitRuleHead();
		LOGGER_.trace("applying init rules:");
		while (rule != null) {
			LOGGER_.trace("init rule: {}", rule.getName());
			rule.accept(ruleAppVisitor_, conclusion, premises, producer_);
			rule = rule.next();
		}
		return true;
	}

	@Override
	public Boolean visit(Contradiction conclusion, ContextPremises premises) {
		ruleAppVisitor_.visit(ContradictionPropagationRule.getInstance(),
				conclusion, premises, producer_);
		return true;
	}

	@Override
	public Boolean visit(DecomposedSubsumer<?> conclusion,
			ContextPremises premises) {
		applyCompositionRules(conclusion, premises);
		applyDecompositionRules(conclusion, premises);
		return true;
	}

	@Override
	public Boolean visit(DisjointSubsumer conclusion, ContextPremises premises) {
		if (premises.isInconsistForDisjointnessAxiom(conclusion.getAxiom())) {
			ruleAppVisitor_.visit(CONTRADICTION_COMPOSITION_RULE_, conclusion,
					premises, producer_);
		}
		return true;
	}

	@Override
	public Boolean visit(ForwardLink conclusion, ContextPremises premises) {
		// TODO: reuse the code for non-redundant local rules
		// generate backward links
		ruleAppVisitor_.visit(BackwardLinkFromForwardLinkRule.getInstance(),
				conclusion, premises, producer_);
		// compose with reflexive backward links
		ruleAppVisitor_.visit(
				ReflexiveBackwardLinkCompositionRule.getRuleFor(conclusion),
				conclusion, premises, producer_);
		// compose with non-reflexive backward links
		ruleAppVisitor_.visit(
				NonReflexiveBackwardLinkCompositionRule.getRuleFor(conclusion),
				conclusion, premises, producer_);
		return true;
	}

	void applyCompositionRules(Subsumer<?> conclusion, ContextPremises premises) {
		IndexedClassExpression subsumer = conclusion.getExpression();
		LinkedSubsumerRule compositionRule = subsumer.getCompositionRuleHead();
		while (compositionRule != null) {
			compositionRule.accept(ruleAppVisitor_, subsumer, premises,
					producer_);
			compositionRule = compositionRule.next();
		}
	}

	void applyDecompositionRules(Subsumer<?> conclusion,
			ContextPremises premises) {
		conclusion.getExpression().accept(
				new SubsumerDecompositionVisitor(ruleAppVisitor_, premises,
						producer_));
	}

}
