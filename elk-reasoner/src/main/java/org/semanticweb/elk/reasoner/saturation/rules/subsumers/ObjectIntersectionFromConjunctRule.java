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

import java.util.Map;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedConjunction;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * A {@link ChainableSubsumerRule} producing {@link Subsumer} for an
 * {@link IndexedObjectIntersectionOf} when processing one of its conjunct
 * {@link IndexedClassExpression} and when the other conjunct is contained in
 * the {@link Context}
 * 
 * @author "Yevgeny Kazakov"
 */
public class ObjectIntersectionFromConjunctRule extends
		AbstractChainableSubsumerRule {

	// logger for events
	/*
	 * private static final Logger LOGGER_ = LoggerFactory
	 * .getLogger(ObjectIntersectionFromConjunctRule.class);
	 */

	public static final String NAME = "ObjectIntersectionOf Introduction";

	private final Map<IndexedClassExpression, IndexedObjectIntersectionOf> conjunctionsByConjunct_;

	private ObjectIntersectionFromConjunctRule(ChainableSubsumerRule tail) {
		super(tail);
		this.conjunctionsByConjunct_ = new ArrayHashMap<IndexedClassExpression, IndexedObjectIntersectionOf>(
				4);
	}

	private ObjectIntersectionFromConjunctRule(IndexedClassExpression conjunct,
			IndexedObjectIntersectionOf conjunction) {
		this(null);
		this.conjunctionsByConjunct_.put(conjunct, conjunction);
	}

	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * Add {@link ObjectIntersectionFromConjunctRule}s for the given
	 * {@link ModifiableIndexedObjectIntersectionOf} in the given
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param conjunction
	 * @param index
	 */
	public static boolean addRulesFor(
			ModifiableIndexedObjectIntersectionOf conjunction,
			ModifiableOntologyIndex index) {
		ModifiableIndexedClassExpression firstConjunct = conjunction
				.getFirstConjunct();
		ModifiableIndexedClassExpression secondConjunct = conjunction
				.getSecondConjunct();
		if (!index.add(firstConjunct, new ObjectIntersectionFromConjunctRule(
				secondConjunct, conjunction)))
			return false;
		// if both conjuncts are the same, we are done
		if (secondConjunct.equals(firstConjunct))
			return true;
		// else index the second conjunct
		if (index.add(secondConjunct, new ObjectIntersectionFromConjunctRule(
				firstConjunct, conjunction)))
			return true;
		// else revert the changes made
		index.remove(firstConjunct, new ObjectIntersectionFromConjunctRule(
				secondConjunct, conjunction));
		return false;
	}

	/**
	 * Removes {@link ObjectIntersectionFromConjunctRule}s for the given
	 * {@link ModifiableIndexedObjectIntersectionOf} in the given
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param conjunction
	 * @param index
	 */
	public static boolean removeRulesFor(
			ModifiableIndexedObjectIntersectionOf conjunction,
			ModifiableOntologyIndex index) {
		ModifiableIndexedClassExpression firstConjunct = conjunction
				.getFirstConjunct();
		ModifiableIndexedClassExpression secondConjunct = conjunction
				.getSecondConjunct();
		if (!index.remove(firstConjunct,
				new ObjectIntersectionFromConjunctRule(secondConjunct,
						conjunction)))
			return false;
		// if both conjuncts are the same, we are done
		if (secondConjunct.equals(firstConjunct))
			return true;
		// else index the second conjunct
		if (index.remove(secondConjunct,
				new ObjectIntersectionFromConjunctRule(firstConjunct,
						conjunction)))
			return true;
		// else revert the changes made
		index.add(firstConjunct, new ObjectIntersectionFromConjunctRule(
				secondConjunct, conjunction));
		return false;
	}

	// TODO: hide this method
	public Map<IndexedClassExpression, IndexedObjectIntersectionOf> getConjunctionsByConjunct() {
		return conjunctionsByConjunct_;
	}

	@Override
	public void apply(IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		for (IndexedClassExpression common : new LazySetIntersection<IndexedClassExpression>(
				conjunctionsByConjunct_.keySet(), premises.getSubsumers())) {
			// producer.produce(premises.getRoot(), new
			// ComposedSubsumer(conjunctionsByConjunct_.get(common)));
			producer.produce(premises.getRoot(), new ComposedConjunction(
					conjunctionsByConjunct_.get(common)));
		}

	}

	@Override
	public boolean addTo(Chain<ChainableSubsumerRule> ruleChain) {
		if (isEmpty())
			return true;
		ObjectIntersectionFromConjunctRule rule = ruleChain.getCreate(MATCHER_,
				FACTORY_);
		boolean success = true;
		int added = 0;
		for (Map.Entry<IndexedClassExpression, IndexedObjectIntersectionOf> entry : this.conjunctionsByConjunct_
				.entrySet()) {
			if (rule.addConjunctionByConjunct(entry.getValue(), entry.getKey()))
				added++;
			else {
				success = false;
				break;
			}
		}
		if (success)
			return true;
		// else revert all changes
		for (Map.Entry<IndexedClassExpression, IndexedObjectIntersectionOf> entry : this.conjunctionsByConjunct_
				.entrySet()) {
			if (added == 0)
				break;
			added--;
			rule.removeConjunctionByConjunct(entry.getValue(), entry.getKey());
		}
		return false;
	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
		if (isEmpty())
			return true;
		ObjectIntersectionFromConjunctRule rule = ruleChain.find(MATCHER_);
		if (rule == null)
			return false;
		// else
		boolean success = true;
		int removed = 0;
		for (Map.Entry<IndexedClassExpression, IndexedObjectIntersectionOf> entry : this.conjunctionsByConjunct_
				.entrySet()) {
			if (rule.removeConjunctionByConjunct(entry.getValue(),
					entry.getKey()))
				removed++;
			else {
				success = false;
				break;
			}
		}
		if (success) {
			if (rule.isEmpty()) {
				ruleChain.remove(MATCHER_);
			}
			return true;
		}
		// else revert all changes
		for (Map.Entry<IndexedClassExpression, IndexedObjectIntersectionOf> entry : this.conjunctionsByConjunct_
				.entrySet()) {
			if (removed == 0)
				break;
			removed--;
			rule.addConjunctionByConjunct(entry.getValue(), entry.getKey());
		}
		return false;
	}

	@Override
	public void accept(LinkedSubsumerRuleVisitor visitor,
			IndexedClassExpression premise, ContextPremises premises,
			ConclusionProducer producer) {
		visitor.visit(this, premise, premises, producer);
	}

	private boolean addConjunctionByConjunct(
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct) {
		IndexedObjectIntersectionOf previous = conjunctionsByConjunct_.put(
				conjunct, conjunction);
		if (previous == null)
			return true;
		// else revert the change;
		conjunctionsByConjunct_.put(conjunct, previous);
		return false;
	}

	private boolean removeConjunctionByConjunct(
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct) {
		IndexedObjectIntersectionOf previous = conjunctionsByConjunct_
				.remove(conjunct);
		if (previous == conjunction)
			return true;
		// else revert the change
		if (previous != null)
			conjunctionsByConjunct_.put(conjunct, previous);
		return false;
	}

	/**
	 * @return {@code true} if this rule never does anything
	 */
	private boolean isEmpty() {
		return conjunctionsByConjunct_.isEmpty();
	}

	private static final Matcher<ChainableSubsumerRule, ObjectIntersectionFromConjunctRule> MATCHER_ = new SimpleTypeBasedMatcher<ChainableSubsumerRule, ObjectIntersectionFromConjunctRule>(
			ObjectIntersectionFromConjunctRule.class);

	private static final ReferenceFactory<ChainableSubsumerRule, ObjectIntersectionFromConjunctRule> FACTORY_ = new ReferenceFactory<ChainableSubsumerRule, ObjectIntersectionFromConjunctRule>() {
		@Override
		public ObjectIntersectionFromConjunctRule create(
				ChainableSubsumerRule tail) {
			return new ObjectIntersectionFromConjunctRule(tail);
		}
	};

}
