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
import java.util.Map;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.context.SubContextPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagationGenerated;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.rules.ClassInferenceProducer;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.LazySetUnion;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * A {@link ChainableSubsumerRule} producing {@link Propagation} of a
 * {@link SubClassInclusion} {@link IndexedObjectSomeValuesFrom} over
 * {@link BackwardLink}s when the {@link IndexedClassExpression} filler of this
 * {@link IndexedObjectSomeValuesFrom} provided it can be used with at least one
 * {@link BackwardLink} in this {@link Context}
 * 
 * @author "Yevgeny Kazakov"
 */
public class PropagationFromExistentialFillerRule extends
		AbstractChainableSubsumerRule {

	// logger for events
	/*
	 * private static final Logger LOGGER_ = LoggerFactory
	 * .getLogger(PropagationFromExistentialFillerRule.class);
	 */

	public static final String NAME = "ObjectSomeValuesFrom Propagation Introduction";

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

	@Deprecated
	public Collection<IndexedObjectSomeValuesFrom> getNegativeExistentials() {
		return negExistentials_;
	}

	public static boolean addRuleFor(
			ModifiableIndexedObjectSomeValuesFrom existential,
			ModifiableOntologyIndex index) {
		return index.add(existential.getFiller(),
				new PropagationFromExistentialFillerRule(existential));
	}

	public static boolean removeRuleFor(
			ModifiableIndexedObjectSomeValuesFrom existential,
			ModifiableOntologyIndex index) {
		return index.remove(existential.getFiller(),
				new PropagationFromExistentialFillerRule(existential));
	}

	@Override
	public String toString() {
		return NAME;
	}

	@Override
	public void apply(IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {

		final Map<IndexedObjectProperty, ? extends SubContextPremises> subContextMap = premises
				.getSubContextPremisesByObjectProperty();
		final Set<IndexedObjectProperty> candidatePropagationProperties = new LazySetUnion<IndexedObjectProperty>(
				premises.getLocalReflexiveObjectProperties(),
				subContextMap.keySet());

		if (candidatePropagationProperties.isEmpty()) {
			return;
		}

		for (IndexedObjectSomeValuesFrom e : negExistentials_) {
			IndexedObjectProperty relation = e.getProperty();
			/*
			 * creating propagations for relevant sub-properties of the relation
			 */
			SaturatedPropertyChain saturation = relation.getSaturated();
			for (IndexedObjectProperty property : new LazySetIntersection<IndexedObjectProperty>(
					candidatePropagationProperties,
					saturation.getSubProperties())) {
				if (subContextMap.get(property).isInitialized()) {
					producer.produce(new PropagationGenerated(premises
							.getRoot(), property, e));
				}
			}
		}
	}

	@Override
	public boolean isTracingRule() {
		return false;
	}

	@Override
	public boolean addTo(Chain<ChainableSubsumerRule> ruleChain) {
		if (isEmpty())
			return true;
		PropagationFromExistentialFillerRule rule = ruleChain.getCreate(
				MATCHER_, FACTORY_);
		rule.negExistentials_.addAll(this.negExistentials_);
		return true;
	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
		if (isEmpty())
			return true;
		PropagationFromExistentialFillerRule rule = ruleChain.find(MATCHER_);
		if (rule == null)
			return false;
		// else
		boolean success = true;
		int removed = 0;
		for (IndexedObjectSomeValuesFrom negExistential : this.negExistentials_) {
			if (rule.negExistentials_.remove(negExistential)) {
				removed++;
			} else {
				success = false;
				break;
			}
		}
		if (success) {
			if (rule.isEmpty())
				ruleChain.remove(MATCHER_);
			return true;
		}
		// else revert all changes
		for (IndexedObjectSomeValuesFrom negExistential : this.negExistentials_) {
			if (removed == 0)
				break;
			removed--;
			rule.negExistentials_.add(negExistential);
		}
		return false;
	}

	@Override
	public void accept(LinkedSubsumerRuleVisitor<?> visitor,
			IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

	/**
	 * @return {@code true} if this rule never does anything
	 */
	private boolean isEmpty() {
		return negExistentials_.isEmpty();
	}

	/**
	 * Produces propagations of {@link IndexedObjectSomeValuesFrom} over the
	 * given {@link IndexedObjectProperty} in the given {@link Context}
	 * 
	 * @param property
	 * @param premises
	 * @param producer
	 */
	void applyForProperty(IndexedObjectProperty property,
			ContextPremises premises, ClassInferenceProducer producer) {

		for (IndexedObjectSomeValuesFrom e : negExistentials_) {
			if (e.getProperty().getSaturated().getSubPropertyChains()
					.contains(property)) {
				producer.produce(new PropagationGenerated(premises.getRoot(),
						property, e));
			}
		}

	}

	public static void applyForProperty(LinkedSubsumerRule rule,
			IndexedObjectProperty property, ContextPremises premises,
			ClassInferenceProducer producer) {
		for (;;) {
			if (rule == null)
				return;
			PropagationFromExistentialFillerRule matchedRule = MATCHER_
					.match(rule);
			if (matchedRule != null) {
				matchedRule.applyForProperty(property, premises, producer);
				return;
			}
			// else
			rule = rule.next();
		}
	}

	private static final Matcher<LinkedSubsumerRule, PropagationFromExistentialFillerRule> MATCHER_ = new SimpleTypeBasedMatcher<LinkedSubsumerRule, PropagationFromExistentialFillerRule>(
			PropagationFromExistentialFillerRule.class);

	private static final ReferenceFactory<ChainableSubsumerRule, PropagationFromExistentialFillerRule> FACTORY_ = new ReferenceFactory<ChainableSubsumerRule, PropagationFromExistentialFillerRule>() {
		@Override
		public PropagationFromExistentialFillerRule create(
				ChainableSubsumerRule next) {
			return new PropagationFromExistentialFillerRule(next);
		}
	};

}
