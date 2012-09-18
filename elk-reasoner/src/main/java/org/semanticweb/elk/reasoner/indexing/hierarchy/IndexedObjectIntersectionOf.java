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

import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectIntersectionOfVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ContextRules;
import org.semanticweb.elk.reasoner.saturation.rules.RuleEngine;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

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
			registerContextRules();
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		if (negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			// no negative occurrences of this conjunction left
			deregisterContextRules();
		}

	}

	@Override
	public String toString() {
		return "ObjectIntersectionOf(" + this.firstConjunct + ' '
				+ this.secondConjunct + ')';
	}

	@Override
	public void applyDecompositionRule(RuleEngine ruleEngine, Context context) {
		ruleEngine.produce(context, new PositiveSuperClassExpression(
				firstConjunct));
		ruleEngine.produce(context, new PositiveSuperClassExpression(
				secondConjunct));
	}

	public void registerContextRules() {
		firstConjunct
				.getChainCompositionRules()
				.getCreate(ThisCompositionRule.MATCHER_,
						ThisCompositionRule.FACTORY_)
				.addConjunctionByConjunct(this, secondConjunct);
		secondConjunct
				.getChainCompositionRules()
				.getCreate(ThisCompositionRule.MATCHER_,
						ThisCompositionRule.FACTORY_)
				.addConjunctionByConjunct(this, firstConjunct);
	}

	public void deregisterContextRules() {
		deregister(ThisCompositionRule.MATCHER_, firstConjunct, secondConjunct);
		deregister(ThisCompositionRule.MATCHER_, secondConjunct, firstConjunct);
	}

	@SuppressWarnings("static-method")
	private void deregister(Matcher<ContextRules, ThisCompositionRule> matcher,
			IndexedClassExpression conjunctOne,
			IndexedClassExpression conjunctTwo) {
		Chain<ContextRules> rules = conjunctOne.getChainCompositionRules();
		ThisCompositionRule rule = rules.find(matcher);
		if (rule != null) {
			rule.removeConjunctionByConjunct(conjunctTwo);
			if (rule.isEmpty())
				rules.remove(matcher);
		} else {
			// TODO: throw/log something, this should never happen
		}
	}

	private static class ThisCompositionRule extends ContextRules {

		private final Map<IndexedClassExpression, IndexedObjectIntersectionOf> conjunctionsByConjunct_;

		ThisCompositionRule(ContextRules tail) {
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
				ruleEngine.produce(context, new NegativeSuperClassExpression(
						conjunctionsByConjunct_.get(common)));
		}

		
		private static Matcher<ContextRules, ThisCompositionRule> MATCHER_ = new SimpleTypeBasedMatcher<ContextRules, ThisCompositionRule>(ThisCompositionRule.class);
		
		private static ReferenceFactory<ContextRules, ThisCompositionRule> FACTORY_ = new ReferenceFactory<ContextRules, ThisCompositionRule>() {
			@Override
			public ThisCompositionRule create(ContextRules tail) {
				return new ThisCompositionRule(tail);
			}
		};
	}
}