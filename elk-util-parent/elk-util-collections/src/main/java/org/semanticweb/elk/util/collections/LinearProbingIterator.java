package org.semanticweb.elk.util.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A skeleton for creating iterators of collections backed by arrays with linear
 * probing resolution of hash collisions.
 * 
 * @see LinearProbing
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <E>
 *            the elements for which linear probing is used
 * @param <V>
 *            the elements that should be iterated by the iterator
 */
abstract class LinearProbingIterator<E, V> implements Iterator<V> {
	// copy of the data
	final E[] dataSnapshot;
	// expected size to check for concurrent modifications
	private int sizeSnapshot_;
	// the element at which to start iteration
	private int start_;
	// cursor of the current element
	private int current_;
	// cursor of the next element
	private int next_;

	LinearProbingIterator(E[] data, int size) {
		this.dataSnapshot = data;
		this.sizeSnapshot_ = size;
		this.start_ = seekFirstNull();
		this.next_ = seekNext(start_);
		this.current_ = next_;
	}

	/**
	 * Checks if the given size is the correct size of the data.
	 * 
	 * @param expectedSize
	 */
	abstract void checkSize(int expectedSize);

	/**
	 * Removes the element of the data at the given position, moving other
	 * elements if necessary
	 * 
	 * @param data
	 * @param pos
	 */
	abstract void remove(E[] data, int pos);

	/**
	 * Converts the given element and its position in the data to the
	 * corresponding value of the iterator.
	 * 
	 * @param element
	 * @param pos
	 * @return
	 */
	abstract V getValue(E element, int pos);

	/**
	 * @return the position of the first {@code null} element
	 */
	int seekFirstNull() {
		for (int pos = 0; pos < dataSnapshot.length; pos++) {
			if (dataSnapshot[pos] == null)
				return pos;
		}
		throw new RuntimeException("Set is full!");
	}

	/**
	 * Searches for the next non-{@code null} element after the given position
	 * before the start position
	 * 
	 * @param pos
	 *            position after which to search
	 * @return the position of the non-{@code null} element, or {@link #start_}
	 *         if there are no such element
	 */
	int seekNext(int pos) {
		for (;;) {
			if (++pos == dataSnapshot.length)
				pos = 0;
			if (pos == start_ || dataSnapshot[pos] != null)
				return pos;
		}
	}

	@Override
	public boolean hasNext() {
		return next_ != start_;
	}

	@Override
	public V next() {
		checkSize(sizeSnapshot_);
		if (next_ == start_)
			throw new NoSuchElementException();
		this.current_ = next_;
		E element = dataSnapshot[current_];
		this.next_ = seekNext(current_);
		return getValue(element, current_);
	}

	@Override
	public void remove() {
		checkSize(sizeSnapshot_);
		if (current_ == next_)
			// the current element was not returned or was already removed
			throw new IllegalStateException();
		remove(dataSnapshot, current_);
		if (dataSnapshot[current_] != null)
			// something was copied to the current position
			next_ = current_;
		else
			current_ = next_;
		sizeSnapshot_--;
	}

}