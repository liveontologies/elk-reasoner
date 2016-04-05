package org.semanticweb.elk.reasoner.saturation.rules;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link RuleVisitor} which applies the rules that it visits to the
 * given arguments, producing also redundant inferences
 * 
 * @see Rule#apply(Object, ContextPremises, ClassInferenceProducer)
 * @see Rule#applyRedundant(Object, ContextPremises, ClassInferenceProducer)
 * 
 * @author Yevgeny Kazakov
 */
public class AllInferencesRuleVisitor extends BasicRuleVisitor {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AllInferencesRuleVisitor.class);

	@Override
	protected <P> Void defaultVisit(Rule<P> rule, P premise,
			ContextPremises premises, ClassInferenceProducer producer) {
		super.defaultVisit(rule, premise, premises, producer);
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("{}: process {} by {}", premises, premise, rule);
		}
		rule.applyRedundant(premise, premises, producer);
		return null;
	}

}
