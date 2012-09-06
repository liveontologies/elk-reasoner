/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
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
package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.rules.CompositionRules;
import org.semanticweb.elk.reasoner.indexing.rules.RuleEngine;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A {@link SuperClassExpression} to which composition rules should be applied.
 * Decomposition rules do not need to by applied to this object.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class NegativeSuperClassExpression extends SuperClassExpression {

	public NegativeSuperClassExpression(
			IndexedClassExpression superClassExpression) {
		super(superClassExpression);
	}

	@Override
	public void process(Context context, RuleEngine ruleEngine) {
		if (!storeInContext(context, ruleEngine))
			return;

		// applying all composition rules
		CompositionRules rules = expression.getNext();

		for (;;) {
			if (rules == null)
				return;
			rules.apply(ruleEngine, context);
			rules = rules.getNext();
		}

	}
}
