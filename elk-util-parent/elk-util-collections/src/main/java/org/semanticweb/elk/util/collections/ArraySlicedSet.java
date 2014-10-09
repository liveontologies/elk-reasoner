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

	static int S_ = 1;

	/**
	 * The table for the elements; the length MUST always be a power of two.
	 */
	transient E[] data;

	/**
	 * The masks encoding the membership of elements in slices
	 */
	transient int[] masks;

	/**
	 * The number of elements contained in the respective slice
	 */
	transient int[] sizes;

	/**
	 * The number of non-{@code null} elements in {@link #data}
	 */
	transient int occupied = 0;

	@SuppressWarnings("unchecked")
	public ArraySlicedSet(int initialCapacity) {
		int capacity = LinearProbing.getInitialCapacity(initialCapacity);
		this.data = (E[]) new Object[capacity];
		this.masks = new int[getMaskCapacity(capacity)];
		initSizes();
	}

	@SuppressWarnings("unchecked")
	public ArraySlicedSet() {
		int capacity = LinearProbing.DEFAULT_INITIAL_CAPACITY;
		this.data = (E[]) new Object[capacity];
		this.masks = new int[getMaskCapacity(capacity)];
		initSizes();
	}

	void initSizes() {
		int slices = 1 << S_;
		this.sizes = new int[slices];
		for (int i = 0; i < slices; i++)
			this.sizes[i] = 0;
	}

	static int getMaskCapacity(int capacity) {
		int result = capacity >> 4;
		if (result == 0)
			result = 1;
		return result;
	}

	static int msk(int n) {
		return (1 << (n + 1)) - 1;
	}

	static int getFragment(int[] masks, int pos) {
		int shift = (5 - S_);
		return (masks[pos >> shift] >> ((pos & msk(shift)) << S_)) & msk(S_);
	}

	static void changeFragment(int[] masks, int pos, int diff) {
		int shift = (5 - S_);
		masks[pos >> shift] ^= diff << ((pos & msk(shift)) << S_);
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

	private static <E> int addMask(E[] data, int[] masks, E e, int mask) {
		int pos = LinearProbing.getPosition(data, e);
		int oldMask = getFragment(masks, pos);
		if (data[pos] == null) {
			data[pos] = e;
			changeFragment(masks, pos, oldMask ^ mask);
			return 0;
		}
		// else
		int newMask = (oldMask | mask);
		if (newMask != oldMask)
			changeFragment(masks, pos, oldMask ^ newMask);
		return oldMask;
	}

	private static <K, V> void remove(K[] k, int[] masks, int pos) {
		int oldFragment = getFragment(masks, pos);
		for (;;) {
			int next = LinearProbing.getMovedPosition(k, pos);
			K moved = k[pos] = k[next];
			int newFragment = getFragment(masks, next);
			changeFragment(masks, pos, oldFragment ^ newFragment);
			if (moved == null)
				return;
			// else
			pos = next;
			oldFragment = newFragment;
		}
	}

	private static <E> int removeMask(E[] data, int[] masks, Object o, int mask) {
		int pos = LinearProbing.getPosition(data, o);
		if (data[pos] == null)
			return 0;
		// else
		int oldFragment = getFragment(masks, pos);
		int newFragment = oldFragment & (~mask);
		if (newFragment == 0)
			remove(data, masks, pos);
		else
			changeFragment(masks, pos, oldFragment ^ newFragment);
		return oldFragment;
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
		int[] oldMasks = masks;
		int newCapacity = oldCapacity << 1;
		@SuppressWarnings("unchecked")
		E[] newData = (E[]) new Object[newCapacity];
		int[] newMasks = new int[getMaskCapacity(newCapacity)];
		for (int i = 0; i < oldCapacity; i++) {
			E e = oldData[i];
			if (e != null)
				addMask(newData, newMasks, e, getFragment(oldMasks, i));
		}
		this.data = newData;
		this.masks = newMasks;
	}

	/**
	 * Decreasing the capacity of the table
	 */
	private void shrink() {
		int oldCapacity = data.length;
		if (oldCapacity == 1)
			return;
		E[] oldData = data;
		int[] oldMasks = masks;
		int newCapacity = oldCapacity >> 1;
		@SuppressWarnings("unchecked")
		E[] newData = (E[]) new Object[newCapacity];
		int[] newMasks = new int[getMaskCapacity(newCapacity)];
		for (int i = 0; i < oldCapacity; i++) {
			E e = oldData[i];
			if (e != null)
				addMask(newData, newMasks, e, getFragment(oldMasks, i));
		}
		this.data = newData;
		this.masks = newMasks;
	}

	public boolean contains(int s, Object o) {
		if (o == null)
			throw new NullPointerException();
		// to avoid problems in the middle of resizing, we copy data and masks
		// when they have the same size
		E[] d;
		int[] m;
		for (;;) {
			d = this.data;
			m = this.masks;
			if (m.length == getMaskCapacity(d.length))
				break;
		}
		int pos = LinearProbing.getPosition(d, o);
		if (d[pos] == null)
			return false;
		// else
		int mask = 1 << s;
		return ((getFragment(m, pos) & mask) == mask);
	}

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
		int mask = (1 << s);
		int oldMask = addMask(data, masks, e, mask);
		int newMask = oldMask | mask;
		if (newMask == oldMask)
			return false;
		else if (oldMask == 0
				&& ++occupied == LinearProbing.getUpperSize(data.length))
			enlarge();
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
		int mask = 1 << s;
		int oldMask = removeMask(data, masks, o, mask);
		int newMask = oldMask & ~mask;
		if (newMask == oldMask)
			return false;
		// else
		if (newMask == 0
				&& --occupied == LinearProbing.getLowerSize(data.length))
			shrink();
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
	 * Clears all slices of this {@link ArraySlicedSet}. After calling this
	 * methods, all slices are empty.
	 */
	@SuppressWarnings("unchecked")
	public void clear() {
		int capacity = data.length >> 2;
		if (capacity == 0)
			capacity = 1;
		initSizes();
		this.data = (E[]) new Object[capacity];
		this.masks = new int[getMaskCapacity(capacity)];
	}

	/**
	 * @param s
	 *            the slice id (should be between 0 and 1)
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
			if (s > 1 || s < 0)
				throw new IllegalArgumentException("Slice should be in [0;1]: "
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

	}

	private class ElementIterator extends LinearProbingIterator<E, E> {

		final int s;
		final int mask;

		ElementIterator(int s) {
			super(data, sizes[s]);
			this.s = s;
			mask = (1 << s);
			init();
		}

		@Override
		void checkSize(int expectedSize) {
			if (expectedSize != sizes[s])
				throw new ConcurrentModificationException();
		}

		@Override
		boolean isOccupied(int pos) {
			return (dataSnapshot[pos] != null && (getFragment(masks, pos) & mask) == mask);
		}

		@Override
		void remove(int pos) {
			int oldMask = getFragment(masks, pos);
			int newMask = oldMask & (~mask);
			if (newMask == 0) {
				ArraySlicedSet.remove(data, masks, pos);
				occupied--;
			} else
				changeFragment(masks, pos, oldMask ^ newMask);
			sizes[s]--;
		}

		@Override
		E getValue(E element, int pos) {
			return element;
		}
	}
}
