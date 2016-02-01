package org.semanticweb.elk.reasoner.saturation.rules;

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

import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;

/**
 * A {@link RuleVisitor} thar returns {@code true} for local rules regardless of
 * all other parameters
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see Rule#isLocal()
 *
 */
public class RuleLocalityChecker extends AbstractRuleVisitor<Boolean> {

	@Override
	<P> Boolean defaultVisit(Rule<P> rule, P premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		return rule.isLocal();
	}

}
