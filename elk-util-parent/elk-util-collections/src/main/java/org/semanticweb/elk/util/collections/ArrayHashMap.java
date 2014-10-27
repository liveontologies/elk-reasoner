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
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
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
public class ArrayHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {

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

	@SuppressWarnings("unchecked")
	public ArrayHashMap(int initialCapacity) {
		int capacity = LinearProbing.getInitialCapacity(initialCapacity);
		this.keys = (K[]) new Object[capacity];
		this.values = (V[]) new Object[capacity];
		this.size = 0;
	}

	@SuppressWarnings("unchecked")
	public ArrayHashMap() {
		int capacity = LinearProbing.DEFAULT_INITIAL_CAPACITY;
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

	@Override
	public boolean containsKey(Object key) {
		if (key == null)
			throw new NullPointerException();
		return LinearProbing.contains(keys, key);
	}

	@Override
	public boolean containsValue(Object value) {
		if (value == null)
			throw new NullPointerException();
		V[] v = this.values;
		// we could not do much better than linear search
		for (int i = 0; i < keys.length; i++)
			if (value.equals(v[i]))
				return true;
		return false;
	}

	@Override
	public V get(Object key) {
		if (key == null)
			throw new NullPointerException();
		// to avoid problems in the middle of resizing, we copy keys and values
		// when they have the same size
		K[] k;
		V[] v;
		for (;;) {
			k = this.keys;
			v = this.values;
			if (k.length == v.length)
				break;
		}
		int pos = LinearProbing.getPosition(k, key);
		if (k[pos] == null)
			return null;
		// else
		return v[pos];
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
	private static <K, V> V putKeyValue(K[] keys, V[] values, K key, V value) {
		int pos = LinearProbing.getPosition(keys, key);
		if (keys[pos] == null) {
			keys[pos] = key;
			values[pos] = value;
			return null;
		}
		// else
		V oldValue = values[pos];
		values[pos] = value;
		return oldValue;
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
	private static <K, V> V removeEntry(K[] keys, V[] values, Object key) {
		int pos = LinearProbing.getPosition(keys, key);
		if (keys[pos] == null)
			return null;
		// else
		V result = values[pos];
		LinearProbing.remove(keys, values, pos);
		return result;
	}

	/**
	 * Increasing the capacity of the map
	 */
	private void enlarge() {
		int oldCapacity = keys.length;
		if (oldCapacity == LinearProbing.MAXIMUM_CAPACITY)
			throw new IllegalArgumentException(
					"Map cannot grow beyond capacity: "
							+ LinearProbing.MAXIMUM_CAPACITY);
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
	}

	/**
	 * Decreasing the capacity of the map
	 */
	private void shrink() {
		int oldCapacity = keys.length;
		if (oldCapacity <= LinearProbing.DEFAULT_INITIAL_CAPACITY)
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
	}

	@Override
	public V put(K key, V value) {
		if (key == null)
			throw new NullPointerException();
		V result = putKeyValue(keys, values, key, value);
		if (result == null && ++size == LinearProbing.getUpperSize(keys.length))
			enlarge();
		return result;
	}

	@Override
	public V remove(Object key) {
		if (key == null)
			throw new NullPointerException();
		V result = removeEntry(keys, values, key);
		if (result != null && --size == LinearProbing.getLowerSize(keys.length))
			shrink();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void clear() {
		int capacity = keys.length >> 2;
		if (capacity == 0)
			capacity = 1;
		size = 0;
		this.keys = (K[]) new Object[capacity];
		this.values = (V[]) new Object[capacity];
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

	private class KeyIterator extends LinearProbingIterator<K, K> {

		KeyIterator() {
			super(keys, size);
			init();
		}

		@Override
		void checkSize(int expectedSize) {
			if (expectedSize != size)
				throw new ConcurrentModificationException();
		}

		@Override
		void remove(int pos) {
			LinearProbing.remove(dataSnapshot, values, pos);
			size--;
		}

		@Override
		K getValue(K element, int pos) {
			return element;
		}

	}

	private final class KeySet extends AbstractSet<K> implements
			DirectAccess<K> {

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
			return ArrayHashMap.this.remove(o) != null;
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public void clear() {
			ArrayHashMap.this.clear();
		}

		@Override
		public K[] getRawData() {
			return keys;
		}

	}

	private class ValueIterator extends LinearProbingIterator<V, V> {

		ValueIterator() {
			super(values, size);
			init();
		}

		@Override
		void checkSize(int expectedSize) {
			if (expectedSize != size)
				throw new ConcurrentModificationException();
		}

		@Override
		void remove(int pos) {
			LinearProbing.remove(keys, dataSnapshot, pos);
			size--;
		}

		@Override
		V getValue(V element, int pos) {
			return element;
		}

	}

	private final class ValueCollection extends AbstractCollection<V> implements
			DirectAccess<V> {

		@Override
		public Iterator<V> iterator() {
			return new ValueIterator();
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public void clear() {
			ArrayHashMap.this.clear();
		}

		@Override
		public V[] getRawData() {
			return values;
		}

	}

	private class EntryIterator extends
			LinearProbingIterator<K, Map.Entry<K, V>> {

		final V[] valuesSnapshot;

		EntryIterator() {
			super(keys, size);
			this.valuesSnapshot = values;
			init();
		}

		@Override
		void checkSize(int expectedSize) {
			if (expectedSize != size)
				throw new ConcurrentModificationException();
		}

		@Override
		void remove(int pos) {
			LinearProbing.remove(dataSnapshot, values, pos);
			size--;
		}

		@Override
		Map.Entry<K, V> getValue(K element, int pos) {
			return new Entry(this, pos);
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
			return iterator.dataSnapshot[cursor];
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
			if (!(o instanceof Map.Entry<?, ?>))
				return false;
			Object k = ((Map.Entry<?, ?>) o).getKey();
			return ArrayHashMap.this.remove(k) != null;
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
