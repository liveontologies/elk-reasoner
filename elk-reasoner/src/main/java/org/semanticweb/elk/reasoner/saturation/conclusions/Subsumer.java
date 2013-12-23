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
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleVisitor;

/**
 * A {@link Conclusion} representing a subsumer {@link IndexedClassExpression}
 * of the root of the {@link Context} for which it is produced. Intuitively, if
 * a subclass axiom {@code SubClassOf(:A :B)} is derived by inference rules,
 * then a {@link Subsumer} corresponding to {@code :B} can be produced for the
 * context with root {@code :A}
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class Subsumer implements Conclusion {

	/**
	 * the implied {@code IndexedClassExpression} represented by this
	 * {@link Subsumer}
	 */
	private final IndexedClassExpression expression_;

	public Subsumer(IndexedClassExpression expression) {
		this.expression_ = expression;
	}

	/**
	 * @return the {@code IndexedClassExpression} represented by this
	 *         {@link Subsumer}
	 */
	public IndexedClassExpression getExpression() {
		return expression_;
	}

	@Override
	public String toString() {
		return expression_.toString() + ": " + expression_.hashCode();
	}

	@Override
	public void accept(CompositionRuleVisitor ruleAppVisitor,
			SaturationStateWriter writer, Context context) {

		LinkRule<Context> compositionRule = expression_
				.getCompositionRuleHead();
		while (compositionRule != null) {
			compositionRule.accept(ruleAppVisitor, writer, context);
			compositionRule = compositionRule.next();
		}
	}

	@Override
	public Context getSourceContext(Context contextWhereStored) {
		return contextWhereStored;
	}
}
