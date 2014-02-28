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

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A {@link ConclusionVisitor} that applies all redundant rules for the visited
 * {@link Conclusion}s using the provided {@link RuleVisitor} to track rule
 * applications and {@link ConclusionProducer} to output the {@link Conclusion}s
 * of the applied rules. A rule is redundant if its application is not necessary
 * for completeness. Redundancy of a rule depends on other
 * {@link ContextPremises}: a non-redundant rule might become redundant when
 * other {@link ContextPremises} are added. The methods always return {@link
 * true}.
 * 
 * @see NonRedundantRuleApplicationConclusionVisitor
 * 
 * @author "Yevgeny Kazakov"
 */
public class RedundantRuleApplicationConclusionVisitor extends
		RedundantLocalRuleApplicationConclusionVisitor {

	public RedundantRuleApplicationConclusionVisitor(
			RuleVisitor ruleAppVisitor, ConclusionProducer producer) {
		super(ruleAppVisitor, producer);
	}

	// at the moment all redundant rules are local

}
