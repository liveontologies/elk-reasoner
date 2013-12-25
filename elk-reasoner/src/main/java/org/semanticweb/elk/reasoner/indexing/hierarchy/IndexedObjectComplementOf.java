package org.semanticweb.elk.reasoner.indexing.hierarchy;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectComplementOfVisitor;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.SubsumerDecompositionVisitor;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents all occurrences of an {@link ElkObjectComplementOf} in an
 * ontology.
 * 
 * @author "Yevgeny Kazakov"
 */
public class IndexedObjectComplementOf extends IndexedClassExpression {

	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(IndexedObjectComplementOf.class);

	private final IndexedClassExpression negated_;

	protected IndexedObjectComplementOf(IndexedClassExpression negated) {
		this.negated_ = negated;
	}

	public IndexedClassExpression getNegated() {
		return negated_;
	}

	public <O> O accept(IndexedObjectComplementOfVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedObjectComplementOfVisitor<O>) visitor);
	}

	@Override
	void updateOccurrenceNumbers(ModifiableOntologyIndex index, int increment,
			int positiveIncrement, int negativeIncrement) {
		if (positiveOccurrenceNo == 0 && positiveIncrement > 0) {
			// first positive occurrence of this expression
			index.add(negated_, new ContradictionCompositionRule(this));
		}

		if (negativeOccurrenceNo == 0 && negativeIncrement > 0) {
			// first negative occurrence of this expression
			if (LOGGER_.isWarnEnabled()) {
				LoggerWrap
						.log(LOGGER_,
								LogLevel.WARN,
								"reasoner.indexing.IndexedObjectComplementOf",
								"ELK does not support negative occurrences of ObjectComplementOf. Reasoning might be incomplete!");
			}
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		checkOccurrenceNumbers();

		if (positiveOccurrenceNo == 0 && positiveIncrement < 0) {
			// no positive occurrences of this expression left
			index.remove(negated_, new ContradictionCompositionRule(this));
		}
	}

	@Override
	public void accept(SubsumerDecompositionVisitor visitor, Context context) {
		visitor.visit(this, context);
	}

	@Override
	public String toStringStructural() {
		return "ObjectComplementOf(" + this.negated_ + ')';
	}

	/**
	 * The composition rule producing {@link Contradiction} when processing the
	 * negated {@link IndexedClassExpression} of an
	 * {@link IndexedObjectComplementOf} if this
	 * {@link IndexedObjectComplementOf} is contained in the {@code Context}.
	 * 
	 * @author "Yevgeny Kazakov"
	 */
	public static class ContradictionCompositionRule extends
			ModifiableLinkImpl<ChainableRule<IndexedClassExpression>> implements
			ChainableRule<IndexedClassExpression> {

		private static final String NAME_ = "ObjectComplementOf Clash";

		private IndexedClassExpression negation_;

		private ContradictionCompositionRule(
				ChainableRule<IndexedClassExpression> tail) {
			super(tail);

		}

		ContradictionCompositionRule(IndexedClassExpression complement) {
			this((ChainableRule<IndexedClassExpression>) null);
			this.negation_ = complement;
		}

		@Override
		public String getName() {
			return NAME_;
		}

		// TODO: hide this method
		public IndexedClassExpression getNegation() {
			return negation_;
		}

		@Override
		public void apply(IndexedClassExpression premise, Context context,
				SaturationStateWriter writer) {
			LOGGER_.trace("Applying {} to {}", NAME_, context);

			if (negation_ != null && context.getSubsumers().contains(negation_))
				writer.produce(context, Contradiction.getInstance());
		}

		@Override
		public boolean addTo(
				Chain<ChainableRule<IndexedClassExpression>> ruleChain) {
			ContradictionCompositionRule rule = ruleChain.getCreate(MATCHER_,
					FACTORY_);
			boolean changed = false;

			if (negation_ != null && rule.negation_ != negation_) {
				if (rule.negation_ == null)
					rule.negation_ = negation_;
				else
					throw new ElkUnexpectedIndexingException(getName()
							+ " complement value " + rule.negation_
							+ " cannot be overwritten with " + negation_);
				changed = true;
			}

			return changed;

		}

		@Override
		public boolean removeFrom(
				Chain<ChainableRule<IndexedClassExpression>> ruleChain) {
			ContradictionCompositionRule rule = ruleChain.find(MATCHER_);
			boolean changed = false;

			if (rule != null) {
				if (negation_ != null && rule.negation_ == negation_) {
					rule.negation_ = null;
					changed = true;
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

		/**
		 * @return {@code true} if this rule never does anything
		 */
		private boolean isEmpty() {
			return negation_ == null;
		}

		private static final Matcher<ChainableRule<IndexedClassExpression>, ContradictionCompositionRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableRule<IndexedClassExpression>, ContradictionCompositionRule>(
				ContradictionCompositionRule.class);

		private static final ReferenceFactory<ChainableRule<IndexedClassExpression>, ContradictionCompositionRule> FACTORY_ = new ReferenceFactory<ChainableRule<IndexedClassExpression>, ContradictionCompositionRule>() {
			@Override
			public ContradictionCompositionRule create(
					ChainableRule<IndexedClassExpression> tail) {
				return new ContradictionCompositionRule(tail);
			}
		};

	}

}
