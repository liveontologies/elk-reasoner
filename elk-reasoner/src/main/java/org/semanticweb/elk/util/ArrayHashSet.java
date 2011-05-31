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
package org.semanticweb.elk.util;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * hash sets using array and linear probing for resolving hash collision see [1]
 * p.526.
 * 
 * [1] Donald E. Knuth, The Art of Computer Programming, Volume 3, Sorting and
 * Searching, Second Edition
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ArrayHashSet<E> implements Set<E> {

	// the data stored in the set
	protected transient E[] data;
	// the number of the elements in the set
	protected transient int size;

	@SuppressWarnings("unchecked")
	public ArrayHashSet(int initialCapacity) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: "
					+ initialCapacity);
		this.data = (E[]) new Object[initialCapacity];
		this.size = 0;
	}

	public ArrayHashSet() {
		this(113);
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	private int getIndex(int length, Object o) {
		int index = (o.hashCode() * 129) % length;
		if (index < 0)
			return -index;
		else
			return index;
	}

	public boolean contains(Object o) {
		if (o == null)
			throw new NullPointerException();
		E[] data = this.data;
		int i = getIndex(data.length, o);
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
			if (i == j)
				return false;
		}
	}

	private boolean addElement(E[] data, E o) {
		int i = getIndex(data.length, o);
		for (;;) {
			Object probe = data[i];
			if (probe == null) {
				data[i] = o;
				return true;
			} else if (o.equals(probe))
				return false;
			if (i == 0)
				i = data.length - 1;
			else
				i--;
		}
	}

	public void ensureCapacity(int size) {
		int oldCapacity = data.length;
		if (size > oldCapacity || (size > 128 && size > 3 * oldCapacity / 4)) {
			E oldData[] = data;
			int newCapacity = (size * 2) + 1;
			@SuppressWarnings("unchecked")
			E newData[] = (E[]) new Object[newCapacity];
			for (int i = 0; i < oldCapacity; i++) {
				E e = oldData[i];
				if (e != null)
					addElement(newData, e);
			}
			this.data = newData;
		}
	}

	public boolean add(E e) {
		if (e == null)
			throw new NullPointerException();
		ensureCapacity(size + 1);
		boolean result = addElement(data, e);
		if (result)
			size++;
		return result;
	}

	public Iterator<E> iterator() {
		return new ElementIterator();
	}

	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
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
