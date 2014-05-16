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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ModifiableOntologyIndex;
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
 * A {@link ChainableContextInitRule} that produces {@link Subsumer}
 * {@code owl:Thing} in a context. It should be applied only if
 * {@code owl:Thing} occurs negatively in the ontology.
 */
public class OwlThingContextInitRule extends AbstractChainableContextInitRule {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(OwlThingContextInitRule.class);

	public static final String NAME = "owl:Thing Introduction";

	private IndexedClassExpression owlThing_;

	private OwlThingContextInitRule(ChainableContextInitRule tail) {
		super(tail);
	}

	private OwlThingContextInitRule(IndexedClassExpression owlThing) {
		super(null);
		this.owlThing_ = owlThing;
	}

	/**
	 * Add an {@link OwlThingContextInitRule} to the given
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param owlThing
	 * @param index
	 */
	public static void addRuleFor(IndexedClass owlThing,
			ModifiableOntologyIndex index) {
		LOGGER_.trace("Adding {} to {}", owlThing, NAME);
		index.addContextInitRule(new OwlThingContextInitRule(owlThing));
	}

	/**
	 * Removes an {@link OwlThingContextInitRule} from the given
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param owlThing
	 * @param index
	 */
	public static void removeRuleFor(IndexedClass owlThing,
			ModifiableOntologyIndex index) {
		index.removeContextInitRule(new OwlThingContextInitRule(owlThing));
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void apply(ContextInitialization premise, ContextPremises premises,
			ConclusionProducer producer) {
		// producer.produce(premises.getRoot(), new
		// DecomposedSubsumer(owlThing_));
		producer.produce(premises.getRoot(),
				new InitializationSubsumer<IndexedClassExpression>(owlThing_));
	}

	@Override
	public boolean addTo(Chain<ChainableContextInitRule> ruleChain) {
		OwlThingContextInitRule rule = ruleChain.getCreate(MATCHER_, FACTORY_);
		if (rule.owlThing_ == null) {
			// new rule created
			rule.owlThing_ = owlThing_;
			return true;
		}
		// owl:Thing was already registered
		return false;
	}

	@Override
	public boolean removeFrom(Chain<ChainableContextInitRule> ruleChain) {
		return ruleChain.remove(MATCHER_) != null;
	}

	@Override
	public void accept(LinkedContextInitRuleVisitor visitor,
			ContextInitialization premise, ContextPremises premises,
			ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

	private static final Matcher<ChainableContextInitRule, OwlThingContextInitRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableContextInitRule, OwlThingContextInitRule>(
			OwlThingContextInitRule.class);

	private static final ReferenceFactory<ChainableContextInitRule, OwlThingContextInitRule> FACTORY_ = new ReferenceFactory<ChainableContextInitRule, OwlThingContextInitRule>() {
		@Override
		public OwlThingContextInitRule create(ChainableContextInitRule tail) {
			return new OwlThingContextInitRule(tail);
		}
	};

}
