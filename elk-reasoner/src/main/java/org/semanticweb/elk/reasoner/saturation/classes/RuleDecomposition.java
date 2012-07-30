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

import org.semanticweb.elk.reasoner.datatypes.valuespaces.EmptyValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationFactory;

/**
 * TODO: documentation
 * 
 * @author Frantisek Simancik
 * 
 * @param <C>
 *            the type of contexts that can be used with this inference rule
 */
public class RuleDecomposition<C extends ContextElClassSaturation> implements
		InferenceRulePosSCE<C> {

	private class ClassExpressionDecomposer implements
			IndexedClassExpressionVisitor<Void> {

		private final C context;
		private final RuleApplicationFactory.Engine engine;

		public ClassExpressionDecomposer(C context,
				RuleApplicationFactory.Engine engine) {
			this.context = context;
			this.engine = engine;
		}

		@Override
		public Void visit(IndexedClass ice) {
			return null;
		}

		@Override
		public Void visit(IndexedObjectIntersectionOf ice) {
			engine.enqueue(context,
					new PositiveSuperClassExpression<C>(ice.getFirstConjunct()));
			engine.enqueue(
					context,
					new PositiveSuperClassExpression<C>(ice.getSecondConjunct()));
			return null;
		}

		@Override
		public Void visit(IndexedObjectSomeValuesFrom ice) {
			engine.enqueue(engine.getCreateContext(ice.getFiller()),
					new BackwardLink<C>(ice.getRelation(), context));
			return null;
		}

		@Override
		public Void visit(IndexedDatatypeExpression ide) {
			IndexedDataProperty idp = ide.getProperty();
			ValueSpace vs = ide.getValueSpace();
			if (vs == EmptyValueSpace.INSTANCE) {
				// this means that value space is inconsistent; in this
				// case we are done
				engine.enqueue(context, new PositiveSuperClassExpression<C>(
						engine.getOwlNothing()));
				return null;
			}
			for (IndexedDataProperty superProperty : idp.getSaturated()
					.getSuperProperties()) {
				Iterable<IndexedDatatypeExpression> negativeDatatypeExpressions = superProperty
						.getNegativeDatatypeExpressions();
				if (negativeDatatypeExpressions == null)
					continue;
				for (IndexedDatatypeExpression candidate : negativeDatatypeExpressions) {
					if (candidate == ide)
						// already derived
						continue;
					// check if the candidate value space subsumes the current
					// value space
					if (vs.isSubsumedBy(candidate.getValueSpace())) {
						engine.enqueue(context,
						// no decomposition rule should be applied to the result
								new NegativeSuperClassExpression<C>(candidate));
					}
				}
			}
			return null;
		}

		@Override
		public Void visit(IndexedIndividual element) {
			return null;
		}

	};

	@Override
	public void applySCE(PositiveSuperClassExpression<C> argument, C context,
			RuleApplicationFactory.Engine engine) {
		ClassExpressionDecomposer decomposer = new ClassExpressionDecomposer(
				context, engine);
		argument.getExpression().accept(decomposer);
	}

}
