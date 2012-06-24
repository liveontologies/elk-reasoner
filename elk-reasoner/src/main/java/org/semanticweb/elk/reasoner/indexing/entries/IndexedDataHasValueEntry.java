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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;

/**
 * Implements an equality view for instances of {@link IndexedClassDataHasValue}
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
public class IndexedDataHasValueEntry<T, K extends IndexedDataHasValue> extends IndexedClassExpressionEntry<T, K> {

	public IndexedDataHasValueEntry(K representative) {
		super(representative);
	}

	@Override
	public int computeHashCode() {
		return combinedHashCode(IndexedDataHasValueEntry.class, this.key.getProperty().getIri(), this.key.getFiller().getLexicalForm());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof IndexedDataHasValueEntry<?, ?>) {
			IndexedDataHasValueEntry<?, ?> otherEntry = (IndexedDataHasValueEntry<?, ?>) other;
			return this.key.getProperty().getIri().equals(otherEntry.key.getProperty().getIri())
					&& this.key.getFiller().equals(otherEntry.key.getFiller())
					&& this.key.getDatatype().equals(otherEntry.key.getDatatype());
		}
		return false;
	}
}