package org.semanticweb.elk.util.collections;
/*
 * #%L
 * ELK Utilities Collections
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Represents a {@link Set} view for the union of elements of two sets. The
 * resulted set is backed by the input sets, so changes to any of the sets are
 * reflected in the union. The main purpose of the class is to facilitate
 * iteration and checking the membership over union of sets without computing
 * the set explicitly (and thus potentially saving memory). When iterating over
 * the union, the common elements of the two sets are iterated only once. An
 * element is contained in the union as reported by {@code contains} if it is
 * contained in one of the input sets. The size of the union reported by
 * {@code size} is the sum of the sizes for the sets. This way it is possible to
 * use the result to build other {@link LazySetIntersection} and
 * {@link LazySetUnion} objects. If any of the sets is modified while an
 * iteration over the union is in progress, the results of the iteration are
 * undefined. The set union does not support additions or removal of elements;
 * if attempted, an {@link UnsupportedOperationException} will be thrown.
 * 
 * 
 * @author Yevgeny Kazakov
 * @param <E>
 *            the type of the elements in this set
 * @see LazySetIntersection
 * 
 */
public class LazySetUnion<E> extends AbstractSet<E> {

	final Set<E> firstSet;
	final Set<E> secondSet;

	/**
	 * Returns a new {@link Set} view for union of two input sets.
	 * 
	 * @param firstSet
	 *            the first set of the union
	 * @param secondSet
	 *            the second set of the union
	 */
	public LazySetUnion(Set<E> firstSet, Set<E> secondSet) {
		this.firstSet = firstSet;
		this.secondSet = secondSet;
	}

	@Override
	public Iterator<E> iterator() {
		return new SetUnionIterator<E>(firstSet, secondSet);
	}

	@Override
	public boolean contains(Object o) {
		return firstSet.contains(o) || secondSet.contains(o);
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return firstSet.size() + secondSet.size();
	}

	@Override
	public boolean isEmpty() {
		return firstSet.isEmpty() && secondSet.isEmpty();
	}

	static class SetUnionIterator<E> implements Iterator<E> {
		final Iterator<E> firstIterator;
		final Iterator<E> secondIterator;
		final Set<E> secondChecker;

		SetUnionIterator(Set<E> firstSet, Set<E> secondSet) {
			this.firstIterator = firstSet.iterator();
			this.secondIterator = secondSet.iterator();
			this.secondChecker = secondSet;
		}

		@Override
		public boolean hasNext() {
			return firstIterator.hasNext() || secondIterator.hasNext();
		}

		@Override
		public E next() {
			while (firstIterator.hasNext()) {
				E next = firstIterator.next();
				if (!secondChecker.contains(next))
					return next;
			}
			return secondIterator.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
