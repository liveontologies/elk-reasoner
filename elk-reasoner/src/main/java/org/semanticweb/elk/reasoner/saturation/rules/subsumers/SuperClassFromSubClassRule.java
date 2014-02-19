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
import org.semanticweb.elk.reasoner.indexing.hierarchy.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
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

		toldSuperClassExpressions_.add(ice);
	}

	public static void addRuleFor(IndexedSubClassOfAxiom axiom,
			ModifiableOntologyIndex index) {
		index.add(axiom.getSubClass(),
				new SuperClassFromSubClassRule(axiom.getSuperClass()));
	}

	public static void removeRuleFor(IndexedSubClassOfAxiom axiom,
			ModifiableOntologyIndex index) {
		index.remove(axiom.getSubClass(),
				new SuperClassFromSubClassRule(axiom.getSuperClass()));
	}

	// TODO: hide this method
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
			producer.produce(premises.getRoot(),
					new DecomposedSubsumer(implied));
		}
	}

	@Override
	public boolean addTo(Chain<ChainableSubsumerRule> ruleChain) {
		SuperClassFromSubClassRule rule = ruleChain.getCreate(
				SuperClassFromSubClassRule.MATCHER_,
				SuperClassFromSubClassRule.FACTORY_);
		boolean changed = false;

		for (IndexedClassExpression ice : toldSuperClassExpressions_) {
			LOGGER_.trace("Adding {} to {}", ice, NAME);

			changed |= rule.addToldSuperClassExpression(ice);
		}

		return changed;

	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
		SuperClassFromSubClassRule rule = ruleChain
				.find(SuperClassFromSubClassRule.MATCHER_);
		boolean changed = false;

		if (rule != null) {
			for (IndexedClassExpression ice : toldSuperClassExpressions_) {
				LOGGER_.trace("Removing {} from {}", ice, NAME);

				changed |= rule.removeToldSuperClassExpression(ice);
			}

			if (rule.isEmpty()) {
				ruleChain.remove(SuperClassFromSubClassRule.MATCHER_);

				LOGGER_.trace("{}: removed ", NAME);

				return true;
			}
		}

		return changed;

	}

	@Override
	public void accept(LinkedSubsumerRuleVisitor visitor,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

	protected boolean addToldSuperClassExpression(
			IndexedClassExpression superClassExpression) {
		return toldSuperClassExpressions_.add(superClassExpression);
	}

	/**
	 * @param superClassExpression
	 * @return true if successfully removed
	 */
	protected boolean removeToldSuperClassExpression(
			IndexedClassExpression superClassExpression) {
		return toldSuperClassExpressions_.remove(superClassExpression);
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