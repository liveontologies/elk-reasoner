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
package org.semanticweb.elk.reasoner.saturation.markers;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * An implementation of EntrySet<K, E> based on our implementation of
 * ArrayHashSet. Only the method contains is thread safe, other methods might
 * corrupt the state when used concurrently.
 * 
 * @author Frantisek Simancik
 * 
 */
public class EntryHashSet<K, E extends Entry<K>> implements EntrySet<K, E> {

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
	protected transient E[] data;

	/**
	 * The number of elements contained in this set.
	 */
	protected transient int size;

	/**
	 * The next size value at which to resize the table.
	 * 
	 * @serial
	 */
	int threshold;

	@SuppressWarnings("unchecked")
	public EntryHashSet(int initialCapacity) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: "
					+ initialCapacity);
		if (initialCapacity > MAXIMUM_CAPACITY)
			initialCapacity = MAXIMUM_CAPACITY;
		// Find a power of 2 >= initialCapacity
		int capacity = 1;
		while (capacity < initialCapacity)
			capacity <<= 1;
		this.data = (E[]) new Entry[capacity];
		this.size = 0;
		this.threshold = computeThreshold(capacity);
	}

	@SuppressWarnings("unchecked")
	public EntryHashSet() {
		int capacity = DEFAULT_INITIAL_CAPACITY;
		this.data = (E[]) new Entry[capacity];
		this.size = 0;
		this.threshold = computeThreshold(capacity);
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Computes a maximum number of elements for a given capacity after which to
	 * resize the table.
	 * 
	 * @param capacity
	 *            the capacity of the table.
	 * @return the threshold for the given capacity.
	 */
	static int computeThreshold(int capacity) {
		if (capacity > 128)
			return (3 * capacity) / 4; // max 75% filled
		else
			return capacity;
	}

	private int getIndex(Object key, int length) {
		return key.hashCode() & (length - 1);
	}

	private boolean addElement(E[] data, E e) {
		int i = getIndex(e.getKey(), data.length);
		for (;;) {
			E probe = data[i];
			if (probe == null) {
				data[i] = e;
				return true;
			} else if (e.getKey().equals(probe.getKey()))
				return false;
			if (i == 0)
				i = data.length - 1;
			else
				i--;
		}
	}

	/**
	 * Return the position of an Entry with Key key in data, or -1 if not present.
	 */
	private int getPosition(E[] data, Object key) {
		int i = getIndex(key, data.length);
		int j = i; // for cycle detection
		for (;;) {
			E probe = data[i];
			if (probe == null)
				return -1;
			else if (key.equals(probe.getKey()))
				return i;
			if (i == 0)
				i = data.length - 1;
			else
				i--;
			if (i == j)
				return -1;
		}
	}
	
	public boolean containsKey(Object o) {
		return (getPosition(data, o) != -1);
	}

	public boolean contains(Object o) {
		if (o == null)
			throw new NullPointerException();
		if (!(o instanceof Entry<?>))
			return false;
		E[] data = this.data;
		int i = getPosition(data, ((Entry<?>) o).getKey());
		if (i == -1)
			return false;
		return o.equals(data[i]);
	}

	public boolean replace(E e) {
		if (e == null)
			throw new NullPointerException();
		E[] data = this.data;
		int i = getPosition(data, e);
		if (i == -1)
			return false;
		data[i] = e;
		return true;
	}
	
	public E get(K key) {
		E[] data = this.data;
		int i = getPosition(data, key);
		if (i == -1)
			return null;
		return data[i];
	}

	public void resize() {
		int oldCapacity = data.length;
		if (oldCapacity == MAXIMUM_CAPACITY)
			throw new IllegalArgumentException(
					"The set cannot grow beyond the capacity: "
							+ MAXIMUM_CAPACITY);
		E oldData[] = data;
		int newCapacity = oldCapacity << 1;
		@SuppressWarnings("unchecked")
		E newData[] = (E[]) new Entry[newCapacity];
		for (int i = 0; i < oldCapacity; i++) {
			E e = oldData[i];
			if (e != null)
				addElement(newData, e);
		}
		this.data = newData;
		this.threshold = computeThreshold(newCapacity);
	}

	public boolean add(E e) {
		if (e == null)
			throw new NullPointerException();
		if (size == threshold)
			resize();
		boolean result = addElement(data, e);
		if (result)
			size++;
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

		public final E next() {
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
	
	private class KeyIterator implements Iterator<K> {
		final ElementIterator iterator = new ElementIterator();
		
		public final K next() {
			return iterator.next().getKey();
		}

		public boolean hasNext() {
			return iterator.hasNext();
		}

		public void remove() {
			iterator.remove();
		}
	}

	private class KeySetView extends AbstractSet<K> {
		
		@Override
		public boolean contains(Object o) {
			return containsKey(o);
		}
		
		@Override
		public Iterator<K> iterator() {
			return new KeyIterator();
		}

		@Override
		public int size() {
			return size;
		}
	}

	public Set<K> keySet() {
		return new KeySetView();
	}

}
