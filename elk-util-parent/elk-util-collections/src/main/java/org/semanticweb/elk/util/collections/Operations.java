/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.util.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class Operations {

	@SuppressWarnings("unchecked")
	public static <T> Iterable<T> concat(final Iterable<? extends T> a,
			final Iterable<? extends T> b) {
		return concat(Arrays.asList(a, b));
	}

	public static <T> Iterable<T> concat(
			final Iterable<? extends Iterable<? extends T>> input) {
		assert input != null;

		return new Iterable<T>() {

			public Iterator<T> iterator() {

				return new Iterator<T>() {
					Iterator<? extends Iterable<? extends T>> outer = input
							.iterator();

					Iterator<? extends T> inner;
					boolean hasNext = advance();

					public boolean hasNext() {
						return hasNext;
					}

					public T next() {
						if (hasNext) {
							T result = inner.next();
							hasNext = advance();
							return result;
						}
						throw new NoSuchElementException();
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}

					boolean advance() {
						while (true) {
							if (inner != null && inner.hasNext())
								return true;

							if (outer.hasNext())
								inner = outer.next().iterator();
							else
								return false;
						}
					}
				};
			}
		};
	}

	/**
	 * An interface for boolean conditions over some type
	 */
	public interface Condition {
		/**
		 * Checks if the condition holds for an element
		 * 
		 * @param element
		 *            the element for which to check the condition
		 * @return <tt>true</tt> if the condition holds for the element and
		 *         otherwise <tt>false</tt>
		 */
		public boolean holds(Object element);
	}

	public static <T> Iterable<T> filter(final Iterable<T> input,
			final Condition condition) {
		assert input != null;

		return new Iterable<T>() {

			public Iterator<T> iterator() {

				return new Iterator<T>() {
					Iterator<T> i = input.iterator();
					T next;
					boolean hasNext = advance();

					public boolean hasNext() {
						return hasNext;
					}

					public T next() {
						if (hasNext) {
							T result = next;
							hasNext = advance();
							return result;
						}
						throw new NoSuchElementException();
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}

					boolean advance() {
						while (i.hasNext()) {
							next = i.next();
							if (condition.holds(next))
								return true;
						}
						return false;
					}
				};
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static <T, S> Iterable<T> filter(final Iterable<S> input,
			final Class<T> type) {

		return (Iterable<T>) filter(input, new Condition() {
			public boolean holds(Object element) {
				return type.isInstance(element);
			}
		});
	}

	/**
	 * Returns read-only view of the given set consisting of the elements
	 * satisfying a given condition, if the number of such elements is known
	 * 
	 * @param input
	 *            the given set to be filtered
	 * @param condition
	 *            the condition used for filtering the set
	 * @param size
	 *            the number of elements in the filtered set
	 * @return the set consisting of the elements of the input set satisfying
	 *         the given condition
	 */
	public static <T> Set<T> filter(final Set<T> input,
			final Condition condition, final int size) {
		return new Set<T>() {

			public int size() {
				return size;
			}

			public boolean isEmpty() {
				return size == 0;
			}

			public boolean contains(Object o) {
				return input.contains(o) && condition.holds(o);
			}

			public Iterator<T> iterator() {
				return filter(input, condition).iterator();
			}

			public Object[] toArray() {
				Object[] result = new Object[size];
				int i = 0;
				for (Object o : filter(input, condition)) {
					result[i++] = o;
				}
				return result;
			}

			public <S> S[] toArray(S[] a) {
				throw new UnsupportedOperationException();
			}

			public boolean add(T e) {
				throw new UnsupportedOperationException();
			}

			public boolean remove(Object o) {
				throw new UnsupportedOperationException();
			}

			public boolean containsAll(Collection<?> c) {
				for (Object o : c) {
					if (!condition.holds(o) || !input.contains(o))
						return false;
				}
				return true;
			}

			public boolean addAll(Collection<? extends T> c) {
				throw new UnsupportedOperationException();
			}

			public boolean retainAll(Collection<?> c) {
				throw new UnsupportedOperationException();
			}

			public boolean removeAll(Collection<?> c) {
				throw new UnsupportedOperationException();
			}

			public void clear() {
				throw new UnsupportedOperationException();
			}

		};

	}
}
