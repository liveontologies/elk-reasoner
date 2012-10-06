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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ContextRules;
import org.semanticweb.elk.reasoner.saturation.rules.RuleEngine;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * Defines the disjointness inference rule for indexed class expressions
 * 
 * @author Frantisek Simancik
 * @author Pavel Klinov
 * @author "Yevgeny Kazakov"
 * 
 */
public class IndexedDisjointnessAxiom extends IndexedAxiom {

	/**
	 * We use two types of composition rules for disjointness axioms. If the
	 * number of members in the axiom exceeds the threshold, we register
	 * {@link ThisNaryCompositionRule}s with the members; otherwise, we register
	 * {@link ThisBinaryCompositionRule} with the members.
	 */
	final static int DISJOINT_AXIOM_BINARIZATION_THRESHOLD = 3;

	private final List<IndexedClassExpression> members_;

	IndexedDisjointnessAxiom(List<IndexedClassExpression> members) {
		members_ = members;
	}

	public List<IndexedClassExpression> getMembers() {
		return members_;
	}

	@Override
	protected void updateOccurrenceNumbers(int increment) {
		if (increment > 0) {
			registerCompositionRule();
		} else {
			deregisterCompositionRule();
		}
	}

	private void registerCompositionRule() {
		if (members_.size() > DISJOINT_AXIOM_BINARIZATION_THRESHOLD) {
			for (IndexedClassExpression ice : members_) {
				ice.getChainCompositionRules()
						.getCreate(ThisNaryCompositionRule.MATCHER_,
								ThisNaryCompositionRule.FACTORY_)
						.addDisjointnessAxiom(this);
			}
		} else {
			for (final IndexedClassExpression member : members_) {
				ThisBinaryCompositionRule rule = member
						.getChainCompositionRules().getCreate(
								ThisBinaryCompositionRule.MATCHER_,
								ThisBinaryCompositionRule.FACTORY_);
				boolean selfFound = false; // true when otherMember = member
				for (IndexedClassExpression otherMember : members_) {
					if (!selfFound && otherMember == member) {
						selfFound = true;
						continue;
					}
					rule.addDisjointClass(otherMember);
				}
			}
		}

	}

	private void deregisterCompositionRule() {
		if (members_.size() > DISJOINT_AXIOM_BINARIZATION_THRESHOLD) {
			for (IndexedClassExpression ice : members_) {
				Chain<ContextRules> compositionRules = ice
						.getChainCompositionRules();
				ThisNaryCompositionRule rule = compositionRules
						.find(ThisNaryCompositionRule.MATCHER_);
				rule.removeDisjointnessAxiom(this);
				if (rule.isEmpty()) {
					compositionRules.remove(ThisNaryCompositionRule.MATCHER_);
				}
			}
		} else {
			for (final IndexedClassExpression member : members_) {
				Chain<ContextRules> compositionRules = member
						.getChainCompositionRules();
				ThisBinaryCompositionRule rule = compositionRules.getCreate(
						ThisBinaryCompositionRule.MATCHER_,
						ThisBinaryCompositionRule.FACTORY_);
				boolean selfFound = false; // true when otherMember = member
				for (IndexedClassExpression otherMember : members_) {
					if (!selfFound && otherMember == member) {
						selfFound = true;
						continue;
					}
					rule.removeDisjointClass(otherMember);
				}
				if (rule.isEmpty()) {
					compositionRules.remove(ThisBinaryCompositionRule.MATCHER_);
				}
			}
		}
	}

	/**
	 * This composition rule derives the disjointness axioms as a new kind of a
	 * super class expression. For each member, all disjointness axioms
	 * containing this member are registered with this rule (possibly several
	 * times if the member occurs several times in one axiom). If the rule
	 * produce some disjointness axiom in a context at least two times, this
	 * means that two different members of this disjointness axioms have been
	 * derived in the context. Therefore, a contradiction should be produced.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 * @author "Yevgeny Kazakov"
	 */
	private static class ThisNaryCompositionRule extends ContextRules {

		/**
		 * List of relevant disjointness axioms axioms in which the member, for
		 * which this rule is registered, appears. Note: this should allow for
		 * duplicate axioms in order to handle correctly situations when
		 * disjointness axioms contain multiple copies of the same element.
		 */
		private final List<IndexedDisjointnessAxiom> disjointnessAxioms_;

		public ThisNaryCompositionRule(ContextRules tail) {
			super(tail);
			disjointnessAxioms_ = new LinkedList<IndexedDisjointnessAxiom>();
		}

		@Override
		public void apply(RuleEngine ruleEngine, Context context) {
			for (IndexedDisjointnessAxiom disAxiom : disjointnessAxioms_)
				/*
				 * if the disjointness axiom has been already derived for this
				 * context, it must have been derived for a different member, so
				 * a contradiction should be produced
				 */
				if (!context.addDisjointnessAxiom(disAxiom))
					ruleEngine.produce(
							context,
							new PositiveSuperClassExpression(ruleEngine
									.getOwlNothing()));

		}

		protected boolean isEmpty() {
			return disjointnessAxioms_.isEmpty();
		}

		/**
		 * @param disjointnessAxiom
		 *            the axiom to be added to this rule
		 */
		protected void addDisjointnessAxiom(
				IndexedDisjointnessAxiom disjointnessAxiom) {
			disjointnessAxioms_.add(disjointnessAxiom);
		}

		/**
		 * @param disjointnessAxiom
		 *            the axiom to be removed from this rule
		 * @return {@code true} if successfully removed
		 */
		protected boolean removeDisjointnessAxiom(
				IndexedDisjointnessAxiom disjointnessAxiom) {
			return disjointnessAxioms_.remove(disjointnessAxiom);
		}

		private static Matcher<ContextRules, ThisNaryCompositionRule> MATCHER_ = new SimpleTypeBasedMatcher<ContextRules, ThisNaryCompositionRule>(
				ThisNaryCompositionRule.class);

		private static ReferenceFactory<ContextRules, ThisNaryCompositionRule> FACTORY_ = new ReferenceFactory<ContextRules, ThisNaryCompositionRule>() {
			@Override
			public ThisNaryCompositionRule create(ContextRules tail) {
				return new ThisNaryCompositionRule(tail);
			}
		};
	}

	/**
	 * This composition rule can produce only contradiction. Each rule for a
	 * member registers all other members with which this member occurs in a
	 * disjointness axioms. When the rule is applied, it is checked if the
	 * intersection of this set of "forbidden super classes" with the super
	 * classes derived in the context is non-empty, and if so, derives a
	 * contradiction.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 * @author "Yevgeny Kazakov"
	 */
	private static class ThisBinaryCompositionRule extends ContextRules {

		/**
		 * {@link IndexedClassExpression}s that appear with the member for which
		 * this rule is registered. The member itself is not included in this
		 * set, unless it appears in some disjointness axiom at least two times.
		 */
		private final Set<IndexedClassExpression> forbiddenSuperClasses_;

		public ThisBinaryCompositionRule(ContextRules tail) {
			super(tail);
			forbiddenSuperClasses_ = new ArrayHashSet<IndexedClassExpression>();
		}

		@Override
		public void apply(RuleEngine ruleEngine, Context context) {

			if (!new LazySetIntersection<IndexedClassExpression>(
					forbiddenSuperClasses_, context.getSuperClassExpressions())
					.isEmpty()) {

				ruleEngine.produce(context, new PositiveSuperClassExpression(
						ruleEngine.getOwlNothing()));
			}
		}

		protected boolean isEmpty() {
			return forbiddenSuperClasses_.isEmpty();
		}

		protected void addDisjointClass(IndexedClassExpression disjointClass) {
			forbiddenSuperClasses_.add(disjointClass);
		}

		/**
		 * @param disjointClass
		 * @return {@code true} if successfully removed
		 */
		protected boolean removeDisjointClass(
				IndexedClassExpression disjointClass) {
			return forbiddenSuperClasses_.remove(disjointClass);
		}

		private static Matcher<ContextRules, ThisBinaryCompositionRule> MATCHER_ = new SimpleTypeBasedMatcher<ContextRules, ThisBinaryCompositionRule>(
				ThisBinaryCompositionRule.class);

		private static ReferenceFactory<ContextRules, ThisBinaryCompositionRule> FACTORY_ = new ReferenceFactory<ContextRules, ThisBinaryCompositionRule>() {
			@Override
			public ThisBinaryCompositionRule create(ContextRules tail) {
				return new ThisBinaryCompositionRule(tail);
			}
		};
	}

}