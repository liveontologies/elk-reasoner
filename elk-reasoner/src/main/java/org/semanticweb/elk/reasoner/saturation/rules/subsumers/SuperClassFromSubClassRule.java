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
import java.util.List;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.SubClassOfSubsumer;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * A {@link ChainableSubsumerRule} producing {@link Subsumer} for the super
 * class of {@link IndexedSubClassOfAxiom} when processing its sub class
 * {@link IndexedClassExpression}
 * 
 * @see IndexedSubClassOfAxiom#getSuperClass()
 * @see IndexedSubClassOfAxiom#getSubClass()
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SuperClassFromSubClassRule extends AbstractChainableSubsumerRule {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(SuperClassFromSubClassRule.class);

	public static final String NAME = "SubClassOf Expansion";

	/**
	 * Correctness of axioms deletions requires that toldSuperClassExpressions
	 * is a List.
	 */
	private final List<IndexedClassExpression> toldSuperClassExpressions_;

	private SuperClassFromSubClassRule(ChainableSubsumerRule tail) {
		super(tail);
		this.toldSuperClassExpressions_ = new ArrayList<IndexedClassExpression>(
				1);
	}

	private SuperClassFromSubClassRule(IndexedClassExpression ice) {
		super(null);
		this.toldSuperClassExpressions_ = new ArrayList<IndexedClassExpression>(
				1);
		this.toldSuperClassExpressions_.add(ice);
	}

	public static boolean addRuleFor(ModifiableIndexedSubClassOfAxiom axiom,
			ModifiableOntologyIndex index) {
		return index.add(axiom.getSubClass(), new SuperClassFromSubClassRule(
				axiom.getSuperClass()));
	}

	public static boolean removeRuleFor(ModifiableIndexedSubClassOfAxiom axiom,
			ModifiableOntologyIndex index) {
		return index.remove(axiom.getSubClass(),
				new SuperClassFromSubClassRule(axiom.getSuperClass()));
	}

	@Deprecated
	public Collection<IndexedClassExpression> getToldSuperclasses() {
		return toldSuperClassExpressions_;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void apply(IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		for (IndexedClassExpression implied : toldSuperClassExpressions_) {
			// producer.produce(premises.getRoot(), new
			// DecomposedSubsumer(implied));
			producer.produce(premises.getRoot(),
					new SubClassOfSubsumer<IndexedClassExpression>(premise,
							implied));
		}
	}

	@Override
	public boolean addTo(Chain<ChainableSubsumerRule> ruleChain) {
		if (isEmpty())
			return true;
		SuperClassFromSubClassRule rule = ruleChain.getCreate(
				SuperClassFromSubClassRule.MATCHER_,
				SuperClassFromSubClassRule.FACTORY_);
		boolean success = true;
		int added = 0;
		for (IndexedClassExpression ice : toldSuperClassExpressions_) {
			LOGGER_.trace("{}: adding to {}", ice, NAME);
			if (rule.toldSuperClassExpressions_.add(ice)) {
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
		for (IndexedClassExpression ice : toldSuperClassExpressions_) {
			if (added == 0)
				break;
			added--;
			LOGGER_.trace("{}: removing from {} [revert]", ice, NAME);
			rule.toldSuperClassExpressions_.remove(ice);
		}
		return false;
	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
		if (isEmpty())
			return true;
		SuperClassFromSubClassRule rule = ruleChain
				.find(SuperClassFromSubClassRule.MATCHER_);
		if (rule == null)
			return false;
		// else
		boolean success = true;
		int removed = 0;
		for (IndexedClassExpression ice : toldSuperClassExpressions_) {
			LOGGER_.trace("{}: removing from {}", ice, NAME);
			if (rule.toldSuperClassExpressions_.remove(ice)) {
				removed++;
			} else {
				success = false;
				break;
			}
		}
		if (success) {
			if (rule.isEmpty()) {
				ruleChain.remove(SuperClassFromSubClassRule.MATCHER_);
				LOGGER_.trace("{}: removed ", NAME);
			}
			return true;
		}
		// else revert all changes
		for (IndexedClassExpression ice : toldSuperClassExpressions_) {
			if (removed == 0)
				break;
			removed--;
			LOGGER_.trace("{}: adding to {} [revert]", ice, NAME);
			rule.toldSuperClassExpressions_.add(ice);
		}
		return false;
	}

	@Override
	public void accept(LinkedSubsumerRuleVisitor visitor,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
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

	private static final Matcher<ChainableSubsumerRule, SuperClassFromSubClassRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableSubsumerRule, SuperClassFromSubClassRule>(
			SuperClassFromSubClassRule.class);

	private static final ReferenceFactory<ChainableSubsumerRule, SuperClassFromSubClassRule> FACTORY_ = new ReferenceFactory<ChainableSubsumerRule, SuperClassFromSubClassRule>() {
		@Override
		public SuperClassFromSubClassRule create(ChainableSubsumerRule tail) {
			return new SuperClassFromSubClassRule(tail);
		}
	};

}
