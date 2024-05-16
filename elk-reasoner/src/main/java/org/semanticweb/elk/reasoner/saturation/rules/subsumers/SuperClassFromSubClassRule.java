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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusion;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionExpandedSubClassOf;
import org.semanticweb.elk.reasoner.saturation.rules.ClassInferenceProducer;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * A {@link ChainableSubsumerRule} producing {@link SubClassInclusion} for the super
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

	private final List<IndexedClassExpression> toldSubsumers_;

	private final List<ElkAxiom> reasons_;

	SuperClassFromSubClassRule(ChainableSubsumerRule tail) {
		super(tail);
		this.toldSubsumers_ = new ArrayList<IndexedClassExpression>(1);
		this.reasons_ = new ArrayList<ElkAxiom>(1);
	}

	SuperClassFromSubClassRule(IndexedClassExpression ice, ElkAxiom reason) {
		this(null);
		this.toldSubsumers_.add(ice);
		this.reasons_.add(reason);
	}

	public static boolean addRuleFor(ModifiableIndexedSubClassOfAxiom axiom,
			ModifiableOntologyIndex index, ElkAxiom reason) {
		return index.add(axiom.getSubClass(), new SuperClassFromSubClassRule(
				axiom.getSuperClass(), reason));
	}

	public static boolean removeRuleFor(ModifiableIndexedSubClassOfAxiom axiom,
			ModifiableOntologyIndex index, ElkAxiom reason) {
		return index.remove(axiom.getSubClass(),
				new SuperClassFromSubClassRule(axiom.getSuperClass(), reason));
	}

	@Deprecated
	public Collection<IndexedClassExpression> getToldSubsumers() {
		return toldSubsumers_;
	}

	/**
	 * 
	 * @param subsumer
	 *            {@link IndexedClassExpression} for which to retrieve the
	 *            resposible {@link ElkAxiom}
	 * @return the {@link ElkAxiom} that is responsible for the given told
	 *         subsumer or {@code null} if such an axiom does not exist
	 */
	public ElkAxiom getReasonForSubsumer(IndexedClassExpression subsumer) {
		for (int i = 0; i < toldSubsumers_.size(); i++) {
			if (subsumer == toldSubsumers_.get(i)) {
				return reasons_.get(i);
			}
		}
		// not found
		return null;
	}

	@Override
	public String toString() {
		return NAME;
	}

	@Override
	public void apply(IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		for (int i = 0; i < toldSubsumers_.size(); i++) {
			producer.produce(new SubClassInclusionExpandedSubClassOf(premises.getRoot(),
					premise, toldSubsumers_.get(i), reasons_.get(i)));
		}
	}

	@Override
	public boolean isTracingRule() {
		return true;
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
		for (int i = 0; i < toldSubsumers_.size(); i++) {
			IndexedClassExpression subsumer = toldSubsumers_.get(i);
			ElkAxiom reason = reasons_.get(i);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("{}: adding to {} reason: {}", subsumer, NAME,
						reason);
			}
			if (rule.toldSubsumers_.add(subsumer)) {
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
		for (int i = 0; i < toldSubsumers_.size(); i++) {
			if (added == 0)
				break;
			added--;
			IndexedClassExpression subsumer = toldSubsumers_.get(i);
			ElkAxiom reason = reasons_.get(i);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("{}: removing from {} reason: {} [revert]", subsumer,
						NAME, reason);
			}
			int j = rule.indexOf(subsumer, reason);
			rule.toldSubsumers_.remove(j);
			rule.reasons_.remove(j);
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
		for (int i = 0; i < toldSubsumers_.size(); i++) {
			IndexedClassExpression subsumer = toldSubsumers_.get(i);
			ElkAxiom reason = reasons_.get(i);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("{}: removing from {} reason: {}", subsumer,
						NAME, reason);
			}
			int j = rule.indexOf(subsumer, reason);
			if (j >= 0) {
				rule.toldSubsumers_.remove(j);
				rule.reasons_.remove(j);
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
		for (int i = 0; i < toldSubsumers_.size(); i++) {
			if (removed == 0)
				break;
			removed--;
			IndexedClassExpression subsumer = toldSubsumers_.get(i);
			ElkAxiom reason = reasons_.get(i);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("{}: adding to {} reason: {} [revert]", subsumer,
						NAME, reason);
			}
			rule.toldSubsumers_.add(subsumer);
			rule.reasons_.add(reason);
		}
		return false;
	}

	private int indexOf(IndexedClassExpression subsumer, ElkAxiom reason) {
		for (int i = 0; i < toldSubsumers_.size(); i++) {
			if (toldSubsumers_.get(i).equals(subsumer)
					&& reasons_.get(i).equals(reason))
				return i;
		}
		// else not found
		return -1;
	}

	@Override
	public void accept(LinkedSubsumerRuleVisitor<?> visitor,
			IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

	/**
	 * @return {@code true} if this rule never does anything
	 */
	protected boolean isEmpty() {
		return toldSubsumers_.isEmpty();
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
