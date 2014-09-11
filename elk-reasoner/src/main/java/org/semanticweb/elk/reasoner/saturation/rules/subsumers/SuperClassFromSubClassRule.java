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
	protected final List<IndexedClassExpression> toldSuperClassExpressions;

	SuperClassFromSubClassRule(ChainableSubsumerRule tail) {
		super(tail);
		this.toldSuperClassExpressions = new ArrayList<IndexedClassExpression>(
				1);
	}

	SuperClassFromSubClassRule(IndexedClassExpression ice) {
		super(null);
		this.toldSuperClassExpressions = new ArrayList<IndexedClassExpression>(
				1);

		toldSuperClassExpressions.add(ice);
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
		return toldSuperClassExpressions;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void apply(IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		for (IndexedClassExpression implied : toldSuperClassExpressions) {
			producer.produce(premises.getRoot(),
					new SubClassOfSubsumer<IndexedClassExpression>(premise,
							implied));
		}
	}

	protected SuperClassFromSubClassRule getCreate(Chain<ChainableSubsumerRule> ruleChain) {
		return ruleChain.getCreate(MATCHER_, FACTORY_);
	}
	
	protected SuperClassFromSubClassRule get(Chain<ChainableSubsumerRule> ruleChain) {
		return ruleChain.find(MATCHER_);
	}
	
	@Override
	public boolean addTo(Chain<ChainableSubsumerRule> ruleChain) {
		SuperClassFromSubClassRule rule = getCreate(ruleChain);
		boolean changed = false;

		for (int i = 0; i < toldSuperClassExpressions.size(); i++) {
			changed |= rule.addToldSuperClassExpression(toldSuperClassExpressions.get(i));
		}

		return changed;
	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
		SuperClassFromSubClassRule rule = get(ruleChain);
		boolean changed = false;

		if (rule != null) {
			for (int i = 0; i < toldSuperClassExpressions.size(); i++) {
				changed |= rule.removeToldSuperClassExpression(toldSuperClassExpressions.get(i));
			}

			if (rule.isEmpty()) {
				return removeEmpty(ruleChain, MATCHER_);
			}
		}

		return changed;
	}
	
	protected boolean removeEmpty(Chain<ChainableSubsumerRule> ruleChain, Matcher<ChainableSubsumerRule, ? extends SuperClassFromSubClassRule> matcher) {
		ruleChain.remove(matcher);

		LOGGER_.trace("{}: removed ", NAME);

		return true;
	}

	@Override
	public void accept(LinkedSubsumerRuleVisitor visitor,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

	protected boolean addToldSuperClassExpression(
			IndexedClassExpression superClassExpression) {
		LOGGER_.trace("Adding {} to {}", superClassExpression, NAME);
		
		return toldSuperClassExpressions.add(superClassExpression);
	}

	/**
	 * @param superClassExpression
	 * @return true if successfully removed
	 */
	protected boolean removeToldSuperClassExpression(
			IndexedClassExpression superClassExpression) {
		LOGGER_.trace("Removing {} from {}", superClassExpression, NAME);
		
		return toldSuperClassExpressions.remove(superClassExpression);
	}

	/**
	 * @return {@code true} if this rule never does anything
	 */
	protected boolean isEmpty() {
		return toldSuperClassExpressions.isEmpty();
	}

	@Override
	public String toString() {
		return getName() + ": " + toldSuperClassExpressions;
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
