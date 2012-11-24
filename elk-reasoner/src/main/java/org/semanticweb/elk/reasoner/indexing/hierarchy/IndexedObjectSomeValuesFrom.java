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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectSomeValuesFromVisitor;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * Represents all occurrences of an ElkObjectSomeValuesFrom in an ontology.
 * 
 * @author Frantisek Simancik
 * 
 */
public class IndexedObjectSomeValuesFrom extends IndexedClassExpression {

	// logger for this class
	// private static final Logger LOGGER_ =
	// Logger.getLogger(IndexedObjectSomeValuesFrom.class);

	protected final IndexedObjectProperty property;

	protected final IndexedClassExpression filler;

	IndexedObjectSomeValuesFrom(IndexedObjectProperty indexedObjectProperty,
			IndexedClassExpression filler) {
		this.property = indexedObjectProperty;
		this.filler = filler;
	}

	/**
	 * @return The indexed object property comprising this ObjectSomeValuesFrom.
	 */
	public IndexedObjectProperty getRelation() {
		return property;
	}

	/**
	 * @return The indexed class expression comprising this
	 *         ObjectSomeValuesFrom.
	 */
	public IndexedClassExpression getFiller() {
		return filler;
	}

	public <O> O accept(IndexedObjectSomeValuesFromVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedClassExpressionVisitor<O> visitor) {
		return accept((IndexedObjectSomeValuesFromVisitor<O>) visitor);
	}

	@Override
	protected void updateOccurrenceNumbers(final IndexUpdater indexUpdater,
			final int increment, final int positiveIncrement,
			final int negativeIncrement) {

		if (negativeOccurrenceNo == 0 && negativeIncrement > 0) {
			// first negative occurrence of this expression
			// register the composition rule for the filler
			indexUpdater.add(filler, new ThisCompositionRule(this));
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		if (negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			// no negative occurrences of this expression left
			indexUpdater.remove(filler, new ThisCompositionRule(this));
		}

	}

	@Override
	public String toString() {
		return "ObjectSomeValuesFrom(" + this.property + ' ' + this.filler
				+ ')';
	}

	@Override
	public void applyDecompositionRule(SaturationState.Writer writer,
			Context context) {
		/*
		 * RuleStatistics stats = ruleEngine.getRulesTimer();
		 * 
		 * stats.timeObjectSomeValuesFromDecompositionRule -=
		 * CachedTimeThread.currentTimeMillis;
		 * stats.countObjectSomeValuesFromDecompositionRule++;
		 */

		try {
			writer.produce(writer.getCreateContext(filler), new BackwardLink(
					context, property));
		} finally {
			// stats.timeObjectSomeValuesFromDecompositionRule +=
			// CachedTimeThread.currentTimeMillis;
		}
	}

	public static void generatePropagations(SaturationState.Writer writer,
			IndexedPropertyChain property, Context context) {
		for (IndexedClassExpression ice : context.getSubsumers()) {
			ThisCompositionRule rule = ice.getCompositionRuleChain().find(
					ThisCompositionRule.MATCHER_);
			if (rule == null)
				continue;
			rule.apply(writer, property, context);
		}
	}

	/**
	 * 
	 */
	private static class ThisCompositionRule extends
			ModifiableLinkImpl<ChainableRule<Context>> implements
			ChainableRule<Context> {

		private final Collection<IndexedObjectSomeValuesFrom> negExistentials_;

		private ThisCompositionRule(ChainableRule<Context> next) {
			super(next);
			this.negExistentials_ = new ArrayList<IndexedObjectSomeValuesFrom>(
					1);
		}

		ThisCompositionRule(IndexedObjectSomeValuesFrom negExistential) {
			super(null);
			this.negExistentials_ = new ArrayList<IndexedObjectSomeValuesFrom>(
					1);
			this.negExistentials_.add(negExistential);
		}

		private boolean addNegExistential(
				IndexedObjectSomeValuesFrom existential) {
			return negExistentials_.add(existential);
		}

		private boolean removeNegExistential(
				IndexedObjectSomeValuesFrom existential) {
			return negExistentials_.remove(existential);
		}

		/**
		 * @return {@code true} if this rule never does anything
		 */
		private boolean isEmpty() {
			return negExistentials_.isEmpty();
		}

		@Override
		public void apply(SaturationState.Writer writer, Context context) {

			/*
			 * RuleStatistics stats = ruleEngine.getRulesTimer();
			 * 
			 * stats.timeObjectSomeValuesFromCompositionRule -=
			 * CachedTimeThread.currentTimeMillis;
			 * stats.countObjectSomeValuesFromCompositionRule++;
			 */

			try {

				// final Set<IndexedPropertyChain>
				// candidatePropagationProperties = context
				// .getRoot().getPosPropertiesInExistentials();

				final Set<IndexedPropertyChain> candidatePropagationProperties = context
						.getBackwardLinksByObjectProperty().keySet();

				// TODO: deal with reflexive roles using another composition
				// rule and uncomment this

				// if (candidatePropagationProperties.isEmpty()) {
				// return;
				// }

				for (IndexedObjectSomeValuesFrom e : negExistentials_) {
					IndexedPropertyChain relation = e.getRelation();
					/*
					 * creating propagations for relevant sub-properties of the
					 * relation
					 */
					/*
					 * for (IndexedPropertyChain property :
					 * relation.getSaturated().getSubProperties()) {
					 * state.produce(context, new Propagation(property, e)); }
					 */
					for (IndexedPropertyChain property : new LazySetIntersection<IndexedPropertyChain>(
							candidatePropagationProperties, relation
									.getSaturated().getSubProperties())) {
						writer.produce(context, new Propagation(property, e));
					}

					// TODO: create a composition rule to deal with reflexivity
					// propagating to the this context if relation is reflexive
					if (relation.getSaturated().isReflexive())
						writer.produce(context, new NegativeSubsumer(e));
				}
			} finally {
				// stats.timeObjectSomeValuesFromCompositionRule +=
				// CachedTimeThread.currentTimeMillis;
			}
		}

		private void apply(SaturationState.Writer writer,
				IndexedPropertyChain property, Context context) {

			for (IndexedObjectSomeValuesFrom e : negExistentials_) {
				if (e.getRelation().getSaturated().getSubProperties()
						.contains(property)) {
					writer.produce(context, new Propagation(property, e));
				}
			}

		}

		private static final Matcher<ChainableRule<Context>, ThisCompositionRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableRule<Context>, ThisCompositionRule>(
				ThisCompositionRule.class);

		private static final ReferenceFactory<ChainableRule<Context>, ThisCompositionRule> FACTORY_ = new ReferenceFactory<ChainableRule<Context>, ThisCompositionRule>() {
			@Override
			public ThisCompositionRule create(ChainableRule<Context> next) {
				return new ThisCompositionRule(next);
			}
		};

		@Override
		public boolean addTo(Chain<ChainableRule<Context>> ruleChain) {
			ThisCompositionRule rule = ruleChain.getCreate(MATCHER_, FACTORY_);
			boolean changed = false;

			for (IndexedObjectSomeValuesFrom negExistential : negExistentials_) {
				changed |= rule.addNegExistential(negExistential);
			}

			return changed;

		}

		@Override
		public boolean removeFrom(Chain<ChainableRule<Context>> ruleChain) {
			boolean changed = false;
			ThisCompositionRule rule = ruleChain.find(MATCHER_);

			if (rule != null) {
				for (IndexedObjectSomeValuesFrom negExistential : negExistentials_) {
					changed |= rule.removeNegExistential(negExistential);
				}

				if (rule.isEmpty()) {
					ruleChain.remove(MATCHER_);
					changed = true;
				}
			}

			return changed;

		}

	}

}
