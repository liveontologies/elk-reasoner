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

import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectSomeValuesFromVisitor;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.SubsumerDecompositionVisitor;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents all occurrences of an {@link ElkObjectSomeValuesFrom} in an
 * ontology.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class IndexedObjectSomeValuesFrom extends IndexedClassExpression {

	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(IndexedObjectSomeValuesFrom.class);

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
	protected void updateOccurrenceNumbers(final ModifiableOntologyIndex index,
			final int increment, final int positiveIncrement,
			final int negativeIncrement) {

		if (negativeOccurrenceNo == 0 && negativeIncrement > 0) {
			// first negative occurrence of this expression
			// register the composition rule for the filler
			index.add(filler, new PropagationCompositionRule(this));
		}

		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;

		if (negativeOccurrenceNo == 0 && negativeIncrement < 0) {
			// no negative occurrences of this expression left
			index.remove(filler, new PropagationCompositionRule(this));
		}

	}

	@Override
	public String toStringStructural() {
		return "ObjectSomeValuesFrom(" + this.property + ' ' + this.filler
				+ ')';
	}

	@Override
	public void accept(SubsumerDecompositionVisitor visitor, Context context) {
		visitor.visit(this, context);
	}

	public static void generatePropagations(SaturationStateWriter writer,
			IndexedPropertyChain property, Context context) {
		for (IndexedClassExpression ice : context.getSubsumers()) {
			PropagationCompositionRule rule = ice.getCompositionRuleChain()
					.find(PropagationCompositionRule.MATCHER_);

			if (rule == null)
				continue;

			rule.apply(writer, property, context);
		}
	}

	/**
	 * The composition rule producing {@link Propagation} of a {@link Subsumer}
	 * {@link IndexedObjectSomeValuesFrom} over {@link BackwardLink}s when the
	 * {@link IndexedClassExpression} filler of this
	 * {@link IndexedObjectSomeValuesFrom} provided it can be used with at least
	 * one {@link BackwardLink} in this {@link Context}
	 * 
	 * @author "Yevgeny Kazakov"
	 */
	public static class PropagationCompositionRule extends
			ModifiableLinkImpl<ChainableRule<IndexedClassExpression>> implements
			ChainableRule<IndexedClassExpression> {

		private static final String NAME_ = "ObjectSomeValuesFrom Introduction";

		private final Collection<IndexedObjectSomeValuesFrom> negExistentials_;

		private PropagationCompositionRule(
				ChainableRule<IndexedClassExpression> next) {
			super(next);
			this.negExistentials_ = new ArrayList<IndexedObjectSomeValuesFrom>(
					1);
		}

		PropagationCompositionRule(IndexedObjectSomeValuesFrom negExistential) {
			super(null);
			this.negExistentials_ = new ArrayList<IndexedObjectSomeValuesFrom>(
					1);
			this.negExistentials_.add(negExistential);
		}

		// TODO: hide this method
		public Collection<IndexedObjectSomeValuesFrom> getNegativeExistentials() {
			return negExistentials_;
		}

		@Override
		public String getName() {
			return NAME_;
		}

		@Override
		public void apply(IndexedClassExpression premise, Context context,
				SaturationStateWriter writer) {
			LOGGER_.trace("Applying {} to {}", NAME_, context);

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
				for (IndexedPropertyChain property : new LazySetIntersection<IndexedPropertyChain>(
						candidatePropagationProperties, relation.getSaturated()
								.getSubProperties())) {
					writer.produce(context, new Propagation(property, e));
				}

				// TODO: create a composition rule to deal with reflexivity
				// propagating to the this context if relation is reflexive
				if (relation.getSaturated().isDerivedReflexive())
					writer.produce(context, new ComposedSubsumer(e));
			}
		}

		@Override
		public boolean addTo(
				Chain<ChainableRule<IndexedClassExpression>> ruleChain) {
			PropagationCompositionRule rule = ruleChain.getCreate(MATCHER_,
					FACTORY_);
			boolean changed = false;

			for (IndexedObjectSomeValuesFrom negExistential : negExistentials_) {
				changed |= rule.addNegExistential(negExistential);
			}

			return changed;

		}

		@Override
		public boolean removeFrom(
				Chain<ChainableRule<IndexedClassExpression>> ruleChain) {
			boolean changed = false;
			PropagationCompositionRule rule = ruleChain.find(MATCHER_);

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

		@Override
		public void accept(CompositionRuleVisitor visitor,
				IndexedClassExpression premise, Context context,
				SaturationStateWriter writer) {
			visitor.visit(this, premise, context, writer);
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

		/**
		 * Produces propagations for sub-properties of the given
		 * {@link IndexedPropertyChain} in the given {@link Context}
		 * 
		 * @param writer
		 * @param property
		 * @param context
		 */
		private void apply(SaturationStateWriter writer,
				IndexedPropertyChain property, Context context) {

			for (IndexedObjectSomeValuesFrom e : negExistentials_) {
				if (e.getRelation().getSaturated().getSubProperties()
						.contains(property)) {
					writer.produce(context, new Propagation(property, e));
				}
			}

		}

		private static final Matcher<ChainableRule<IndexedClassExpression>, PropagationCompositionRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableRule<IndexedClassExpression>, PropagationCompositionRule>(
				PropagationCompositionRule.class);

		private static final ReferenceFactory<ChainableRule<IndexedClassExpression>, PropagationCompositionRule> FACTORY_ = new ReferenceFactory<ChainableRule<IndexedClassExpression>, PropagationCompositionRule>() {
			@Override
			public PropagationCompositionRule create(
					ChainableRule<IndexedClassExpression> next) {
				return new PropagationCompositionRule(next);
			}
		};

	}

}
