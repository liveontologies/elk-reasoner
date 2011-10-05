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
package org.semanticweb.elk.reasoner.indexing.views;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;

/**
 * Implements a view for instances of {@link IndexedObjectProperty}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped indexed object property composition
 */
public class IndexedBinaryPropertyChainView<T extends IndexedBinaryPropertyChain>
		extends IndexedPropertyChainView<T> {

	IndexedBinaryPropertyChainView(T representative) {
		super(representative);
	}

	@Override
	public int hashCode() {
		return combinedHashCode(IndexedBinaryPropertyChainView.class,
				this.representative.getLeftProperty(),
				this.representative.getRightProperty());
	}

	@Override
	public boolean equals(Object other) {
		IndexedBinaryPropertyChainView<?> otherView = (IndexedBinaryPropertyChainView<?>) other;
		return this.representative.getLeftProperty().equals(
				otherView.representative.getLeftProperty())
				&& this.representative.getRightProperty().equals(
						otherView.representative.getRightProperty());
	}
}
