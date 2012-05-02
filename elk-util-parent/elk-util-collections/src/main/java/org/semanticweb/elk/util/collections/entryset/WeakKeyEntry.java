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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * A prototype class for entries with weak keys, which can be collected by the
 * garbage collector. Sub-classes should implement the method for computing the
 * equality function for the entry and hash code of the keys, which will be used
 * for initializing the hash code of the entry. The hashCode and equals methods
 * should be compatible as usual. Note that the keys are mutable because they
 * may become null at some later time. Thus, special care needs to be taken when
 * implementing and using the equals method: it might be the case that the
 * result of equality changes over time. For example, it would be unsafe to
 * define two entries equal if both of them have null keys, since they might
 * have been initialized with different hash values, and equals would be not
 * compatible with the hash code.
 * 
 * The hash code function does not have such a side effect
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <K>
 *            the type of the keys of the record
 * 
 * @param <T>
 *            the type of the next element the record is connected with
 */
public abstract class WeakKeyEntry<K, T> extends WeakReference<K> implements
		KeyEntry<T, K> {

	/**
	 * The value used as a hash code of this record
	 */
	protected final int hash;

	/**
	 * The reference to the next element
	 */
	protected KeyEntry<T, ? extends T> next;

	/**
	 * Create a weak record for the key
	 * 
	 * @param key
	 *            the key for which the weak record is created
	 */
	public WeakKeyEntry(K key) {
		super(key);
		hash = computeHashCode();
	}

	/**
	 * Create a weak record for the key register it with a reference queue,
	 * which accumulates records whose keys were garbage collected.
	 * 
	 * @param key
	 *            the key for which the weak record is created
	 * @param q
	 *            the reference queue with which to register this record
	 */
	WeakKeyEntry(K key, ReferenceQueue<? super K> q) {
		super(key, q);
		hash = computeHashCode();
	}

	@Override
	public K getKey() {
		return this.get();
	}

	@Override
	public void setNext(KeyEntry<T, ? extends T> next) {
		this.next = next;
	}

	@Override
	public KeyEntry<T, ? extends T> getNext() {
		return next;
	}

	@Override
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
	@Override
	public abstract boolean equals(Object object);

}
