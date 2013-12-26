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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The rule producing {@link Contradiction} when processing a an
 * {@link IndexedClassExpression} that is present in an
 * {@link IndexedDisjointnessAxiom} at least twice.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ContradictionFromDisjointnessRule extends
		ModifiableLinkImpl<ChainableSubsumerRule> implements
		ChainableSubsumerRule {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ContradictionFromDisjointnessRule.class);

	public static final String NAME_ = "DisjointClasses Contradiction Introduction";

	/**
	 * The number of {@link IndexedDisjointnessAxiom}s in which the
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

	public static void addRulesFor(IndexedDisjointnessAxiom axiom,
			ModifiableOntologyIndex index) {
		for (IndexedClassExpression ice : axiom.getInconsistentMembers())
			index.add(ice, new ContradictionFromDisjointnessRule());
	}

	public static void removeRulesFor(IndexedDisjointnessAxiom axiom,
			ModifiableOntologyIndex index) {
		for (IndexedClassExpression ice : axiom.getInconsistentMembers())
			index.remove(ice, new ContradictionFromDisjointnessRule());
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public void apply(IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		LOGGER_.trace("Applying {} to {}", NAME_, context);

		writer.produce(context, Contradiction.getInstance());
	}

	@Override
	public boolean addTo(Chain<ChainableSubsumerRule> ruleChain) {
		ContradictionFromDisjointnessRule rule = ruleChain.getCreate(MATCHER_,
				FACTORY_);
		rule.contradictionCounter_ += this.contradictionCounter_;
		return this.contradictionCounter_ != 0;
	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
		ContradictionFromDisjointnessRule rule = ruleChain.find(MATCHER_);
		if (rule == null) {
			return false;
		}
		rule.contradictionCounter_ -= this.contradictionCounter_;
		if (rule.isEmpty())
			ruleChain.remove(MATCHER_);
		return this.contradictionCounter_ != 0;
	}

	@Override
	public void accept(LinkedSubsumerRuleVisitor visitor,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		visitor.visit(this, premise, context, writer);
	}

	protected boolean isEmpty() {
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