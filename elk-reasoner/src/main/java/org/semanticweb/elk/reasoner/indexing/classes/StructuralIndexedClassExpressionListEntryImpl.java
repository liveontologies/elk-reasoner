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

import java.util.List;

import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedClassExpressionListEntry;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedSubObject;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Implements {@link StructuralIndexedClassExpressionListEntry}
 * 
 * @author "Yevgeny Kazakov"
 */
class StructuralIndexedClassExpressionListEntryImpl extends
		StructuralIndexedSubObjectImpl<StructuralIndexedClassExpressionListEntryImpl, StructuralIndexedClassExpressionListEntry<?>>
		implements
		StructuralIndexedClassExpressionListEntry<StructuralIndexedClassExpressionListEntryImpl> {

	/**
	 * The elements of the list
	 */
	private final List<? extends ModifiableIndexedClassExpression> elements_;

	StructuralIndexedClassExpressionListEntryImpl(
			List<? extends ModifiableIndexedClassExpression> members) {
		super(structuralHashCode(members));
		this.elements_ = members;
	}

	@Override
	public final List<? extends ModifiableIndexedClassExpression> getElements() {
		return elements_;
	}

	static int structuralHashCode(
			List<? extends ModifiableIndexedClassExpression> members) {
		return HashGenerator.combinedHashCode(
				StructuralIndexedClassExpressionListEntryImpl.class,
				HashGenerator.combinedHashCode(members));
	}

	@Override
	public StructuralIndexedClassExpressionListEntryImpl structuralEquals(Object other) {
		if (this == other) {
			return this;
		}
		if (other instanceof StructuralIndexedClassExpressionListEntryImpl) {
			StructuralIndexedClassExpressionListEntryImpl secondEntry = (StructuralIndexedClassExpressionListEntryImpl) other;
			if (getElements().equals(secondEntry.getElements()))
				return secondEntry;
		}
		// else
		return null;
	}

	@Override
	public final <O> O accept(IndexedSubObject.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(StructuralIndexedSubObject.Visitor<O> visitor) {
		return visitor.visit(this);
	}

}
