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

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A {@link ConclusionVisitor} that applies all redundant local rules for the
 * visited {@link Conclusion}s using the provided {@link RuleVisitor} to track
 * rule applications and {@link ConclusionProducer} to output the
 * {@link Conclusion}s of the applied rules. A rule is redundant if its
 * application is not necessary for completeness. A rule is local if it produces
 * only {@link Conclusion}s that logically belong to the same root as the
 * {@link Conclusion} to which it applies. The methods always return {@link
 * true}.
 * 
 * @see RedundantRuleApplicationConclusionVisitor
 * @see NonRedundantRuleApplicationConclusionVisitor
 * 
 * @author "Yevgeny Kazakov"
 */
public class RedundantLocalRuleApplicationConclusionVisitor extends
		AbstractRuleApplicationConclusionVisitor {

	public RedundantLocalRuleApplicationConclusionVisitor(
			RuleVisitor ruleAppVisitor, ConclusionProducer producer) {
		super(ruleAppVisitor, producer);
	}

	@Override
	protected Boolean defaultVisit(Conclusion conclusion, ContextPremises input) {
		// no redundant rules by default
		return true;
	}

	@Override
	public Boolean visit(ComposedSubsumer<?> conclusion,
			ContextPremises premises) {
		// if subsumer was composed, it is not necessary to decompose it
		applyDecompositionRules(conclusion, premises);
		return true;
	}

}
