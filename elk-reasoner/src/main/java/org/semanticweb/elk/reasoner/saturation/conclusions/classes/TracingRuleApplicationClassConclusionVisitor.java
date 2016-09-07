package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

import org.semanticweb.elk.Reference;

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
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.context.HybridContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassInferenceProducer;
import org.semanticweb.elk.reasoner.saturation.rules.Rule;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitors;

/**
 * A {@link ClassConclusion.Visitor} that applies all {@link Rule}s to visited
 * {@link ClassConclusion}s for which {@link Rule#isTracingRule()} returns
 * {@code true}.
 * 
 * When applying a {@link Rule} for a visited {@link ClassConclusion}, premises
 * with the same values of {@link ClassConclusion#getTraceRoot()} and
 * {@link SubClassConclusion#getTraceSubRoot()} as the conclusion are taken from
 * the local {@link ContextPremises} of the {@link Reference} and other premises
 * from the corresponding {@link Context} in the main {@link SaturationState}.
 * This is done to ensure that every rule is applied at most once and no
 * inference is lost.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see Rule#isTracingRule()
 */
public class TracingRuleApplicationClassConclusionVisitor
		extends
			DummyClassConclusionVisitor<Boolean> {

	private final HybrridContextPremises hybridPremisesRef_;

	/**
	 * {@link ClassConclusion.Visitor} applying local rules
	 */
	private final ClassConclusion.Visitor<Boolean> localRuleApplicator_;

	public TracingRuleApplicationClassConclusionVisitor(
			SaturationState<?> mainState,
			Reference<? extends ContextPremises> localPremisesRef,
			RuleVisitor<?> ruleVisitor,
			ClassInferenceProducer conclusionProducer) {
		this.hybridPremisesRef_ = new HybrridContextPremises(localPremisesRef,
				mainState);
		this.localRuleApplicator_ = new RuleApplicationClassConclusionVisitor(
				hybridPremisesRef_,
				mainState.getOntologyIndex().getContextInitRuleHead(),
				RuleVisitors.getTracingVisitor(ruleVisitor),
				conclusionProducer);
	}

	@Override
	protected Boolean defaultVisit(ClassConclusion conclusion) {
		hybridPremisesRef_.setConclusion(conclusion);
		conclusion.accept(localRuleApplicator_);
		return true;
	}

	/**
	 * A {@link Reference} to {@link ContextPremises} that should be used in
	 * rules with the given {@link ClassConclusion}. That effectively comprises
	 * of all local premises (premises with the same origin root and sub-root as
	 * the given {@link ClassConclusion}) of the local saturation state and
	 * non-local premises of the main saturation state.
	 * 
	 * @author Yevgeny Kazakov
	 */
	private static class HybrridContextPremises
			implements
				Reference<ContextPremises> {

		private ClassConclusion conclusion_;

		private final Reference<? extends ContextPremises> localPremisesRef_;

		private final SaturationState<?> mainState_;

		HybrridContextPremises(
				Reference<? extends ContextPremises> localPremisesRef,
				SaturationState<?> mainState) {
			this.localPremisesRef_ = localPremisesRef;
			this.mainState_ = mainState;
		}

		void setConclusion(ClassConclusion conclusion) {
			this.conclusion_ = conclusion;
		}

		@Override
		public ContextPremises get() {
			ContextPremises localPremises = localPremisesRef_.get();
			ContextPremises mainPremises = mainState_
					.getContext(localPremises.getRoot());
			if (conclusion_ instanceof SubClassConclusion) {
				// this conclusion is not local for the context; it can be used
				// in rules only with conclusions that are local for the context
				return mainPremises;
			}
			// else conclusion must be local for the context
			return new HybridContextPremises(localPremises, mainPremises);
		}

	}

}
