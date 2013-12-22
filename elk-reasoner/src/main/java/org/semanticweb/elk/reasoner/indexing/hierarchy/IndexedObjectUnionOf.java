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

import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectUnionOfVisitor;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.SubsumerDecompositionVisitor;
import org.semanticweb.elk.util.collections.ArrayHashSet;
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
 * Represents all occurrences of an {@link ElkObjectUnionOf} in an ontology.
 * 
 * @author "Yevgeny Kazakov"
 */
public class IndexedObjectUnionOf extends IndexedClassExpression {

	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(IndexedObjectUnionOf.class);

	private final Set<IndexedClassExpression> disjuncts_;

	IndexedObjectUnionOf(List<IndexedClassExpression> disjuncts) {
		this.disjuncts_ = new ArrayHashSet<IndexedClassExpression>(2);
		for (IndexedClassExpression disjunct : disjuncts) {
			disjuncts_.add(disjunct);
		}
	}

	public Set<IndexedClassExpression> getDisjuncts() {
		return disjuncts_;
	}

	public <O> O accept(IndexedObjectUnionOfVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedObjectUnionOfVisitor<O>) visitor);
	}

	@Override
	void updateOccurrenceNumbers(ModifiableOntologyIndex index, int increment,
			int positiveIncrement, int negativeIncrement) {

		if (negativeOccurrenceNo == 0 && negativeIncrement > 0) {
			// first negative occurrence of this expression
			for (IndexedClassExpression disjunct : disjuncts_)
				index.add(disjunct, new ThisCompositionRule(this));
		}

		if (positiveOccurrenceNo == 0 && positiveIncrement > 0) {
			// first positive occurrence of this expression
			if (LOGGER_.isWarnEnabled()) {
				LoggerWrap
						.log(LOGGER_,
								LogLevel.WARN,
								"reasoner.indexing.IndexedObjectUnionOf",
								"ELK does not support positive occurrences of ObjectUnionOf. Reasoning might be incomplete!");
			}
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		checkOccurrenceNumbers();

		if (negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			// no negative occurrences of this expression left
			for (IndexedClassExpression disjunct : disjuncts_)
				index.remove(disjunct, new ThisCompositionRule(this));
		}
	}

	@Override
	public void accept(SubsumerDecompositionVisitor visitor, Context context) {
		visitor.visit(this, context);
	}

	@Override
	public String toStringStructural() {
		return "ObjectUnionOf(" + disjuncts_ + ')';
	}

	/**
	 * 
	 */
	public static class ThisCompositionRule extends
			ModifiableLinkImpl<ChainableRule<Context>> implements
			ChainableRule<Context> {

		private static final String NAME_ = "ObjectUnionOf Introduction";

		/**
		 * All disjunctions containing the disjunct for which this rule is
		 * registered
		 */
		private final Set<IndexedClassExpression> disjunctions_;

		private ThisCompositionRule(ChainableRule<Context> tail) {
			super(tail);
			disjunctions_ = new ArrayHashSet<IndexedClassExpression>();

		}

		ThisCompositionRule(IndexedClassExpression disjunction) {
			this((ChainableRule<Context>) null);
			this.disjunctions_.add(disjunction);
		}

		@Override
		public String getName() {
			return NAME_;
		}

		@Override
		public void accept(RuleApplicationVisitor visitor,
				SaturationStateWriter writer, Context context) {
			visitor.visit(this, writer, context);
		}

		// TODO: hide this method
		public Set<IndexedClassExpression> getDisjunctions() {
			return disjunctions_;
		}

		@Override
		public void apply(SaturationStateWriter writer, Context context) {
			LOGGER_.trace("Applying {} to {}", NAME_, context);

			for (IndexedClassExpression disjunction : disjunctions_)
				writer.produce(context, new ComposedSubsumer(disjunction));
		}

		@Override
		public boolean addTo(Chain<ChainableRule<Context>> ruleChain) {
			ThisCompositionRule rule = ruleChain.getCreate(MATCHER_, FACTORY_);
			return rule.disjunctions_.addAll(this.disjunctions_);
		}

		@Override
		public boolean removeFrom(Chain<ChainableRule<Context>> ruleChain) {
			ThisCompositionRule rule = ruleChain.find(MATCHER_);
			boolean changed = false;
			if (rule != null) {
				changed = rule.disjunctions_.removeAll(this.disjunctions_);
				if (rule.isEmpty())
					ruleChain.remove(MATCHER_);
			}
			return changed;
		}

		/**
		 * @return {@code true} if this rule never does anything
		 */
		private boolean isEmpty() {
			return disjunctions_.isEmpty();
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
