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

	public static <T> Iterable<T> singleton(final T element) {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new Iterator<T>() {
					boolean hasNext = true;

					@Override
					public boolean hasNext() {
						return hasNext;
					}

					@Override
					public T next() {
						hasNext = false;
						return element;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

	public static <T> Iterable<T> concat(
			final Iterable<? extends Iterable<? extends T>> input) {
		assert input != null;

		return new Iterable<T>() {

			@Override
			public Iterator<T> iterator() {

				return new Iterator<T>() {
					Iterator<? extends Iterable<? extends T>> outer = input
							.iterator();

					Iterator<? extends T> inner;
					boolean hasNext = advance();

					@Override
					public boolean hasNext() {
						return hasNext;
					}

					@Override
					public T next() {
						if (hasNext) {
							T result = inner.next();
							hasNext = advance();
							return result;
						}
						throw new NoSuchElementException();
					}

					@Override
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
	 * An interface for boolean conditions over some type.
	 * 
	 */
	public interface Condition<T> {
		/**
		 * Checks if the condition holds for an element
		 * 
		 * @param element
		 *            the element for which to check the condition
		 * @return <tt>true</tt> if the condition holds for the element and
		 *         otherwise <tt>false</tt>
		 */
		public boolean holds(T element);
	}

	/**
	 * 
	 * @param input
	 * @param condition 
	 * @return
	 */
	public static <T> Iterable<T> filter(final Iterable<T> input, final Condition<? super T> condition) {
		assert input != null;

		return new Iterable<T>() {

			@Override
			public Iterator<T> iterator() {

				return new Iterator<T>() {
					Iterator<T> i = input.iterator();
					T next;
					boolean hasNext = advance();

					@Override
					public boolean hasNext() {
						return hasNext;
					}

					@Override
					public T next() {
						if (hasNext) {
							T result = next;
							hasNext = advance();
							return result;
						}
						throw new NoSuchElementException();
					}

					@Override
					public void remove() {
						i.remove();
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
	public static <T, S> Iterable<T> filter(final Iterable<S> input, final Class<T> type) {

		return (Iterable<T>) filter(input, new Condition<S>() {
			@Override
			public boolean holds(S element) {
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
	 *            the condition used for filtering the set. 
	 *            Must be consistent with equals() for T, that is: a.equals(b) must imply that holds(a) == holds(b)
	 * @param size
	 *            the number of elements in the filtered set
	 * @return the set consisting of the elements of the input set satisfying
	 *         the given condition
	 */
	public static <T> Set<T> filter(final Set<T> input, final Condition<? super T> condition, final int size) {
		return new Set<T>() {

			@Override
			public int size() {
				return size;
			}

			@Override
			public boolean isEmpty() {
				return size == 0;
			}

			@Override
			@SuppressWarnings("unchecked")
			public boolean contains(Object o) {
				
				if (!input.contains(o)) return false;
				
				T elem = null;
				
				try {
					elem = (T) o;
				} catch (ClassCastException cce) {
					return false;
				}
				//here's why the condition must be consistent with equals():
				//we check it on the passed element while we really need to check it on the element 
				//which is in the underlying set (and is equal to o according to equals()). 
				//However, as long as the condition is consistent, the result will be the same.
				return condition.holds(elem);				
			}

			@Override
			public Iterator<T> iterator() {
				return filter(input, condition).iterator();
			}

			@Override
			public Object[] toArray() {
				Object[] result = new Object[size];
				int i = 0;
				for (Object o : filter(input, condition)) {
					result[i++] = o;
				}
				return result;
			}

			@Override
			public <S> S[] toArray(S[] a) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean add(T e) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean remove(Object o) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean containsAll(Collection<?> c) {
				for (Object o : c) {
					if (contains(o))
						return false;
				}
				return true;
			}

			@Override
			public boolean addAll(Collection<? extends T> c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void clear() {
				throw new UnsupportedOperationException();
			}

		};

	}
}
