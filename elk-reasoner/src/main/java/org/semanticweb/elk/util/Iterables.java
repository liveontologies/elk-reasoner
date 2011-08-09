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
package org.semanticweb.elk.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Iterables {
	
	@SuppressWarnings("unchecked")
	public static <T> Iterable<T> concat(final Iterable<? extends T> a, final Iterable<? extends T> b) {
		return concat(Arrays.asList(a, b));
	}
	
	public static <T> Iterable<T> concat(final Iterable<? extends Iterable<? extends T>> input) {
		assert input != null;
		
		return new Iterable<T> () {

			public Iterator<T> iterator() {

				return new Iterator<T> () {
					Iterator<? extends Iterable <? extends T>> outer = input.iterator();
				
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

	public static <T> Iterable<T> filter(final Iterable<?> input, final Class<T> type) {
		assert input != null;

		return new Iterable<T> () {

			public Iterator<T> iterator() {

				return new Iterator<T> () {
					Iterator<?> i = input.iterator();
					Object next;
					boolean hasNext = advance();

					public boolean hasNext() {
						return hasNext;
					}

					public T next() {
						if (hasNext) {
							@SuppressWarnings("unchecked")
							T result = (T) next;
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
							if (type.isInstance(next))
								return true;
						}
						return false;
					}
				};
			}
		};
	}

}
