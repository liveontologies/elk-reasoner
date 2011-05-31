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
package org.semanticweb.elk.util;

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
 * p.526.
 * 
 * [1] Donald E. Knuth, The Art of Computer Programming, Volume 3, Sorting and
 * Searching, Second Edition
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ArrayHashMap<K, V> implements Map<K, V> {

	// the keys of the map
	protected transient K[] keys;
	// the values of the map
	protected transient V[] values;
	// the number of key-value entries in the set
	protected transient int size;

	@SuppressWarnings("unchecked")
	public ArrayHashMap(int initialCapacity) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: "
					+ initialCapacity);
		this.keys = (K[]) new Object[initialCapacity];
		this.values = (V[]) new Object[initialCapacity];
		this.size = 0;
	}

	public ArrayHashMap() {
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

	public boolean containsKey(Object key) {
		if (key == null)
			throw new NullPointerException();
		K[] keys = this.keys;
		int i = getIndex(keys.length, key);
		int j = i; // for cycle detection
		for (;;) {
			Object probe = keys[i];
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
		K[] keys = this.keys;
		V[] values = this.values;
		int i = getIndex(keys.length, key);
		int j = i;
		for (;;) {
			Object probe = keys[i];
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

	private V putKeyValue(K[] keys, V[] values, K key, V value) {
		int i = getIndex(keys.length, key);
		for (;;) {
			Object probe = keys[i];
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

	public void ensureCapacity(int size) {
		int oldCapacity = keys.length;
		if (size > oldCapacity || (size > 128 && size > 3 * oldCapacity / 4)) {
			K oldKeys[] = keys;
			V oldValues[] = values;
			int newCapacity = (size * 2) + 1;
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
	}

	public V put(K key, V value) {
		if (key == null)
			throw new NullPointerException();
		ensureCapacity(size + 1);
		V result = putKeyValue(keys, values, key, value);
		if (result == null)
			size++;
		return result;
	}

	public V remove(Object key) {
		throw new UnsupportedOperationException();
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException();
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
		K nextKey;

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
			throw new UnsupportedOperationException();
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
		V nextValue;

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
			if (!(o instanceof Map.Entry))
				return false;
			Object k = ((Map.Entry<?, ?>) o).getKey();
			return ArrayHashMap.this.containsKey(k);
		}

		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}

		public int size() {
			return size;
		}

		public void clear() {
			ArrayHashMap.this.clear();
		}
	}

}
