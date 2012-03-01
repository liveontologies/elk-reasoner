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
package org.semanticweb.elk.reasoner.indexing.entries;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;

/**
 * Implements a view for instances of {@link IndexedObjectProperty}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            The type of the elements in the set where this entry is used
 * 
 * @param <K>
 *            the type of the wrapped indexed object used as the key of the
 *            entry
 */
public class IndexedObjectPropertyEntry<T, K extends IndexedObjectProperty>
		extends IndexedPropertyChainEntry<T, K> {

	public IndexedObjectPropertyEntry(K representative) {
		super(representative);
	}

	@Override
	public int computeHashCode() {
		return combinedHashCode(IndexedClassEntry.class, this.key
				.getElkObjectProperty().getIri());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof IndexedObjectPropertyEntry<?, ?>) {
			IndexedObjectPropertyEntry<?, ?> otherEntry = (IndexedObjectPropertyEntry<?, ?>) other;
			return this.key.getElkObjectProperty().getIri()
					.equals(otherEntry.key.getElkObjectProperty().getIri());
		}
		return false;
	}
}
