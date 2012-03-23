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
 * 
 */
public class ArrayHashSet<E> implements Set<E> {

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

	public int size() {
		return size;
	}

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
		if (capacity > 128)
			return (3 * capacity) / 4; // max 75% filled
		else
			return capacity;
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

	public boolean contains(Object o) {
		if (o == null)
			throw new NullPointerException();
		E[] data = this.data;
		int i = getIndex(o, data.length);
		int j = i; // for cycle detection
		for (;;) {
			Object probe = data[i];
			if (probe == null)
				return false;
			else if (o.equals(probe))
				return true;
			if (i == 0)
				i = data.length - 1;
			else
				i--;
			if (i == j) // full cycle
				return false;
		}
	}

	/**
	 * Adds the element to the set represented by given data array, if it did
	 * not contain there already.
	 * 
	 * @param data
	 *            the elements of the set
	 * @param e
	 *            the element to be added to the set
	 * @return <tt>true</tt> if the set has changed (the element is added),
	 *         <tt>false</tt> otherwise
	 */
	private boolean addElement(E[] data, E e) {
		int i = getIndex(e, data.length);
		for (;;) {
			Object probe = data[i];
			if (probe == null) {
				data[i] = e;
				return true;
			} else if (e.equals(probe))
				return false;
			if (i == 0)
				i = data.length - 1;
			else
				i--;
		}
	}

	/**
	 * Removes an element at position <tt>i</tt> of <tt>data</tt> shifting, if
	 * necessary, other elements so that all elements can be found by linear
	 * probing.
	 * 
	 * @param data
	 *            the array of the elements
	 * @param i
	 *            the position of data at which to delete the element
	 */
	private void shift(E[] data, int i) {
		int del = i; // the position at which the element is about to be deleted
		int j = i; // testing if the element at this position can still be
					// found by linear probing
		for (;;) {
			if (j == 0)
				j = data.length - 1;
			else
				j--;
			// invariant: interval [j, del[ contains non-null elements whose
			// index is in [j, del[
			if (j == del) {
				// we made a full cycle; no elements have to be shifted
				data[del] = null;
				return;
			}
			E test = data[j];
			if (test == null) {
				// no further elements to the left need to be shifted
				data[del] = null;
				return;
			}
			int k = getIndex(test, data.length);
			// check if k is in [j, del[
			if ((j < del) ? (j <= k) && (k < del) : (j <= k) || (k < del))
				// the index is in [j, del[, so the test element should not be
				// shifted
				continue;
			else {
				// copy the element to the position of deleted element and
				// start deleting its previous location
				data[del] = test;
				del = j;
				continue;
			}
		}
	}

	private boolean removeElement(E[] data, Object o) {
		int i = getIndex(o, data.length);
		int j = i; // for cycle detection
		for (;;) {
			Object probe = data[i];
			if (probe == null) {
				return false;
			} else if (o.equals(probe)) {
				shift(data, i);
				return true;
			}
			if (i == 0)
				i = data.length - 1;
			else
				i--;
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

	public Iterator<E> iterator() {
		return new ElementIterator();
	}

	public Object[] toArray() {
		Object[] result = new Object[size];
		int i = 0;
		for (E element : this) {
			result[i++] = element;
		}
		return result;
	}

	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o))
				return false;
		}
		return true;
	}

	public boolean addAll(Collection<? extends E> c) {
		boolean changed = false;
		for (E e : c)
			changed = changed || add(e);
		return changed;
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : c)
			changed = changed || remove(o);
		return changed;
	}

	public void clear() {
		for (int i = 0; i < data.length; i++)
			if (data[i] != null)
				data[i] = null;
		size = 0;
	}

	private class ElementIterator implements Iterator<E> {
		// copy of the data
		final E[] dataSnapshot;
		// expected size to check for concurrent modifications
		final int expectedSize;
		// current cursor
		int cursor;
		// next element to return
		E nextElement;

		ElementIterator() {
			this.expectedSize = size;
			this.dataSnapshot = data;
			cursor = 0;

			seekNext();
		}

		void seekNext() {
			for (nextElement = null; cursor < dataSnapshot.length
					&& (nextElement = dataSnapshot[cursor]) == null; cursor++)
				;
		}

		public boolean hasNext() {
			return nextElement != null;
		}

		public E next() {
			if (expectedSize != size)
				throw new ConcurrentModificationException();
			if (nextElement == null)
				throw new NoSuchElementException();
			E result = nextElement;
			cursor++;
			seekNext();
			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
