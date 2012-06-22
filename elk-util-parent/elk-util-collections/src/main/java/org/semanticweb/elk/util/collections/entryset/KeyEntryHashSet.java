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

import java.util.Iterator;

/**
 * A hash set containing elements modulo equality. The comparison of elements is
 * performed by wrapping them into {@link KeyEntry} objects where the key is a
 * specified input element. For this purpose, these objects should redefine the
 * {@link equals()} and {@link hashCode()} methods accordingly. A factory for
 * producing {@link KeyEntry} objects from the elements should be provided so
 * that elements can be wrapped.
 * 
 * The main method provided is merging a entry into the set, which returns an
 * equal element from the set, if there is one (according to the specified way
 * to compare elements), or, otherwise, inserts the input element into the set
 * returning itself. Other methods include finding an element in the set that is
 * equal to the given one, deleting such an element from the set, if found, and
 * iterating over the entries.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 * 
 *            the type of elements in the set
 */
public class KeyEntryHashSet<T> extends
		AbstractEntryHashSet<KeyEntry<T, ? extends T>> implements Iterable<T> {

	protected final KeyEntryFactory<T> keyEntryFactory;

	/**
	 * Create an empty set associated with a given factory for creating
	 * {@link KeyEntry} wrapper objects and with the given initial capacity. The
	 * set will be resized as necessary to accommodate new elements.
	 * 
	 * @param keyEntryFactory
	 *            factory for creating {@link KeyEntry} wrapper objects
	 * 
	 * @param initialCapacity
	 *            the estimated
	 */
	public KeyEntryHashSet(KeyEntryFactory<T> keyEntryFactory,
			int initialCapacity) {
		super(initialCapacity);
		this.keyEntryFactory = keyEntryFactory;
	}

	/**
	 * Create an empty set associated with a given factory for creating
	 * {@link KeyEntry} wrapper objects.
	 * 
	 * @param keyEntryFactory
	 */
	public KeyEntryHashSet(KeyEntryFactory<T> keyEntryFactory) {
		super();
		this.keyEntryFactory = keyEntryFactory;
	}

	/**
	 * Get the element in set that is equal to the input entry if there is one,
	 * or otherwise insert the given entry into the set and return itself.
	 * Equality of entries is decided by wrapping them into the respective
	 * {@link KeyEntry} objects using the factory.
	 * 
	 * 
	 * @param key
	 *            the element to be merged into the set
	 * 
	 * @return the element in the set that is equal to the input element if
	 *         there is one, or the input element otherwise
	 * 
	 */
	public T merge(T key) {

		return mergeEntry(keyEntryFactory.createEntry(key)).getKey();

	}

	/**
	 * Retrieves the element in the set that is equal to the given object, if it
	 * exists, or returns null otherwise. Equality of entries is decided by
	 * wrapping them into the respective {@link KeyEntry} objects using the
	 * factory.
	 * 
	 * @param key
	 *            the object that is used for finding the entry
	 * @return the entry in the set that is equal to the given object, if there
	 *         exists one, or null otherwise
	 */
	public T get(T key) {

		KeyEntry<T, ? extends T> entry = getEntry(keyEntryFactory
				.createEntry(key));
		if (entry == null)
			return null;
		else
			return entry.getKey();

	}

	/**
	 * Removes and returns the element in the set that is equal to the input
	 * element. Returns null if the set contains no such element. Equality of
	 * entries is decided by wrapping them into the respective {@link KeyEntry}
	 * objects using the factory.
	 * 
	 * @param key
	 *            the element that is used for finding the element to remove
	 * @return the removed element, or null if no element that is equal to the
	 *         given one is found
	 */
	public T remove(T key) {

		KeyEntry<T, ? extends T> entry = removeEntry(keyEntryFactory
				.createEntry(key));

		if (entry == null)
			return null;
		else
			return entry.getKey();

	}

	/**
	 * Creates an iterator over the elements in the set.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			final Iterator<KeyEntry<T, ? extends T>> recordIterator = entryIterator();

			@Override
			public boolean hasNext() {
				return recordIterator.hasNext();
			}

			@Override
			public T next() {
				return recordIterator.next().getKey();
			}

			@Override
			public void remove() {
				recordIterator.remove();
			}
		};
	}
}
