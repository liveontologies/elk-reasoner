package org.semanticweb.elk.reasoner.saturation.rules.contextinit;

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

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassInferenceProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ChainableSubsumerRule;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;

/**
 * A skeleton implementation of {@link ChainableSubsumerRule}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
abstract class AbstractChainableContextInitRule extends
		ModifiableLinkImpl<ChainableContextInitRule> implements
		ChainableContextInitRule {

	AbstractChainableContextInitRule(ChainableContextInitRule tail) {
		super(tail);
	}
	
	@Override
	public void applyRedundant(ContextInitialization premise,
			ContextPremises premises, ClassInferenceProducer producer) {
		// by default does not do anything
	}

	@Override
	public void accept(RuleVisitor<?> visitor, ContextInitialization premise,
			ContextPremises premises, ClassInferenceProducer producer) {
		accept((ContextInitRuleVisitor<?>) visitor, premise, premises, producer);
	}

	@Override
	public void accept(ContextInitRuleVisitor<?> visitor,
			ContextInitialization premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		accept((LinkedContextInitRuleVisitor<?>) visitor, premise, premises,
				producer);
	}
}
