package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

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
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.LinkedSubsumerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SubsumerDecompositionVisitor;

public abstract class AbstractRuleApplicationClassConclusionVisitor extends
		AbstractClassConclusionVisitor<ContextPremises, Boolean> {

	/**
	 * {@link RuleVisitor} to track rule applications
	 */
	final RuleVisitor<?> ruleAppVisitor;

	/**
	 * {@link ClassConclusionProducer} to produce the {@link ClassConclusion}s of the
	 * applied rules
	 */
	final ClassConclusionProducer producer;

	AbstractRuleApplicationClassConclusionVisitor(RuleVisitor<?> ruleAppVisitor,
			ClassConclusionProducer producer) {
		this.ruleAppVisitor = ruleAppVisitor;
		this.producer = producer;
	}

	void applyCompositionRules(Subsumer conclusion, ContextPremises premises) {
		IndexedClassExpression subsumer = conclusion.getExpression();
		LinkedSubsumerRule compositionRule = subsumer.getCompositionRuleHead();
		while (compositionRule != null) {
			compositionRule
					.accept(ruleAppVisitor, subsumer, premises, producer);
			compositionRule = compositionRule.next();
		}
	}

	void applyDecompositionRules(Subsumer conclusion, ContextPremises premises) {
		IndexedClassExpression subsumer = conclusion.getExpression();
		subsumer.accept(new SubsumerDecompositionVisitor(ruleAppVisitor,
				premises, producer));
//		ComposedFromDecomposedSubsumerRule.getInstance().accept(ruleAppVisitor,
//				subsumer, premises, producer);
	}

}
