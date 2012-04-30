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
package org.semanticweb.elk.reasoner.saturation.classes;

import org.semanticweb.elk.reasoner.indexing.hierarchy.*;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationEngine;

/**
 * @author Frantisek Simancik
 *
 */
public class RuleDecomposition<C extends ContextElClassSaturation> implements InferenceRulePosSCE<C> {

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
			for (IndexedDatatypeExpression negDatatypeExpr :
					element.getProperty().getSatisfyingNegExistentials(element)) {
				if (element != negDatatypeExpr) {
					engine.enqueue(context,
							new PositiveSuperClassExpression<C>(negDatatypeExpr));
				}
			}
			return null;
		}

		public Void visit(IndexedDataSomeValuesFrom element) {
			for (IndexedDatatypeExpression negDatatypeExpr :
					element.getProperty().getSatisfyingNegExistentials(element)) {
				if (element != negDatatypeExpr) {
					engine.enqueue(context,
							new PositiveSuperClassExpression<C>(negDatatypeExpr));
				}
			}
			return null;
		}

		public Void visit(IndexedNominal element) {
			return null;
		}
	};

	public void applySCE(PositiveSuperClassExpression<C> argument,
			C context, RuleApplicationEngine engine) {
		ClassExpressionDecomposer decomposer = new ClassExpressionDecomposer(
				context, engine);
		argument.getExpression().accept(decomposer);
	}
}
