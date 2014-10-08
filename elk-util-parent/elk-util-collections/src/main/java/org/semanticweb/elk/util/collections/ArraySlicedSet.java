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
 * A compact representation of up to 8 sets (called "slices") that can share
 * many elements. The representation is backed by arrays that stores the
 * elements and numerical masks indicating to which "slices" those elements
 * belong to. The membership is checked using hash values of elements with
 * linear probing to resolve hash collisions: see [1] p.526. Parts of the code
 * are inspired by the implementation of {@link java.util.HashMap}.
 * 
 * [1] Donald E. Knuth, The Art of Computer Programming, Volume 3, Sorting and
 * Searching, Second Edition
 * 
 * @author Yevgeny Kazakov
 * @param <E>
 *            the type of the elements in the sets
 * 
 */
public class ArraySlicedSet<E> {

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
	 * The masks encoding the membership of elements in slices
	 */
	transient byte[] masks;

	/**
	 * The number of elements contained in the respective slice
	 */
	transient int[] sizes;

	/**
	 * The number of non-{@code null} elements in {@link #data}
	 */
	transient int occupied = 0;

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
	// int lowerSize;

	@SuppressWarnings("unchecked")
	public ArraySlicedSet(int initialCapacity) {
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
		this.masks = new byte[capacity];
		this.sizes = new int[8];
		for (int i = 0; i < 8; i++)
			this.sizes[i] = 0;
		this.upperSize = computeUpperSize(capacity);
		// this.lowerSize = computeLowerSize(capacity);
	}

	@SuppressWarnings("unchecked")
	public ArraySlicedSet() {
		int capacity = DEFAULT_INITIAL_CAPACITY;
		this.data = (E[]) new Object[capacity];
		this.masks = new byte[capacity];
		this.sizes = new int[8];
		for (int i = 0; i < 8; i++)
			this.sizes[i] = 0;
		this.upperSize = computeUpperSize(capacity);
		// this.lowerSize = computeLowerSize(capacity);
	}

	/**
	 * @param s
	 *            the slice id (between 0 and 7)
	 * @return the number of elements currently stored in the provided slice
	 */
	public int size(int s) {
		return sizes[s];
	}

	/**
	 * @param s
	 *            the slice id (between 0 and 7)
	 * @return {@code true} if the given slice does not contain any elements and
	 *         {@code false} otherwise
	 */
	public boolean isEmpty(int s) {
		return sizes[s] == 0;
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
	// static private int computeLowerSize(int capacity) {
	// return capacity / 4;
	// }

	static private int getIndex(Object o, int length) {
		return o.hashCode() & (length - 1);
	}

	public boolean contains(int n, Object o) {
		if (o == null)
			throw new NullPointerException();
		// to avoid problems in the middle of resizing, we copy data and masks
		// when they have the same size
		E[] d;
		byte[] m;
		for (;;) {
			d = this.data;
			m = this.masks;
			if (d.length == m.length)
				break;
		}
		int i = getIndex(o, d.length);
		int j = i; // for cycle detection
		byte mask = (byte) (1 << n);
		for (;;) {
			Object probe = d[i];
			if (probe == null)
				return false;
			else if (o.equals(probe))
				return ((m[i] & mask) == mask);
			if (++i == d.length)
				i = 0;
			if (i == j) // full cycle
				return false;
		}
	}

	private static <E> byte addMask(E[] data, byte[] masks, E e, byte mask) {
		int i = getIndex(e, data.length);
		for (;;) {
			Object probe = data[i];
			if (probe == null) {
				data[i] = e;
				masks[i] = mask;
				return 0;
			} else if (e.equals(probe)) {
				byte oldMask = masks[i];
				byte newMask = (byte) (oldMask | mask);
				if (newMask != oldMask)
					masks[i] = newMask;
				return oldMask;
			}
			if (++i == data.length)
				i = 0;
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
	private static <E> void shift(E[] data, byte[] masks, int i) {
		int del = i; // the position at which the element is about to be deleted
		int j = i; // testing if the element at this position can still be
					// found by linear probing
		for (;;) {
			if (++j == data.length)
				j = 0;
			// invariant: interval ]del, j] contains non-null elements whose
			// index is in ]del, j]
			if (j == del) {
				// we made a full cycle; no elements have to be shifted
				data[del] = null;
				return;
			}
			E test = data[j];
			if (test == null) {
				// no further elements need to be shifted
				data[del] = null;
				return;
			}
			int k = getIndex(test, data.length);
			// check if k is in ]del, j] (possibly wrapped over)
			if ((del < j) ? (del < k) && (k <= j) : (k <= j) || (del < k))
				// the test element should not be shifted
				continue;
			// else
			// copy the element to the position of deleted element and
			// start deleting its previous location
			data[del] = test;
			masks[del] = masks[j];
			del = j;
		}
	}

	private static <E> byte removeMask(E[] data, byte[] masks, Object o,
			byte mask) {
		int i = getIndex(o, data.length);
		int j = i; // for cycle detection
		for (;;) {
			Object probe = data[i];
			if (probe == null) {
				return 0;
			} else if (o.equals(probe)) {
				byte oldMask = masks[i];
				byte newMask = (byte) (oldMask & (~mask));
				if (newMask == 0)
					shift(data, masks, i);
				else
					masks[i] = newMask;
				return oldMask;
			}
			if (++i == data.length)
				i = 0;
			if (i == j) // full cycle
				return 0;
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
		byte[] oldMasks = masks;
		int newCapacity = oldCapacity << 1;
		@SuppressWarnings("unchecked")
		E[] newData = (E[]) new Object[newCapacity];
		byte[] newMasks = new byte[newCapacity];
		for (int i = 0; i < oldCapacity; i++) {
			E e = oldData[i];
			if (e != null)
				addMask(newData, newMasks, e, oldMasks[i]);
		}
		this.data = newData;
		this.masks = newMasks;
		this.upperSize = computeUpperSize(newCapacity);
		// this.lowerSize = computeLowerSize(newCapacity);
	}

	/**
	 * Decreasing the capacity of the table
	 */
	// private void shrink() {
	// int oldCapacity = data.length;
	// if (oldCapacity == 1)
	// return;
	// E[] oldData = data;
	// byte[] oldMasks = masks;
	// int newCapacity = oldCapacity >> 1;
	// @SuppressWarnings("unchecked")
	// E[] newData = (E[]) new Object[newCapacity];
	// byte[] newMasks = new byte[newCapacity];
	// for (int i = 0; i < oldCapacity; i++) {
	// E e = oldData[i];
	// if (e != null)
	// addMask(newData, newMasks, e, oldMasks[i]);
	// }
	// this.data = newData;
	// this.masks = newMasks;
	// this.upperSize = computeUpperSize(newCapacity);
	// // this.lowerSize = computeLowerSize(newCapacity);
	// }

	/**
	 * Inserts a given element into the given slice
	 * 
	 * @param s
	 *            the slice id (between 0 and 7)
	 * @param e
	 *            the elements to be inserted in to the given slice
	 * @return {@code true} if the given element did not occur in the given
	 *         slice and thus was inserted. Otherwise {@code false} is returned
	 *         and nothing is modified.
	 */
	public boolean add(int s, E e) {
		if (e == null)
			throw new NullPointerException();
		if (occupied == upperSize)
			stretch();
		byte mask = (byte) (1 << s);
		byte oldMask = addMask(data, masks, e, mask);
		byte newMask = (byte) (oldMask | mask);
		if (oldMask == 0)
			occupied++;
		if (newMask == oldMask)
			return false;
		// else
		sizes[s]++;
		return true;
	}

	/**
	 * Removes the given object from the given slice
	 * 
	 * @param s
	 *            the slice id (between 0 and 7)
	 * @param o
	 *            the object that should be removed from the given slice
	 * @return {@code true} if the given object is equal to some element of the
	 *         given slice; this element will be removed from the slice. If
	 *         there is no such an object, {@code false} is returned and nothing
	 *         is modified
	 */
	public boolean remove(int s, Object o) {
		if (o == null)
			throw new NullPointerException();
		byte mask = (byte) (1 << s);
		byte oldMask = removeMask(data, masks, o, mask);
		byte newMask = (byte) (oldMask & ~mask);
		if (newMask == 0)
			occupied--;
		if (newMask == oldMask)
			return false;
		// else
		sizes[s]--;
		return true;
	}

	/**
	 * Removes all element of the given {@link Collection} from the given slice
	 * 
	 * @param s
	 *            the slice id (between 0 and 7)
	 * @param c
	 *            the collection whose elements should be removed
	 * @return {@code true} if at least one element is removed from the slice.
	 *         An element is removed if an equal element is present in the given
	 *         collection. If no elements are removed, {@code false} is returned
	 *         and nothing is modified.
	 */
	public boolean removeAll(int s, Collection<?> c) {
		boolean modified = false;
		for (Object o : c) {
			modified |= remove(s, o);
		}
		return modified;
	}

	/**
	 * Removes all elements in the given slice
	 * 
	 * @param s
	 *            the slice id (between 0 and 7) from which elements should be
	 *            removed
	 */
	public void clear(int s) {
		byte mask = (byte) (1 << s);
		for (int i = 0; i < data.length; i++) {
			for (;;) {
				byte oldMask = masks[i];
				byte newMask = (byte) (oldMask & ~mask);
				if (newMask == 0 && data[i] != null)
					shift(data, masks, i);
				else {
					masks[i] = newMask;
					break;
				}
			}
		}
		sizes[s] = 0;
	}

	/**
	 * Clears all slices of this {@link ArraySlicedSet}. After calling this
	 * methods, all slices are empty.
	 */
	@SuppressWarnings("unchecked")
	public void clear() {
		int capacity = data.length >> 2;
		if (capacity == 0)
			capacity = 1;
		this.sizes = new int[8];
		for (int i = 0; i < 8; i++)
			this.sizes[i] = 0;
		this.data = (E[]) new Object[capacity];
		this.masks = new byte[capacity];
		this.upperSize = computeUpperSize(capacity);
		// this.lowerSize = computeLowerSize(capacity);
	}

	/**
	 * @param s
	 *            the slice id (should be between 0 and 7)
	 * @return the set corresponding to this slice backed by this
	 *         {@link ArraySlicedSet}; it can be modified
	 */
	public Set<E> getSlice(int s) {
		return new Slice(s);
	}

	private class Slice extends AbstractSet<E> implements Set<E> {

		/**
		 * the slice which is viewed by this set
		 */
		final byte s;

		Slice(int s) {
			if (s > 7 || s < 0)
				throw new IllegalArgumentException("Slice should be in [0;7]: "
						+ s);
			this.s = (byte) s;
		}

		@Override
		public int size() {
			return ArraySlicedSet.this.size(s);
		}

		@Override
		public boolean isEmpty() {
			return ArraySlicedSet.this.isEmpty(s);
		}

		@Override
		public boolean contains(Object o) {
			return ArraySlicedSet.this.contains(s, o);
		}

		@Override
		public boolean add(E e) {
			return ArraySlicedSet.this.add(s, e);
		}

		@Override
		public boolean remove(Object o) {
			return ArraySlicedSet.this.remove(s, o);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return ArraySlicedSet.this.removeAll(s, c);
		}

		@Override
		public Iterator<E> iterator() {
			return new ElementIterator(s);
		}

		@Override
		public String toString() {
			return Arrays.toString(toArray());
		}

		@Override
		public void clear() {
			ArraySlicedSet.this.clear(s);
		}
	}

	private class ElementIterator implements Iterator<E> {

		// the slice over which iterating
		final byte s;
		// copy of the data
		final E[] dataSnapshot;
		// copy of masks
		final byte[] masksSnapshot;
		// expected size to check for concurrent modifications
		final int expectedSize;
		// current cursor
		int cursor;
		// next element to return
		E nextElement;

		ElementIterator(byte n) {
			this.s = n;
			this.expectedSize = sizes[n];
			this.dataSnapshot = data;
			this.masksSnapshot = masks;
			cursor = 0;
			seekNext();
		}

		void seekNext() {
			byte mask = (byte) (1 << s);
			while (cursor < dataSnapshot.length) {
				int currentCursor = cursor++;
				if ((nextElement = dataSnapshot[currentCursor]) != null
						&& (masksSnapshot[currentCursor] & mask) == mask)
					return;
			}
			// no next element
			nextElement = null;
		}

		@Override
		public boolean hasNext() {
			return nextElement != null;
		}

		@Override
		public E next() {
			if (expectedSize != sizes[s])
				throw new ConcurrentModificationException();
			if (nextElement == null)
				throw new NoSuchElementException();
			E result = nextElement;
			seekNext();
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
