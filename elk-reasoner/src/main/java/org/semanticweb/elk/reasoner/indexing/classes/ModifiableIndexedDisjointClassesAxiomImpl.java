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
package org.semanticweb.elk.reasoner.indexing.classes;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedClassExpressionListEntry;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.DisjointSubsumerFromMemberRule;

/**
 * Implements {@link StructuralIndexedClassExpressionListEntry}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <A>
 *            the type of the {@link ElkAxiom} from which this axiom originates
 */
public class ModifiableIndexedDisjointClassesAxiomImpl<A extends ElkAxiom>
		extends
			IndexedDisjointClassesAxiomImpl<A, ModifiableIndexedClassExpressionList>
		implements
			ModifiableIndexedDisjointClassesAxiom {

	protected ModifiableIndexedDisjointClassesAxiomImpl(A originalAxiom,
			ModifiableIndexedClassExpressionList members) {
		super(originalAxiom, members);
	}

	@Override
	public boolean addOccurrence(ModifiableOntologyIndex index) {
		if (!DisjointSubsumerFromMemberRule.addRulesFor(this, index,
				getOriginalAxiom())) {
			return false;
		}
		return true;
	}

	@Override
	public boolean removeOccurrence(ModifiableOntologyIndex index) {
		if (!DisjointSubsumerFromMemberRule.removeRulesFor(this, index,
				getOriginalAxiom())) {
			// revert the changes
			return false;
		}
		return true;
	}
	
	@Override
	public <O> O accept(ModifiableIndexedAxiom.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
