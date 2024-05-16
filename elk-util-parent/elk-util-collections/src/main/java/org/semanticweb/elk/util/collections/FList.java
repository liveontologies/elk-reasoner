package org.semanticweb.elk.util.collections;

/*
 * #%L
 * ELK Utilities Collections
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A simple interface for lists as used in functional programming;
 * 
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <E>
 *            the elements contained in the list
 */
public interface FList<E> extends Iterable<E> {

	/**
	 * @return the first element in the list; it cannot be {@code null}
	 */
	E getHead();

	/**
	 * @return the remaining elements of this {@link FList} except for the first
	 *         one, or {@code null} if there are no such elements
	 */
	FList<E> getTail();

	public static class FListIterator<E> implements Iterator<E> {

		private FList<E> list_;

		public FListIterator(FList<E> list) {
			this.list_ = list;
		}

		@Override
		public boolean hasNext() {
			return list_ != null;
		}

		@Override
		public E next() {
			if (list_ == null)
				throw new NoSuchElementException();
			E result = list_.getHead();
			list_ = list_.getTail();
			return result;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"The list cannot be modified!");
		}

	}

	public static class Equality {

		public static boolean equals(FList<?> first, Object second) {
			if (second instanceof FList) {
				return equals(first, (FList<?>) second);
			}
			// else
			return false;
		}

		private static boolean equals(FList<?> first, FList<?> second) {
			// implementation without recursive call
			for (;;) {
				if (first == second) {
					return true;
				}
				// else
				if (first == null || second == null) {
					return false;
				}
				// else
				if (!first.getHead().equals(second.getHead()))
					return false;
				// else
				first = first.getTail();
				second = second.getTail();
			}

		}
	}

	public static class Hash {

		public static int hashCode(FList<?> list) {
			// compute has code like for a list
			int result = 0;
			for (;;) {
				if (list == null)
					return result;
				// else
				result = 31 * list.getHead().hashCode();
				list = list.getTail();
			}
		}
	}

}
