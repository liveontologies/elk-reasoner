package org.semanticweb.elk.reasoner.saturation.rules.subsumers;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassInferenceProducer;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * A {@link ChainableSubsumerRule} that produces {@link SubClassInclusion} for an
 * {@link IndexedObjectIntersectionOf} when processing its second conjunct
 * {@link IndexedClassExpression} and when the first conjunct is contained in
 * the {@link Context}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see ObjectIntersectionFromSecondConjunctRule
 */
public class ObjectIntersectionFromFirstConjunctRule extends
		AbstractObjectIntersectionFromConjunctRule {

	public static final String NAME = "ObjectIntersectionOf From 1st Conjunct";

	ObjectIntersectionFromFirstConjunctRule(ChainableSubsumerRule tail) {
		super(tail);
	}

	ObjectIntersectionFromFirstConjunctRule(IndexedClassExpression conjunct,
			IndexedObjectIntersectionOf conjunction) {
		super(conjunct, conjunction);
	}

	@Override
	public String toString() {
		return NAME;
	}

	/**
	 * Add {@link ObjectIntersectionFromFirstConjunctRule}s for the given
	 * {@link ModifiableIndexedObjectIntersectionOf} in the given
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param conjunction
	 * @param index
	 */
	public static boolean addRulesFor(
			ModifiableIndexedObjectIntersectionOf conjunction,
			ModifiableOntologyIndex index) {
		return index.add(
				conjunction.getSecondConjunct(),
				new ObjectIntersectionFromFirstConjunctRule(conjunction
						.getFirstConjunct(), conjunction));
	}

	/**
	 * Removes {@link ObjectIntersectionFromFirstConjunctRule}s for the given
	 * {@link ModifiableIndexedObjectIntersectionOf} in the given
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param conjunction
	 * @param index
	 */
	public static boolean removeRulesFor(
			ModifiableIndexedObjectIntersectionOf conjunction,
			ModifiableOntologyIndex index) {
		return index.remove(
				conjunction.getSecondConjunct(),
				new ObjectIntersectionFromFirstConjunctRule(conjunction
						.getFirstConjunct(), conjunction));
	}

	@Override
	public boolean addTo(Chain<ChainableSubsumerRule> ruleChain) {
		if (isEmpty())
			return true;
		return addTo(ruleChain.getCreate(MATCHER_, FACTORY_));
	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
		if (isEmpty())
			return true;
		AbstractObjectIntersectionFromConjunctRule rule = ruleChain
				.find(MATCHER_);
		if (rule == null)
			return false;
		boolean success = removeFrom(rule);
		if (success && rule.isEmpty()) {
			ruleChain.remove(MATCHER_);
		}
		return success;
	}

	@Override
	public void accept(LinkedSubsumerRuleVisitor<?> visitor,
			IndexedClassExpression premise, ContextPremises premises,
			ClassInferenceProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

	private static final Matcher<ChainableSubsumerRule, ObjectIntersectionFromFirstConjunctRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableSubsumerRule, ObjectIntersectionFromFirstConjunctRule>(
			ObjectIntersectionFromFirstConjunctRule.class);

	private static final ReferenceFactory<ChainableSubsumerRule, ObjectIntersectionFromFirstConjunctRule> FACTORY_ = new ReferenceFactory<ChainableSubsumerRule, ObjectIntersectionFromFirstConjunctRule>() {
		@Override
		public ObjectIntersectionFromFirstConjunctRule create(
				ChainableSubsumerRule tail) {
			return new ObjectIntersectionFromFirstConjunctRule(tail);
		}
	};

}
