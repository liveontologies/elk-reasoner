/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.indexing.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectFilter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromDisjointnessRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.DisjointSubsumerFromMemberRule;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * Implements {@link CachedIndexedDisjointnessAxiom}
 * 
 * @author "Yevgeny Kazakov"
 */
class CachedIndexedDisjointnessAxiomImpl extends
		CachedIndexedAxiomImpl<CachedIndexedDisjointnessAxiom> implements
		CachedIndexedDisjointnessAxiom {

	/**
	 * {@link IndexedClassExpression}s that have at least two equal occurrences
	 * (according to the {@link Object#equals(Object)} method) in this
	 * {@link IndexedDisjointnessAxiom}
	 */
	private final Set<ModifiableIndexedClassExpression> inconsistentMembers_;
	/**
	 * {@link IndexedClassExpression}s that occur exactly once in this
	 * {@link IndexedDisjointnessAxiom}
	 */
	private final Set<ModifiableIndexedClassExpression> disjointMembers_;

	/**
	 * This counts how often this {@link IndexedDisjointnessAxiom} occurs in the
	 * ontology.
	 */
	int occurrenceNo_ = 0;

	CachedIndexedDisjointnessAxiomImpl(
			List<? extends ModifiableIndexedClassExpression> members) {
		this(new Initializer(members));
	}

	private CachedIndexedDisjointnessAxiomImpl(Initializer init) {
		super(CachedIndexedDisjointnessAxiom.Helper.structuralHashCode(
				init.inconsistentMembers_, init.disjointMembers_));
		this.inconsistentMembers_ = init.inconsistentMembers_;
		this.disjointMembers_ = init.disjointMembers_;
	}

	private static class Initializer {
		private final Set<ModifiableIndexedClassExpression> inconsistentMembers_;
		private final Set<ModifiableIndexedClassExpression> disjointMembers_;

		Initializer(List<? extends ModifiableIndexedClassExpression> members) {
			inconsistentMembers_ = new ArrayHashSet<ModifiableIndexedClassExpression>(
					1);
			disjointMembers_ = new ArrayHashSet<ModifiableIndexedClassExpression>(
					2);
			for (ModifiableIndexedClassExpression member : members) {
				if (inconsistentMembers_.contains(member))
					continue;
				if (!disjointMembers_.add(member)) {
					disjointMembers_.remove(member);
					inconsistentMembers_.add(member);
				}
			}
		}
	}

	@Override
	public final boolean occurs() {
		return occurrenceNo_ > 0;
	}

	@Override
	public final Set<ModifiableIndexedClassExpression> getInconsistentMembers() {
		return inconsistentMembers_;
	}

	@Override
	public final Set<ModifiableIndexedClassExpression> getDisjointMembers() {
		return disjointMembers_;
	}

	@Override
	public final CachedIndexedDisjointnessAxiom structuralEquals(Object other) {
		return CachedIndexedDisjointnessAxiom.Helper.structuralEquals(this,
				other);
	}

	@Override
	public final boolean updateOccurrenceNumbers(ModifiableOntologyIndex index,
			int increment, int positiveIncrement, int negativeIncrement) {

		if (occurrenceNo_ == 0 && increment > 0) {
			// first occurrence of this axiom
			if (!ContradictionFromDisjointnessRule.addRulesFor(this, index))
				return false;
			if (!DisjointSubsumerFromMemberRule.addRulesFor(this, index)) {
				// revert the changes
				ContradictionFromDisjointnessRule.removeRulesFor(this, index);
				return false;
			}
		}

		occurrenceNo_ += increment;
		index.updatePositiveOwlNothingOccurrenceNo(increment);

		if (occurrenceNo_ == 0 && increment < 0) {
			// last occurrence of this axiom
			if (!ContradictionFromDisjointnessRule.removeRulesFor(this, index)) {
				// revert the changes
				occurrenceNo_ -= increment;
				return false;
			}
			if (!DisjointSubsumerFromMemberRule.removeRulesFor(this, index)) {
				// revert the changes
				occurrenceNo_ -= increment;
				ContradictionFromDisjointnessRule.addRulesFor(this, index);
				return false;
			}
		}
		// success!
		return true;
	}

	@Override
	public final String toStringStructural() {
		List<IndexedClassExpression> members = new LinkedList<IndexedClassExpression>();
		for (IndexedClassExpression inconsistentMember : inconsistentMembers_) {
			// each inconsistent member is added two times
			members.add(inconsistentMember);
			members.add(inconsistentMember);
		}
		members.addAll(disjointMembers_);
		return "DisjointClasses(" + members + ")";
	}

	@Override
	public final <O> O accept(IndexedAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public CachedIndexedDisjointnessAxiom accept(
			CachedIndexedObjectFilter filter) {
		return filter.filter(this);
	}

}