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
	 * The table for the elements; the length MUST always be a power of two.
	 */
	transient E[] data;

	/**
	 * The number of elements contained in this set.
	 */
	transient int size;

	@SuppressWarnings("unchecked")
	public ArrayHashSet(int initialCapacity) {
		int capacity = LinearProbing.getInitialCapacity(initialCapacity);
		this.data = (E[]) new Object[capacity];
		this.size = 0;
	}

	@SuppressWarnings("unchecked")
	public ArrayHashSet() {
		int capacity = LinearProbing.DEFAULT_INITIAL_CAPACITY;
		this.data = (E[]) new Object[capacity];
		this.size = 0;
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
	 * Increasing the capacity of the table
	 */
	private void enlarge() {
		int oldCapacity = data.length;
		if (oldCapacity == LinearProbing.MAXIMUM_CAPACITY)
			throw new IllegalArgumentException(
					"The set cannot grow beyond the capacity: "
							+ LinearProbing.MAXIMUM_CAPACITY);
		E[] oldData = data;
		int newCapacity = oldCapacity << 1;
		@SuppressWarnings("unchecked")
		E[] newData = (E[]) new Object[newCapacity];
		for (int i = 0; i < oldCapacity; i++) {
			E e = oldData[i];
			if (e != null)
				LinearProbing.add(newData, e);
		}
		this.data = newData;
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
				LinearProbing.add(newData, e);
		}
		this.data = newData;
	}

	@Override
	public boolean contains(Object o) {
		if (o == null)
			throw new NullPointerException();
		return LinearProbing.contains(data, o);
	}

	@Override
	public boolean add(E e) {
		if (e == null)
			throw new NullPointerException();
		if (size == LinearProbing.getUpperSize(data.length))
			enlarge();
		boolean added = LinearProbing.add(data, e);
		if (added)
			size++;
		return added;
	}

	@Override
	public boolean remove(Object o) {
		if (o == null)
			throw new NullPointerException();
		boolean removed = LinearProbing.remove(data, o);
		if (removed)
			size--;
		if (size == LinearProbing.getLowerSize(data.length))
			shrink();
		return removed;
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

	private class ElementIterator extends LinearProbingIterator<E, E> {

		ElementIterator() {
			super(data, size);
		}

		@Override
		void checkSize(int expectedSize) {
			if (expectedSize != size)
				throw new ConcurrentModificationException();
		}

		@Override
		void remove(E[] expectedData, int pos) {
			LinearProbing.remove(expectedData, pos);
			size--;
		}

		@Override
		E getValue(E element, int pos) {
			return element;
		}
	}

}
