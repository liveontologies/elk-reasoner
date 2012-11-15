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
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A {@link SuperClassExpression}, for which the structure of the enclosed
 * {@link IndexedClassExpression} should not be taken into account when applying
 * the rules within {@link Context}. That is, only composition rules stored with
 * this {@link IndexedClassExpression} should be applied to
 * {@link NegativeSuperClassExpression}s.
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
	public void apply(SaturationState.Engine engine, Context context) {
		// ConclusionsCounter statistics = ruleEngine.getConclusionsCounter();
		// statistics.superClassExpressionTime -=
		// CachedTimeThread.currentTimeMillis;

		try {
			// applying all composition rules
			applyCompositionRules(engine, context);

		} finally {
			// statistics.superClassExpressionTime +=
			// CachedTimeThread.currentTimeMillis;
		}
	}

	@Override
	public void deapply(SaturationState.Engine engine, Context context) {
		// ConclusionsCounter statistics = ruleEngine.getConclusionsCounter();
		// statistics.superClassExpressionTime -=
		// CachedTimeThread.currentTimeMillis;

		try {
			expression.applyDecompositionRule(engine, context);
			// applying all composition rules
			applyCompositionRules(engine, context);

		} finally {
			// statistics.superClassExpressionTime +=
			// CachedTimeThread.currentTimeMillis;
		}
	}

	@Override
	public <R> R accept(ConclusionVisitor<R> visitor, Context context) {
		return visitor.visit(this, context);
	}
}