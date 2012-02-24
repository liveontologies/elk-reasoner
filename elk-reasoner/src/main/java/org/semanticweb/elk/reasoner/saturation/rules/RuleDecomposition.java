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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedNominal;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.expressions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.expressions.PositiveSuperClassExpression;

public class RuleDecomposition extends PositiveSuperClassExpressionRule implements InferenceRule {
	
	private class ClassExpressionDecomposer implements IndexedClassExpressionVisitor<Void> {
		
		private final Context context;
		private final RuleApplicationEngine engine;

		public ClassExpressionDecomposer(Context context, RuleApplicationEngine engine) {
			this.context = context;
			this.engine = engine;
		}
		
		public Void visit(IndexedClass ice) {
			if (ice == engine.owlNothing)
				context.isSatisfiable = false;
			return null;
		}

		public Void visit(IndexedObjectIntersectionOf ice) {
			engine.enqueue(context, new PositiveSuperClassExpression(ice.getFirstConjunct()));
			engine.enqueue(context, new PositiveSuperClassExpression(ice.getSecondConjunct()));
			return null;
		}

		public Void visit(IndexedObjectSomeValuesFrom ice) {
			engine.enqueue(engine.getCreateContext(ice.getFiller()),
					new BackwardLink(ice.getRelation(), context));
			return null;
		}

		public Void visit(IndexedDataHasValue element) {
			return null;
		}

		public Void visit(IndexedNominal element) {
			return null;
		}
	};
	
	public void apply(PositiveSuperClassExpression argument, Context context, RuleApplicationEngine engine) {
		ClassExpressionDecomposer decomposer = new ClassExpressionDecomposer(context, engine);
		argument.getExpression().accept(decomposer);
	}
	
	public RegistrableRule[] getComponentRules() {
		return new RegistrableRule[] { this };
	}
}
