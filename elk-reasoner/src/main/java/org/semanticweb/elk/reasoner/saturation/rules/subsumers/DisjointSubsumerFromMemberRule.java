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

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.CompositionRuleVisitor;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ModifiableLinkImpl;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The composition rule producing {@link DisjointSubsumer} when processing an
 * {@link IndexedClassExpression} that is present in an
 * {@link IndexedDisjointnessAxiom} of this {@link DisjointSubsumer} exactly
 * once.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 */
public class DisjointSubsumerFromMemberRule extends
		ModifiableLinkImpl<ChainableRule<IndexedClassExpression>> implements
		ChainableRule<IndexedClassExpression> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(DisjointSubsumerFromMemberRule.class);

	private static final String NAME_ = "DisjointClasses Introduction";

	/**
	 * Set of relevant {@link IndexedDisjointnessAxiom}s in which the member,
	 * for which this rule is registered, appears.
	 */
	private final Set<IndexedDisjointnessAxiom> disjointnessAxioms_;

	private DisjointSubsumerFromMemberRule(
			ChainableRule<IndexedClassExpression> tail) {
		super(tail);
		disjointnessAxioms_ = new ArrayHashSet<IndexedDisjointnessAxiom>();
	}

	private DisjointSubsumerFromMemberRule(IndexedDisjointnessAxiom axiom) {
		this((ChainableRule<IndexedClassExpression>) null);
		disjointnessAxioms_.add(axiom);
	}

	public static void addRulesFor(IndexedDisjointnessAxiom axiom,
			ModifiableOntologyIndex index) {
		for (IndexedClassExpression ice : axiom.getDisjointMembers())
			index.add(ice, new DisjointSubsumerFromMemberRule(axiom));
	}

	public static void removeRulesFor(IndexedDisjointnessAxiom axiom,
			ModifiableOntologyIndex index) {
		for (IndexedClassExpression ice : axiom.getDisjointMembers())
			index.remove(ice, new DisjointSubsumerFromMemberRule(axiom));
	}

	// TODO: hide this method
	public Set<IndexedDisjointnessAxiom> getDisjointnessAxioms() {
		return disjointnessAxioms_;
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public void accept(CompositionRuleVisitor visitor,
			IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		visitor.visit(this, premise, context, writer);
	}

	@Override
	public void apply(IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		LOGGER_.trace("Applying {} to {}", NAME_, context);

		for (IndexedDisjointnessAxiom disAxiom : disjointnessAxioms_)
			writer.produce(context, new DisjointSubsumer(disAxiom));
	}

	protected boolean isEmpty() {
		return disjointnessAxioms_.isEmpty();
	}

	@Override
	public boolean addTo(Chain<ChainableRule<IndexedClassExpression>> ruleChain) {
		DisjointSubsumerFromMemberRule rule = ruleChain.getCreate(MATCHER_,
				FACTORY_);
		return rule.disjointnessAxioms_.addAll(this.disjointnessAxioms_);
	}

	@Override
	public boolean removeFrom(
			Chain<ChainableRule<IndexedClassExpression>> ruleChain) {
		DisjointSubsumerFromMemberRule rule = ruleChain.find(MATCHER_);
		boolean changed = false;
		if (rule != null) {
			changed = rule.disjointnessAxioms_
					.removeAll(this.disjointnessAxioms_);
			if (rule.isEmpty())
				ruleChain.remove(MATCHER_);
		}
		return changed;
	}

	private static Matcher<ChainableRule<IndexedClassExpression>, DisjointSubsumerFromMemberRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableRule<IndexedClassExpression>, DisjointSubsumerFromMemberRule>(
			DisjointSubsumerFromMemberRule.class);

	private static ReferenceFactory<ChainableRule<IndexedClassExpression>, DisjointSubsumerFromMemberRule> FACTORY_ = new ReferenceFactory<ChainableRule<IndexedClassExpression>, DisjointSubsumerFromMemberRule>() {
		@Override
		public DisjointSubsumerFromMemberRule create(
				ChainableRule<IndexedClassExpression> tail) {
			return new DisjointSubsumerFromMemberRule(tail);
		}
	};
}