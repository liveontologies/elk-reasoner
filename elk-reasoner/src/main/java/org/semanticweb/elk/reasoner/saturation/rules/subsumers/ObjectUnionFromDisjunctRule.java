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
import org.semanticweb.elk.reasoner.indexing.hierarchy.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
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
 * The composition rule producing {@link Subsumer} for an
 * {@link IndexedObjectUnionOf} when processing one of its disjunct
 * {@link IndexedClassExpression}
 * 
 * @author "Yevgeny Kazakov"
 */
public class ObjectUnionFromDisjunctRule extends
		ModifiableLinkImpl<ChainableRule<IndexedClassExpression>> implements
		ChainableRule<IndexedClassExpression> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ObjectUnionFromDisjunctRule.class);

	private static final String NAME_ = "ObjectUnionOf Introduction";

	/**
	 * All disjunctions containing the disjunct for which this rule is
	 * registered
	 */
	private final Set<IndexedClassExpression> disjunctions_;

	private ObjectUnionFromDisjunctRule(
			ChainableRule<IndexedClassExpression> tail) {
		super(tail);
		disjunctions_ = new ArrayHashSet<IndexedClassExpression>();

	}

	private ObjectUnionFromDisjunctRule(IndexedClassExpression disjunction) {
		this((ChainableRule<IndexedClassExpression>) null);
		this.disjunctions_.add(disjunction);
	}

	public static void addRulesFor(IndexedObjectUnionOf disjunction,
			ModifiableOntologyIndex index) {
		for (IndexedClassExpression disjunct : disjunction.getDisjuncts())
			index.add(disjunct, new ObjectUnionFromDisjunctRule(disjunction));
	}

	public static void removeRulesFor(IndexedObjectUnionOf disjunction,
			ModifiableOntologyIndex index) {
		for (IndexedClassExpression disjunct : disjunction.getDisjuncts())
			index.remove(disjunct, new ObjectUnionFromDisjunctRule(disjunction));
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

	// TODO: hide this method
	public Set<IndexedClassExpression> getDisjunctions() {
		return disjunctions_;
	}

	@Override
	public void apply(IndexedClassExpression premise, Context context,
			SaturationStateWriter writer) {
		LOGGER_.trace("Applying {} to {}", NAME_, context);

		for (IndexedClassExpression disjunction : disjunctions_)
			writer.produce(context, new ComposedSubsumer(disjunction));
	}

	@Override
	public boolean addTo(Chain<ChainableRule<IndexedClassExpression>> ruleChain) {
		ObjectUnionFromDisjunctRule rule = ruleChain.getCreate(MATCHER_,
				FACTORY_);
		return rule.disjunctions_.addAll(this.disjunctions_);
	}

	@Override
	public boolean removeFrom(
			Chain<ChainableRule<IndexedClassExpression>> ruleChain) {
		ObjectUnionFromDisjunctRule rule = ruleChain.find(MATCHER_);
		boolean changed = false;
		if (rule != null) {
			changed = rule.disjunctions_.removeAll(this.disjunctions_);
			if (rule.isEmpty())
				ruleChain.remove(MATCHER_);
		}
		return changed;
	}

	/**
	 * @return {@code true} if this rule never does anything
	 */
	private boolean isEmpty() {
		return disjunctions_.isEmpty();
	}

	private static final Matcher<ChainableRule<IndexedClassExpression>, ObjectUnionFromDisjunctRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableRule<IndexedClassExpression>, ObjectUnionFromDisjunctRule>(
			ObjectUnionFromDisjunctRule.class);

	private static final ReferenceFactory<ChainableRule<IndexedClassExpression>, ObjectUnionFromDisjunctRule> FACTORY_ = new ReferenceFactory<ChainableRule<IndexedClassExpression>, ObjectUnionFromDisjunctRule>() {
		@Override
		public ObjectUnionFromDisjunctRule create(
				ChainableRule<IndexedClassExpression> tail) {
			return new ObjectUnionFromDisjunctRule(tail);
		}
	};

}