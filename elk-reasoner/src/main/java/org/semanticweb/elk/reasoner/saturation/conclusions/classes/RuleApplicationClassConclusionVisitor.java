package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

import org.semanticweb.elk.Reference;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
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

	
	
	public RuleApplicationClassConclusionVisitor(
			Reference<? extends ContextPremises> premisesRef,
			RuleVisitor<?> ruleAppVisitor,
			ClassConclusionProducer producer) {
		super(premisesRef, ruleAppVisitor, producer);
	}

	@Override
	protected Boolean defaultVisit(ClassConclusion conclusion) {
		// all methods should be explicitly implemented
		throw new RuntimeException("Rules for " + conclusion
				+ " not implemented!");
	}

	@Override
	public Boolean visit(BackwardLink subConclusion) {
		ContextPremises premises = get();
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
	public Boolean visit(Propagation subConclusion) {
		// propagate over non-reflexive backward links
		ruleAppVisitor.visit(SubsumerPropagationRule.getInstance(),
				subConclusion, get(), producer);
		return true;
	}

	@Override
	public Boolean visit(SubContextInitialization subConclusion) {
		ContextPremises premises = get();
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("{}::{} applying sub-concept init rules:",
					premises.getRoot(), subConclusion.getConclusionSubRoot());
		}
		PropagationInitializationRule.getInstance().accept(ruleAppVisitor,
				subConclusion, premises, producer);
		return true;
	}

	@Override
	public Boolean visit(SubClassInclusionComposed conclusion) {
		applyCompositionRules(conclusion);
		return true;
	}

	@Override
	public Boolean visit(ContextInitialization conclusion) {
		LinkedContextInitRule rule = conclusion.getContextInitRuleHead();
		LOGGER_.trace("applying init rules:");
		while (rule != null) {
			LOGGER_.trace("init rule: {}", rule);
			rule.accept(ruleAppVisitor, conclusion, get(), producer);
			rule = rule.next();
		}
		return true;
	}

	@Override
	public Boolean visit(Contradiction conclusion) {
		ruleAppVisitor.visit(ContradictionPropagationRule.getInstance(),
				conclusion, get(), producer);
		return true;
	}

	@Override
	public Boolean visit(SubClassInclusionDecomposed conclusion) {
		applyDecompositionRules(conclusion);
		return true;
	}

	@Override
	public Boolean visit(DisjointSubsumer conclusion) {
		ruleAppVisitor.visit(CONTRADICTION_COMPOSITION_RULE_, conclusion,
				get(), producer);
		return true;
	}

	@Override
	public Boolean visit(ForwardLink conclusion) {
		ContextPremises premises = get();
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
