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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;

/**
 * Implements a view for instances of {@link IndexedObjectSomeValuesFrom}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped indexed class expression
 */
public class IndexedObjectSomeValuesFromView<T extends IndexedObjectSomeValuesFrom>
		extends IndexedClassExpressionView<T> {

	IndexedObjectSomeValuesFromView(T representative) {
		super(representative);
	}

	@Override
	public int hashCode() {
		return combinedHashCode(IndexedObjectSomeValuesFromView.class,
				this.representative.getFiller(), this.representative.getClass());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof IndexedObjectSomeValuesFromView<?>) {
			IndexedObjectSomeValuesFromView<?> otherView = (IndexedObjectSomeValuesFromView<?>) other;
			return this.representative.getFiller().equals(
					otherView.representative.getFiller())
					&& this.representative.getClass().equals(
							otherView.representative.getClass());
		}
		return false;
	}
}
