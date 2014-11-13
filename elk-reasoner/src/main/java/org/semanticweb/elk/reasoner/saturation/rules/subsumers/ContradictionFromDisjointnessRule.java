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
import java.util.List;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Contradiction;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ContradictionFromInconsistentDisjointnessAxiom;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * A {@link ChainableSubsumerRule} producing {@link Contradiction} when
 * processing an {@link IndexedClassExpression} that is present in an
 * {@link IndexedDisjointnessAxiom} at least twice.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ContradictionFromDisjointnessRule extends
		AbstractChainableSubsumerRule {

	public static final String NAME = "DisjointClasses Contradiction Introduction";

	/**
	 * All {@link IndexedDisjointnessAxiom}s in which the
	 * {@link IndexedClassExpression}, for which this rule is registered, occurs
	 * more than once.
	 */
	private List<IndexedDisjointnessAxiom> inconsistentAxioms_ = new ArrayList<IndexedDisjointnessAxiom>(1);

	private ContradictionFromDisjointnessRule(ChainableSubsumerRule tail) {
		super(tail);
	}

	private ContradictionFromDisjointnessRule(IndexedDisjointnessAxiom axiom) {
		this((ChainableSubsumerRule) null);
		
		inconsistentAxioms_.add(axiom);
	}

	public static void addRulesFor(IndexedDisjointnessAxiom axiom,
			ModifiableOntologyIndex index) {
		for (IndexedClassExpression ice : axiom.getInconsistentMembers()) {
			index.add(ice, new ContradictionFromDisjointnessRule(axiom));
		}
	}

	public static void removeRulesFor(IndexedDisjointnessAxiom axiom,
			ModifiableOntologyIndex index) {
		for (IndexedClassExpression ice : axiom.getInconsistentMembers()) {
 			index.remove(ice, new ContradictionFromDisjointnessRule(axiom));
		}
	}
	
	private void addAxioms(List<IndexedDisjointnessAxiom> ax) {
		inconsistentAxioms_.addAll(ax);
	}
	
	private void removeAxioms(List<IndexedDisjointnessAxiom> ax) {
		inconsistentAxioms_.removeAll(ax);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void apply(IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		for (IndexedDisjointnessAxiom ax : inconsistentAxioms_) {
			producer.produce(premises.getRoot(), new ContradictionFromInconsistentDisjointnessAxiom(premise, ax));
		}
	}

	@Override
	public boolean addTo(Chain<ChainableSubsumerRule> ruleChain) {
		ContradictionFromDisjointnessRule rule = ruleChain.getCreate(MATCHER_, FACTORY_);

		rule.addAxioms(inconsistentAxioms_);
		
		return !isEmpty();
	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
		ContradictionFromDisjointnessRule rule = ruleChain.find(MATCHER_);
		
		if (rule == null) {
			return false;
		}
		
		rule.removeAxioms(inconsistentAxioms_);
		
		if (rule.isEmpty()) {
			ruleChain.remove(MATCHER_);
		}
		
		return !isEmpty();
	}

	@Override
	public void accept(LinkedSubsumerRuleVisitor visitor,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

	protected boolean isEmpty() {
		return inconsistentAxioms_.isEmpty();
	}

	private static Matcher<ChainableSubsumerRule, ContradictionFromDisjointnessRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableSubsumerRule, ContradictionFromDisjointnessRule>(
			ContradictionFromDisjointnessRule.class);

	private static ReferenceFactory<ChainableSubsumerRule, ContradictionFromDisjointnessRule> FACTORY_ = new ReferenceFactory<ChainableSubsumerRule, ContradictionFromDisjointnessRule>() {
		@Override
		public ContradictionFromDisjointnessRule create(
				ChainableSubsumerRule tail) {
			return new ContradictionFromDisjointnessRule(tail);
		}
	};

}