/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;
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
import org.semanticweb.elk.reasoner.saturation.rules.ContextRules;
import org.semanticweb.elk.util.collections.chains.AbstractChain;
import org.semanticweb.elk.util.collections.chains.Chain;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IncrementalContextRuleChain  extends AbstractChain<ContextRules> implements Conclusion {

	private static final Logger LOGGER_ = Logger.getLogger(IncrementalContextRuleChain.class);
	
	private ContextRules contextRules_;

	public IncrementalContextRuleChain(ContextRules rules) {
		contextRules_ = rules;
	}
	
	@Override
	public ContextRules next() {
		return contextRules_;
	}

	@Override
	public void setNext(ContextRules tail) {
		contextRules_ = tail;
	}	
	
	@Override
	public void deapply(SaturationState state, Context context) {
		apply(state, context);
	}

	@Override
	public void apply(SaturationState state, Context context) {
		ContextRules compositionRule = contextRules_;

		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("Applying rules to the index change in the context of " + context.getRoot());
		}
		
		for (;;) {
			if (compositionRule == null)
				return;
			compositionRule.apply(state, context);
			compositionRule = compositionRule.next();
		}
	}

	@Override
	public <R> R accept(ConclusionVisitor<R> visitor, Context context) {
		return visitor.visit(this, context);
	}

	@Override
	public String toString() {
		return "Set of incremental changes";
	}
	
	public void addTo(Chain<ContextRules> rules) {
		ContextRules next = contextRules_;
		
		while (next != null) {
			next.addTo(rules);
			next = next.next();
		}
	}
	
	public void removeFrom(Chain<ContextRules> rules) {
		ContextRules next = contextRules_;
		
		while (next != null) {
			next.removeFrom(rules);
			next = next.next();
		}
	}	
}