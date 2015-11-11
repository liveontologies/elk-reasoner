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
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.InitializationSubsumer;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
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
	 * @return {@code true} if the operation was successful and {@code false}
	 *         otherwise; if {@code false} is returned, the index remains
	 *         unchanged
	 */
	public static boolean addRuleFor(IndexedClass owlThing,
			ModifiableOntologyIndex index) {
		LOGGER_.trace("{}: adding {}", owlThing, NAME);
		return index.addContextInitRule(new OwlThingContextInitRule(owlThing));
	}

	/**
	 * Removes an {@link OwlThingContextInitRule} from the given
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param owlThing
	 * @param index
	 * @return {@code true} if the operation was successful and {@code false}
	 *         otherwise; if {@code false} is returned, the index remains
	 *         unchanged
	 */
	public static boolean removeRuleFor(IndexedClass owlThing,
			ModifiableOntologyIndex index) {
		LOGGER_.trace("{}: removing {}", owlThing, NAME);
		return index
				.removeContextInitRule(new OwlThingContextInitRule(owlThing));
	}

	@Override
	public String toString() {
		return NAME;
	}

	@Override
	public void apply(ContextInitialization premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		// producer.produce(premises.getRoot(), new
		// DecomposedSubsumer(owlThing_));
		producer.produce(new InitializationSubsumer(premises.getRoot(),
				owlThing_));
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	@Override
	public boolean addTo(Chain<ChainableContextInitRule> ruleChain) {
		OwlThingContextInitRule rule = ruleChain.getCreate(MATCHER_, FACTORY_);
		if (rule.owlThing_ == null) {
			// new rule created
			rule.owlThing_ = owlThing_;
		}
		// owl:Thing was already registered
		return true;
	}

	@Override
	public boolean removeFrom(Chain<ChainableContextInitRule> ruleChain) {
		return ruleChain.remove(MATCHER_) != null;
	}

	@Override
	public void accept(LinkedContextInitRuleVisitor<?> visitor,
			ContextInitialization premise, ContextPremises premises,
			ClassConclusionProducer producer) {
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
