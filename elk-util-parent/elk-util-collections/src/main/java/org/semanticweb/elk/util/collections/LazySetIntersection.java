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
 * @author Yevgeny Kazakov, May 26, 2011
 */
package org.semanticweb.elk.util.collections;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Represents a {@link Set} view for common elements of two sets. The
 * intersection is backed by the input sets, so changes to any of the sets are
 * reflected in the intersection. The main purpose of the class is to facilitate
 * iteration over intersection of sets without computing the intersection
 * explicitly. Iteration over the intersection is organized by iterating over
 * the elements in the smallest set and checking if the elements are present in
 * the other set. An element is contained in the intersection as reported by
 * {@code contains} if it is contained in all input sets. The size of the
 * intersection reported by {@code size} is the minimum of the sizes for the
 * sets. This way it is possible to use the result to build other
 * {@link LazySetIntersection} and {@link LazySetUnion} objects. If any of the
 * sets is modified while an iteration over the intersection is in progress, the
 * results of the iteration are undefined. The set intersection does not support
 * additions or removal of elements; if attempted, an
 * {@link UnsupportedOperationException} will be thrown.
 * 
 * 
 * @author Yevgeny Kazakov
 * @param <E>
 *            the type of the elements in this set
 * @see LazySetUnion
 * 
 */
public class LazySetIntersection<E> extends AbstractSet<E> {

	final Set<E> smallSet;
	final Set<E> largeSet;

	/**
	 * Returns a new {@link Set} view for intersection of two input sets.
	 * 
	 * @param firstSet
	 *            the first set of the intersection
	 * @param secondSet
	 *            the second set of the intersection
	 */
	public LazySetIntersection(Set<E> firstSet, Set<E> secondSet) {
		if (firstSet.size() < secondSet.size()) {
			this.smallSet = firstSet;
			this.largeSet = secondSet;
		} else {
			this.smallSet = secondSet;
			this.largeSet = firstSet;
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<E> iterator() {
		try {
			// create a more efficient iterator if the set supports direct
			// access
			return new RawSetIntersectionIterator<E>(
					((DirectAccess<E>) smallSet).getRawData(), largeSet);
		} catch (ClassCastException e) {
			// resort to generic set intersection otherwise
			return new SetIntersectionIterator<E>(smallSet, largeSet);
		}
	}

	@Override
	public boolean contains(Object o) {
		return smallSet.contains(o) && largeSet.contains(o);
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return smallSet.size();
	}

	@Override
	public boolean isEmpty() {
		return !iterator().hasNext();
	}

	static class SetIntersectionIterator<E> implements Iterator<E> {
		final Iterator<E> elementIterator;
		final Set<E> elementChecker;
		// reference to the next element
		E next;

		SetIntersectionIterator(Set<E> iteratingSet, Set<E> checkingSet) {
			this.elementIterator = iteratingSet.iterator();
			this.elementChecker = checkingSet;
			seekNext();
		}

		void seekNext() {
			while (elementIterator.hasNext()) {
				next = elementIterator.next();
				if (elementChecker.contains(next))
					return;
			}
			next = null;
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public E next() {
			if (next == null)
				throw new NoSuchElementException();
			E result = next;
			seekNext();
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	static class RawSetIntersectionIterator<E> implements Iterator<E> {
		final E[] rawElements;
		final Set<E> elementChecker;
		// current position within randomAccessSet
		int pos = 0;
		// reference to the next element
		E next;

		RawSetIntersectionIterator(E[] rawElements, Set<E> checkingSet) {
			this.rawElements = rawElements;
			this.elementChecker = checkingSet;
			seekNext();
		}

		void seekNext() {
			while (pos < rawElements.length) {
				next = rawElements[pos++];
				if (next != null && elementChecker.contains(next))
					return;
			}
			next = null;
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public E next() {
			if (next == null)
				throw new NoSuchElementException();
			E result = next;
			seekNext();
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}
