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

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.model.IndexedEntity;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedClassEntry;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedSubObject;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Implements {@link StructuralIndexedClassEntry}
 * 
 * @author "Yevgeny Kazakov"
 */
abstract class StructuralIndexedClassEntryImpl extends
		ModifiableIndexedClassEntityImpl<StructuralIndexedClassEntryImpl, StructuralIndexedClassEntry<?>>
		implements
		StructuralIndexedClassEntry<StructuralIndexedClassEntryImpl> {

	/**
	 * The represented {@link ElkClass}
	 */
	private final ElkClass elkClass_;

	StructuralIndexedClassEntryImpl(ElkClass entity) {
		super(structuralHashCode(entity));
		elkClass_ = entity;
	}

	@Override
	public final ElkClass getElkEntity() {
		return elkClass_;
	}
	
	static int structuralHashCode(ElkClass entity) {
		return HashGenerator.combinedHashCode(StructuralIndexedClassEntryImpl.class,
				entity.getIri());
	}

	@Override
	public StructuralIndexedClassEntryImpl structuralEquals(Object other) {
		if (this == other) {
			return this;
		}
		if (other instanceof StructuralIndexedClassEntryImpl) {
			StructuralIndexedClassEntryImpl secondEntry = (StructuralIndexedClassEntryImpl) other;
			if (getElkEntity().getIri()
					.equals(secondEntry.getElkEntity().getIri()))
				return secondEntry;
		}
		return null;
	}

	@Override
	public final <O> O accept(IndexedEntity.Visitor<O> visitor) {
		return accept((IndexedClassEntity.Visitor<O>) visitor);
	}

	@Override
	public <O> O accept(StructuralIndexedSubObject.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}