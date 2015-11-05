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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedDisjunction;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * A {@link ChainableSubsumerRule} producing {@link Subsumer} for an
 * {@link IndexedObjectUnionOf} when processing one of its disjunct
 * {@link IndexedClassExpression}
 * 
 * @author "Yevgeny Kazakov"
 */
public class ObjectUnionFromDisjunctRule extends AbstractChainableSubsumerRule {

	public static final String NAME = "ObjectUnionOf Introduction";

	/**
	 * All disjunctions containing the disjunct for which this rule is
	 * registered
	 */
	private final Set<IndexedObjectUnionOf> disjunctions_;

	private ObjectUnionFromDisjunctRule(ChainableSubsumerRule tail) {
		super(tail);
		disjunctions_ = new ArrayHashSet<IndexedObjectUnionOf>();

	}

	private ObjectUnionFromDisjunctRule(IndexedObjectUnionOf disjunction) {
		this((ChainableSubsumerRule) null);
		this.disjunctions_.add(disjunction);
	}

	public static boolean addRulesFor(
			ModifiableIndexedObjectUnionOf disjunction,
			ModifiableOntologyIndex index) {
		boolean success = true;
		int added = 0;
		for (ModifiableIndexedClassExpression disjunct : disjunction
				.getDisjuncts()) {
			if (index.add(disjunct,
					new ObjectUnionFromDisjunctRule(disjunction))) {
				added++;
			} else {
				success = false;
				break;
			}
		}
		if (success)
			return true;
		// else revert the changes made
		for (ModifiableIndexedClassExpression disjunct : disjunction
				.getDisjuncts()) {
			if (added == 0)
				break;
			// else
			added--;
			index.remove(disjunct, new ObjectUnionFromDisjunctRule(disjunction));
		}
		return false;
	}

	public static boolean removeRulesFor(
			ModifiableIndexedObjectUnionOf disjunction,
			ModifiableOntologyIndex index) {
		boolean success = true;
		int removed = 0;
		for (ModifiableIndexedClassExpression disjunct : disjunction
				.getDisjuncts()) {
			if (index.remove(disjunct, new ObjectUnionFromDisjunctRule(
					disjunction))) {
				removed++;
			} else {
				success = false;
				break;
			}
		}
		if (success)
			return true;
		// else revert the changes made
		for (ModifiableIndexedClassExpression disjunct : disjunction
				.getDisjuncts()) {
			if (removed == 0)
				break;
			// else
			removed--;
			index.add(disjunct, new ObjectUnionFromDisjunctRule(disjunction));
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
			ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

	@Deprecated
	public Set<IndexedObjectUnionOf> getDisjunctions() {
		return disjunctions_;
	}

	@Override
	public void apply(IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		for (IndexedObjectUnionOf disjunction : disjunctions_) {
			producer.produce(new ComposedDisjunction(premises.getRoot(),
					premise, disjunction));
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
		ObjectUnionFromDisjunctRule rule = ruleChain.getCreate(MATCHER_,
				FACTORY_);
		rule.disjunctions_.addAll(this.disjunctions_);
		return true;
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
		for (IndexedObjectUnionOf disjunction : this.disjunctions_) {
			if (rule.disjunctions_.remove(disjunction))
				removed++;
			else {
				success = false;
				break;
			}
		}
		if (success) {
			if (rule.isEmpty())
				ruleChain.remove(MATCHER_);
			return true;
		}
		// else revert all changes
		for (IndexedObjectUnionOf disjunction : this.disjunctions_) {
			if (removed == 0)
				break;
			removed--;
			rule.disjunctions_.add(disjunction);
		}
		return false;
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
