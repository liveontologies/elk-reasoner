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
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.context.HybridContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitors;

/**
 * A {@link ConclusionVisitor} that applies local rules (rules producing only
 * {@link Conclusion}s with the same origin root and sub-root as for the
 * premise) for visited {@link Conclusion}s using the provided
 * {@link RuleVisitor} and {@link ConclusionProducer}.
 * 
 * When applying local rules, to the visited {@link Conclusion}, local premises
 * (premises with the same origin root and sub-root as the {@link Conclusion})
 * are taken from the local {@link Context} and other premises from the
 * corresponding {@link Context} in the main saturation state. This is done to
 * ensure that every rule is applied at most once and no inference is lost when
 * processing only local {@link Conclusion}s.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see Rule#isLocal()
 * @see RuleApplicationConclusionVisitor
 */
public class LocalRuleApplicationConclusionVisitor extends
		AbstractConclusionVisitor<Context, Boolean> {

	/**
	 * {@link ConclusionVisitor} applying local rules
	 */
	private final ConclusionVisitor<? super ContextPremises, Boolean> localRuleApplicator_;

	/**
	 * the main {@link SaturationState} to take the non-local premises from
	 */
	private final SaturationState<?> mainState_;

	public LocalRuleApplicationConclusionVisitor(SaturationState<?> mainState,
			RuleVisitor<?> ruleVisitor, ConclusionProducer conclusionProducer) {
		this.mainState_ = mainState;
		this.localRuleApplicator_ = new RuleApplicationConclusionVisitor(
				RuleVisitors.localize(ruleVisitor), conclusionProducer);
	}

	@Override
	protected Boolean defaultVisit(Conclusion conclusion, Context input) {
		ContextPremises premises = getPremises(conclusion, input);
		conclusion.accept(localRuleApplicator_, premises);
		return true;
	}

	/**
	 * @param conclusion
	 * @param input
	 * @return the {@link ContextPremises} that should be used in rules with the
	 *         given {@link Conclusion}. That effectively comprises of all local
	 *         premises (premises with the same origin root and sub-root as the
	 *         given {@link Conclusion}) of the local saturation state and
	 *         non-local premises of the main saturation state.
	 */
	private ContextPremises getPremises(Conclusion conclusion, Context input) {
		ContextPremises mainPremises = mainState_.getContext(input.getRoot());
		if (conclusion instanceof SubConclusion) {
			// this conclusion is not local for the context; it can be used
			// in rules only with conclusions that are local for the context
			return mainPremises;
		}
		// else conclusion must be local for the context
		return new HybridContextPremises(input, mainPremises);
	}
}
