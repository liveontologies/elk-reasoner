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
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ContextRules;
import org.semanticweb.elk.reasoner.saturation.rules.RuleEngine;

/**
 * A {@link SuperClassExpression}, for which the structure of the enclosed
 * {@link IndexedClassExpression} should be taken into account. That is, in
 * addition to composition rules stored with this {@link IndexedClassExpression}
 * , the so-called decomposition rule, which takes into account the topmost
 * constructor of this {@link IndexedClassExpression}, should be applied.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class PositiveSuperClassExpression extends SuperClassExpression {

	public PositiveSuperClassExpression(
			IndexedClassExpression superClassExpression) {
		super(superClassExpression);
	}

	@Override
	public void apply(RuleEngine ruleEngine, Context context) {
		if (!storeInContext(context, ruleEngine.getConclusionsCounter()))
			return;

		// apply decomposition rules
		expression.applyDecompositionRule(ruleEngine, context);

		// applying all composition rules
		ContextRules compositionRule = expression.getCompositionRules();

		for (;;) {
			if (compositionRule == null)
				return;
			compositionRule.apply(ruleEngine, context);
			compositionRule = compositionRule.next();
		}

	}

}
