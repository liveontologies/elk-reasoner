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

public class IndexedSubClassOfAxiom extends IndexedAxiom {

	protected final IndexedClassExpression subClass, superClass;

	protected IndexedSubClassOfAxiom(IndexedClassExpression subClass,
			IndexedClassExpression superClass) {
		this.subClass = subClass;
		this.superClass = superClass;
	}

	@Override
	protected void updateOccurrenceNumbers(int increment) {

		if (increment > 0) {
			registerCompositionRule();
		} else {
			deregisterCompositionRule();
		}
	}

	public void registerCompositionRule() {
		subClass.getChainCompositionRules()
				.getCreate(ThisCompositionRule.MATCHER_,
						ThisCompositionRule.FACTORY_)
				.addToldSuperClassExpression(superClass);
	}

	public void deregisterCompositionRule() {
		Chain<ContextRules> compositionRules = subClass
				.getChainCompositionRules();
		ThisCompositionRule rule = compositionRules
				.find(ThisCompositionRule.MATCHER_);
		rule.removeToldSuperClassExpression(superClass);
		if (rule.isEmpty())
			compositionRules.remove(ThisCompositionRule.MATCHER_);
	}

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

		protected void addToldSuperClassExpression(
				IndexedClassExpression superClassExpression) {
			toldSuperClassExpressions_.add(superClassExpression);
		}

		/**
		 * @param superClassExpression
		 * @return true if successfully removed
		 */
		protected void removeToldSuperClassExpression(
				IndexedClassExpression superClassExpression) {
			toldSuperClassExpressions_.remove(superClassExpression);
		}

		/**
		 * @return {@code true} if this rule never does anything
		 */
		private boolean isEmpty() {
			return toldSuperClassExpressions_.isEmpty();
		}

		@Override
		public void apply(RuleEngine ruleEngine, Context context) {

			for (IndexedClassExpression implied : toldSuperClassExpressions_)
				ruleEngine.produce(context, new PositiveSuperClassExpression(
						implied));
		}

		private static Matcher<ContextRules, ThisCompositionRule> MATCHER_ = new Matcher<ContextRules, ThisCompositionRule>() {
			@Override
			public ThisCompositionRule match(ContextRules chain) {
				if (chain instanceof ThisCompositionRule)
					return (ThisCompositionRule) chain;
				else
					return null;
			}
		};

		private static ReferenceFactory<ContextRules, ThisCompositionRule> FACTORY_ = new ReferenceFactory<ContextRules, ThisCompositionRule>() {
			@Override
			public ThisCompositionRule create(ContextRules tail) {
				return new ThisCompositionRule(tail);
			}
		};

	}

}