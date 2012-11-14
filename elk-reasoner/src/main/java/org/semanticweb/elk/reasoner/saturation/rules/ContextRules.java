package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.ChainImpl;

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

/**
 * A skeleton class for implementing rules that can be applied to
 * {@link Context}s.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class ContextRules extends ChainImpl<RuleChain<Context>>
		implements RuleChain<Context> {

	/**
	 * Creates a new chain of {@link ContextRules} by appending to the given
	 * chain of {@link ContextRules}.
	 * 
	 * @param tail
	 *            a chain of {@link ContextRules} to be appended to this rule
	 */
	public ContextRules(RuleChain<Context> tail) {
		super(tail);
	}

	@Override
	public boolean addAllTo(Chain<RuleChain<Context>> chain) {
		RuleChain<Context> current = this;
		boolean result = false;
		for (;;) {
			result |= current.addTo(chain);
			if ((current = current.next()) == null)
				break;
		}
		return result;
	}

	@Override
	public boolean removeAllFrom(Chain<RuleChain<Context>> chain) {
		RuleChain<Context> current = this;
		boolean result = false;
		for (;;) {
			result |= current.removeFrom(chain);
			if ((current = current.next()) == null)
				break;
		}
		return result;
	}

}