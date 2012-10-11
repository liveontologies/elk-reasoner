package org.semanticweb.elk.reasoner.indexing.hierarchy;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ContextRules;
import org.semanticweb.elk.reasoner.saturation.rules.RuleEngine;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.semanticweb.elk.util.logging.CachedTimeThread;

public class IndexedSubClassOfAxiom extends IndexedAxiom {

	protected final IndexedClassExpression subClass, superClass;

	protected IndexedSubClassOfAxiom(IndexedClassExpression subClass,
			IndexedClassExpression superClass) {
		this.subClass = subClass;
		this.superClass = superClass;
	}

	@Override
	protected void updateOccurrenceNumbers(final IndexUpdater indexUpdater, final int increment) {

		if (increment > 0) {
			registerCompositionRule(indexUpdater);
		} else {
			deregisterCompositionRule(indexUpdater);
		}
	}

	public void registerCompositionRule(IndexUpdater indexUpdater) {
		indexUpdater.add(subClass, this);
	}

	public void deregisterCompositionRule(IndexUpdater indexUpdater) {
		indexUpdater.remove(subClass, this);
	}

	@Override
	public boolean add(IndexedClassExpression target) {
		return target.getChainCompositionRules()
				.getCreate(ThisCompositionRule.MATCHER_,
						ThisCompositionRule.FACTORY_)
				.addToldSuperClassExpression(superClass);
	}

	@Override
	public boolean remove(IndexedClassExpression target) {
		Chain<ContextRules> compositionRules = target
				.getChainCompositionRules();
		ThisCompositionRule rule = compositionRules
				.find(ThisCompositionRule.MATCHER_);
		boolean changed = rule.removeToldSuperClassExpression(superClass);
		
		if (rule.isEmpty()) {
			compositionRules.remove(ThisCompositionRule.MATCHER_);
			
			return true;
		}
		
		return changed;
	}	
	
	/**
	 * 
	 */
	private static class ThisCompositionRule extends ContextRules {

		/**
		 * Correctness of axioms deletions requires that
		 * toldSuperClassExpressions is a List.
		 */
		private List<IndexedClassExpression> toldSuperClassExpressions_;

		ThisCompositionRule(ContextRules tail) {
			super(tail);
			this.toldSuperClassExpressions_ = new ArrayList<IndexedClassExpression>(
					1);
		}

		protected boolean addToldSuperClassExpression(
				IndexedClassExpression superClassExpression) {
			return toldSuperClassExpressions_.add(superClassExpression);
		}

		/**
		 * @param superClassExpression
		 * @return true if successfully removed
		 */
		protected boolean removeToldSuperClassExpression(
				IndexedClassExpression superClassExpression) {
			return toldSuperClassExpressions_.remove(superClassExpression);
		}

		/**
		 * @return {@code true} if this rule never does anything
		 */
		private boolean isEmpty() {
			return toldSuperClassExpressions_.isEmpty();
		}

		@Override
		public void apply(RuleEngine ruleEngine, Context context) {

			RuleStatistics stats = ruleEngine.getRulesTimer();

			stats.timeSubClassOfRule -= CachedTimeThread.currentTimeMillis;
			stats.countSubClassOfRule++;

			try {

				for (IndexedClassExpression implied : toldSuperClassExpressions_)
					ruleEngine.produce(context,
							new PositiveSuperClassExpression(implied));
			} finally {
				stats.timeSubClassOfRule += CachedTimeThread.currentTimeMillis;
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