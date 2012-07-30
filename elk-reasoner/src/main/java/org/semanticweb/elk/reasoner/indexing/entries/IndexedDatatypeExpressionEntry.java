/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;

/**
 * 
 * @author Pospishyi Olexandr
 * @author "Yevgeny Kazakov"
 */
public class IndexedDatatypeExpressionEntry<T, K extends IndexedDatatypeExpression>
		extends IndexedClassExpressionEntry<T, K> {

	public IndexedDatatypeExpressionEntry(K representative) {
		super(representative);
	}

	@Override
	public int computeHashCode() {
		return combinedHashCode(IndexedDatatypeExpressionEntry.class, this.key
				.getProperty().getIri(), this.key.getValueSpace());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof IndexedDatatypeExpressionEntry<?, ?>) {
			IndexedDatatypeExpressionEntry<?, ?> otherView = (IndexedDatatypeExpressionEntry<?, ?>) other;
			return this.key.getProperty().equals(otherView.key.getProperty())
					&& this.key.getValueSpace().equals(
							otherView.key.getValueSpace());
		}
		return false;
	}
}
