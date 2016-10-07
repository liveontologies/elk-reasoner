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
 * {@link IndexedObjectIntersectionOf} when processing its first conjunct
 * {@link IndexedClassExpression} and when the second conjunct is contained in
 * the {@link Context}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see ObjectIntersectionFromFirstConjunctRule
 */
public class ObjectIntersectionFromSecondConjunctRule extends
		AbstractObjectIntersectionFromConjunctRule {

	public static final String NAME = "ObjectIntersectionOf From 2nd Conjunct";

	ObjectIntersectionFromSecondConjunctRule(ChainableSubsumerRule tail) {
		super(tail);
	}

	ObjectIntersectionFromSecondConjunctRule(IndexedClassExpression conjunct,
			IndexedObjectIntersectionOf conjunction) {
		super(conjunct, conjunction);
	}

	@Override
	public String toString() {
		return NAME;
	}

	/**
	 * Add {@link ObjectIntersectionFromSecondConjunctRule}s for the given
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
				conjunction.getFirstConjunct(),
				new ObjectIntersectionFromSecondConjunctRule(conjunction
						.getSecondConjunct(), conjunction));
	}

	/**
	 * Removes {@link ObjectIntersectionFromSecondConjunctRule}s for the given
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
				conjunction.getFirstConjunct(),
				new ObjectIntersectionFromSecondConjunctRule(conjunction
						.getSecondConjunct(), conjunction));
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

	private static final Matcher<ChainableSubsumerRule, ObjectIntersectionFromSecondConjunctRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableSubsumerRule, ObjectIntersectionFromSecondConjunctRule>(
			ObjectIntersectionFromSecondConjunctRule.class);

	private static final ReferenceFactory<ChainableSubsumerRule, ObjectIntersectionFromSecondConjunctRule> FACTORY_ = new ReferenceFactory<ChainableSubsumerRule, ObjectIntersectionFromSecondConjunctRule>() {
		@Override
		public ObjectIntersectionFromSecondConjunctRule create(
				ChainableSubsumerRule tail) {
			return new ObjectIntersectionFromSecondConjunctRule(tail);
		}
	};

}
