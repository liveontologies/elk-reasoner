package org.semanticweb.elk.reasoner.saturation.rules.contextinit;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedReflexiveObjectPropertyAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.InitializationSubsumer;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ChainableContextInitRule} that produces {@link Subsumer}s for ranges
 * of told-reflexive properties. It should be applied only if there exists at
 * least one ReflexiveObjectProperty axiom in the ontology.
 */
public class ReflexivePropertyRangesContextInitRule extends
		AbstractChainableContextInitRule {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ReflexivePropertyRangesContextInitRule.class);

	public static final String NAME = "Reflexive Property Ranges Introduction";

	/**
	 * Properties that are asserted reflexive in ReflexiveObjectProperty axioms
	 */
	private final List<IndexedObjectProperty> toldReflexiveProperties_;

	private ReflexivePropertyRangesContextInitRule(ChainableContextInitRule tail) {
		super(tail);
		this.toldReflexiveProperties_ = new ArrayList<IndexedObjectProperty>(1);
	}

	private ReflexivePropertyRangesContextInitRule(
			IndexedObjectProperty reflexive) {
		super(null);
		this.toldReflexiveProperties_ = new ArrayList<IndexedObjectProperty>(1);
		this.toldReflexiveProperties_.add(reflexive);
	}

	/**
	 * Add an {@link ReflexivePropertyRangesContextInitRule} for the given to
	 * the given {@link IndexedReflexiveObjectPropertyAxiom} to the given
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param axiom
	 * @param index
	 * @return {@code true} if the operation was successful and {@code false}
	 *         otherwise; if {@code false} is returned, the index remains
	 *         unchanged
	 */
	public static boolean addRuleFor(IndexedReflexiveObjectPropertyAxiom axiom,
			ModifiableOntologyIndex index) {
		return index
				.addContextInitRule(new ReflexivePropertyRangesContextInitRule(
						axiom.getProperty()));
	}

	/**
	 * Removes an {@link ReflexivePropertyRangesContextInitRule} for the given
	 * from the given {@link IndexedReflexiveObjectPropertyAxiom}
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param axiom
	 * @param index
	 * @return {@code true} if the operation was successful and {@code false}
	 *         otherwise; if {@code false} is returned, the index remains
	 *         unchanged
	 */
	public static boolean removeRuleFor(
			IndexedReflexiveObjectPropertyAxiom axiom,
			ModifiableOntologyIndex index) {
		return index
				.removeContextInitRule(new ReflexivePropertyRangesContextInitRule(
						axiom.getProperty()));
	}

	@Deprecated
	public Collection<IndexedObjectProperty> getToldReflexiveProperties() {
		return toldReflexiveProperties_;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void apply(ContextInitialization premise, ContextPremises premises,
			ConclusionProducer producer) {
		for (IndexedObjectProperty reflexive : toldReflexiveProperties_) {
			for (IndexedClassExpression range : reflexive.getSaturated()
					.getRanges()) {
				producer.produce(
				// TODO: introduce a specific inference
				new InitializationSubsumer<IndexedClassExpression>(premises
						.getRoot(), range));
			}
		}
	}

	@Override
	public boolean addTo(Chain<ChainableContextInitRule> ruleChain) {
		if (isEmpty())
			return true;
		ReflexivePropertyRangesContextInitRule rule = ruleChain.getCreate(
				ReflexivePropertyRangesContextInitRule.MATCHER_,
				ReflexivePropertyRangesContextInitRule.FACTORY_);
		boolean success = true;
		int added = 0;
		for (IndexedObjectProperty reflexive : toldReflexiveProperties_) {
			LOGGER_.trace("{}: adding to {}", reflexive, NAME);
			if (rule.toldReflexiveProperties_.add(reflexive)) {
				added++;
			} else {
				success = false;
				break;
			}
		}
		if (success) {
			return true;
		}
		// else revert all changes
		for (IndexedObjectProperty reflexive : toldReflexiveProperties_) {
			if (added == 0)
				break;
			added--;
			LOGGER_.trace("{}: removing from {} [revert]", reflexive, NAME);
			rule.toldReflexiveProperties_.remove(reflexive);
		}
		return false;
	}

	@Override
	public boolean removeFrom(Chain<ChainableContextInitRule> ruleChain) {
		if (isEmpty())
			return true;
		ReflexivePropertyRangesContextInitRule rule = ruleChain
				.find(ReflexivePropertyRangesContextInitRule.MATCHER_);
		if (rule == null)
			return false;
		// else
		boolean success = true;
		int removed = 0;
		for (IndexedObjectProperty reflexive : toldReflexiveProperties_) {
			LOGGER_.trace("{}: removing from {}", reflexive, NAME);
			if (rule.toldReflexiveProperties_.remove(reflexive)) {
				removed++;
			} else {
				success = false;
				break;
			}
		}
		if (success) {
			if (rule.isEmpty()) {
				ruleChain
						.remove(ReflexivePropertyRangesContextInitRule.MATCHER_);
				LOGGER_.trace("{}: removed ", NAME);
			}
			return true;
		}
		// else revert all changes
		for (IndexedObjectProperty reflexive : toldReflexiveProperties_) {
			if (removed == 0)
				break;
			removed--;
			LOGGER_.trace("{}: adding to {} [revert]", reflexive, NAME);
			rule.toldReflexiveProperties_.add(reflexive);
		}
		return false;
	}

	/**
	 * @return {@code true} if this rule never does anything
	 */
	private boolean isEmpty() {
		return toldReflexiveProperties_.isEmpty();
	}

	@Override
	public void accept(LinkedContextInitRuleVisitor visitor,
			ContextInitialization premise, ContextPremises premises,
			ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

	private static final Matcher<ChainableContextInitRule, ReflexivePropertyRangesContextInitRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableContextInitRule, ReflexivePropertyRangesContextInitRule>(
			ReflexivePropertyRangesContextInitRule.class);

	private static final ReferenceFactory<ChainableContextInitRule, ReflexivePropertyRangesContextInitRule> FACTORY_ = new ReferenceFactory<ChainableContextInitRule, ReflexivePropertyRangesContextInitRule>() {
		@Override
		public ReflexivePropertyRangesContextInitRule create(
				ChainableContextInitRule tail) {
			return new ReflexivePropertyRangesContextInitRule(tail);
		}
	};

}
