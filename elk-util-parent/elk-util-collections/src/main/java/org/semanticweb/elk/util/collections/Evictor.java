/*-
 * #%L
 * ELK Utilities Collections
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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

import java.util.Iterator;

import org.semanticweb.elk.util.statistics.HasStats;

import com.google.common.base.Predicate;

/**
 * Keeps track of added elements and informs of which of these elements were
 * evicted.
 * 
 * @author Peter Skocovsky
 *
 * @param <E>
 *            The type of the elements.
 */
public interface Evictor<E> extends HasStats {

	/**
	 * Add the provided element.
	 * 
	 * @param element
	 *            The added element.
	 */
	void add(E element);

	/**
	 * Return elements that are evicted.
	 * 
	 * @return The evicted elements.
	 */
	Iterator<E> evict();

	/**
	 * Return elements that are evicted. Does not evict the elements for which
	 * {@code retain} returns {@code true}.
	 * 
	 * @param retain
	 *            A {@link Predicate} that is {@code true} for elements that
	 *            should be retained.
	 * @return The evicted elements.
	 */
	Iterator<E> evict(Predicate<E> retain);

	/**
	 * Add the provided element and return elements that were evicted.
	 * 
	 * @param element
	 *            The added element.
	 * @return The evicted elements.
	 */
	Iterator<E> addAndEvict(E element);

	/**
	 * Add the provided element and return elements that were evicted. Does not
	 * evict the elements for which {@code retain} returns {@code true}.
	 * 
	 * @param element
	 *            The added element.
	 * @param retain
	 *            A {@link Predicate} that is {@code true} for elements that
	 *            should be retained.
	 * @return The evicted elements.
	 */
	Iterator<E> addAndEvict(E element, Predicate<E> retain);

	public static interface Builder {
		<E> Evictor<E> build();
	}

}
