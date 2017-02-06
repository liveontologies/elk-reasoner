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
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedEntity;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;

/**
 * Implements {@link ModifiableIndexedDeclarationAxiom}
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <A>
 *            the type of the {@link ElkAxiom} from which this axiom originates
 * 
 */
class ModifiableIndexedDeclarationAxiomImpl<A extends ElkAxiom>
		extends IndexedDeclarationAxiomImpl<A, ModifiableIndexedEntity>
		implements ModifiableIndexedDeclarationAxiom {

	ModifiableIndexedDeclarationAxiomImpl(A originalAxiom,
			ModifiableIndexedEntity entity) {
		super(originalAxiom, entity);
	}

	/*
	 * declarations do not have any semantic meaning, they just make sure that
	 * the entity occurs in the ontology (corresponding number of times)
	 */

	@Override
	public boolean addOccurrence(ModifiableOntologyIndex index) {
		return true;
	}

	@Override
	public boolean removeOccurrence(ModifiableOntologyIndex index) {
		return true;
	}

	@Override
	public <O> O accept(ModifiableIndexedAxiom.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}