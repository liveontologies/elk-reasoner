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

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedObjectFilter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.DisjointSubsumerFromMemberRule;

/**
 * Implements {@link CachedIndexedDisjointClassesAxiom}
 * 
 * @author "Yevgeny Kazakov"
 */
class CachedIndexedDisjointClassesAxiomImpl extends
		CachedIndexedAxiomImpl<CachedIndexedDisjointClassesAxiom> implements
		CachedIndexedDisjointClassesAxiom {

	/**
	 * the {@link IndexedClassExpression}s stated to be disjoint. Note that the
	 * same can appear two times in this list, in which case it should be
	 * inconsistent (disjoint with itself)
	 */
	private final List<? extends ModifiableIndexedClassExpression> members_;
	
	/**
	 * This counts how often this {@link IndexedDisjointClassesAxiom} occurs in
	 * the ontology.
	 */
	int totalOccurrenceNo_ = 0;

	CachedIndexedDisjointClassesAxiomImpl(
			List<? extends ModifiableIndexedClassExpression> members) {
		super(CachedIndexedDisjointClassesAxiom.Helper.structuralHashCode(members));
		this.members_ = members;
	}
	
	@Override
	public final boolean occurs() {
		return totalOccurrenceNo_ > 0;
	}

	@Override
	public final List<? extends ModifiableIndexedClassExpression> getMembers() {
		return members_;
	}

	@Override
	public final CachedIndexedDisjointClassesAxiom structuralEquals(Object other) {
		return CachedIndexedDisjointClassesAxiom.Helper.structuralEquals(this,
				other);
	}

	@Override
	public boolean addOccurrence(ModifiableOntologyIndex index, ElkAxiom reason) {
		if (!DisjointSubsumerFromMemberRule.addRulesFor(this, index, reason)) {
			return false;
		}
		if (!index.updatePositiveOwlNothingOccurrenceNo(1)) {
			// revert the changes
			DisjointSubsumerFromMemberRule.removeRulesFor(this, index, reason);
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
		if (!DisjointSubsumerFromMemberRule.removeRulesFor(this, index, reason)) {
			// revert the changes
			index.updatePositiveOwlNothingOccurrenceNo(1);
			totalOccurrenceNo_++;
			return false;
		}
		return true;
	}

	@Override
	public final String toStringStructural() {
		return "DisjointClasses(" + members_ + ")";
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
