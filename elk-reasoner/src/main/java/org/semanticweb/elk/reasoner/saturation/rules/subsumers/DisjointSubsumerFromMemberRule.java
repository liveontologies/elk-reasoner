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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerFromSubsumer;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ChainableSubsumerRule} producing {@link DisjointSubsumer} when
 * processing an {@link IndexedClassExpression} that is present in an
 * {@link IndexedDisjointClassesAxiom} of this {@link DisjointSubsumer} exactly
 * once.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 */
public class DisjointSubsumerFromMemberRule extends
		AbstractChainableSubsumerRule {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(DisjointSubsumerFromMemberRule.class);

	public static final String NAME = "DisjointClasses Introduction";

	/**
	 * The {@link IndexedDisjointClassesAxiom}s in which the member,
	 * for which this rule is registered, appears.
	 */
	private final List<IndexedDisjointClassesAxiom> axioms_;
	
	/**
	 * The position in the member list of the respective
	 * {@link IndexedDisjointClassesAxiom} in which it appears 
	 */
	private final List<Integer> positions_;

	/**
	 * The {@link ElkAxiom} that are responsible for the respective inconsistent
	 * axiom
	 */
	private final List<ElkAxiom> reasons_;

	private DisjointSubsumerFromMemberRule(ChainableSubsumerRule tail) {
		super(tail);
		this.axioms_ = new ArrayList<IndexedDisjointClassesAxiom>(1);
		this.positions_ = new ArrayList<Integer>(1);
		this.reasons_ = new ArrayList<ElkAxiom>(1);
	}

	private DisjointSubsumerFromMemberRule(IndexedDisjointClassesAxiom axiom,
			int position, ElkAxiom reason) {
		this((ChainableSubsumerRule) null);
		axioms_.add(axiom);
		positions_.add(position);
		reasons_.add(reason);
	}

	public static boolean addRulesFor(
			ModifiableIndexedDisjointClassesAxiom axiom,
			ModifiableOntologyIndex index, ElkAxiom reason) {
		boolean success = true;
		int added = 0;
		List<? extends ModifiableIndexedClassExpression> members =
				axiom.getMembers();
		for (int pos = 0; pos < members.size(); pos++) {
			ModifiableIndexedClassExpression ice = members.get(pos); 
			if (index.add(ice,
					new DisjointSubsumerFromMemberRule(axiom, pos, reason))) {
				added++;
			} else {
				success = false;
				break;
			}
		}
		if (success)
			return true;
		// else revert the changes made
		for (int pos = 0; pos < members.size(); pos++) {
			ModifiableIndexedClassExpression ice = members.get(pos);
			if (added == 0)
				break;
			// else
			added--;
			index.remove(ice, new DisjointSubsumerFromMemberRule(axiom, pos, reason));
		}
		return false;
	}

	public static boolean removeRulesFor(
			ModifiableIndexedDisjointClassesAxiom axiom,
			ModifiableOntologyIndex index, ElkAxiom reason) {
		boolean success = true;
		int removed = 0;
		List<? extends ModifiableIndexedClassExpression> members =
				axiom.getMembers();
		for (int pos = 0; pos < members.size(); pos++) {
			ModifiableIndexedClassExpression ice = members.get(pos);
			if (index.remove(ice, new DisjointSubsumerFromMemberRule(axiom, pos,
					reason))) {
				removed++;
			} else {
				success = false;
				break;
			}
		}
		if (success)
			return true;
		// else revert the changes made
		for (int pos = 0; pos < members.size(); pos++) {
			ModifiableIndexedClassExpression ice = members.get(pos);
			if (removed == 0)
				break;
			// else
			removed--;
			index.add(ice, new DisjointSubsumerFromMemberRule(axiom, pos, reason));
		}
		return false;
	}

	@Deprecated
	public List<IndexedDisjointClassesAxiom> getDisjointnessAxioms() {
		return axioms_;
	}

	@Override
	public String toString() {
		return NAME;
	}

	@Override
	public void accept(LinkedSubsumerRuleVisitor<?> visitor,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

	@Override
	public void apply(IndexedClassExpression member, ContextPremises premises,
			ConclusionProducer producer) {
		for (int i = 0; i < axioms_.size(); i++) {
			producer.produce(new DisjointSubsumerFromSubsumer(
					premises.getRoot(), axioms_.get(i), positions_.get(i),
					reasons_.get(i)));
		}
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	@Override
	public boolean addTo(Chain<ChainableSubsumerRule> ruleChain) {
		if (isEmpty())
			return true;
		DisjointSubsumerFromMemberRule rule = ruleChain.getCreate(MATCHER_,
				FACTORY_);
		boolean success = true;
		int added = 0;
		for (int i = 0; i < axioms_.size(); i++) {
			IndexedDisjointClassesAxiom axiom = axioms_.get(i);
			int position = positions_.get(i);
			ElkAxiom reason = reasons_.get(i);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("{}: adding to {} reason: {}", axiom, NAME,
						reason);
			}
			if (rule.axioms_.add(axiom)) {
				rule.positions_.add(position);
				rule.reasons_.add(reason);
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
		for (int i = 0; i < axioms_.size(); i++) {
			if (added == 0)
				break;
			added--;
			IndexedDisjointClassesAxiom axiom = axioms_.get(i);
			ElkAxiom reason = reasons_.get(i);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("{}: removing from {} reason: {}", axiom, NAME,
						reason);
			}
			int j = rule.indexOf(axiom, reason);
			rule.axioms_.remove(j);
			rule.positions_.remove(j);
			rule.reasons_.remove(j);
		}
		return false;
	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
		if (isEmpty())
			return true;
		DisjointSubsumerFromMemberRule rule = ruleChain.find(MATCHER_);
		if (rule == null)
			return false;
		// else
		boolean success = true;
		int removed = 0;
		for (int i = 0; i < axioms_.size(); i++) {
			IndexedDisjointClassesAxiom axiom = axioms_.get(i);
			ElkAxiom reason = reasons_.get(i);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("{}: removing from {} reason: {}", axiom, NAME,
						reason);
			}
			int j = rule.indexOf(axiom, reason);
			if (j >= 0) {
				rule.axioms_.remove(j);
				rule.positions_.remove(j);
				rule.reasons_.remove(j);
				removed++;
			} else {
				success = false;
				break;
			}
		}
		if (success) {
			if (rule.isEmpty()) {
				ruleChain.remove(MATCHER_);
				LOGGER_.trace("{}: removed ", NAME);
			}
			return true;
		}
		// else revert all changes
		for (int i = 0; i < axioms_.size(); i++) {
			if (removed == 0)
				break;
			removed--;
			IndexedDisjointClassesAxiom axiom = axioms_.get(i);
			int position = positions_.get(i);
			ElkAxiom reason = reasons_.get(i);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("{}: adding to {} reason: {} [revert]", axiom,
						NAME, reason);
			}
			rule.axioms_.add(axiom);
			rule.positions_.add(position);
			rule.reasons_.add(reason);
		}
		return false;
	}

	private int indexOf(IndexedDisjointClassesAxiom axiom, ElkAxiom reason) {
		for (int i = 0; i < axioms_.size(); i++) {
			if (axioms_.get(i).equals(axiom) && reasons_.get(i).equals(reason))
				return i;
		}
		// else not found
		return -1;
	}

	private boolean isEmpty() {
		return axioms_.isEmpty();
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
