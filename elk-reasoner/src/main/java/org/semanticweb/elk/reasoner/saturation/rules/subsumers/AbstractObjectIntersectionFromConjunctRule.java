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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.LazySetIntersection;

/**
 * A skeleton for a {@link ChainableSubsumerRule} that produces {@link SubClassInclusion}
 * for an {@link IndexedObjectIntersectionOf} when processing one of its
 * conjunct {@link IndexedClassExpression} and when the other conjunct is
 * contained in the {@link Context}
 * 
 * @author "Yevgeny Kazakov"
 */
public abstract class AbstractObjectIntersectionFromConjunctRule extends
		AbstractChainableSubsumerRule {

	private final Map<IndexedClassExpression, IndexedObjectIntersectionOf> conjunctionsByConjunct_;

	AbstractObjectIntersectionFromConjunctRule(ChainableSubsumerRule tail) {
		super(tail);
		this.conjunctionsByConjunct_ = new ArrayHashMap<IndexedClassExpression, IndexedObjectIntersectionOf>(
				4);
	}

	AbstractObjectIntersectionFromConjunctRule(IndexedClassExpression conjunct,
			IndexedObjectIntersectionOf conjunction) {
		this(null);
		this.conjunctionsByConjunct_.put(conjunct, conjunction);
	}

	@Deprecated
	public Map<IndexedClassExpression, IndexedObjectIntersectionOf> getConjunctionsByConjunct() {
		return conjunctionsByConjunct_;
	}

	@Override
	public void apply(IndexedClassExpression premise, ContextPremises premises,
			ClassConclusionProducer producer) {
		for (IndexedClassExpression common : new LazySetIntersection<IndexedClassExpression>(
				conjunctionsByConjunct_.keySet(),
				premises.getComposedSubsumers())) {
			producer.produce(new SubClassInclusionComposedObjectIntersectionOf(premises.getRoot(),
					conjunctionsByConjunct_.get(common)));
		}
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	public boolean addTo(AbstractObjectIntersectionFromConjunctRule rule) {
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

	public boolean removeFrom(AbstractObjectIntersectionFromConjunctRule rule) {
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
	boolean isEmpty() {
		return conjunctionsByConjunct_.isEmpty();
	}

}
