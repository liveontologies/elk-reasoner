/**
 * 
 */
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
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Represents a {@link Collection} view consisting of elements from a given
 * {@link Collection} without elements from the given {@link Set}. The given
 * {@link Collection} and {@link Set} are not modified as a result of this
 * operation. The resulting {@link Collection} does not support addition or
 * removal of elements; if attempted, an {@link UnsupportedOperationException}
 * will be thrown.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <E>
 *            the type of elements in the collection
 */
public class LazyCollectionMinusSet<E> extends AbstractCollection<E> {

	private final Collection<? extends E> collection_;
	private final Set<? extends E> set_;

	public LazyCollectionMinusSet(final Collection<? extends E> collection,
			final Set<? extends E> set) {
		collection_ = collection;
		set_ = set;
	}

	@Override
	public boolean isEmpty() {
		return set_.containsAll(collection_);
	}

	@Override
	public boolean contains(Object o) {
		return collection_.contains(o) && !set_.contains(o);
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {

			private final Iterator<? extends E> iter_ = collection_.iterator();
			private E next_ = null;

			@Override
			public boolean hasNext() {

				while (next_ == null && iter_.hasNext()) {
					E elem = iter_.next();

					next_ = set_.contains(elem) ? null : elem;
				}

				return next_ != null;
			}

			@Override
			public E next() {
				if (next_ != null) {
					return giveAway();
				} else if (hasNext()) {
					return giveAway();
				} else {
					throw new NoSuchElementException();
				}
			}

			private E giveAway() {
				E elem = next_;

				next_ = null;

				return elem;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	@Override
	public int size() {
		return collection_.size() - set_.size();
	}
}