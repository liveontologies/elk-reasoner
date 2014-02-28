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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.context.HybridContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A {@link ConclusionVisitor} that applies local rules (rules producing only
 * {@link Conclusion}s with the same source as for the premise) for visited
 * {@link Conclusion}s using the provided {@link RuleVisitor}s and
 * {@link ConclusionProducer}s for respectively non-redundant and redundant rule
 * applications.
 * 
 * When applying local rules, to the visited {@link Conclusion}, local premises
 * (premises with the same source as the {@link Conclusion}) are taken from the
 * local {@link Context} and other premises from the corresponding
 * {@link Context} in the main saturation state. This is done to ensure that
 * every rule is applied at most once and no inference is lost when processing
 * only local {@link Conclusion}s.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see NonRedundantLocalRuleApplicationConclusionVisitor
 * @see RedundantLocalRuleApplicationConclusionVisitor
 */
public class HybridLocalRuleApplicationConclusionVisitor extends
		AbstractConclusionVisitor<Context, Boolean> {

	/**
	 * {@link ConclusionVisitor} applying non-redundant local rules
	 */
	private final ConclusionVisitor<? super ContextPremises, Boolean> nonRedundantLocalRuleApplicator_;

	/**
	 * {@link ConclusionVisitor} applying redundant local rules
	 */
	private final ConclusionVisitor<? super ContextPremises, Boolean> redundantLocalRuleApplicator_;

	/**
	 * the main {@link SaturationState} to take the non-local premises from
	 */
	private final SaturationState<?> mainState_;

	public HybridLocalRuleApplicationConclusionVisitor(
			SaturationState<?> mainState, RuleVisitor nonRedundantRuleVisitor,
			RuleVisitor redundantRuleVisitor,
			ConclusionProducer nonRedundantProducer,
			ConclusionProducer redundantProducer) {
		this.mainState_ = mainState;
		this.nonRedundantLocalRuleApplicator_ = new NonRedundantLocalRuleApplicationConclusionVisitor(
				nonRedundantRuleVisitor, nonRedundantProducer);
		this.redundantLocalRuleApplicator_ = new RedundantLocalRuleApplicationConclusionVisitor(
				redundantRuleVisitor, redundantProducer);
	}

	@Override
	protected Boolean defaultVisit(Conclusion conclusion, Context input) {
		ContextPremises premises = getPremises(conclusion, input);
		conclusion.accept(nonRedundantLocalRuleApplicator_, premises);
		conclusion.accept(redundantLocalRuleApplicator_, premises);
		return true;
	}

	private ContextPremises getPremises(Conclusion conclusion, Context input) {
		IndexedClassExpression root = input.getRoot();
		ContextPremises mainPremises = mainState_.getContext(root);
		if (conclusion.getSourceRoot(root) != root)
			// there are currently no rules which can use other context premises
			// with the same source, so we can just take all main premises
			return mainPremises;
		// else
		return new HybridContextPremises(input, mainPremises);
	}
}
