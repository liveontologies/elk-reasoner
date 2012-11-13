/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleChain;
import org.semanticweb.elk.util.collections.chains.AbstractChain;
import org.semanticweb.elk.util.collections.chains.Chain;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class IncrementalContextRuleChain extends
		AbstractChain<RuleChain<Context>> implements Chain<RuleChain<Context>> {

	private static final Logger LOGGER_ = Logger
			.getLogger(IncrementalContextRuleChain.class);

	private RuleChain<Context> contextRules_;

	public IncrementalContextRuleChain(RuleChain<Context> rules) {
		contextRules_ = rules;
	}

	@Override
	public RuleChain<Context> next() {
		return contextRules_;
	}

	@Override
	public void setNext(RuleChain<Context> tail) {
		contextRules_ = tail;
	}

	public void apply(SaturationState state, Context context) {
		RuleChain<Context> compositionRule = contextRules_;

		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("Applying rules to the index change in the context of "
					+ context.getRoot());
		}

		for (;;) {
			if (compositionRule == null)
				return;
			compositionRule.apply(state, context);
			compositionRule = compositionRule.next();
		}
	}

	public void addTo(Chain<RuleChain<Context>> rules) {
		RuleChain<Context> next = contextRules_;

		while (next != null) {
			next.addTo(rules);
			next = next.next();
		}
	}

	public void removeFrom(Chain<RuleChain<Context>> rules) {
		RuleChain<Context> next = contextRules_;

		while (next != null) {
			next.removeFrom(rules);
			next = next.next();
		}
	}
}