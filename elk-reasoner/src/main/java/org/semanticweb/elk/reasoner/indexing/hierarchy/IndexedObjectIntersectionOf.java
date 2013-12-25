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

import org.semanticweb.elk.owl.exceptions.ElkRuntimeException;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectIntersectionOfVisitor;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.SubsumerDecompositionVisitor;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents all occurrences of an {@link ElkObjectIntersectionOf} in an
 * ontology.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class IndexedObjectIntersectionOf extends IndexedClassExpression {

	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(IndexedObjectIntersectionOf.class);

	/**
	 * The conjunction has only two conjuncts. To ensure uniqueness of a
	 * conjunction for the conjuncts, the conjuncts are sorted according to the
	 * comparator of {@link IndexedClassExpression}. This is required for
	 * correct construction of {@link ThisCompositionRule} because conjunctions
	 * (A & B) and (B & A) result in the same rules.
	 */
	private final IndexedClassExpression firstConjunct_, secondConjunct_;

	protected IndexedObjectIntersectionOf(IndexedClassExpression conjunctA,
			IndexedClassExpression conjunctB) {

		if (conjunctA.compareTo(conjunctB) < 0) {
			this.firstConjunct_ = conjunctA;
			this.secondConjunct_ = conjunctB;
		} else {
			this.firstConjunct_ = conjunctB;
			this.secondConjunct_ = conjunctA;
		}
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
	protected void updateOccurrenceNumbers(ModifiableOntologyIndex index,
			int increment, int positiveIncrement, int negativeIncrement) {

		if (negativeOccurrenceNo == 0 && negativeIncrement > 0) {
			// first negative occurrence of this expression
			index.add(firstConjunct_, new ThisCompositionRule(secondConjunct_,
					this));
			// if both conjuncts are the same, do not index the second time
			if (!secondConjunct_.equals(firstConjunct_))
				index.add(secondConjunct_, new ThisCompositionRule(
						firstConjunct_, this));
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		checkOccurrenceNumbers();

		if (negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			// no negative occurrences of this conjunction left
			index.remove(firstConjunct_, new ThisCompositionRule(
					secondConjunct_, this));
			// if both conjuncts are the same, do not de-index the second time
			if (!secondConjunct_.equals(firstConjunct_))
				index.remove(secondConjunct_, new ThisCompositionRule(
						firstConjunct_, this));
		}

	}

	@Override
	public String toStringStructural() {
		return "ObjectIntersectionOf(" + this.firstConjunct_ + ' '
				+ this.secondConjunct_ + ')';
	}

	@Override
	public void accept(SubsumerDecompositionVisitor visitor, Context context) {
		visitor.visit(this, context);
	}

	/**
	 * The composition rule producing {@link Subsumer} for an
	 * {@link IndexedObjectIntersectionOf} when processing one of its conjunct
	 * {@link IndexedClassExpression} and when the other conjunct is contained
	 * in the {@link Context}
	 * 
	 * @author "Yevgeny Kazakov"
	 */
	public static class ThisCompositionRule extends
			ModifiableLinkImpl<ChainableRule<IndexedClassExpression>> implements
			ChainableRule<IndexedClassExpression> {

		private static final String NAME_ = "ObjectIntersectionOf Introduction";

		private final Map<IndexedClassExpression, IndexedObjectIntersectionOf> conjunctionsByConjunct_;

		private ThisCompositionRule(ChainableRule<IndexedClassExpression> tail) {
			super(tail);
			this.conjunctionsByConjunct_ = new ArrayHashMap<IndexedClassExpression, IndexedObjectIntersectionOf>(
					4);
		}

		ThisCompositionRule(IndexedClassExpression conjunct,
				IndexedObjectIntersectionOf conjunction) {
			this(null);
			this.conjunctionsByConjunct_.put(conjunct, conjunction);
		}

		@Override
		public String getName() {
			return NAME_;
		}

		// TODO: hide this method
		public Map<IndexedClassExpression, IndexedObjectIntersectionOf> getConjunctionsByConjunct() {
			return conjunctionsByConjunct_;
		}

		@Override
		public void apply(IndexedClassExpression premise, Context context,
				SaturationStateWriter writer) {
			LOGGER_.trace("Applying {} to {}", NAME_, context);

			for (IndexedClassExpression common : new LazySetIntersection<IndexedClassExpression>(
					conjunctionsByConjunct_.keySet(), context.getSubsumers()))
				writer.produce(context, new ComposedSubsumer(
						conjunctionsByConjunct_.get(common)));

		}

		@Override
		public boolean addTo(
				Chain<ChainableRule<IndexedClassExpression>> ruleChain) {
			ThisCompositionRule rule = ruleChain.getCreate(MATCHER_, FACTORY_);
			boolean changed = false;

			for (Map.Entry<IndexedClassExpression, IndexedObjectIntersectionOf> entry : conjunctionsByConjunct_
					.entrySet()) {
				changed |= rule.addConjunctionByConjunct(entry.getValue(),
						entry.getKey());
			}

			return changed;

		}

		@Override
		public boolean removeFrom(
				Chain<ChainableRule<IndexedClassExpression>> ruleChain) {
			ThisCompositionRule rule = ruleChain.find(MATCHER_);
			boolean changed = false;

			if (rule != null) {
				for (IndexedClassExpression conjunct : conjunctionsByConjunct_
						.keySet()) {
					changed |= rule.removeConjunctionByConjunct(conjunct);
				}

				if (rule.isEmpty()) {
					ruleChain.remove(MATCHER_);
				}
			}

			return changed;

		}

		@Override
		public void accept(CompositionRuleVisitor visitor,
				IndexedClassExpression premise, Context context,
				SaturationStateWriter writer) {
			visitor.visit(this, premise, context, writer);
		}

		private boolean addConjunctionByConjunct(
				IndexedObjectIntersectionOf conjunction,
				IndexedClassExpression conjunct) {
			Object previous = conjunctionsByConjunct_
					.put(conjunct, conjunction);

			if (previous == null)
				return true;

			throw new ElkRuntimeException("Conjunction " + conjunction
					+ "is already indexed: " + previous);
		}

		private boolean removeConjunctionByConjunct(
				IndexedClassExpression conjunct) {
			return conjunctionsByConjunct_.remove(conjunct) != null;
		}

		/**
		 * @return {@code true} if this rule never does anything
		 */
		private boolean isEmpty() {
			return conjunctionsByConjunct_.isEmpty();
		}

		private static final Matcher<ChainableRule<IndexedClassExpression>, ThisCompositionRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableRule<IndexedClassExpression>, ThisCompositionRule>(
				ThisCompositionRule.class);

		private static final ReferenceFactory<ChainableRule<IndexedClassExpression>, ThisCompositionRule> FACTORY_ = new ReferenceFactory<ChainableRule<IndexedClassExpression>, ThisCompositionRule>() {
			@Override
			public ThisCompositionRule create(
					ChainableRule<IndexedClassExpression> tail) {
				return new ThisCompositionRule(tail);
			}
		};

	}
}
