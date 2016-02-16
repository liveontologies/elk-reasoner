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
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyOfOwlNothing;
import org.semanticweb.elk.reasoner.saturation.rules.ClassInferenceProducer;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * A {@link ChainableSubsumerRule} producing {@link ClassInconsistency} when
 * processing an {@link IndexedClassExpression} that corresponds to
 * {@code owl:Nothing}.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ContradictionFromOwlNothingRule extends
		AbstractChainableSubsumerRule {

	public static final String NAME = "Owl:Nothing Contradiction Introduction";

	private ContradictionFromOwlNothingRule(ChainableSubsumerRule tail) {
		super(tail);
	}

	private ContradictionFromOwlNothingRule() {
		this((ChainableSubsumerRule) null);
	}

	private static void checkOwlNothing(IndexedClass candidate) {
		if (candidate.getElkEntity() != PredefinedElkClass.OWL_NOTHING)
			throw new IllegalArgumentException(
					"The rule can be registered only for owl:Nothing");
	}

	public static boolean addRuleFor(ModifiableIndexedClass owlNothing,
			ModifiableOntologyIndex index) {
		checkOwlNothing(owlNothing);
		return index.add(owlNothing, new ContradictionFromOwlNothingRule());
	}

	public static boolean removeRuleFor(ModifiableIndexedClass owlNothing,
			ModifiableOntologyIndex index) {
		checkOwlNothing(owlNothing);
		return index.remove(owlNothing, new ContradictionFromOwlNothingRule());
	}

	@Override
	public String toString() {
		return NAME;
	}

	@Override
	public void apply(IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		producer.produce(new ClassInconsistencyOfOwlNothing(premises.getRoot(),
				(IndexedClass) premise));
	}

	@Override
	public boolean isTracing() {
		return true;
	}

	@Override
	public boolean addTo(Chain<ChainableSubsumerRule> ruleChain) {
		ContradictionFromOwlNothingRule rule = ruleChain.find(MATCHER_);
		if (rule != null) {
			// the rule is already registered
			return false;
		}
		// else will create and add to the chain
		ruleChain.getCreate(MATCHER_, FACTORY_);
		return true;
	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
		ContradictionFromOwlNothingRule previous = ruleChain.remove(MATCHER_);
		if (previous == null) {
			// the rule was not registered
			return false;
		}
		// else
		return true;
	}

	@Override
	public void accept(LinkedSubsumerRuleVisitor<?> visitor,
			IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		visitor.visit(this, premise, premises, producer);
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
