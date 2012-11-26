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

import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
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
		for (IndexedClassExpression ice : members_) {
			indexUpdater.add(ice, new ThisCompositionRule(this));
		}
	}

	private void deregisterCompositionRule(IndexUpdater indexUpdater) {
		for (IndexedClassExpression ice : members_) {
			indexUpdater.remove(ice, new ThisCompositionRule(this));
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
	 * {@link ThisCompositionRule} derives the disjointness axioms as a new kind
	 * of a super class expression. For each member, all disjointness axioms
	 * containing this member are registered with this rule (possibly several
	 * times if the member occurs several times in one axiom). If the rule
	 * produces some disjointness axiom in a context at least two times, this
	 * means that two different members of this disjointness axioms have been
	 * derived in the context. Therefore, a contradiction should be produced.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 * @author "Yevgeny Kazakov"
	 */
	private static class ThisCompositionRule extends
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

		private ThisCompositionRule(ChainableRule<Context> tail) {
			super(tail);
			disjointnessAxioms_ = new LinkedList<IndexedDisjointnessAxiom>();
		}

		ThisCompositionRule(IndexedDisjointnessAxiom axiom) {
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
			ThisCompositionRule rule = ruleChain.getCreate(MATCHER_, FACTORY_);
			return rule.disjointnessAxioms_.addAll(this.disjointnessAxioms_);
		}

		@Override
		public boolean removeFrom(Chain<ChainableRule<Context>> ruleChain) {
			ThisCompositionRule rule = ruleChain.find(MATCHER_);
			boolean changed = false;
			if (rule != null) {
				changed = rule.disjointnessAxioms_
						.removeAll(this.disjointnessAxioms_);
				if (rule.isEmpty())
					ruleChain.remove(MATCHER_);
			}
			return changed;
		}

		private static Matcher<ChainableRule<Context>, ThisCompositionRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableRule<Context>, ThisCompositionRule>(
				ThisCompositionRule.class);

		private static ReferenceFactory<ChainableRule<Context>, ThisCompositionRule> FACTORY_ = new ReferenceFactory<ChainableRule<Context>, ThisCompositionRule>() {
			@Override
			public ThisCompositionRule create(ChainableRule<Context> tail) {
				return new ThisCompositionRule(tail);
			}
		};
	}

}
