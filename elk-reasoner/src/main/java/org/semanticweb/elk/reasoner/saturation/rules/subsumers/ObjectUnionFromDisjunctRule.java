package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusion;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectUnionOf;
import org.semanticweb.elk.reasoner.saturation.rules.ClassInferenceProducer;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ChainableSubsumerRule} producing {@link SubClassInclusion} for an
 * {@link IndexedObjectUnionOf} when processing one of its disjunct
 * {@link IndexedClassExpression}
 * 
 * @author "Yevgeny Kazakov"
 */
public class ObjectUnionFromDisjunctRule extends AbstractChainableSubsumerRule {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ObjectUnionFromDisjunctRule.class);

	public static final String NAME = "ObjectUnionOf Introduction";

	/**
	 * All disjunctions containing the disjunct for which this rule is
	 * registered
	 */
	private final List<IndexedObjectUnionOf> disjunctions_;

	/**
	 * The position of the disjunct for which this rule is registered in the
	 * respective disjunction
	 */
	private final List<Integer> positions_;

	private ObjectUnionFromDisjunctRule(ChainableSubsumerRule tail) {
		super(tail);
		this.disjunctions_ = new ArrayList<IndexedObjectUnionOf>();
		this.positions_ = new ArrayList<Integer>();
	}

	private ObjectUnionFromDisjunctRule(IndexedObjectUnionOf disjunction,
			int position) {
		this((ChainableSubsumerRule) null);
		this.disjunctions_.add(disjunction);
		this.positions_.add(position);
	}

	public static boolean addRulesFor(
			ModifiableIndexedObjectUnionOf disjunction,
			ModifiableOntologyIndex index) {
		boolean success = true;
		int added = 0;
		List<? extends ModifiableIndexedClassExpression> disjuncts = disjunction
				.getDisjuncts();
		for (int pos = 0; pos < disjuncts.size(); pos++) {
			ModifiableIndexedClassExpression disjunct = disjuncts.get(pos);
			if (index.add(disjunct,
					new ObjectUnionFromDisjunctRule(disjunction, pos))) {
				added++;
			} else {
				success = false;
				break;
			}
		}
		if (success)
			return true;
		// else revert the changes made
		for (int pos = 0; pos < disjuncts.size(); pos++) {
			if (added == 0)
				break;
			// else
			added--;
			index.remove(disjuncts.get(pos),
					new ObjectUnionFromDisjunctRule(disjunction, pos));
		}
		return false;
	}

	public static boolean removeRulesFor(
			ModifiableIndexedObjectUnionOf disjunction,
			ModifiableOntologyIndex index) {
		boolean success = true;
		int removed = 0;
		List<? extends ModifiableIndexedClassExpression> disjuncts = disjunction
				.getDisjuncts();
		for (int pos = 0; pos < disjuncts.size(); pos++) {
			ModifiableIndexedClassExpression disjunct = disjuncts.get(pos);
			if (index.remove(disjunct,
					new ObjectUnionFromDisjunctRule(disjunction, pos))) {
				removed++;
			} else {
				success = false;
				break;
			}
		}
		if (success)
			return true;
		// else revert the changes made
		for (int pos = 0; pos < disjuncts.size(); pos++) {
			if (removed == 0)
				break;
			// else
			removed--;
			index.add(disjuncts.get(pos),
					new ObjectUnionFromDisjunctRule(disjunction, pos));
		}
		return false;
	}

	@Override
	public String toString() {
		return NAME;
	}

	@Override
	public void accept(LinkedSubsumerRuleVisitor<?> visitor,
			IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

	@Deprecated
	public Collection<IndexedObjectUnionOf> getDisjunctions() {
		return disjunctions_;
	}

	@Override
	public void apply(IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		for (int i = 0; i < disjunctions_.size(); i++) {
			producer.produce(new SubClassInclusionComposedObjectUnionOf(
					premises.getRoot(), disjunctions_.get(i),
					positions_.get(i)));
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
		ObjectUnionFromDisjunctRule rule = ruleChain.getCreate(MATCHER_,
				FACTORY_);
		boolean success = true;
		int added = 0;
		for (int i = 0; i < disjunctions_.size(); i++) {
			IndexedObjectUnionOf disjunction = disjunctions_.get(i);
			int position = positions_.get(i);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("{}: adding to {} matching disjunct position: {}",
						disjunction, NAME, position);
			}
			if (rule.disjunctions_.add(disjunction)) {
				rule.positions_.add(position);
			} else {
				success = false;
				break;
			}
		}
		if (success) {
			return true;
		}
		// else revert all changes
		for (int i = 0; i < disjunctions_.size(); i++) {
			if (added == 0)
				break;
			added--;
			IndexedObjectUnionOf disjunction = disjunctions_.get(i);
			int position = positions_.get(i);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace(
						"{}: removing to {} matching disjunct position: {} [revert]",
						disjunction, NAME, position);
			}
			int j = rule.indexOf(disjunction, position);
			rule.disjunctions_.remove(j);
			rule.positions_.remove(j);
		}
		return false;
	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
		if (isEmpty())
			return true;
		ObjectUnionFromDisjunctRule rule = ruleChain.find(MATCHER_);
		if (rule == null)
			return false;
		// else
		boolean success = true;
		int removed = 0;
		for (int i = 0; i < disjunctions_.size(); i++) {
			IndexedObjectUnionOf disjunction = disjunctions_.get(i);
			int position = positions_.get(i);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace(
						"{}: removing to {} matching disjunct position: {} [revert]",
						disjunction, NAME, position);
			}
			int j = rule.indexOf(disjunction, position);
			if (j >= 0) {
				rule.disjunctions_.remove(j);
				rule.positions_.remove(j);
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
		for (int i = 0; i < disjunctions_.size(); i++) {
			if (removed == 0)
				break;
			removed--;
			IndexedObjectUnionOf disjunction = disjunctions_.get(i);
			int position = positions_.get(i);
			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace(
						"{}: adding to {} matching disjunct position: {} [revert]",
						disjunction, NAME, position);
			}
			rule.disjunctions_.add(disjunction);
			rule.positions_.add(position);
		}
		return false;
	}

	private int indexOf(IndexedObjectUnionOf disjunction, int position) {
		for (int i = 0; i < disjunctions_.size(); i++) {
			if (disjunctions_.get(i).equals(disjunction)
					&& positions_.get(i).equals(position))
				return i;
		}
		// else not found
		return -1;
	}

	/**
	 * @return {@code true} if this rule never does anything
	 */
	private boolean isEmpty() {
		return disjunctions_.isEmpty();
	}

	private static final Matcher<ChainableSubsumerRule, ObjectUnionFromDisjunctRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableSubsumerRule, ObjectUnionFromDisjunctRule>(
			ObjectUnionFromDisjunctRule.class);

	private static final ReferenceFactory<ChainableSubsumerRule, ObjectUnionFromDisjunctRule> FACTORY_ = new ReferenceFactory<ChainableSubsumerRule, ObjectUnionFromDisjunctRule>() {
		@Override
		public ObjectUnionFromDisjunctRule create(ChainableSubsumerRule tail) {
			return new ObjectUnionFromDisjunctRule(tail);
		}
	};

}
