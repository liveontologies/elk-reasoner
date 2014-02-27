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
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
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
 * applications. Essentially, it just calls
 * {@link Conclusion#applyNonRedundantLocalRules(RuleVisitor, ContextPremises, ConclusionProducer)}
 * and
 * {@link Conclusion#applyRedundantLocalRules(RuleVisitor, ContextPremises, ConclusionProducer)}
 * using the respective parameters.
 * 
 * When applying local rules, to the visited {@link Conclusion}, local premises
 * (premises with the same source) are taken from the local {@link Context} and
 * other premises from the corresponding {@link Context} in the main saturation
 * state. This is done to ensure that every rule is applied at most once and no
 * inference is lost when processing only local {@link Conclusion}s.
 * 
 * @see HybridRuleApplicationConclusionVisitor
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class HybridLocalRuleApplicationConclusionVisitor extends
		AbstractConclusionVisitor<Context, Boolean> {

	/**
	 * the main {@link SaturationState} to take the non-local premises from
	 */
	private final SaturationState mainState_;

	/**
	 * a {@link RuleVisitor} for non-redundant rule applications
	 */
	private final RuleVisitor nonRedundantRuleVisitor_;

	/**
	 * a {@link RuleVisitor} for redundant rule applications
	 */
	private final RuleVisitor redundantRuleVisitor_;

	/**
	 * a {@link ConclusionProducer} to produce the {@link Conclusion}s of the
	 * non-redundant rules
	 */
	private final ConclusionProducer nonRedundantProducer_;

	/**
	 * a {@link ConclusionProducer} to produce the {@link Conclusion}s of the
	 * redundant rules
	 */
	private final ConclusionProducer redundantProducer_;

	public HybridLocalRuleApplicationConclusionVisitor(
			SaturationState mainState, RuleVisitor nonRedundantRuleVisitor,
			RuleVisitor redundantRuleVisitor,
			ConclusionProducer nonRedundantProducer,
			ConclusionProducer redundantProducer) {
		this.mainState_ = mainState;
		this.nonRedundantRuleVisitor_ = nonRedundantRuleVisitor;
		this.redundantRuleVisitor_ = redundantRuleVisitor;
		this.nonRedundantProducer_ = nonRedundantProducer;
		this.redundantProducer_ = redundantProducer;
	}

	@Override
	protected Boolean defaultVisit(Conclusion conclusion, Context input) {
		IndexedClassExpression root = input.getRoot();
		if (conclusion.getSourceRoot(root) == root) {
			// applying rules for hybrid premises
			ContextPremises hybridPremises = new HybridContextPremises(input,
					mainState_.getContext(input.getRoot()));
			conclusion.applyNonRedundantLocalRules(nonRedundantRuleVisitor_,
					hybridPremises, nonRedundantProducer_);
			conclusion.applyRedundantLocalRules(redundantRuleVisitor_,
					hybridPremises, redundantProducer_);
		} else {
			// applying rules with non-local premises
			ContextPremises mainPremises = mainState_.getContext(input
					.getRoot());
			conclusion.applyNonRedundantLocalRules(nonRedundantRuleVisitor_,
					mainPremises, nonRedundantProducer_);
			conclusion.applyRedundantLocalRules(redundantRuleVisitor_,
					mainPremises, redundantProducer_);
		}
		return true;
	}
}
