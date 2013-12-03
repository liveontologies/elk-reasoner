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
import java.util.Collection;
import java.util.List;

import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomVisitor;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleApplicationVisitor;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexedSubClassOfAxiom extends IndexedAxiom {

	private static final Logger LOGGER_ = LoggerFactory
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
	protected void updateOccurrenceNumbers(final ModifiableOntologyIndex index,
			final int increment) {
		if (increment > 0) {
			index.add(subClass_, new ThisCompositionRule(superClass_));
		} else {
			index.remove(subClass_, new ThisCompositionRule(superClass_));
		}
	}

	/**
	 * 
	 */
	public static class ThisCompositionRule extends
			ModifiableLinkImpl<ChainableRule<Conclusion, Context>> implements
			ChainableRule<Conclusion, Context> {

		private static final String NAME = "SubClassOf Expansion";

		/**
		 * Correctness of axioms deletions requires that
		 * toldSuperClassExpressions is a List.
		 */
		private final List<IndexedClassExpression> toldSuperClassExpressions_;

		ThisCompositionRule(ChainableRule<Conclusion, Context> tail) {
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

		// TODO: hide this method
		public Collection<IndexedClassExpression> getToldSuperclasses() {
			return toldSuperClassExpressions_;
		}

		@Override
		public String getName() {
			return NAME;
		}

		@Override
		public void apply(BasicSaturationStateWriter writer, Conclusion premise, Context context) {
			LOGGER_.trace("Applying {}: {} to {}", NAME, toldSuperClassExpressions_, context);
			
			for (IndexedClassExpression implied : toldSuperClassExpressions_) {
				//writer.produce(context, new PositiveSubsumer(implied));
				writer.produce(context, writer.getConclusionFactory().subsumptionInference(premise, implied));
			}
		}

		@Override
		public boolean addTo(Chain<ChainableRule<Conclusion, Context>> ruleChain) {
			ThisCompositionRule rule = ruleChain.getCreate(
					ThisCompositionRule.MATCHER_, ThisCompositionRule.FACTORY_);
			boolean changed = false;

			for (IndexedClassExpression ice : toldSuperClassExpressions_) {
				LOGGER_.trace("Adding {} to {}", ice, NAME);
				
				changed |= rule.addToldSuperClassExpression(ice);
			}

			return changed;

		}

		@Override
		public boolean removeFrom(Chain<ChainableRule<Conclusion, Context>> ruleChain) {
			ThisCompositionRule rule = ruleChain
					.find(ThisCompositionRule.MATCHER_);
			boolean changed = false;

			if (rule != null) {
				for (IndexedClassExpression ice : toldSuperClassExpressions_) {
					LOGGER_.trace("Removing {} from {}", ice, NAME);
					
					changed |= rule.removeToldSuperClassExpression(ice);
				}

				if (rule.isEmpty()) {
					ruleChain.remove(ThisCompositionRule.MATCHER_);
					
					LOGGER_.trace("{}: removed ", NAME);

					return true;
				}
			}

			return changed;

		}

		@Override
		public void accept(CompositionRuleApplicationVisitor visitor,
				BasicSaturationStateWriter writer, Conclusion premise, Context context) {
			visitor.visit(this, writer, premise, context);
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

		@Override
		public String toString() {
			return getName() + ": " + toldSuperClassExpressions_;
		}

		private static final Matcher<ChainableRule<Conclusion, Context>, ThisCompositionRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableRule<Conclusion, Context>, ThisCompositionRule>(
				ThisCompositionRule.class);

		private static final ReferenceFactory<ChainableRule<Conclusion, Context>, ThisCompositionRule> FACTORY_ = new ReferenceFactory<ChainableRule<Conclusion, Context>, ThisCompositionRule>() {
			@Override
			public ThisCompositionRule create(ChainableRule<Conclusion, Context> tail) {
				return new ThisCompositionRule(tail);
			}
		};

	}

}
