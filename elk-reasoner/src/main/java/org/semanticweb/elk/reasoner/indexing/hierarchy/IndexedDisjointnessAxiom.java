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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;
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
	private final static int DISJOINT_AXIOM_BINARIZATION_THRESHOLD = 3;

	private final List<IndexedClassExpression> members_;

	IndexedDisjointnessAxiom(List<IndexedClassExpression> members) {
		members_ = members;
	}

	public List<IndexedClassExpression> getMembers() {
		return members_;
	}

	@Override
	protected void updateOccurrenceNumbers(final IndexUpdater indexUpdater,
			final int increment) {
		if (increment > 0) {
			registerCompositionRule(indexUpdater);
		} else {
			deregisterCompositionRule(indexUpdater);
		}
	}

	private void registerCompositionRule(IndexUpdater indexUpdater) {
		if (members_.size() > DISJOINT_AXIOM_BINARIZATION_THRESHOLD) {
			for (IndexedClassExpression ice : members_) {
				indexUpdater.add(ice, new ThisNaryCompositionRule(this));
			}
		} else {
			for (final IndexedClassExpression member : members_) {
				boolean selfFound = false; // true when otherMember = member
				for (IndexedClassExpression otherMember : members_) {
					if (!selfFound && otherMember == member) {
						selfFound = true;
						continue;
					}
					indexUpdater.add(member, new ThisBinaryCompositionRule(
							otherMember));
				}
			}
		}
	}

	private void deregisterCompositionRule(IndexUpdater indexUpdater) {
		if (members_.size() > DISJOINT_AXIOM_BINARIZATION_THRESHOLD) {
			for (IndexedClassExpression ice : members_) {
				indexUpdater.remove(ice, new ThisNaryCompositionRule(this));
			}
		} else {
			for (final IndexedClassExpression member : members_) {
				boolean selfFound = false; // true when otherMember = member
				for (IndexedClassExpression otherMember : members_) {
					if (!selfFound && otherMember == member) {
						selfFound = true;
						continue;
					}
					indexUpdater.remove(member, new ThisBinaryCompositionRule(
							otherMember));
				}
			}
		}
	}

	/*
	 * the following two methods are required because indexed axioms do not go
	 * through the index cache (only instances of IndexedClassExpression do)
	 */
	@Override
	public int hashCode() {
		return Arrays.hashCode(getMembers().toArray());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof IndexedDisjointnessAxiom) {
			IndexedDisjointnessAxiom axiom = (IndexedDisjointnessAxiom) obj;

			return getMembers().equals(axiom.getMembers());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "DisjointClasses(" + Arrays.toString(getMembers().toArray())
				+ ")";
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
	private static class ThisNaryCompositionRule extends
			ModifiableLinkImpl<ChainableRule<Context>> implements
			ChainableRule<Context> {

		/**
		 * List of relevant {@link IndexedDisjointnessAxiom}s in which the
		 * member, for which this rule is registered, appears. Note: this should
		 * allow for duplicate axioms in order to handle correctly situations
		 * when {@link IndexedDisjointnessAxiom}s contain multiple copies of the
		 * same element.
		 */
		private final List<IndexedDisjointnessAxiom> disjointnessAxioms_;

		private ThisNaryCompositionRule(ChainableRule<Context> tail) {
			super(tail);
			disjointnessAxioms_ = new LinkedList<IndexedDisjointnessAxiom>();
		}

		ThisNaryCompositionRule(IndexedDisjointnessAxiom axiom) {
			this((ChainableRule<Context>) null);
			disjointnessAxioms_.add(axiom);
		}

		@Override
		public void apply(SaturationState.Writer writer, Context context) {
			for (IndexedDisjointnessAxiom disAxiom : disjointnessAxioms_)
				writer.produce(context, new DisjointnessAxiom(disAxiom));
		}

		protected boolean isEmpty() {
			return disjointnessAxioms_.isEmpty();
		}

		@Override
		public boolean addTo(Chain<ChainableRule<Context>> ruleChain) {
			ThisNaryCompositionRule rule = ruleChain.getCreate(MATCHER_,
					FACTORY_);
			return rule.disjointnessAxioms_.addAll(this.disjointnessAxioms_);
		}

		@Override
		public boolean removeFrom(Chain<ChainableRule<Context>> ruleChain) {
			ThisNaryCompositionRule rule = ruleChain.find(MATCHER_);
			boolean changed = false;
			if (rule != null) {
				changed = rule.disjointnessAxioms_
						.removeAll(this.disjointnessAxioms_);
				if (rule.isEmpty())
					ruleChain.remove(MATCHER_);
			}
			return changed;
		}

		private static Matcher<ChainableRule<Context>, ThisNaryCompositionRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableRule<Context>, ThisNaryCompositionRule>(
				ThisNaryCompositionRule.class);

		private static ReferenceFactory<ChainableRule<Context>, ThisNaryCompositionRule> FACTORY_ = new ReferenceFactory<ChainableRule<Context>, ThisNaryCompositionRule>() {
			@Override
			public ThisNaryCompositionRule create(ChainableRule<Context> tail) {
				return new ThisNaryCompositionRule(tail);
			}
		};
	}

	/**
	 * This composition rule can produce only {@link Contradiction}. Each rule
	 * for a member registers all other members with which this member occurs in
	 * an {@link IndexedDisjointnessAxiom}. When the rule is applied, it is
	 * checked if the intersection of this set of "forbidden subsumers" with the
	 * subsumers derived in the context is non-empty, and if so, derives a
	 * {@link Contradiction}.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 * @author "Yevgeny Kazakov"
	 */
	private static class ThisBinaryCompositionRule extends
			ModifiableLinkImpl<ChainableRule<Context>> implements
			ChainableRule<Context> {

		/**
		 * {@link IndexedClassExpression}s that appear with the member for which
		 * this rule is registered. The member itself is not included in this
		 * set, unless it appears in some disjointness axiom at least two times.
		 */
		private final Set<IndexedClassExpression> forbiddenSubsumers_;

		public ThisBinaryCompositionRule(ChainableRule<Context> tail) {
			super(tail);
			this.forbiddenSubsumers_ = new ArrayHashSet<IndexedClassExpression>();
		}

		ThisBinaryCompositionRule(IndexedClassExpression forbiddenSuperClass) {
			this((ChainableRule<Context>) null);
			this.forbiddenSubsumers_.add(forbiddenSuperClass);
		}

		@Override
		public void apply(SaturationState.Writer writer, Context context) {
			if (!new LazySetIntersection<IndexedClassExpression>(
					forbiddenSubsumers_, context.getSubsumers()).isEmpty()) {
				writer.produce(context, new Contradiction());
			}
		}

		protected boolean isEmpty() {
			return forbiddenSubsumers_.isEmpty();
		}

		@Override
		public boolean addTo(Chain<ChainableRule<Context>> ruleChain) {
			ThisBinaryCompositionRule rule = ruleChain.getCreate(MATCHER_,
					FACTORY_);
			return rule.forbiddenSubsumers_.addAll(this.forbiddenSubsumers_);
		}

		@Override
		public boolean removeFrom(Chain<ChainableRule<Context>> ruleChain) {
			ThisBinaryCompositionRule rule = ruleChain.find(MATCHER_);
			boolean changed = false;
			if (rule != null) {
				changed = rule.forbiddenSubsumers_
						.removeAll(this.forbiddenSubsumers_);
				if (rule.isEmpty())
					ruleChain.remove(MATCHER_);
			}
			return changed;
		}

		private static Matcher<ChainableRule<Context>, ThisBinaryCompositionRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableRule<Context>, ThisBinaryCompositionRule>(
				ThisBinaryCompositionRule.class);

		private static ReferenceFactory<ChainableRule<Context>, ThisBinaryCompositionRule> FACTORY_ = new ReferenceFactory<ChainableRule<Context>, ThisBinaryCompositionRule>() {
			@Override
			public ThisBinaryCompositionRule create(ChainableRule<Context> tail) {
				return new ThisBinaryCompositionRule(tail);
			}
		};
	}

}
