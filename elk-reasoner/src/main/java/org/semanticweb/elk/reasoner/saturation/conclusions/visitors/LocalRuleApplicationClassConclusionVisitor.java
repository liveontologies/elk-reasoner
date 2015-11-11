package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.context.HybridContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitors;

/**
 * A {@link ClassConclusion.Visitor} that applies local rules (rules producing only
 * {@link ClassConclusion}s with the same origin root and sub-root as for the
 * premise) for visited {@link ClassConclusion}s using the provided
 * {@link RuleVisitor} and {@link ClassConclusionProducer}.
 * 
 * When applying local rules, to the visited {@link ClassConclusion}, local premises
 * (premises with the same origin root and sub-root as the {@link ClassConclusion})
 * are taken from the local {@link Context} and other premises from the
 * corresponding {@link Context} in the main saturation state. This is done to
 * ensure that every rule is applied at most once and no inference is lost when
 * processing only local {@link ClassConclusion}s.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see Rule#isLocal()
 * @see RuleApplicationClassConclusion.Visitor
 */
public class LocalRuleApplicationClassConclusionVisitor extends
		AbstractClassConclusionVisitor<Context, Boolean> {

	/**
	 * {@link ClassConclusion.Visitor} applying local rules
	 */
	private final ClassConclusion.Visitor<? super ContextPremises, Boolean> localRuleApplicator_;

	/**
	 * the main {@link SaturationState} to take the non-local premises from
	 */
	private final SaturationState<?> mainState_;

	public LocalRuleApplicationClassConclusionVisitor(SaturationState<?> mainState,
			RuleVisitor<?> ruleVisitor, ClassConclusionProducer conclusionProducer) {
		this.mainState_ = mainState;
		this.localRuleApplicator_ = new RuleApplicationClassConclusionVisitor(
				RuleVisitors.localize(ruleVisitor), conclusionProducer);
	}

	@Override
	protected Boolean defaultVisit(ClassConclusion conclusion, Context input) {
		ContextPremises premises = getPremises(conclusion, input);
		conclusion.accept(localRuleApplicator_, premises);
		return true;
	}

	/**
	 * @param conclusion
	 * @param input
	 * @return the {@link ContextPremises} that should be used in rules with the
	 *         given {@link ClassConclusion}. That effectively comprises of all local
	 *         premises (premises with the same origin root and sub-root as the
	 *         given {@link ClassConclusion}) of the local saturation state and
	 *         non-local premises of the main saturation state.
	 */
	private ContextPremises getPremises(ClassConclusion conclusion, Context input) {
		ContextPremises mainPremises = mainState_.getContext(input.getRoot());
		if (conclusion instanceof SubClassConclusion) {
			// this conclusion is not local for the context; it can be used
			// in rules only with conclusions that are local for the context
			return mainPremises;
		}
		// else conclusion must be local for the context
		return new HybridContextPremises(input, mainPremises);
	}
}
