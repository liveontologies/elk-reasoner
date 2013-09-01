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
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.DecompositionRuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationVisitor;
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
			index.add(negated_, new ThisCompositionRule(this));
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
			index.remove(negated_, new ThisCompositionRule(this));
		}
	}

	@Override
	public void accept(DecompositionRuleApplicationVisitor visitor,
			Context context) {
		visitor.visit(this, context);
	}

	@Override
	public String toStringStructural() {
		return "ObjectComplementOf(" + this.negated_ + ')';
	}

	/**
	 * 
	 */
	public static class ThisCompositionRule extends
			ModifiableLinkImpl<ChainableRule<Context>> implements
			ChainableRule<Context> {

		private static final String NAME = "ObjectComplementOf Clash";

		private IndexedClassExpression negation_;

		private ThisCompositionRule(ChainableRule<Context> tail) {
			super(tail);

		}

		ThisCompositionRule(IndexedClassExpression complement) {
			this((ChainableRule<Context>) null);
			this.negation_ = complement;
		}

		@Override
		public String getName() {
			return NAME;
		}

		// TODO: hide this method
		public IndexedClassExpression getNegation() {
			return negation_;
		}

		@Override
		public void apply(BasicSaturationStateWriter writer, Context context) {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("Applying " + NAME + " to " + context);
			}
			if (negation_ != null && context.getSubsumers().contains(negation_))
				writer.produce(context, Contradiction.getInstance());
		}

		@Override
		public boolean addTo(Chain<ChainableRule<Context>> ruleChain) {
			ThisCompositionRule rule = ruleChain.getCreate(MATCHER_, FACTORY_);
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
		public boolean removeFrom(Chain<ChainableRule<Context>> ruleChain) {
			ThisCompositionRule rule = ruleChain.find(MATCHER_);
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
		public void accept(RuleApplicationVisitor visitor,
				BasicSaturationStateWriter writer, Context context) {
			visitor.visit(this, writer, context);
		}

		/**
		 * @return {@code true} if this rule never does anything
		 */
		private boolean isEmpty() {
			return negation_ == null;
		}

		private static final Matcher<ChainableRule<Context>, ThisCompositionRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableRule<Context>, ThisCompositionRule>(
				ThisCompositionRule.class);

		private static final ReferenceFactory<ChainableRule<Context>, ThisCompositionRule> FACTORY_ = new ReferenceFactory<ChainableRule<Context>, ThisCompositionRule>() {
			@Override
			public ThisCompositionRule create(ChainableRule<Context> tail) {
				return new ThisCompositionRule(tail);
			}
		};

	}

}
