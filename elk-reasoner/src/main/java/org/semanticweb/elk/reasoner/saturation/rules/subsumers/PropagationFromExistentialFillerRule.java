package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The rule producing {@link Propagation} of a {@link Subsumer}
 * {@link IndexedObjectSomeValuesFrom} over {@link BackwardLink}s when the
 * {@link IndexedClassExpression} filler of this
 * {@link IndexedObjectSomeValuesFrom} provided it can be used with at least one
 * {@link BackwardLink} in this {@link Context}
 * 
 * @author "Yevgeny Kazakov"
 */
public class PropagationFromExistentialFillerRule extends
		ModifiableLinkImpl<ChainableSubsumerRule> implements
		ChainableSubsumerRule {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(PropagationFromExistentialFillerRule.class);

	private static final String NAME_ = "ObjectSomeValuesFrom Propagation Introduction";

	private final Collection<IndexedObjectSomeValuesFrom> negExistentials_;

	private PropagationFromExistentialFillerRule(ChainableSubsumerRule next) {
		super(next);
		this.negExistentials_ = new ArrayList<IndexedObjectSomeValuesFrom>(1);
	}

	private PropagationFromExistentialFillerRule(
			IndexedObjectSomeValuesFrom negExistential) {
		super(null);
		this.negExistentials_ = new ArrayList<IndexedObjectSomeValuesFrom>(1);
		this.negExistentials_.add(negExistential);
	}

	// TODO: hide this method
	public Collection<IndexedObjectSomeValuesFrom> getNegativeExistentials() {
		return negExistentials_;
	}

	public static void addRuleFor(IndexedObjectSomeValuesFrom existential,
			ModifiableOntologyIndex index) {
		index.add(existential.getFiller(),
				new PropagationFromExistentialFillerRule(existential));
	}

	public static void removeRuleFor(IndexedObjectSomeValuesFrom existential,
			ModifiableOntologyIndex index) {
		index.remove(existential.getFiller(),
				new PropagationFromExistentialFillerRule(existential));
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

		// TODO: deal with reflexive roles using another
		// rule and uncomment this

		// if (candidatePropagationProperties.isEmpty()) {
		// return;
		// }

		for (IndexedObjectSomeValuesFrom e : negExistentials_) {
			IndexedPropertyChain relation = e.getRelation();
			/*
			 * creating propagations for relevant sub-properties of the relation
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
	public boolean addTo(Chain<ChainableSubsumerRule> ruleChain) {
		PropagationFromExistentialFillerRule rule = ruleChain.getCreate(
				MATCHER_, FACTORY_);
		boolean changed = false;

		for (IndexedObjectSomeValuesFrom negExistential : negExistentials_) {
			changed |= rule.addNegExistential(negExistential);
		}

		return changed;

	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
		boolean changed = false;
		PropagationFromExistentialFillerRule rule = ruleChain.find(MATCHER_);

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
	public void accept(LinkedSubsumerRuleVisitor visitor,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		visitor.visit(this, premise, context, writer);
	}

	private boolean addNegExistential(IndexedObjectSomeValuesFrom existential) {
		return negExistentials_.add(existential);
	}

	private boolean removeNegExistential(IndexedObjectSomeValuesFrom existential) {
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
	 * @param property
	 * @param context
	 * @param writer
	 */
	void applyForProperty(IndexedPropertyChain property, Context context,
			SaturationStateWriter writer) {

		for (IndexedObjectSomeValuesFrom e : negExistentials_) {
			if (e.getRelation().getSaturated().getSubProperties()
					.contains(property)) {
				writer.produce(context, new Propagation(property, e));
			}
		}

	}

	public static void applyForProperty(Chain<ChainableSubsumerRule> ruleChain,
			IndexedPropertyChain property, Context context,
			SaturationStateWriter writer) {
		PropagationFromExistentialFillerRule rule = ruleChain.find(MATCHER_);

		if (rule == null)
			return;

		rule.applyForProperty(property, context, writer);

	}

	private static final Matcher<ChainableSubsumerRule, PropagationFromExistentialFillerRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableSubsumerRule, PropagationFromExistentialFillerRule>(
			PropagationFromExistentialFillerRule.class);

	private static final ReferenceFactory<ChainableSubsumerRule, PropagationFromExistentialFillerRule> FACTORY_ = new ReferenceFactory<ChainableSubsumerRule, PropagationFromExistentialFillerRule>() {
		@Override
		public PropagationFromExistentialFillerRule create(
				ChainableSubsumerRule next) {
			return new PropagationFromExistentialFillerRule(next);
		}
	};

}