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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.liveontologies.puli.statistics.Stat;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

/**
 * Evicts least recent elements after the <em>capacity</em> is exceeded.
 * Eviction is trying to reduce the number of elements in this evictor to
 * capacity times <em>load factor</em>. However, it still does not evict the
 * elements that should be retained.
 * 
 * @author Peter Skocovsky
 *
 * @param <E>
 *            The type of the elements.
 */
public class RecencyEvictor<E> extends AbstractEvictor<E> {

	private static final int DEFAULT_ELEMENTS_CAPACITY_ = Builder.DEFAULT_CAPACITY;
	private static final float DEFAULT_ELEMENTS_LOAD_FACTOR_ = 0.75f;

	private final LinkedHashMap<E, Boolean> elements_ = new LinkedHashMap<E, Boolean>(
			DEFAULT_ELEMENTS_CAPACITY_, DEFAULT_ELEMENTS_LOAD_FACTOR_, true);
	private final double loadFactor_;

	private int capacity_;

	RecencyEvictor(final int capacity, final double loadFactor) {
		this.capacity_ = capacity;
		this.loadFactor_ = loadFactor;

		this.stats = new Stats();
	}

	@Override
	public Iterator<E> addAndEvict(final E element, final Predicate<E> retain) {
		Preconditions.checkNotNull(retain);
		// else
		return protectedAddAndEvict(element, retain);
	}

	protected Iterator<E> protectedAddAndEvict(final E element,
			final Predicate<E> retain) {
		elements_.put(element, true);
		return evict(retain);
	}

	private Iterator<E> evict(final Predicate<E> retain) {

		if (elements_.size() <= capacity_) {
			// Evict nothing.
			return Collections.<E> emptyList().iterator();
		}
		// else

		final int goalCapacity = (int) (capacity_ * loadFactor_);
		final List<E> evicted = new ArrayList<E>(goalCapacity < elements_.size()
				? elements_.size() - goalCapacity : 0);
		final Iterator<E> iterator = elements_.keySet().iterator();
		while (iterator.hasNext() && elements_.size() > goalCapacity) {
			final E element = iterator.next();
			if (!retain.apply(element)) {
				evicted.add(element);
				iterator.remove();
			}
		}

		return evicted.iterator();
	}

	public int getCapacity() {
		return capacity_;
	}

	public void setCapacity(final int capacity) {
		if (0 > capacity) {
			throw new IllegalArgumentException("Capacity cannot be negative!");
		}
		this.capacity_ = capacity;
	}

	protected static abstract class ProtectedBuilder<B extends ProtectedBuilder<B>> {

		public static final int DEFAULT_CAPACITY = 128;
		public static final double DEFAULT_LOAD_FACTOR = 0.75;

		protected int capacity_ = DEFAULT_CAPACITY;
		protected double loadFactor_ = DEFAULT_LOAD_FACTOR;

		/**
		 * When the provided capacity is exceeded, elements will be evicted.
		 * Capacity not must be negative!
		 * <p>
		 * If not called, capacity defaults to {@link #DEFAULT_CAPACITY}.
		 * 
		 * @param capacity
		 *            The capacity of the evictor.
		 * @return This builder.
		 * @throws IllegalArgumentException
		 *             When the argument is negative.
		 */
		public B capacity(final int capacity) {
			if (0 > capacity) {
				throw new IllegalArgumentException(
						"Capacity cannot be negative!");
			}
			this.capacity_ = capacity;
			return convertThis();
		}

		/**
		 * Load factor is the proportion of the capacity that should be achieved
		 * when evicting. Eviction is trying to reduce the number of elements in
		 * this evictor to capacity times load factor. Load factor must be
		 * between 0 and 1 inclusive!
		 * <p>
		 * If not called, load factor defaults to {@link #DEFAULT_LOAD_FACTOR}.
		 * 
		 * @param loadFactor
		 *            The load factor of this evictor.
		 * @return This builder.
		 * @throws IllegalArgumentException
		 *             When the argument is not between 0 and 1 inclusive.
		 */
		public B loadFactor(final double loadFactor) {
			if (0 > loadFactor || loadFactor > 1) {
				throw new IllegalArgumentException(
						"Load factor must be between 0 and 1 inclusive!");
			}
			this.loadFactor_ = loadFactor;
			return convertThis();
		}

		public <E> Evictor<E> build() {
			return new RecencyEvictor<E>(capacity_, loadFactor_);
		}

		protected abstract B convertThis();

	}

	public static class Builder extends ProtectedBuilder<Builder> {

		@Override
		protected Builder convertThis() {
			return this;
		}

	}

	// Stats.
	protected class Stats {

		@Stat
		public int capacity() {
			return getCapacity();
		}

	}

}
