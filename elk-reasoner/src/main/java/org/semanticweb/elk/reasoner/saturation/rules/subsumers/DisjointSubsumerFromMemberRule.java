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
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DisjointSubsumerFromSubsumer;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * A {@link ChainableSubsumerRule} producing {@link DisjointSubsumer} when
 * processing an {@link IndexedClassExpression} that is present in an
 * {@link IndexedDisjointnessAxiom} of this {@link DisjointSubsumer} exactly
 * once.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 */
public class DisjointSubsumerFromMemberRule extends
		AbstractChainableSubsumerRule {

	public static final String NAME = "DisjointClasses Introduction";

	/**
	 * Set of relevant {@link IndexedDisjointnessAxiom}s in which the member,
	 * for which this rule is registered, appears.
	 */
	private final Set<IndexedDisjointnessAxiom> disjointnessAxioms_;

	private DisjointSubsumerFromMemberRule(ChainableSubsumerRule tail) {
		super(tail);
		disjointnessAxioms_ = new ArrayHashSet<IndexedDisjointnessAxiom>();
	}

	private DisjointSubsumerFromMemberRule(IndexedDisjointnessAxiom axiom) {
		this((ChainableSubsumerRule) null);
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
		return NAME;
	}

	@Override
	public void accept(LinkedSubsumerRuleVisitor visitor,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

	@Override
	public void apply(IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		for (IndexedDisjointnessAxiom disAxiom : disjointnessAxioms_)
			producer.produce(premises.getRoot(), new DisjointSubsumerFromSubsumer(disAxiom, premise));
	}

	protected boolean isEmpty() {
		return disjointnessAxioms_.isEmpty();
	}

	@Override
	public boolean addTo(Chain<ChainableSubsumerRule> ruleChain) {
		DisjointSubsumerFromMemberRule rule = ruleChain.getCreate(MATCHER_,
				FACTORY_);
		return rule.disjointnessAxioms_.addAll(this.disjointnessAxioms_);
	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
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

	private static Matcher<ChainableSubsumerRule, DisjointSubsumerFromMemberRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableSubsumerRule, DisjointSubsumerFromMemberRule>(
			DisjointSubsumerFromMemberRule.class);

	private static ReferenceFactory<ChainableSubsumerRule, DisjointSubsumerFromMemberRule> FACTORY_ = new ReferenceFactory<ChainableSubsumerRule, DisjointSubsumerFromMemberRule>() {
		@Override
		public DisjointSubsumerFromMemberRule create(ChainableSubsumerRule tail) {
			return new DisjointSubsumerFromMemberRule(tail);
		}
	};
}