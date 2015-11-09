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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.caching.CachedIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.DisjointSubsumerFromMemberRule;

/**
 * Implements {@link CachedIndexedClassExpressionList}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <A>
 *            the type of the {@link ElkAxiom} from which this axiom originates
 */
public class ModifiableIndexedDisjointClassesAxiomImpl<A extends ElkAxiom>
		extends
			ModifiableIndexedAxiomImpl<A>
		implements
			ModifiableIndexedDisjointClassesAxiom {

	/**
	 * the {@link IndexedClassExpression}s stated to be disjoint. Note that
	 * same may appear two times in this list, in which case they should be
	 * inconsistent (disjoint with itself)
	 */
	private final ModifiableIndexedClassExpressionList members_;
	
	protected ModifiableIndexedDisjointClassesAxiomImpl(A originalAxiom,
			ModifiableIndexedClassExpressionList members) {
		super(originalAxiom);
		this.members_ = members;
	}
	
	@Override
	public final ModifiableIndexedClassExpressionList getMembers() {
		return members_;
	}

	@Override
	public boolean addOccurrence(ModifiableOntologyIndex index) {		
		if (!index.updatePositiveOwlNothingOccurrenceNo(1)) {
			return false;
		}
		if (!DisjointSubsumerFromMemberRule.addRulesFor(this, index, getOriginalAxiom())) {
			// revert the changes
			index.updatePositiveOwlNothingOccurrenceNo(-1);
			return false;
		}		
		return true;
	}

	@Override
	public boolean removeOccurrence(ModifiableOntologyIndex index) {
		if (!index.updatePositiveOwlNothingOccurrenceNo(-1)) {
			return false;
		}
		if (!DisjointSubsumerFromMemberRule.removeRulesFor(this, index, getOriginalAxiom())) {
			// revert the changes
			index.updatePositiveOwlNothingOccurrenceNo(1);
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

}
