package org.semanticweb.elk.reasoner.saturation.rules.contextinit;

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

import org.semanticweb.elk.reasoner.saturation.conclusions.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.util.collections.chains.Link;

/**
 * A {@link ContextInitRule} that is linked to other such
 * {@link LinkedContextInitRule}s, thus representing a chain of
 * {@link ContextInitRule}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public interface LinkedContextInitRule extends ContextInitRule,
		Link<LinkedContextInitRule> {

	public void accept(LinkedContextInitRuleVisitor visitor,
			ContextInitialization premise, ContextPremises premises,
			ConclusionProducer producer);

}
