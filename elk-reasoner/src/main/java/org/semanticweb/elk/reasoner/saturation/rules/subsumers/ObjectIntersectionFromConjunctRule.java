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

import org.semanticweb.elk.owl.exceptions.ElkRuntimeException;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
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
	/*private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ObjectIntersectionFromConjunctRule.class);*/

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
	 * {@link IndexedObjectIntersectionOf} in the given
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param conjunct
	 * @param index
	 */
	public static void addRulesFor(IndexedObjectIntersectionOf conjunct,
			ModifiableOntologyIndex index) {
		IndexedClassExpression firstConjunct = conjunct.getFirstConjunct();
		IndexedClassExpression secondConjunct = conjunct.getSecondConjunct();
		// first negative occurrence of this expression
		index.add(firstConjunct, new ObjectIntersectionFromConjunctRule(
				secondConjunct, conjunct));
		// if both conjuncts are the same, do not index the second time
		if (!secondConjunct.equals(firstConjunct))
			index.add(secondConjunct, new ObjectIntersectionFromConjunctRule(
					firstConjunct, conjunct));
	}

	/**
	 * Removes {@link ObjectIntersectionFromConjunctRule}s for the given
	 * {@link IndexedObjectIntersectionOf} in the given
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param conjunct
	 * @param index
	 */
	public static void removeRulesFor(IndexedObjectIntersectionOf conjunct,
			ModifiableOntologyIndex index) {
		IndexedClassExpression firstConjunct = conjunct.getFirstConjunct();
		IndexedClassExpression secondConjunct = conjunct.getSecondConjunct();
		// first negative occurrence of this expression
		index.remove(firstConjunct, new ObjectIntersectionFromConjunctRule(
				secondConjunct, conjunct));
		// if both conjuncts are the same, do not index the second time
		if (!secondConjunct.equals(firstConjunct))
			index.remove(secondConjunct,
					new ObjectIntersectionFromConjunctRule(firstConjunct,
							conjunct));
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
			//producer.produce(premises.getRoot(), new ComposedSubsumer(conjunctionsByConjunct_.get(common)));
			producer.produce(premises.getRoot(), new ComposedConjunction(premise, common, conjunctionsByConjunct_.get(common)));
		}

	}

	@Override
	public boolean addTo(Chain<ChainableSubsumerRule> ruleChain) {
		ObjectIntersectionFromConjunctRule rule = ruleChain.getCreate(MATCHER_,
				FACTORY_);
		boolean changed = false;

		for (Map.Entry<IndexedClassExpression, IndexedObjectIntersectionOf> entry : conjunctionsByConjunct_
				.entrySet()) {
			changed |= rule.addConjunctionByConjunct(entry.getValue(),
					entry.getKey());
		}

		return changed;

	}

	@Override
	public boolean removeFrom(Chain<ChainableSubsumerRule> ruleChain) {
		ObjectIntersectionFromConjunctRule rule = ruleChain.find(MATCHER_);
		boolean changed = false;

		if (rule != null) {
			for (IndexedClassExpression conjunct : conjunctionsByConjunct_
					.keySet()) {
				changed |= rule.removeConjunctionByConjunct(conjunct);
			}

			if (rule.isEmpty()) {
				ruleChain.remove(MATCHER_);
			}
		}

		return changed;

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
		Object previous = conjunctionsByConjunct_.put(conjunct, conjunction);

		if (previous == null)
			return true;

		throw new ElkRuntimeException("Conjunction " + conjunction
				+ "is already indexed: " + previous);
	}

	private boolean removeConjunctionByConjunct(IndexedClassExpression conjunct) {
		return conjunctionsByConjunct_.remove(conjunct) != null;
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
