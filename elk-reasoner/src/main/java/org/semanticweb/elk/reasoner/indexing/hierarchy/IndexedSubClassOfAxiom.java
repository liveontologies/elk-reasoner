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

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

public class IndexedSubClassOfAxiom extends IndexedAxiom {

	private static final Logger LOGGER_ = Logger
			.getLogger(IndexedSubClassOfAxiom.class);

	protected final IndexedClassExpression subClass, superClass;

	protected IndexedSubClassOfAxiom(IndexedClassExpression subClass,
			IndexedClassExpression superClass) {
		this.subClass = subClass;
		this.superClass = superClass;
	}

	@Override
	protected void updateOccurrenceNumbers(final IndexUpdater indexUpdater,
			final int increment) {
		if (increment > 0) {
			indexUpdater.add(subClass, new ThisCompositionRule(superClass));
		} else {
			indexUpdater.remove(subClass, new ThisCompositionRule(superClass));
		}
	}

	/**
	 * 
	 */
	private static class ThisCompositionRule extends
			ModifiableLinkImpl<ChainableRule<Context>> implements
			ChainableRule<Context> {

		/**
		 * Correctness of axioms deletions requires that
		 * toldSuperClassExpressions is a List.
		 */
		private List<IndexedClassExpression> toldSuperClassExpressions_;

		ThisCompositionRule(ChainableRule<Context> tail) {
			super(tail);
			this.toldSuperClassExpressions_ = new ArrayList<IndexedClassExpression>(
					1);
		}

		/*
		 * used for registration
		 */
		ThisCompositionRule(IndexedClassExpression ice) {
			super(null);
			this.toldSuperClassExpressions_ = new ArrayList<IndexedClassExpression>(
					1);

			toldSuperClassExpressions_.add(ice);
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
		public void apply(SaturationState.Writer writer, Context context) {

			/*
			 * RuleStatistics stats = ruleEngine.getRulesTimer();
			 * 
			 * stats.timeSubClassOfRule -= CachedTimeThread.currentTimeMillis;
			 * stats.countSubClassOfRule++;
			 */

			try {

				for (IndexedClassExpression implied : toldSuperClassExpressions_) {
					writer.produce(context, new PositiveSuperClassExpression(
							implied));
				}
			} finally {
				// stats.timeSubClassOfRule +=
				// CachedTimeThread.currentTimeMillis;
			}
		}

		private static Matcher<ChainableRule<Context>, ThisCompositionRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableRule<Context>, ThisCompositionRule>(
				ThisCompositionRule.class);

		private static ReferenceFactory<ChainableRule<Context>, ThisCompositionRule> FACTORY_ = new ReferenceFactory<ChainableRule<Context>, ThisCompositionRule>() {
			@Override
			public ThisCompositionRule create(ChainableRule<Context> tail) {
				return new ThisCompositionRule(tail);
			}
		};

		@Override
		public boolean addTo(Chain<ChainableRule<Context>> ruleChain) {
			ThisCompositionRule rule = ruleChain.getCreate(
					ThisCompositionRule.MATCHER_, ThisCompositionRule.FACTORY_);
			boolean changed = false;

			for (IndexedClassExpression ice : toldSuperClassExpressions_) {
				changed |= rule.addToldSuperClassExpression(ice);
			}

			return changed;

		}

		@Override
		public boolean removeFrom(Chain<ChainableRule<Context>> ruleChain) {
			ThisCompositionRule rule = ruleChain
					.find(ThisCompositionRule.MATCHER_);
			boolean changed = false;

			if (rule != null) {
				for (IndexedClassExpression ice : toldSuperClassExpressions_) {
					changed |= rule.removeToldSuperClassExpression(ice);
				}

				if (rule.isEmpty()) {
					ruleChain.remove(ThisCompositionRule.MATCHER_);

					if (LOGGER_.isTraceEnabled()) {
						LOGGER_.trace("Removed SubClassOf rule, superclasses: "
								+ toldSuperClassExpressions_);
					}

					return true;
				}
			}

			return changed;

		}

	}
}