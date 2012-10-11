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
import org.semanticweb.elk.util.logging.CachedTimeThread;

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
	private final IndexedClassExpression firstConjunct_, secondConjunct_;

	protected IndexedObjectIntersectionOf(IndexedClassExpression firstConjunct,
			IndexedClassExpression secondConjunct) {
		this.firstConjunct_ = firstConjunct;
		this.secondConjunct_ = secondConjunct; 
	}

	public IndexedClassExpression getFirstConjunct() {
		return firstConjunct_;
	}

	public IndexedClassExpression getSecondConjunct() {
		return secondConjunct_;
	}

	public <O> O accept(IndexedObjectIntersectionOfVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedObjectIntersectionOfVisitor<O>) visitor);
	}

	@Override
	protected void updateOccurrenceNumbers(IndexUpdater indexUpdater, int increment,
			int positiveIncrement, int negativeIncrement) {

		if (negativeOccurrenceNo == 0 && negativeIncrement > 0) {
			// first negative occurrence of this expression
			registerContextRules(indexUpdater);
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		if (negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			// no negative occurrences of this conjunction left
			deregisterContextRules(indexUpdater);
		}

	}

	@Override
	public String toString() {
		return "ObjectIntersectionOf(" + this.firstConjunct_ + ' '
				+ this.secondConjunct_ + ')';
	}

	@Override
	public void applyDecompositionRule(RuleEngine ruleEngine, Context context) {
		RuleStatistics stats = ruleEngine.getRulesTimer();

		stats.timeObjectIntersectionOfDecompositionRule -= CachedTimeThread.currentTimeMillis;
		stats.countObjectIntersectionOfDecompositionRule++;

		try {
			ruleEngine.produce(context, new PositiveSuperClassExpression(
					firstConjunct_));
			ruleEngine.produce(context, new PositiveSuperClassExpression(
					secondConjunct_));
		} finally {
			stats.timeObjectIntersectionOfDecompositionRule += CachedTimeThread.currentTimeMillis;
		}
	}

	public void registerContextRules(final IndexUpdater indexUpdater) {
		indexUpdater.add(firstConjunct_, this);
		indexUpdater.add(secondConjunct_, this);
	}

	public void deregisterContextRules(final IndexUpdater indexUpdater) {
		indexUpdater.remove(firstConjunct_, this);
		indexUpdater.remove(secondConjunct_, this);		
	}

	private boolean register(
			Matcher<ContextRules, ThisCompositionRule> matcher,
			IndexedClassExpression conjunctOne,
			IndexedClassExpression conjunctTwo) {
		return conjunctOne
				.getChainCompositionRules()
				.getCreate(ThisCompositionRule.MATCHER_,
						ThisCompositionRule.FACTORY_)
				.addConjunctionByConjunct(this, conjunctTwo);
	}
	
	private boolean deregister(Matcher<ContextRules, ThisCompositionRule> matcher,
			IndexedClassExpression conjunctOne,
			IndexedClassExpression conjunctTwo) {
		Chain<ContextRules> rules = conjunctOne.getChainCompositionRules();
		ThisCompositionRule rule = rules.find(matcher);
		boolean changed = rule.removeConjunctionByConjunct(conjunctTwo);
		
		if (rule.isEmpty())
			rules.remove(matcher);
		
		return changed;
	}
	
	
	@Override
	public boolean add(IndexedClassExpression target) {
		boolean changed = false;

		if (target == firstConjunct_) {
			changed |= register(ThisCompositionRule.MATCHER_, target, secondConjunct_);
		} else if (target == secondConjunct_) {
			changed |= register(ThisCompositionRule.MATCHER_, target, firstConjunct_);
		}

		return changed;
	}
	
	@Override
	public boolean remove(IndexedClassExpression target) {
		boolean changed = false;

		if (target == firstConjunct_) {
			changed |= deregister(ThisCompositionRule.MATCHER_, target, secondConjunct_);
		} else if (target == secondConjunct_) {
			changed |= deregister(ThisCompositionRule.MATCHER_, target, firstConjunct_);
		}

		return changed;
	}	

	/**
	 * 
	 */
	private static class ThisCompositionRule extends ContextRules {

		private final Map<IndexedClassExpression, IndexedObjectIntersectionOf> conjunctionsByConjunct_;

		ThisCompositionRule(ContextRules tail) {
			super(tail);
			this.conjunctionsByConjunct_ = new ArrayHashMap<IndexedClassExpression, IndexedObjectIntersectionOf>(
					4);
		}

		private boolean addConjunctionByConjunct(
				IndexedObjectIntersectionOf conjunction,
				IndexedClassExpression conjunct) {
			Object previous = conjunctionsByConjunct_.put(conjunct, conjunction);
			
			return previous == null || previous != conjunction;
		}

		private boolean removeConjunctionByConjunct(IndexedClassExpression conjunct) {
			return conjunctionsByConjunct_.remove(conjunct) != null;
		}

		/**
		 * @return {@code true} if this rule never does anything
		 */
		private boolean isEmpty() {
			return conjunctionsByConjunct_.isEmpty();
		}

		@Override
		public void apply(RuleEngine ruleEngine, Context context) {

			RuleStatistics stats = ruleEngine.getRulesTimer();

			stats.timeObjectIntersectionOfCompositionRule -= CachedTimeThread.currentTimeMillis;
			stats.countObjectIntersectionOfCompositionRule++;

			try {

				for (IndexedClassExpression common : new LazySetIntersection<IndexedClassExpression>(
						conjunctionsByConjunct_.keySet(),
						context.getSuperClassExpressions()))
					ruleEngine.produce(context,
							new NegativeSuperClassExpression(
									conjunctionsByConjunct_.get(common)));
			} finally {
				stats.timeObjectIntersectionOfCompositionRule += CachedTimeThread.currentTimeMillis;
			}

		}

		private static Matcher<ContextRules, ThisCompositionRule> MATCHER_ = new SimpleTypeBasedMatcher<ContextRules, ThisCompositionRule>(
				ThisCompositionRule.class);

		private static ReferenceFactory<ContextRules, ThisCompositionRule> FACTORY_ = new ReferenceFactory<ContextRules, ThisCompositionRule>() {
			@Override
			public ThisCompositionRule create(ContextRules tail) {
				return new ThisCompositionRule(tail);
			}
		};
	}
}
