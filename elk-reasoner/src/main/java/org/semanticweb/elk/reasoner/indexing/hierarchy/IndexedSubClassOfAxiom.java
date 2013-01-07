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
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomVisitor;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationVisitor;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

public class IndexedSubClassOfAxiom extends IndexedAxiom {

	private static final Logger LOGGER_ = Logger
			.getLogger(IndexedSubClassOfAxiom.class);

	private final IndexedClassExpression subClass_, superClass_;

	protected IndexedSubClassOfAxiom(IndexedClassExpression subClass,
			IndexedClassExpression superClass) {
		this.subClass_ = subClass;
		this.superClass_ = superClass;
	}

	public IndexedClassExpression getSubClass() {
		return this.subClass_;
	}

	public IndexedClassExpression getSuperClass() {
		return this.superClass_;
	}

	@Override
	public boolean occurs() {
		// we do not cache sub class axioms
		// TODO: introduce a method for testing if we cache an object in the
		// index
		return false;
	}

	@Override
	public String toStringStructural() {
		return "SubClassOf(" + this.subClass_ + ' ' + this.superClass_ + ')';
	}

	@Override
	public <O> O accept(IndexedAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	protected void updateOccurrenceNumbers(final IndexUpdater indexUpdater,
			final int increment) {
		if (increment > 0) {
			indexUpdater.add(subClass_, new ThisCompositionRule(superClass_));
		} else {
			indexUpdater
					.remove(subClass_, new ThisCompositionRule(superClass_));
		}
	}

	/**
	 * 
	 */
	public static class ThisCompositionRule extends
			ModifiableLinkImpl<ChainableRule<Context>> implements
			ChainableRule<Context> {

		private static final String NAME = "SubClassOf Expansion";

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

		@Override
		public String getName() {
			return NAME;
		}

		@Override
		public void apply(SaturationState.Writer writer, Context context) {
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("Applying " + NAME + " to " + context);
			}
			for (IndexedClassExpression implied : toldSuperClassExpressions_) {
				writer.produce(context, new PositiveSubsumer(implied));
			}
		}

		@Override
		public boolean addTo(Chain<ChainableRule<Context>> ruleChain) {
			ThisCompositionRule rule = ruleChain.getCreate(
					ThisCompositionRule.MATCHER_, ThisCompositionRule.FACTORY_);
			boolean changed = false;

			for (IndexedClassExpression ice : toldSuperClassExpressions_) {
				if (LOGGER_.isTraceEnabled())
					LOGGER_.trace("Adding " + ice.toString() + " to " + NAME);
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
					if (LOGGER_.isTraceEnabled())
						LOGGER_.trace("Removing " + ice.toString() + " from "
								+ NAME);
					changed |= rule.removeToldSuperClassExpression(ice);
				}

				if (rule.isEmpty()) {
					ruleChain.remove(ThisCompositionRule.MATCHER_);

					if (LOGGER_.isTraceEnabled()) {
						LOGGER_.trace(NAME + ": removed");
					}

					return true;
				}
			}

			return changed;

		}

		@Override
		public void accept(RuleApplicationVisitor visitor,
				SaturationState.Writer writer, Context context) {
			visitor.visit(this, writer, context);
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