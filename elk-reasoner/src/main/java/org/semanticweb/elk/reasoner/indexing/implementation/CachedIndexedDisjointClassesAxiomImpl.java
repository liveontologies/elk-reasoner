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
package org.semanticweb.elk.reasoner.indexing.implementation;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectFilter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromDisjointnessRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.DisjointSubsumerFromMemberRule;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * Implements {@link CachedIndexedDisjointClassesAxiom}
 * 
 * @author "Yevgeny Kazakov"
 */
class CachedIndexedDisjointClassesAxiomImpl extends
		CachedIndexedAxiomImpl<CachedIndexedDisjointClassesAxiom> implements
		CachedIndexedDisjointClassesAxiom {

	/**
	 * {@link IndexedClassExpression}s that have at least two equal occurrences
	 * (according to the {@link Object#equals(Object)} method) in this
	 * {@link IndexedDisjointClassesAxiom}
	 */
	private final Set<ModifiableIndexedClassExpression> inconsistentMembers_;
	/**
	 * {@link IndexedClassExpression}s that occur exactly once in this
	 * {@link IndexedDisjointClassesAxiom}
	 */
	private final Set<ModifiableIndexedClassExpression> disjointMembers_;

	/**
	 * This counts how often this {@link IndexedDisjointClassesAxiom} occurs in
	 * the ontology.
	 */
	int totalOccurrenceNo_ = 0;

	CachedIndexedDisjointClassesAxiomImpl(
			List<? extends ModifiableIndexedClassExpression> members) {
		this(new Initializer(members));
	}

	private CachedIndexedDisjointClassesAxiomImpl(Initializer init) {
		super(CachedIndexedDisjointClassesAxiom.Helper.structuralHashCode(
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
		return totalOccurrenceNo_ > 0;
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
	public final CachedIndexedDisjointClassesAxiom structuralEquals(Object other) {
		return CachedIndexedDisjointClassesAxiom.Helper.structuralEquals(this,
				other);
	}

	@Override
	public boolean addOccurrence(ModifiableOntologyIndex index, ElkAxiom reason) {
		if (!ContradictionFromDisjointnessRule.addRulesFor(this, index, reason))
			return false;
		if (!DisjointSubsumerFromMemberRule.addRulesFor(this, index, reason)) {
			// revert the changes
			ContradictionFromDisjointnessRule.removeRulesFor(this, index,
					reason);
			return false;
		}
		if (!index.updatePositiveOwlNothingOccurrenceNo(1)) {
			// revert the changes
			DisjointSubsumerFromMemberRule.removeRulesFor(this, index, reason);
			ContradictionFromDisjointnessRule.removeRulesFor(this, index,
					reason);
			return false;
		}
		totalOccurrenceNo_++;
		return true;
	}

	@Override
	public boolean removeOccurrence(ModifiableOntologyIndex index,
			ElkAxiom reason) {
		totalOccurrenceNo_--;
		if (totalOccurrenceNo_ < 0) {
			// revert the change
			totalOccurrenceNo_++;
			return false;
		}
		if (!index.updatePositiveOwlNothingOccurrenceNo(-1)) {
			// revert the changes
			totalOccurrenceNo_++;
			return false;
		}
		if (!ContradictionFromDisjointnessRule.removeRulesFor(this, index,
				reason)) {
			// revert the changes
			index.updatePositiveOwlNothingOccurrenceNo(1);
			totalOccurrenceNo_++;
			return false;
		}
		if (!DisjointSubsumerFromMemberRule.removeRulesFor(this, index, reason)) {
			// revert the changes
			ContradictionFromDisjointnessRule.addRulesFor(this, index, reason);
			index.updatePositiveOwlNothingOccurrenceNo(1);
			totalOccurrenceNo_++;
			return false;
		}
		return false;
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
	public CachedIndexedDisjointClassesAxiom accept(
			CachedIndexedObjectFilter filter) {
		return filter.filter(this);
	}

}