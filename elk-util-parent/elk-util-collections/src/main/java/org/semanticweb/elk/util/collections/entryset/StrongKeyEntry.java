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
 * A prototype class for entries with keys. Sub-classes should implement the
 * method for computing the equality function for the entry and hash code of the
 * keys, which will be used for initializing the hash code of the entry. The
 * hashCode and equals method should be compatible as usual.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the keys for linked entries
 * 
 * @param <K>
 *            the type of the key of this entry
 * 
 */
public abstract class StrongKeyEntry<T, K> implements KeyEntry<T, K> {

	protected final K key;

	/**
	 * The value used as a hash code of this entry
	 */
	protected final int hash;

	/**
	 * The reference to the next element
	 */
	KeyEntry<T, ? extends T> next;

	public StrongKeyEntry(K key) {
		this.key = key;
		this.hash = computeHashCode();
	}

	public K getKey() {
		return this.key;
	}

	public void setNext(KeyEntry<T, ? extends T> next) {
		this.next = next;
	}

	public KeyEntry<T, ? extends T> getNext() {
		return next;
	}

	public int hashCode() {
		return hash;
	}

	/**
	 * Compute the value which will be used as a final hash code for this
	 * object. Must be compatible with the {@link equals()} method usual.
	 * 
	 * @return
	 */
	public abstract int computeHashCode();

	// don't forget to redefine equality
	public abstract boolean equals(Object object);

}
