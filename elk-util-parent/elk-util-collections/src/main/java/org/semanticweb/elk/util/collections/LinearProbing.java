package org.semanticweb.elk.util.collections;

/**
 * A collection of utilities for creating collections (e.g., sets or maps)
 * backed by arrays with linear probing as method for collision resolution: see
 * [1] p.526.
 * 
 * [1] Donald E. Knuth, The Art of Computer Programming, Volume 3, Sorting and
 * Searching, Second Edition
 * 
 * @author Yevgeny Kazakov
 * 
 */
class LinearProbing {

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
	 * Computes the initial capacity of the table for storing elements. The
	 * capacity is the largest power of two that does not exceed the given value
	 * or {@link #MAXIMUM_CAPACITY}.
	 * 
	 * @param capacity
	 *            the input value used as a guideline
	 * @return the largest power of two that does not exceed the given value or
	 *         {@link #MAXIMUM_CAPACITY}
	 */
	static int getInitialCapacity(int capacity) {
		if (capacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: " + capacity);
		if (capacity > LinearProbing.MAXIMUM_CAPACITY)
			capacity = LinearProbing.MAXIMUM_CAPACITY;
		// Find a power of 2 >= initialCapacity
		int result = 1;
		while (result < capacity)
			result <<= 1;
		return result;
	}

	/**
	 * Computes a maximum size of the table for a given capacity after which the
	 * table should be expanded.
	 * 
	 * @param capacity
	 *            the capacity of the table.
	 * @return maximum size of the table for a given capacity after which to
	 *         stretch the table.
	 */
	static int getUpperSize(int capacity) {
		if (capacity > 64)
			return (3 * capacity) / 4; // max 75% filled
		// else
		return capacity - 1;
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
	static int getLowerSize(int capacity) {
		return capacity / 4;
	}

	/**
	 * Computes the index position for the object relative to the given length
	 * of the table. This is the position starting from which the object should
	 * be searched for using linear probing.
	 * 
	 * @param o
	 *            the object for which to compute the position
	 * @param length
	 *            the length of the table in which the objects are located
	 * @return the first position in the table from which the object should be
	 *         searched
	 */
	private static int getIndex(Object o, int length) {
		return o.hashCode() & (length - 1);
	}

	static <E> int getPosition(E[] d, Object o) {
		int i = getIndex(o, d.length);
		for (;;) {
			Object probe = d[i];
			if (probe == null || o.equals(probe))
				return i;
			if (++i == d.length)
				i = 0;
		}
	}

	/**
	 * Removes the element at the given position of the table shifting, if
	 * necessary, other elements so that all elements can be found by linear
	 * probing.
	 * 
	 * @param d
	 *            the array of the elements
	 * @param pos
	 *            the position of data at which to delete the element
	 */
	static <E> void remove(E[] d, int pos) {
		for (;;) {
			int next = getMovedPosition(d, pos);
			E moved = d[pos] = d[next];
			if (moved == null)
				return;
			// else
			pos = next;
		}
	}

	/**
	 * Removes the element at the given position of the table and the
	 * corresponding value at the same position, shifting, if necessary, other
	 * elements and values so that all elements can be found by linear probing.
	 * 
	 * @param d
	 *            the array of the elements
	 * @param pos
	 *            the position of data at which to delete the element
	 */
	static <K, V> void remove(K[] k, V[] v, int pos) {
		for (;;) {
			int next = getMovedPosition(k, pos);
			K moved = k[pos] = k[next];
			v[pos] = v[next];
			if (moved == null)
				return;
			// else
			pos = next;
		}
	}

	/**
	 * Finds the position of the next element starting from the given position
	 * that would not be found by linear probing if the element at the given
	 * position are deleted. This should be the element whose index is smaller
	 * than this position.
	 * 
	 * @param d
	 * @param del
	 * @return
	 */
	static <E> int getMovedPosition(E[] d, int del) {
		int j = del;
		for (;;) {
			if (++j == d.length)
				j = 0;
			// invariant: interval ]del, j] contains only non-null elements
			// whose index is in ]del, j]
			E test = d[j];
			if (test == null)
				return j;
			int k = getIndex(test, d.length);
			// check if k is in ]del, j] (this interval can wrap over)
			if ((del < j) ? (del < k) && (k <= j) : (del < k) || (k <= j))
				// the test element should not be shifted
				continue;
			// else it should be shifted
			return j;
		}

	}

	/**
	 * Tests if the set represented by given data array contains a given object.
	 * 
	 * @param d
	 *            the elements representing the set
	 * @param o
	 *            the object to be tested on inclusion in the set
	 * @return {@code true} if the object occurs in the array and {@code false}
	 *         otherwise
	 */
	static <E> boolean contains(E[] d, Object o) {
		int pos = getPosition(d, o);
		if (d[pos] == null)
			return false;
		// else
		return true;
	}

	/**
	 * Adds the element to the set represented by given data array, if it did
	 * not contain there already.
	 * 
	 * @param d
	 *            the elements of the set
	 * @param e
	 *            the element to be added to the set
	 * @return {@code true} if the set has changed (the element is added),
	 *         {@code false} otherwise
	 */
	static <E> boolean add(E[] d, E e) {
		int pos = getPosition(d, e);
		if (d[pos] == null) {
			d[pos] = e;
			return true;
		}
		// else the element is already there
		return false;
	}

	/**
	 * Removes the element from the set represented by given data array, if it
	 * occurs there.
	 * 
	 * @param d
	 *            the elements of the set
	 * @param e
	 *            the element to be removed from the array
	 * @return {@code true} if the set has changed (the element is added),
	 *         {@code false} otherwise
	 */
	static <E> boolean remove(E[] d, Object o) {
		int pos = getPosition(d, o);
		if (d[pos] == null)
			return false;
		// else
		remove(d, pos);
		return true;
	}
}
