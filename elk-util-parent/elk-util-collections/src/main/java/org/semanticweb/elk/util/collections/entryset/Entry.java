/*
 * #%L
 * ELK Utilities Collections
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
package org.semanticweb.elk.util.collections.entryset;

/**
 * A common interface for implementing entries of an {@link EntryCollection}.
 * Entries should be connected to each other, so basic operations include
 * setting and getting a reference to the next element.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of objects this object can be structurally equal to
 * 
 * @param <N>
 *            the type of the next linked element
 */
public interface Entry<T extends Entry<T, N>, N> {

	/**
	 * Setting the input element as the next element of the entry.
	 * 
	 * @param next
	 *            the object that should be set as the next element of the
	 *            record
	 */
	void setNext(N next);

	/**
	 * Returns the next next element of the entry, or null if there is no next
	 * element
	 * 
	 * @return the next element of the entry
	 */
	N getNext();

	T structuralEquals(Object other);

	int structuralHashCode();

}
