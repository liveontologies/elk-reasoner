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

import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ChainableSubsumerRule} producing {@link Contradiction} when
 * processing an {@link IndexedClassExpression} that corresponds to
 * {@code owl:Nothing}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ContradictionFromOwlNothingRule extends
		AbstractChainableSubsumerRule {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ContradictionFromOwlNothingRule.class);

	public static final String NAME_ = "Owl:Nothing Contradiction Introduction";

	private ContradictionFromOwlNothingRule(ChainableSubsumerRule tail) {
		super(tail);
	}

	private ContradictionFromOwlNothingRule() {
		this((ChainableSubsumerRule) null);
	}

	private static void checkOwlNothing(IndexedClass candidate) {
		if (candidate.getElkClass() != PredefinedElkClass.OWL_NOTHING)
			throw new IllegalArgumentException(
					"The rule can be registered only for owl:Nothing");
	}

	public static void addRuleFor(IndexedClass owlNothing,
			ModifiableOntologyIndex index) {
		checkOwlNothing(owlNothing);
		index.add(owlNothing, new ContradictionFromOwlNothingRule());
	}

	public static void removeRuleFor(IndexedClass owlNothing,
			ModifiableOntologyIndex index) {
		checkOwlNothing(owlNothing);
		index.remove(owlNothing, new ContradictionFromOwlNothingRule());
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public void apply(IndexedClassExpression premise, Context context,
			ConclusionProducer producer) {
		LOGGER_.trace("Applying {} to {}", NAME_, context);
		producer.produce(context, Contradiction.getInstance());
	}

	@Override
	public boolean addTo(Chain<ChainableSubsumerRule> ruleChain) {
		ContradictionFromOwlNothingRule rule = ruleChain.find(MATCHER_);
		if (rule != null)
			// the rule is already registered
			return false;
		// else will create and add to the chain
		ruleChain.getCreate(MATCHER_, FACTORY_);
		return true;
	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
		ContradictionFromOwlNothingRule rule = ruleChain.find(MATCHER_);
		if (rule == null) {
			// the rule was not registered
			return false;
		}
		// else remove it
		ruleChain.remove(MATCHER_);
		return true;
	}

	@Override
	public void accept(LinkedSubsumerRuleVisitor visitor,
			IndexedClassExpression premise, Context context,
			ConclusionProducer producer) {
		visitor.visit(this, premise, context, producer);
	}

	private static Matcher<ChainableSubsumerRule, ContradictionFromOwlNothingRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableSubsumerRule, ContradictionFromOwlNothingRule>(
			ContradictionFromOwlNothingRule.class);

	private static ReferenceFactory<ChainableSubsumerRule, ContradictionFromOwlNothingRule> FACTORY_ = new ReferenceFactory<ChainableSubsumerRule, ContradictionFromOwlNothingRule>() {
		@Override
		public ContradictionFromOwlNothingRule create(ChainableSubsumerRule tail) {
			return new ContradictionFromOwlNothingRule(tail);
		}
	};

}