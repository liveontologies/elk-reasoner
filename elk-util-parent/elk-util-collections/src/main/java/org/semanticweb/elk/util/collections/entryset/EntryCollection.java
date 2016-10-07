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

import java.util.AbstractCollection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * TODO: update documentation
 * 
 * A collection of entries, maintained modulo structural equality
 * {@code Entry#structuralEquals(Object)}. This may be different from
 * {@code Entry#equals(Object)}, which is used for finding an element in the
 * collection. The main methods are inserting, removing, and finding elements
 * modulo structural equality.
 * 
 * The implementation is largely based on the implementation of
 * {@linkplain HashSet} from the standard Java collection library.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * 
 * @param <E>
 *            the type of entries in the collection
 */
public class EntryCollection<E extends Entry<?, E>>
		extends AbstractCollection<E> {

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
	 * The under-load factor used when none specified in constructor.
	 */
	static final float DEFAULT_UNDERLOAD_FACTOR = 0.15f;

	/**
	 * The over-load factor used when none specified in constructor.
	 */
	static final float DEFAULT_OVERLOAD_FACTOR = 0.75f;

	/**
	 * The bucket array, resized as necessary. Length MUST Always be a power of
	 * two.
	 */
	transient E[] buckets;

	/**
	 * The number of entries contained in this map.
	 */
	transient int size;

	/**
	 * The minimum size of the set, below which the set is not shrink.
	 * 
	 * @serial
	 */
	int minsize;

	/**
	 * The next minimum size value at which to shrink the buckets array array
	 * (capacity * under-load factor).
	 * 
	 * @serial
	 */
	int undersize;

	/**
	 * The next maximum size value at which to expand the buckets array
	 * (capacity * over-load factor).
	 * 
	 * @serial
	 */
	int oversize;

	/**
	 * The over-load factor for the buckets array.
	 * 
	 * @serial
	 */
	final float overloadFactor;

	/**
	 * The under-load factor for the buckets array.
	 * 
	 * @serial
	 */
	final float underloadFactor;

	/**
	 * The number of times this {@link EntryCollection} has been modified. This
	 * field is used to make iterators on Collection-views of the HashMap
	 * fail-fast. (See ConcurrentModificationException).
	 */
	transient int modCount;

	/**
	 * Constructs an empty {@link EntryCollection} with the specified initial
	 * capacity, under-load factor, and over-load factor. The over-load factor
	 * needs to be larger than twice the under-load factor (ideally even more).
	 * 
	 * @param initialCapacity
	 *            the initial capacity
	 * @param underloadFactor
	 *            the under-load factor
	 * @param overloadFactor
	 *            the over-load factor
	 * @throws IllegalArgumentException
	 *             if the initial capacity is negative or the load factor is
	 *             non-positive
	 */
	@SuppressWarnings("unchecked")
	public EntryCollection(int initialCapacity, float underloadFactor,
			float overloadFactor) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException(
					"Illegal initial capacity: " + initialCapacity);
		if (initialCapacity > MAXIMUM_CAPACITY)
			initialCapacity = MAXIMUM_CAPACITY;
		if (overloadFactor <= 0 || Float.isNaN(overloadFactor))
			throw new IllegalArgumentException(
					"Illegal load factor: " + overloadFactor);

		// Find a power of 2 >= initialCapacity
		int capacity = 1;
		while (capacity < initialCapacity)
			capacity <<= 1;

		this.underloadFactor = underloadFactor;
		this.overloadFactor = overloadFactor;
		this.minsize = initialCapacity;
		undersize = (int) (capacity * underloadFactor);
		oversize = (int) (capacity * overloadFactor);
		buckets = (E[]) new Entry[capacity];
		init();
	}

	/**
	 * Constructs an empty {@link EntryCollection} with the specified initial
	 * capacity, the default under-load factor (0.15), and the default over-load
	 * factor (0.75).
	 * 
	 * @param initialCapacity
	 *            the initial capacity.
	 * @throws IllegalArgumentException
	 *             if the initial capacity is negative.
	 */
	public EntryCollection(int initialCapacity) {
		this(initialCapacity, DEFAULT_UNDERLOAD_FACTOR,
				DEFAULT_OVERLOAD_FACTOR);
	}

	/**
	 * Constructs an empty {@link EntryCollection} with the default initial
	 * capacity (16), the default under-load factor (0.15), and the default
	 * over-load factor (0.75).
	 */
	@SuppressWarnings("unchecked")
	public EntryCollection() {
		this.underloadFactor = DEFAULT_UNDERLOAD_FACTOR;
		this.overloadFactor = DEFAULT_OVERLOAD_FACTOR;
		undersize = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_UNDERLOAD_FACTOR);
		oversize = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_OVERLOAD_FACTOR);
		buckets = (E[]) new Entry[DEFAULT_INITIAL_CAPACITY];
		init();
	}

	// internal utilities

	/**
	 * Initialization hook for subclasses. This method is called in all
	 * constructors and pseudo-constructors (clone, readObject) after
	 * {@link EntryCollection} has been initialized but before any entries have
	 * been inserted. (In the absence of this method, readObject would require
	 * explicit knowledge of subclasses.)
	 */
	void init() {
	}

	/**
	 * Applies a supplemental hash function to a given hashCode, which defends
	 * against poor quality hash functions. This is critical because
	 * {@link EntryCollection} uses power-of-two length buckets array, that
	 * otherwise encounter collisions for hashCodes that do not differ in lower
	 * bits.
	 */
	static int hash(int h) {
		// This function ensures that hashCodes that differ only by
		// constant multiples at each bit position have a bounded
		// number of collisions (approximately 8 at default load factor).
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	/**
	 * Returns index for hash code h.
	 */
	static int indexFor(int h, int length) {
		return hash(h) & (length - 1);
	}

	/**
	 * Returns the number of entries in this set.
	 * 
	 * @return the number of entries in this set
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * Finds and returns the entry in set that is structurally equal to the
	 * input entry if there is one. Equality of entries is decided using
	 * {@link Entry#structuralHashCode()} and
	 * {@link Entry#structuralEquals(Object)} methods.
	 * 
	 * @param entry
	 *            the entry for which the equal entry should be found
	 * 
	 * @return the entry in the set that is equal to the input entry if there is
	 *         one, or {@code null} otherwise
	 * 
	 */
	public <T extends Entry<T, ?>> T findStructural(Entry<T, ?> entry) {
		int h = entry.structuralHashCode();
		int i = indexFor(h, buckets.length);
		T result = null;
		for (E r = buckets[i]; r != null; r = r.getNext()) {
			if (r.structuralHashCode() == h
					&& (result = entry.structuralEquals(r)) != null)
				return result;
		}
		// else fail
		return null;
	}

	/**
	 * Adds the given entry to this collection; the entry is added even if a
	 * structurally equal entry (modulo {@link Entry#structuralHashCode()} and
	 * {@link Entry#structuralEquals(Object)}) is already present in the
	 * collection
	 * 
	 * @param entry
	 *            the entry to be inserted; it is not allowed to have linked
	 *            elements: {@link Entry#getNext()} should be {@code null}
	 */
	public void addStructural(E entry) {
		if (entry.getNext() != null)
			throw new IllegalArgumentException(
					"The given entry should be fresh!");
		int h = entry.structuralHashCode();
		int i = indexFor(h, buckets.length);
		modCount++;
		E e = buckets[i];
		entry.setNext(e);
		buckets[i] = entry;
		if (size++ >= oversize)
			resize(2 * buckets.length);
	}

	/**
	 * Removes and returns the entry in the set that is structurally equal to
	 * the specified entry. Returns {@code null} if the set contains no such
	 * entry. Equality of entries is decided using
	 * {@link Entry#structuralHashCode()} and
	 * {@link Entry#structuralEquals(Object)} methods.
	 * 
	 * @param entry
	 *            the entry that is used for finding the entry to be removed
	 * @return the removed entry, or {@code null} if no entry that is equal to
	 *         the input object is found
	 */
	public <T extends Entry<T, ?>> T removeStructural(Entry<T, ?> entry) {
		int h = entry.structuralHashCode();
		int i = indexFor(h, buckets.length);
		E prev = buckets[i];
		E r = prev;
		T result = null;

		while (r != null) {
			E next = r.getNext();
			if (r.structuralHashCode() == h
					&& (result = entry.structuralEquals(r)) != null) {
				modCount++;
				if (prev == r)
					buckets[i] = next;
				else
					prev.setNext(next);
				if (size-- <= undersize && buckets.length >= 2 * minsize)
					resize(buckets.length / 2);
				return result;
			}
			prev = r;
			r = next;
		}
		// not found
		return null;
	}

	/**
	 * Rehashes the contents of this map into a new array with a new capacity.
	 * This method is called automatically when the number of entries in this
	 * set becomes below the {@link #undersize} or above the {@link #oversize}.
	 * 
	 * If current capacity is MAXIMUM_CAPACITY, this method does not resize the
	 * map, but sets threshold to Integer.MAX_VALUE. This has the effect of
	 * preventing future calls.
	 * 
	 * @param newCapacity
	 *            the new capacity, MUST be a power of two
	 */
	void resize(int newCapacity) {
		E[] oldTable = buckets;
		int oldCapacity = oldTable.length;
		if (oldCapacity == MAXIMUM_CAPACITY) {
			oversize = Integer.MAX_VALUE;
			oversize = (int) (newCapacity * overloadFactor);
			return;
		}

		@SuppressWarnings("unchecked")
		E[] newTable = (E[]) new Entry[newCapacity];
		transfer(newTable);
		buckets = newTable;
		undersize = (int) (newCapacity * underloadFactor);
		oversize = (int) (newCapacity * overloadFactor);
	}

	/**
	 * Transfers all entries from current buckets array to new buckets array.
	 */
	/**
	 * @param newArray
	 *            the new buckets array into which the entries are transferred.
	 */
	void transfer(E[] newArray) {
		E[] src = buckets;
		int newCapacity = newArray.length;
		for (int j = 0; j < src.length; j++) {
			E e = src[j];
			if (e != null) {
				src[j] = null;
				do {
					E next = e.getNext();
					int i = indexFor(e.structuralHashCode(), newCapacity);
					e.setNext(newArray[i]);
					newArray[i] = e;
					e = next;
				} while (e != null);
			}
		}
	}

	/**
	 * Removes all entries from this set. The set will be empty after this call
	 * returns.
	 */
	@Override
	public void clear() {
		modCount++;
		E[] tab = buckets;
		for (int i = 0; i < tab.length; i++)
			tab[i] = null;
		size = 0;
	}

	@Override
	public Iterator<E> iterator() {
		return new EntryIterator();
	}

	/**
	 * The iterator over the entries in the collection.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class EntryIterator implements Iterator<E> {
		E next; // next entry to return
		int expectedModCount; // For fast-fail
		int index; // current slot
		E current; // current entry

		EntryIterator() {
			expectedModCount = modCount;
			if (size > 0) { // advance to first entry
				E[] t = buckets;
				while (index < t.length && (next = t[index++]) == null)
					;
			}
		}

		@Override
		public final boolean hasNext() {
			return next != null;
		}

		@Override
		public final E next() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
			E e = next;
			if (e == null)
				throw new NoSuchElementException();

			if ((next = e.getNext()) == null) {
				E[] t = buckets;
				while (index < t.length && (next = t[index++]) == null)
					;
			}
			current = e;
			return e;
		}

		@Override
		public void remove() {
			if (current == null)
				throw new IllegalStateException();
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
			EntryCollection.this.removeStructural((Entry<?, ?>) current);
			current = null;
			expectedModCount = modCount;
		}

	}

}
