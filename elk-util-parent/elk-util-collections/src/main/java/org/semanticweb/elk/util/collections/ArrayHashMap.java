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
		this.keys = (K[]) new Object[capacity];
		this.values = (V[]) new Object[capacity];
		this.size = 0;
		this.upperSize = computeUpperSize(capacity);
		this.lowerSize = computeLowerSize(capacity);
	}

	@SuppressWarnings("unchecked")
	public ArrayHashMap() {
		int capacity = DEFAULT_INITIAL_CAPACITY;
		this.keys = (K[]) new Object[capacity];
		this.values = (V[]) new Object[capacity];
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
	 * stretch the tables.
	 * 
	 * @param capacity
	 *            the capacity of the table.
	 * @return maximum size of the table for a given capacity after which to
	 *         stretch the tables.
	 */
	static private int computeUpperSize(int capacity) {
		if (capacity > 128)
			return (3 * capacity) / 4; // max 75% filled
		else
			return capacity;
	}

	/**
	 * Computes a minimum size of the table for a given capacity after which to
	 * shrink the tables.
	 * 
	 * @param capacity
	 *            the capacity of the table.
	 * @return minimum size of the table for a given capacity after which to
	 *         shrink the tables
	 */
	static private int computeLowerSize(int capacity) {
		return capacity / 4;
	}

	static private int getIndex(Object key, int length) {
		return key.hashCode() & (length - 1);
	}

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
			if (i == j) // full cycle
				return false;
		}
	}

	public boolean containsValue(Object value) {
		if (value == null)
			throw new NullPointerException();
		V[] values = this.values;
		for (int i = 0; i < keys.length; i++)
			if (value.equals(values[i]))
				return true;
		return false;
	}

	public V get(Object key) {
		if (key == null)
			throw new NullPointerException();
		// to avoid problems in the middle of resizing, we copy keys and values
		// when they have the same size
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
			if (i == j) // full cycle
				return null;
		}
	}

	/**
	 * Associates the given key with the given value in the map defined by the
	 * keys and value arrays. If an entry with the key equal to the given one
	 * already exists in the map, the value for this key will be overwritten
	 * with the given value.
	 * 
	 * @param keys
	 *            the keys of the map
	 * @param values
	 *            the values of the map
	 * @param key
	 *            the key of the entry
	 * @param value
	 *            the value of the entry
	 * @return the previous value associated with the key or <tt>null</tt> if
	 *         there was no such a previous value.
	 */
	private V putKeyValue(K[] keys, V[] values, K key, V value) {
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

	/**
	 * Removes an element at position <tt>i</tt> of <tt>keys</tt> and
	 * <tt>values</tt> shifting, if necessary, other elements so that all
	 * elements can be found by linear probing.
	 * 
	 * @param keys
	 *            the keys of the map
	 * @param values
	 *            the values of the map
	 * @param i
	 *            the position at which to delete the key and value
	 */
	private void shift(K[] keys, V[] values, int i) {
		int del = i; // the position at which to delete
		int j = i; // the position at which to test if the key can be
					// found by linear probing
		for (;;) {
			// decrement j modulo the length of the arrays
			if (j == 0)
				j = keys.length - 1;
			else
				j--;
			// invariant: interval [j, del[ contains non-null elements whose
			// index is in [j, del[
			if (j == del) {
				// we made a full cycle; no elements have to be shifted
				keys[del] = null;
				values[del] = null;
				return;
			}
			K test = keys[j];
			if (test == null) {
				// no further elements to the left need to be shifted
				keys[del] = null;
				values[del] = null;
				return;
			}
			int k = getIndex(test, keys.length);
			// check if k is in [j, del[
			if ((j < del) ? (j <= k) && (k < del) : (j <= k) || (k < del))
				// the index is in [j, del[, so the test element should not be
				// shifted
				continue;
			else {
				// copying the keys and values to the position of deleted
				// element and start deleting their previous locations
				keys[del] = test;
				values[del] = values[j];
				del = j;
				continue;
			}
		}
	}

	/**
	 * Remove the entry in the keys and values such that the key of the entry is
	 * equal to the given object according to the equality function.
	 * 
	 * @param keys
	 *            the array of keys
	 * @param values
	 *            the arrays of values
	 * @param key
	 *            the key for which to delete the entry
	 * @return the value of the deleted entry, <tt>null</tt> if nothing has been
	 *         deleted
	 */
	private V removeEntry(K[] keys, V[] values, Object key) {
		int i = getIndex(key, keys.length);
		int j = i; // for cycle detection
		for (;;) {
			Object probe = keys[i];
			if (probe == null) {
				return null;
			} else if (key.equals(probe)) {
				V result = values[i];
				shift(keys, values, i);
				return result;
			}
			if (i == 0)
				i = keys.length - 1;
			else
				i--;
			if (i == j) // full cycle
				return null;
		}
	}

	/**
	 * Increasing the capacity of the map
	 */
	private void stretch() {
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
		this.upperSize = computeUpperSize(newCapacity);
		this.lowerSize = computeLowerSize(newCapacity);
	}

	/**
	 * Decreasing the capacity of the map
	 */
	private void shrink() {
		int oldCapacity = keys.length;
		if (oldCapacity == 1)
			return;
		K oldKeys[] = keys;
		V oldValues[] = values;
		int newCapacity = oldCapacity >> 1;
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
		this.upperSize = computeUpperSize(newCapacity);
		this.lowerSize = computeLowerSize(newCapacity);
	}

	public V put(K key, V value) {
		if (key == null)
			throw new NullPointerException();
		if (size == upperSize)
			stretch();
		V result = putKeyValue(keys, values, key, value);
		if (result == null)
			size++;
		return result;
	}

	public V remove(Object key) {
		if (key == null)
			throw new NullPointerException();
		V result = removeEntry(keys, values, key);
		if (result != null)
			size--;
		if (size == lowerSize)
			shrink();
		return result;
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public void clear() {
		for (int i = 0; i < keys.length; i++)
			if (keys[i] != null) {
				keys[i] = null;
				values[i] = null;
			}
		size = 0;
	}

	public Set<K> keySet() {
		return new KeySet();
	}

	public Collection<V> values() {
		return new ValueCollection();
	}

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

		public boolean hasNext() {
			return nextKey != null;
		}

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

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private final class KeySet extends AbstractSet<K> {

		public Iterator<K> iterator() {
			return new KeyIterator();
		}

		public boolean contains(Object o) {
			return ArrayHashMap.this.containsKey(o);
		}

		public boolean remove(Object o) {
			return ArrayHashMap.this.remove(o) != null;
		}

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

		public boolean hasNext() {
			return nextValue != null;
		}

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

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private final class ValueCollection extends AbstractCollection<V> {

		public Iterator<V> iterator() {
			return new ValueIterator();
		}

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

		public boolean hasNext() {
			return nextKey != null;
		}

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

		public K getKey() {
			return iterator.keysSnapshot[cursor];
		}

		public V getValue() {
			return iterator.valuesSnapshot[cursor];
		}

		public V setValue(V value) {
			V previous = iterator.valuesSnapshot[cursor];
			iterator.valuesSnapshot[cursor] = value;
			return previous;
		}

	}

	private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
		public Iterator<Map.Entry<K, V>> iterator() {
			return new EntryIterator();
		}

		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry<?, ?>))
				return false;
			Object k = ((Map.Entry<?, ?>) o).getKey();
			return ArrayHashMap.this.containsKey(k);
		}

		public boolean remove(Object o) {
			if (!(o instanceof Map.Entry<?, ?>))
				return false;
			Object k = ((Map.Entry<?, ?>) o).getKey();
			return ArrayHashMap.this.remove(k) != null;
		}

		public int size() {
			return size;
		}

		public void clear() {
			ArrayHashMap.this.clear();
		}
	}

}
