/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
/**
 * @author Yevgeny Kazakov, May 17, 2011
 */
package org.semanticweb.elk.util.collections;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * hash sets using array and linear probing for resolving hash collision see [1]
 * p.526. Reuses some code from the implementation of {@link java.util.HashMap}.
 * 
 * [1] Donald E. Knuth, The Art of Computer Programming, Volume 3, Sorting and
 * Searching, Second Edition
 * 
 * @author Yevgeny Kazakov
 * @param <E>
 *            the type of the elements in this set
 * 
 */
public class ArrayHashSet<E> extends AbstractSet<E> implements Set<E>,
		DirectAccess<E> {

	/**
	 * The default initial capacity - MUST be a power of two.
	 */
	static final int DEFAULT_INITIAL_CAPACITY = 16;

	/**
	 * The maximum capacity, used if a higher value is implicitly specified by
	 * either of the constructors with arguments. MUST be a power of two <=
	 * 1<<30.
	 */
	static final int MAXIMUM_CAPACITY = 1 << 30;

	/**
	 * The table for the elements; the length MUST always be a power of two.
	 */
	transient E[] data;

	/**
	 * The number of elements contained in this set.
	 */
	transient int size;

	/**
	 * The next upper size value at which to stretch the table.
	 * 
	 * @serial
	 */
	int upperSize;

	/**
	 * The next lower size value at which to shrink the table.
	 * 
	 * @serial
	 */
	int lowerSize;

	@SuppressWarnings("unchecked")
	public ArrayHashSet(int initialCapacity) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: "
					+ initialCapacity);
		if (initialCapacity > MAXIMUM_CAPACITY)
			initialCapacity = MAXIMUM_CAPACITY;
		// Find a power of 2 >= initialCapacity
		int capacity = 1;
		while (capacity < initialCapacity)
			capacity <<= 1;
		this.data = (E[]) new Object[capacity];
		this.size = 0;
		this.upperSize = computeUpperSize(capacity);
		this.lowerSize = computeLowerSize(capacity);
	}

	@SuppressWarnings("unchecked")
	public ArrayHashSet() {
		int capacity = DEFAULT_INITIAL_CAPACITY;
		this.data = (E[]) new Object[capacity];
		this.size = 0;
		this.upperSize = computeUpperSize(capacity);
		this.lowerSize = computeLowerSize(capacity);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Computes a maximum size of the table for a given capacity after which to
	 * stretch the table.
	 * 
	 * @param capacity
	 *            the capacity of the table.
	 * @return maximum size of the table for a given capacity after which to
	 *         stretch the table.
	 */
	static private int computeUpperSize(int capacity) {
		if (capacity > 64)
			return (3 * capacity) / 4; // max 75% filled
		// else
		return capacity - 1;
	}

	/**
	 * Computes a minimum size of the table for a given capacity after which to
	 * shrink the table.
	 * 
	 * @param capacity
	 *            the capacity of the table.
	 * @return minimum size of the table for a given capacity after which to
	 *         shrink the table
	 */
	static private int computeLowerSize(int capacity) {
		return capacity / 4;
	}

	static private int getIndex(Object o, int length) {
		return o.hashCode() & (length - 1);
	}

	@Override
	public boolean contains(Object o) {
		if (o == null)
			throw new NullPointerException();
		E[] d = this.data;
		int i = getIndex(o, d.length);
		int j = i; // for cycle detection
		for (;;) {
			Object probe = d[i];
			if (probe == null)
				return false;
			else if (o.equals(probe))
				return true;
			if (++i == d.length)
				i = 0;
			if (i == j) // full cycle
				return false;
		}
	}

	/**
	 * Adds the element to the set represented by given data array, if it did
	 * not contain there already.
	 * 
	 * @param d
	 *            the elements of the set
	 * @param e
	 *            the element to be added to the set
	 * @return <tt>true</tt> if the set has changed (the element is added),
	 *         <tt>false</tt> otherwise
	 */
	private static <E> boolean addElement(E[] d, E e) {
		int i = getIndex(e, d.length);
		for (;;) {
			Object probe = d[i];
			if (probe == null) {
				d[i] = e;
				return true;
			} else if (e.equals(probe))
				return false;
			if (++i == d.length)
				i = 0;
		}
	}

	/**
	 * Removes an element at position <tt>i</tt> of <tt>data</tt> shifting, if
	 * necessary, other elements so that all elements can be found by linear
	 * probing.
	 * 
	 * @param d
	 *            the array of the elements
	 * @param i
	 *            the position of data at which to delete the element
	 */
	private static <E> void shift(E[] d, int i) {
		int del = i; // the position at which the element is about to be deleted
		int j = i; // testing if the element at this position can still be
					// found by linear probing
		for (;;) {
			if (++j == d.length)
				j = 0;
			// invariant: interval ]del, j] contains non-null elements whose
			// index is in ]del, j]
			if (j == del) {
				// we made a full cycle; no elements have to be shifted
				d[del] = null;
				return;
			}
			E test = d[j];
			if (test == null) {
				// no further elements to the left need to be shifted
				d[del] = null;
				return;
			}
			int k = getIndex(test, d.length);
			// check if k is in ]del, j] (possibly wrapping over)
			if ((del < j) ? (del < k) && (k <= j) : (del < k) || (k <= j))
				// the test element should not be shifted
				continue;
			// else copy the element to the position of deleted element and
			// start deleting its previous location
			d[del] = test;
			del = j;
		}
	}

	private static <E> boolean removeElement(E[] d, Object o) {
		int i = getIndex(o, d.length);
		int j = i; // for cycle detection
		for (;;) {
			Object probe = d[i];
			if (probe == null) {
				return false;
			} else if (o.equals(probe)) {
				shift(d, i);
				return true;
			}
			if (++i == d.length)
				i = 0;
			if (i == j) // full cycle
				return false;
		}
	}

	/**
	 * Increasing the capacity of the table
	 */
	private void stretch() {
		int oldCapacity = data.length;
		if (oldCapacity == MAXIMUM_CAPACITY)
			throw new IllegalArgumentException(
					"The set cannot grow beyond the capacity: "
							+ MAXIMUM_CAPACITY);
		E[] oldData = data;
		int newCapacity = oldCapacity << 1;
		@SuppressWarnings("unchecked")
		E[] newData = (E[]) new Object[newCapacity];
		for (int i = 0; i < oldCapacity; i++) {
			E e = oldData[i];
			if (e != null)
				addElement(newData, e);
		}
		this.data = newData;
		this.upperSize = computeUpperSize(newCapacity);
		this.lowerSize = computeLowerSize(newCapacity);
	}

	/**
	 * Decreasing the capacity of the table
	 */
	private void shrink() {
		int oldCapacity = data.length;
		if (oldCapacity == 1)
			return;
		E[] oldData = data;
		int newCapacity = oldCapacity >> 1;
		@SuppressWarnings("unchecked")
		E[] newData = (E[]) new Object[newCapacity];
		for (int i = 0; i < oldCapacity; i++) {
			E e = oldData[i];
			if (e != null)
				addElement(newData, e);
		}
		this.data = newData;
		this.upperSize = computeUpperSize(newCapacity);
		this.lowerSize = computeLowerSize(newCapacity);
	}

	@Override
	public boolean add(E e) {
		if (e == null)
			throw new NullPointerException();
		if (size == upperSize)
			stretch();
		boolean result = addElement(data, e);
		if (result)
			size++;
		return result;
	}

	@Override
	public boolean remove(Object o) {
		if (o == null)
			throw new NullPointerException();
		boolean result = removeElement(data, o);
		if (result)
			size--;
		if (size == lowerSize)
			shrink();
		return result;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		for (Object o : c) {
			modified |= remove(o);
		}
		return modified;
	}

	@Override
	public Iterator<E> iterator() {
		return new ElementIterator();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void clear() {
		int capacity = data.length >> 2;
		if (capacity == 0)
			capacity = 1;
		size = 0;
		upperSize = computeUpperSize(capacity);
		lowerSize = computeLowerSize(capacity);
		this.data = (E[]) new Object[capacity];
	}

	@Override
	public String toString() {
		return Arrays.toString(toArray());
	}

	@Override
	public E[] getRawData() {
		return data;
	}

	private class ElementIterator implements Iterator<E> {
		// copy of the data
		final E[] dataSnapshot;
		// expected size to check for concurrent modifications
		int expectedSize;
		// the element at which to start iteration
		int start;
		// cursor of the current element
		int current;
		// cursor of the next element
		int next;

		ElementIterator() {
			this.expectedSize = size;
			this.dataSnapshot = data;
			this.start = seekFirstNull();
			this.next = seekNext(start);
			this.current = next;
		}

		/**
		 * @return the position of the first {@code null} element
		 */
		int seekFirstNull() {
			for (int i = 0; i < dataSnapshot.length; i++) {
				if (dataSnapshot[i] == null)
					return i;
			}
			throw new RuntimeException("Set is full!");
		}

		/**
		 * Searches for the next non-{@code null} element after the given
		 * position before the start position
		 * 
		 * @param pos
		 *            position after which to search
		 * @return the position of the non-{@code null} element, or
		 *         {@link #start} if there are no such element
		 */
		int seekNext(int pos) {
			for (;;) {
				if (++pos == dataSnapshot.length)
					pos = 0;
				if (pos == start || dataSnapshot[pos] != null)
					return pos;
			}
		}

		@Override
		public boolean hasNext() {
			return next != start;
		}

		@Override
		public E next() {
			if (expectedSize != size)
				throw new ConcurrentModificationException();
			if (next == start)
				throw new NoSuchElementException();
			this.current = next;
			E result = dataSnapshot[current];
			this.next = seekNext(current);
			return result;
		}

		@Override
		public void remove() {
			if (expectedSize != size)
				throw new ConcurrentModificationException();
			if (current == next)
				// the current element was not returned or was already removed
				throw new IllegalStateException();
			shift(dataSnapshot, current);
			if (dataSnapshot[current] != null)
				// something was copied to the current position
				next = current;
			else
				current = next;
			size--;
			expectedSize--;
		}

	}

}
