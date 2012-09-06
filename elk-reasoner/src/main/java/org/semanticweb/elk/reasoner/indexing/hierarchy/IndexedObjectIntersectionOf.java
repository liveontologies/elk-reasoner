/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.Map;

import org.semanticweb.elk.reasoner.indexing.rules.ChainImpl;
import org.semanticweb.elk.reasoner.indexing.rules.ChainMatcher;
import org.semanticweb.elk.reasoner.indexing.rules.CompositionRules;
import org.semanticweb.elk.reasoner.indexing.rules.RuleEngine;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectIntersectionOfVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.LazySetIntersection;

/**
 * Represents all occurrences of an ElkObjectIntersectionOf in an ontology.
 * 
 * @author Frantisek Simancik
 * 
 */
public class IndexedObjectIntersectionOf extends IndexedClassExpression {
	/**
	 * There are only two conjuncts. This reflects the fact that conjunctions
	 * are binarized during index construction. The conjuncts may not correspond
	 * to any ElkClassExpression in the ontology.
	 */
	protected final IndexedClassExpression firstConjunct, secondConjunct;

	protected IndexedObjectIntersectionOf(IndexedClassExpression firstConjunct,
			IndexedClassExpression secondConjunct) {
		this.firstConjunct = firstConjunct;
		this.secondConjunct = secondConjunct;
	}

	public IndexedClassExpression getFirstConjunct() {
		return firstConjunct;
	}

	public IndexedClassExpression getSecondConjunct() {
		return secondConjunct;
	}

	public <O> O accept(IndexedObjectIntersectionOfVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedObjectIntersectionOfVisitor<O>) visitor);
	}

	@Override
	protected void updateOccurrenceNumbers(int increment,
			int positiveIncrement, int negativeIncrement) {

		if (negativeOccurrenceNo == 0 && negativeIncrement > 0) {
			// first negative occurrence of this expression
			registerCompositionRules();
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		if (negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			// no negative occurrences of this conjunction left
			deregisterCompositionRules();
		}

	}

	@Override
	public String toString() {
		return "ObjectIntersectionOf(" + this.firstConjunct + ' '
				+ this.secondConjunct + ')';
	}

	@Override
	public void applyDecompositionRule(RuleEngine ruleEngine, Context context) {
		ruleEngine.derive(context, new PositiveSuperClassExpression(
				firstConjunct));
		ruleEngine.derive(context, new PositiveSuperClassExpression(
				secondConjunct));
	}

	public void registerCompositionRules() {
		CompositionRuleMatcher matcher = new CompositionRuleMatcher();
		firstConjunct.getCreate(matcher).addConjunctionByConjunct(this,
				secondConjunct);
		secondConjunct.getCreate(matcher).addConjunctionByConjunct(this,
				firstConjunct);
	}

	public void deregisterCompositionRules() {
		CompositionRuleMatcher matcher = new CompositionRuleMatcher();
		deregister(matcher, firstConjunct, secondConjunct);
		deregister(matcher, secondConjunct, firstConjunct);
	}

	@SuppressWarnings("static-method")
	private void deregister(CompositionRuleMatcher matcher,
			IndexedClassExpression conjunctOne,
			IndexedClassExpression conjunctTwo) {
		ThisCompositionRule rule = conjunctOne.find(matcher);
		if (rule != null) {
			rule.removeConjunctionByConjunct(conjunctTwo);
			if (rule.isEmpty())
				conjunctOne.remove(matcher);
		} else {
			// TODO: throw/log something, this should never happen
		}
	}

	private static class ThisCompositionRule extends ChainImpl<CompositionRules>
			implements CompositionRules {

		private final Map<IndexedClassExpression, IndexedObjectIntersectionOf> conjunctionsByConjunct_;

		ThisCompositionRule(CompositionRules tail) {
			super(tail);
			this.conjunctionsByConjunct_ = new ArrayHashMap<IndexedClassExpression, IndexedObjectIntersectionOf>(
					4);
		}

		private void addConjunctionByConjunct(
				IndexedObjectIntersectionOf conjunction,
				IndexedClassExpression conjunct) {
			conjunctionsByConjunct_.put(conjunct, conjunction);
		}

		private void removeConjunctionByConjunct(IndexedClassExpression conjunct) {
			conjunctionsByConjunct_.remove(conjunct);
		}

		/**
		 * @return {@code true} if this rule never does anything
		 */
		private boolean isEmpty() {
			return conjunctionsByConjunct_.isEmpty();
		}

		@Override
		public void apply(RuleEngine ruleEngine, Context context) {
			for (IndexedClassExpression common : new LazySetIntersection<IndexedClassExpression>(
					conjunctionsByConjunct_.keySet(),
					context.getSuperClassExpressions()))
				ruleEngine.derive(context, new NegativeSuperClassExpression(
						conjunctionsByConjunct_.get(common)));
		}

	}

	private class CompositionRuleMatcher implements
			ChainMatcher<CompositionRules, ThisCompositionRule> {

		@Override
		public ThisCompositionRule createNew(CompositionRules tail) {
			return new ThisCompositionRule(tail);
		}

		@Override
		public ThisCompositionRule match(CompositionRules chain) {
			if (chain instanceof ThisCompositionRule)
				return (ThisCompositionRule) chain;
			else
				return null;
		}

	}

}