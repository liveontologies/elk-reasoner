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
package org.semanticweb.elk.reasoner.saturation.elkrulesystem;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedNominal;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.rulesystem.InferenceRule;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationEngine;

public class RuleDecomposition<C extends ContextEl> implements InferenceRule<C> {

	private class ClassExpressionDecomposer implements
			IndexedClassExpressionVisitor<Void> {

		private final C context;
		private final RuleApplicationEngine engine;

		public ClassExpressionDecomposer(C context,
				RuleApplicationEngine engine) {
			this.context = context;
			this.engine = engine;
		}

		public Void visit(IndexedClass ice) {
			if (ice == engine.owlNothing)
				context.setSatisfiable(false);
			return null;
		}

		public Void visit(IndexedObjectIntersectionOf ice) {
			engine.enqueue(context,
					new PositiveSuperClassExpression<C>(ice.getFirstConjunct()));
			engine.enqueue(context,
					new PositiveSuperClassExpression<C>(ice.getSecondConjunct()));
			return null;
		}

		public Void visit(IndexedObjectSomeValuesFrom ice) {
			engine.enqueue(engine.getCreateContext(ice.getFiller()),
					new BackwardLink<C>(ice.getRelation(), context));
			return null;
		}

		public Void visit(IndexedDataHasValue element) {
			return null;
		}

		public Void visit(IndexedNominal element) {
			return null;
		}
	};

	public void apply(PositiveSuperClassExpression<C> argument,
			C context, RuleApplicationEngine engine) {
		ClassExpressionDecomposer decomposer = new ClassExpressionDecomposer(
				context, engine);
		argument.getExpression().accept(decomposer);
	}

}
