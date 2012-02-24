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
package org.semanticweb.elk.reasoner.saturation.rules;

import java.util.List;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.expressions.PositiveSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.expressions.SuperClassExpression;

public class RuleSubsumption extends SuperClassExpressionRule implements InferenceRule {

	public void apply(SuperClassExpression argument, Context context, RuleApplicationEngine engine) {

		List<IndexedClassExpression> implied = argument.getExpression().getToldSuperClassExpressions();

		if (implied == null)
			return;

		for (IndexedClassExpression ice : implied)
			engine.enqueue(context, new PositiveSuperClassExpression(ice));

	}

	public RegistrableRule[] getComponentRules() {
		return new RegistrableRule[] { this };
	}

}
