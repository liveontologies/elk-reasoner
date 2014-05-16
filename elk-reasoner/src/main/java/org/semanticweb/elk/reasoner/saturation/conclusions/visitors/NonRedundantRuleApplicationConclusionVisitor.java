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

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.NonReflexiveBackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.propagations.NonReflexivePropagationRule;

/**
 * A {@link ConclusionVisitor} that applies non-redundant local rules for the
 * visited {@link Conclusion}s using the provided {@link RuleVisitor} to track
 * rule applications and {@link ConclusionProducer} to output the
 * {@link Conclusion}s of the applied rules. The methods always return {@link
 * true}.
 * 
 * @see RedundantRuleApplicationConclusionVisitor
 * @see RedundantLocalRuleApplicationConclusionVisitor
 * 
 * @author "Yevgeny Kazakov"
 */
public class NonRedundantRuleApplicationConclusionVisitor extends
		NonRedundantLocalRuleApplicationConclusionVisitor {

	public NonRedundantRuleApplicationConclusionVisitor(
			RuleVisitor ruleAppVisitor, ConclusionProducer producer) {
		super(ruleAppVisitor, producer);
	}

	@Override
	public Boolean visit(Propagation subConclusion, ContextPremises premises) {
		// propagate over non-reflexive backward links
		ruleAppVisitor.visit(NonReflexivePropagationRule.getInstance(),
				subConclusion, premises, producer);
		// apply non-redundant local rules
		return super.visit(subConclusion, premises);
	}

	@Override
	public Boolean visit(ForwardLink conclusion, ContextPremises premises) {
		// compose with non-reflexive backward links
		ruleAppVisitor.visit(
				NonReflexiveBackwardLinkCompositionRule.getRuleFor(conclusion),
				conclusion, premises, producer);
		// apply non-redundant local rules
		return super.visit(conclusion, premises);
	}

}
