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

import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedEntity;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomVisitor;

/**
 * Implements {@link ModifiableIndexedDeclarationAxiom}
 * 
 * @author "Yevgeny Kazakov"
 *
 */
class ModifiableIndexedDeclarationAxiomImpl extends ModifiableIndexedAxiomImpl
		implements ModifiableIndexedDeclarationAxiom {

	private final ModifiableIndexedEntity entity_;

	ModifiableIndexedDeclarationAxiomImpl(ModifiableIndexedEntity entity) {
		this.entity_ = entity;
	}

	@Override
	public final ModifiableIndexedEntity getEntity() {
		return this.entity_;
	}

	@Override
	public final String toStringStructural() {
		return "Declaration(" + entity_.getEntityType() + "(" + this.entity_
				+ "))";
	}

	@Override
	public final boolean updateOccurrenceNumbers(ModifiableOntologyIndex index,
			int increment) {
		// declarations do not have any semantic meaning, just make sure that
		// the entity occurs in the ontology
		return true;
	}

	@Override
	public final <O> O accept(IndexedAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

}