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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 TODO do we need both the lazy set union and the lazy collection union?
 */
public class LazyCollectionUnion<E> extends AbstractCollection<E> {

	final Collection<? extends E> firstSet;
	final Collection<? extends E> secondSet;

	/**
	 * Returns a new {@link Set} view for union of two input sets.
	 * 
	 * @param firstSet
	 *            the first set of the union
	 * @param secondSet
	 *            the second set of the union
	 */
	public LazyCollectionUnion(Collection<? extends E> firstSet, Collection<? extends E> secondSet) {
		this.firstSet = firstSet == null ? Collections.<E>emptySet() : firstSet;
		this.secondSet = secondSet == null ? Collections.<E>emptySet() : secondSet;
	}

	@Override
	public Iterator<E> iterator() {
		return new CollectionUnionIterator<E>(firstSet, secondSet);
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

	static class CollectionUnionIterator<E> implements Iterator<E> {
		final Iterator<? extends E> firstIterator;
		final Iterator<? extends E> secondIterator;
		final Collection<? extends E> secondChecker;

		CollectionUnionIterator(Collection<? extends E> firstSet, Collection<? extends E> secondSet) {
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
				// make sure that every element will be returned only once
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
