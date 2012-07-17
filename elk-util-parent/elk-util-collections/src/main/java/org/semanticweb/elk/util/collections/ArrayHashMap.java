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
 * @author Yevgeny Kazakov, May 23, 2011
 */
package org.semanticweb.elk.util.collections;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * hash maps using array and linear probing for resolving hash collision see [1]
 * p.526. Reuses some code from the implementation of HashMap.
 * 
 * [1] Donald E. Knuth, The Art of Computer Programming, Volume 3, Sorting and
 * Searching, Second Edition
 * 
 * @author Yevgeny Kazakov
 * @param <K>
 *            the type of the keys
 * @param <V>
 *            the type of the values
 * 
 */
public class ArrayHashMap<K, V> implements Map<K, V> {

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
	 * The table for the keys; the length MUST always be a power of two.
	 */
	protected volatile transient K[] keys;

	/**
	 * The table for the values; the length MUST be equal to the length of keys
	 * and MUST always be a power of two.
	 */
	protected volatile transient V[] values;

	/**
	 * The number of key-value entries contained in this map.
	 */
	protected transient int size;

	/**
	 * The next size value at which to resize (capacity * load factor).
	 * 
	 * @serial
	 */
	int threshold;

	@SuppressWarnings("unchecked")
	public ArrayHashMap(int initialCapacity) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: "
					+ initialCapacity);
		if (initialCapacity > MAXIMUM_CAPACITY)
			initialCapacity = MAXIMUM_CAPACITY;
		// Find a power of 2 >= initialCapacity
		int capacity = 1;
		while (capacity < initialCapacity)
			capacity <<= 1;
		this.threshold = computeThreshold(capacity);
		this.keys = (K[]) new Object[capacity];
		this.values = (V[]) new Object[capacity];
		this.size = 0;
	}

	@SuppressWarnings("unchecked")
	public ArrayHashMap() {
		int capacity = DEFAULT_INITIAL_CAPACITY;
		this.threshold = computeThreshold(capacity);
		this.keys = (K[]) new Object[capacity];
		this.values = (V[]) new Object[capacity];
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
	 * Computes a maximum number of elements for a given capacity after which to
	 * resize the tables.
	 * 
	 * @param capacity
	 *            the capacity of the tables.
	 * @return the threshold for the given capacity.
	 */
	static int computeThreshold(int capacity) {
		if (capacity > 128)
			return (3 * capacity) / 4; // max 75%
		else
			return capacity;
	}

	static int getIndex(Object key, int length) {
		return key.hashCode() & (length - 1);
	}

	@Override
	public boolean containsKey(Object key) {
		if (key == null)
			throw new NullPointerException();
		K[] keys = this.keys;
		int i = getIndex(key, keys.length);
		int j = i; // for cycle detection
		for (;;) {
			K probe = keys[i];
			if (probe == null)
				return false;
			else if (key.equals(probe))
				return true;
			if (i == 0)
				i = keys.length - 1;
			else
				i--;
			if (i == j)
				return false;
		}
	}

	@Override
	public boolean containsValue(Object value) {
		if (value == null)
			throw new NullPointerException();
		V[] values = this.values;
		for (int i = 0; i < keys.length; i++)
			if (value.equals(values[i]))
				return true;
		return false;
	}

	@Override
	public V get(Object key) {
		if (key == null)
			throw new NullPointerException();
		// copy keys and values when they have the same size
		for (;;) {
			K[] keys = this.keys;
			V[] values = this.values;
			if (keys.length == values.length)
				break;
		}
		int i = getIndex(key, keys.length);
		int j = i;
		for (;;) {
			K probe = keys[i];
			if (probe == null)
				return null;
			else if (key.equals(probe))
				return values[i];
			if (i == 0)
				i = keys.length - 1;
			else
				i--;
			if (i == j)
				return null;
		}
	}

	// TODO: make sure it is thread-safe to get the value while putting it
	V putKeyValue(K[] keys, V[] values, K key, V value) {
		int i = getIndex(key, keys.length);
		for (;;) {
			K probe = keys[i];
			if (probe == null) {
				keys[i] = key;
				values[i] = value;
				return null;
			} else if (key.equals(probe)) {
				V oldValue = values[i];
				values[i] = value;
				return oldValue;
			}
			if (i == 0)
				i = keys.length - 1;
			else
				i--;
		}
	}

	public void resize() {
		int oldCapacity = keys.length;
		if (oldCapacity == MAXIMUM_CAPACITY)
			throw new IllegalArgumentException(
					"Map cannot grow beyond capacity: " + MAXIMUM_CAPACITY);
		K oldKeys[] = keys;
		V oldValues[] = values;
		int newCapacity = oldCapacity << 1;
		@SuppressWarnings("unchecked")
		K newKeys[] = (K[]) new Object[newCapacity];
		@SuppressWarnings("unchecked")
		V newValues[] = (V[]) new Object[newCapacity];
		for (int i = 0; i < oldCapacity; i++) {
			K key = oldKeys[i];
			if (key != null)
				putKeyValue(newKeys, newValues, key, oldValues[i]);
		}
		this.keys = newKeys;
		this.values = newValues;
		this.threshold = computeThreshold(newCapacity);
	}

	@Override
	public V put(K key, V value) {
		if (key == null)
			throw new NullPointerException();
		if (size == threshold)
			resize();
		V result = putKeyValue(keys, values, key, value);
		if (result == null)
			size++;
		return result;
	}

	@Override
	public V remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		for (int i = 0; i < keys.length; i++)
			if (keys[i] != null) {
				keys[i] = null;
				values[i] = null;
			}
		size = 0;
	}

	@Override
	public Set<K> keySet() {
		return new KeySet();
	}

	@Override
	public Collection<V> values() {
		return new ValueCollection();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return new EntrySet();
	}

	private class KeyIterator implements Iterator<K> {
		// copy of the keys
		final K[] keysSnapshot;
		// expected size to check for concurrent modifications
		final int expectedSize;
		// current cursor
		int cursor;
		// reference to the next key
		K nextKey = null;

		KeyIterator() {
			this.expectedSize = size;
			this.keysSnapshot = keys;
			cursor = 0;
			seekNext();
		}

		void seekNext() {
			for (nextKey = null; cursor < keysSnapshot.length
					&& (nextKey = keysSnapshot[cursor]) == null; cursor++)
				;
		}

		@Override
		public boolean hasNext() {
			return nextKey != null;
		}

		@Override
		public K next() {
			if (expectedSize != size)
				throw new ConcurrentModificationException();
			if (nextKey == null)
				throw new NoSuchElementException();
			K result = nextKey;
			cursor++;
			seekNext();
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private final class KeySet extends AbstractSet<K> {

		@Override
		public Iterator<K> iterator() {
			return new KeyIterator();
		}

		@Override
		public boolean contains(Object o) {
			return ArrayHashMap.this.containsKey(o);
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			return size;
		}

	}

	private class ValueIterator implements Iterator<V> {
		// copy of the values
		final V[] valuesSnapshot;
		// expected size to check for concurrent modifications
		final int expectedSize;
		// current cursor
		int cursor;
		// reference to the next key
		V nextValue = null;

		ValueIterator() {
			this.expectedSize = size;
			this.valuesSnapshot = values;
			cursor = 0;
			seekNext();
		}

		void seekNext() {
			for (nextValue = null; cursor < valuesSnapshot.length
					&& (nextValue = valuesSnapshot[cursor]) == null; cursor++)
				;
		}

		@Override
		public boolean hasNext() {
			return nextValue != null;
		}

		@Override
		public V next() {
			if (expectedSize != size)
				throw new ConcurrentModificationException();
			if (nextValue == null)
				throw new NoSuchElementException();
			V result = nextValue;
			cursor++;
			seekNext();
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private final class ValueCollection extends AbstractCollection<V> {

		@Override
		public Iterator<V> iterator() {
			return new ValueIterator();
		}

		@Override
		public int size() {
			return size;
		}

	}

	private class EntryIterator implements Iterator<Map.Entry<K, V>> {
		// copy of the keys
		final K[] keysSnapshot;
		// copy of the values
		final V[] valuesSnapshot;
		// expected size to check for modification
		final int expectedSize;

		// current cursor
		int cursor;
		// reference to the next key
		K nextKey;

		EntryIterator() {
			this.expectedSize = size;
			this.keysSnapshot = keys;
			this.valuesSnapshot = values;
			this.cursor = 0;
			seekNext();
		}

		void seekNext() {
			for (nextKey = null; cursor < keysSnapshot.length
					&& (nextKey = keysSnapshot[cursor]) == null; cursor++)
				;
		}

		@Override
		public boolean hasNext() {
			return nextKey != null;
		}

		@Override
		public Entry next() {
			if (expectedSize != size)
				throw new ConcurrentModificationException();
			if (nextKey == null)
				throw new NoSuchElementException();
			Entry result = new Entry(this, cursor);
			cursor++;
			seekNext();
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	class Entry implements Map.Entry<K, V> {
		// copy of the iterator
		final EntryIterator iterator;
		// position of the cursor
		final int cursor;

		Entry(EntryIterator iterator, int cursor) {
			this.iterator = iterator;
			this.cursor = cursor;
		}

		@Override
		public K getKey() {
			return iterator.keysSnapshot[cursor];
		}

		@Override
		public V getValue() {
			return iterator.valuesSnapshot[cursor];
		}

		@Override
		public V setValue(V value) {
			V previous = iterator.valuesSnapshot[cursor];
			iterator.valuesSnapshot[cursor] = value;
			return previous;
		}

	}

	private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
		@Override
		public Iterator<Map.Entry<K, V>> iterator() {
			return new EntryIterator();
		}

		@Override
		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry<?, ?>))
				return false;
			Object k = ((Map.Entry<?, ?>) o).getKey();
			return ArrayHashMap.this.containsKey(k);
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public void clear() {
			ArrayHashMap.this.clear();
		}
	}

}
