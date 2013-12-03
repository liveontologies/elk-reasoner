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

import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomVisitor;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.ContradictionImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointnessAxiomImpl;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines the disjointness inference rule for indexed class expressions
 * 
 * @author Frantisek Simancik
 * @author Pavel Klinov
 * @author "Yevgeny Kazakov"
 * 
 */
public class IndexedDisjointnessAxiom extends IndexedAxiom {

	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(IndexedDisjointnessAxiom.class);

	/**
	 * {@link IndexedClassExpression}s that have at least two equal occurrences
	 * (according to the {@link Object#equals(Object)} method) in this
	 * {@link IndexedDisjointnessAxiom}
	 */
	private final Set<IndexedClassExpression> inconsistentMembers_;
	/**
	 * {@link IndexedClassExpression}s that occur exactly once in this
	 * {@link IndexedDisjointnessAxiom}
	 */
	private final Set<IndexedClassExpression> disjointMembers_;

	/**
	 * This counts how often this {@link IndexedDisjointnessAxiom} occurrs in
	 * the ontology.
	 */
	int occurrenceNo = 0;

	IndexedDisjointnessAxiom(List<IndexedClassExpression> members) {
		this.inconsistentMembers_ = new ArrayHashSet<IndexedClassExpression>(1);
		this.disjointMembers_ = new ArrayHashSet<IndexedClassExpression>(2);
		for (IndexedClassExpression member : members) {
			if (inconsistentMembers_.contains(member))
				continue;
			if (!disjointMembers_.add(member)) {
				disjointMembers_.remove(member);
				inconsistentMembers_.add(member);
			}
		}
	}

	/**
	 * @return {@link IndexedClassExpression}s that have at least two equal
	 *         occurrences (according to the {@link Object#equals(Object)}
	 *         method) in this {@link IndexedDisjointnessAxiom}
	 */
	public Set<IndexedClassExpression> getInconsistentMembers() {
		return inconsistentMembers_;
	}

	/**
	 * {@link IndexedClassExpression}s that occur exactly once in this
	 * {@link IndexedDisjointnessAxiom}
	 */
	public Set<IndexedClassExpression> getDisjointMembers() {
		return disjointMembers_;
	}

	@Override
	public boolean occurs() {
		return occurrenceNo > 0;
	}

	@Override
	protected void updateOccurrenceNumbers(final ModifiableOntologyIndex index,
			final int increment) {

		if (occurrenceNo == 0 && increment > 0) {
			// first occurrence of this axiom
			registerCompositionRule(index);
		}

		occurrenceNo += increment;

		if (occurrenceNo == 0 && increment < 0) {
			// last occurrence of this axiom
			deregisterCompositionRule(index);
		}
	}

	@Override
	public <O> O accept(IndexedAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public String toStringStructural() {
		List<IndexedClassExpression> members = new LinkedList<IndexedClassExpression>();
		for (IndexedClassExpression inconsistentMember : inconsistentMembers_) {
			// each inconsistent member is added two times
			members.add(inconsistentMember);
			members.add(inconsistentMember);
		}
		members.addAll(disjointMembers_);
		return "DisjointClasses(" + members + ")";
	}

	private void registerCompositionRule(ModifiableOntologyIndex index) {
		for (IndexedClassExpression ice : inconsistentMembers_)
			index.add(ice, new ThisContradictionRule());
		for (IndexedClassExpression ice : disjointMembers_) {
			index.add(ice, new ThisCompositionRule(this));
		}
	}

	private void deregisterCompositionRule(ModifiableOntologyIndex index) {
		for (IndexedClassExpression ice : inconsistentMembers_)
			index.remove(ice, new ThisContradictionRule());
		for (IndexedClassExpression ice : disjointMembers_) {
			index.remove(ice, new ThisCompositionRule(this));
		}
	}

	/**
	 * {@link ThisCompositionRule} derives the disjointness axioms as a new kind
	 * of element. For each subsumer, all disjointness axioms containing this
	 * subsumer are registered using this rule.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 * @author "Yevgeny Kazakov"
	 */
	public static class ThisCompositionRule extends
			ModifiableLinkImpl<ChainableRule<Conclusion, Context>> implements
			ChainableRule<Conclusion, Context> {

		public static final String NAME = "DisjointClasses Introduction";

		/**
		 * Set of relevant {@link IndexedDisjointnessAxiom}s in which the
		 * member, for which this rule is registered, appears.
		 */
		private final Set<IndexedDisjointnessAxiom> disjointnessAxioms_;

		private ThisCompositionRule(ChainableRule<Conclusion, Context> tail) {
			super(tail);
			disjointnessAxioms_ = new ArrayHashSet<IndexedDisjointnessAxiom>();
		}

		ThisCompositionRule(IndexedDisjointnessAxiom axiom) {
			this((ChainableRule<Conclusion, Context>) null);
			disjointnessAxioms_.add(axiom);
		}

		// TODO: hide this method
		public Set<IndexedDisjointnessAxiom> getDisjointnessAxioms() {
			return disjointnessAxioms_;
		}

		@Override
		public String getName() {
			return NAME;
		}

		@Override
		public void accept(CompositionRuleApplicationVisitor visitor,
				BasicSaturationStateWriter writer, Conclusion subsumer, Context context) {
			visitor.visit(this, writer, subsumer, context);
		}

		@Override
		public void apply(BasicSaturationStateWriter writer, Conclusion subsumer, Context context) {
			LOGGER_.trace("Applying {} to {}", NAME, context);
			
			for (IndexedDisjointnessAxiom disAxiom : disjointnessAxioms_)
				//TODO use conclusion factory
				writer.produce(context, new DisjointnessAxiomImpl(disAxiom));
		}

		protected boolean isEmpty() {
			return disjointnessAxioms_.isEmpty();
		}

		@Override
		public boolean addTo(Chain<ChainableRule<Conclusion, Context>> ruleChain) {
			ThisCompositionRule rule = ruleChain.getCreate(MATCHER_, FACTORY_);
			return rule.disjointnessAxioms_.addAll(this.disjointnessAxioms_);
		}

		@Override
		public boolean removeFrom(Chain<ChainableRule<Conclusion, Context>> ruleChain) {
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

		private static Matcher<ChainableRule<Conclusion, Context>, ThisCompositionRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableRule<Conclusion, Context>, ThisCompositionRule>(
				ThisCompositionRule.class);

		private static ReferenceFactory<ChainableRule<Conclusion, Context>, ThisCompositionRule> FACTORY_ = new ReferenceFactory<ChainableRule<Conclusion, Context>, ThisCompositionRule>() {
			@Override
			public ThisCompositionRule create(ChainableRule<Conclusion, Context> tail) {
				return new ThisCompositionRule(tail);
			}
		};
	}

	/**
	 * A rule which derives a {@link Contradiction} for inconsistent members of
	 * this {@link IndexedDisjointnessAxiom}.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	public static class ThisContradictionRule extends
			ModifiableLinkImpl<ChainableRule<Conclusion, Context>> implements
			ChainableRule<Conclusion, Context> {

		public static final String NAME = "DisjointClasses Contradiction Introduction";

		/**
		 * The number of {@link IndexedDisjointnessAxiom}s in which the
		 * {@link IndexedClassExpression}, for which this rule is registered,
		 * occurs more than once.
		 */
		private int contradictionCounter_;

		public ThisContradictionRule(ChainableRule<Conclusion, Context> tail) {
			super(tail);
			this.contradictionCounter_ = 0;
		}

		ThisContradictionRule() {
			this((ChainableRule<Conclusion, Context>) null);
			this.contradictionCounter_++;
		}

		@Override
		public String getName() {
			return NAME;
		}

		@Override
		public void apply(BasicSaturationStateWriter writer, Conclusion premise, Context context) {
			LOGGER_.trace("Applying {} to {}", NAME, context);
			//TODO use conclusion factory
			writer.produce(context, ContradictionImpl.getInstance());
		}

		@Override
		public boolean addTo(Chain<ChainableRule<Conclusion, Context>> ruleChain) {
			ThisContradictionRule rule = ruleChain
					.getCreate(MATCHER_, FACTORY_);
			rule.contradictionCounter_ += this.contradictionCounter_;
			return this.contradictionCounter_ != 0;
		}

		@Override
		public boolean removeFrom(Chain<ChainableRule<Conclusion, Context>> ruleChain) {
			ThisContradictionRule rule = ruleChain.find(MATCHER_);
			if (rule == null) {
				return false;
			}
			rule.contradictionCounter_ -= this.contradictionCounter_;
			if (rule.isEmpty())
				ruleChain.remove(MATCHER_);
			return this.contradictionCounter_ != 0;
		}

		@Override
		public void accept(CompositionRuleApplicationVisitor visitor,
				BasicSaturationStateWriter writer, Conclusion premise, Context context) {
			visitor.visit(this, writer, context);
		}

		protected boolean isEmpty() {
			return this.contradictionCounter_ == 0;
		}

		private static Matcher<ChainableRule<Conclusion, Context>, ThisContradictionRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableRule<Conclusion, Context>, ThisContradictionRule>(
				ThisContradictionRule.class);

		private static ReferenceFactory<ChainableRule<Conclusion, Context>, ThisContradictionRule> FACTORY_ = new ReferenceFactory<ChainableRule<Conclusion, Context>, ThisContradictionRule>() {
			@Override
			public ThisContradictionRule create(ChainableRule<Conclusion, Context> tail) {
				return new ThisContradictionRule(tail);
			}
		};

	}

}
