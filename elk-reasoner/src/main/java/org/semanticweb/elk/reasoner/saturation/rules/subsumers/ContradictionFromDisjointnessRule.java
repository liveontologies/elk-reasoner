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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
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
 * {@link IndexedDisjointClassesAxiom} at least twice.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ContradictionFromDisjointnessRule extends
		AbstractChainableSubsumerRule {

	public static final String NAME = "DisjointClasses Contradiction Introduction";

	/**
	 * The number of {@link IndexedDisjointClassesAxiom}s in which the
	 * {@link IndexedClassExpression}, for which this rule is registered, occurs
	 * more than once.
	 */
	private int contradictionCounter_;

	private ContradictionFromDisjointnessRule(ChainableSubsumerRule tail) {
		super(tail);
		this.contradictionCounter_ = 0;
	}

	private ContradictionFromDisjointnessRule() {
		this((ChainableSubsumerRule) null);
		this.contradictionCounter_++;
	}

	public static boolean addRulesFor(ModifiableIndexedDisjointClassesAxiom axiom,
			ModifiableOntologyIndex index) {
		boolean success = true;
		int added = 0;
		for (ModifiableIndexedClassExpression ice : axiom
				.getInconsistentMembers()) {
			if (index.add(ice, new ContradictionFromDisjointnessRule())) {
				added++;
			} else {
				success = false;
				break;
			}
		}
		if (success)
			return true;
		// else revert the changes
		for (ModifiableIndexedClassExpression ice : axiom
				.getInconsistentMembers()) {
			if (added == 0)
				break;
			// else
			added--;
			index.remove(ice, new ContradictionFromDisjointnessRule());
		}
		return false;
	}

	public static boolean removeRulesFor(
			ModifiableIndexedDisjointClassesAxiom axiom,
			ModifiableOntologyIndex index) {
		boolean success = true;
		int removed = 0;
		for (ModifiableIndexedClassExpression ice : axiom
				.getInconsistentMembers()) {
			if (index.remove(ice, new ContradictionFromDisjointnessRule())) {
				removed++;
			} else {
				success = false;
				break;
			}
		}
		if (success)
			return true;
		// else revert the changes
		for (ModifiableIndexedClassExpression ice : axiom
				.getInconsistentMembers()) {
			if (removed == 0)
				break;
			// else
			removed--;
			index.add(ice, new ContradictionFromDisjointnessRule());
		}
		return false;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void apply(IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		// producer.produce(premises.getRoot(),
		// ContradictionImpl.getInstance());
		producer.produce(premises.getRoot(),
				new ContradictionFromInconsistentDisjointnessAxiom(premise));
	}

	@Override
	public boolean addTo(Chain<ChainableSubsumerRule> ruleChain) {
		if (isEmpty())
			return true;
		ContradictionFromDisjointnessRule rule = ruleChain.getCreate(MATCHER_,
				FACTORY_);
		rule.contradictionCounter_ += this.contradictionCounter_;
		return true;
	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
		if (isEmpty())
			return true;
		ContradictionFromDisjointnessRule rule = ruleChain.find(MATCHER_);
		if (rule == null) {
			return false;
		}
		if (rule.contradictionCounter_ < this.contradictionCounter_) {
			return false;
		}
		// else
		rule.contradictionCounter_ -= this.contradictionCounter_;
		if (rule.isEmpty())
			ruleChain.remove(MATCHER_);
		return true;
	}

	@Override
	public void accept(LinkedSubsumerRuleVisitor visitor,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

	private boolean isEmpty() {
		return this.contradictionCounter_ == 0;
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