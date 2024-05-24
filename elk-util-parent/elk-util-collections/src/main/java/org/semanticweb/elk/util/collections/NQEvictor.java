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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.semanticweb.elk.util.statistics.Stat;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

/**
 * Acts as a sequence of {@link RecencyEvictor} (called levels) where if element
 * being added is in some of them, it is removed and added to the next level, or
 * if there is no next level, it is readded to the last one. Elements that are
 * in none of the levels are added to the first one. If there is only one level,
 * this evictor works as {@link RecencyEvictor}.
 * <p>
 * This is a straight-forward generalization of Segmented LRU cache
 * &lt;https://en.wikipedia.org/wiki/Cache_replacement_policies#Segmented_LRU_.28SLRU.29&gt;
 * 
 * @author Peter Skocovsky
 *
 * @param <E>
 *            The type of the elements.
 */
public class NQEvictor<E> extends AbstractEvictor<E> {

	private static final int DEFAULT_ELEMENTS_CAPACITY_ = Builder.DEFAULT_CAPACITY;
	private static final float DEFAULT_ELEMENTS_LOAD_FACTOR_ = 0.75f;

	private final List<LinkedHashMap<E, Boolean>> elements_;
	private final List<Integer> capacities_;
	private final List<Double> loadFactors_;

	NQEvictor(final List<Integer> capacities, final List<Double> loadFactors) {
		this.elements_ = new ArrayList<LinkedHashMap<E, Boolean>>(
				capacities.size());
		for (int i = 0; i < capacities.size(); i++) {
			this.elements_.add(
					new LinkedHashMap<E, Boolean>(DEFAULT_ELEMENTS_CAPACITY_,
							DEFAULT_ELEMENTS_LOAD_FACTOR_, true));
		}
		this.capacities_ = capacities;
		this.loadFactors_ = loadFactors;

		this.stats = new Stats();
	}

	@Override
	public void add(final E element) {
		// If element is in some queue but the last one, upgrade it.
		for (int i = 0; i < elements_.size() - 1; i++) {
			final LinkedHashMap<E, Boolean> iThQueue = elements_.get(i);
			if (iThQueue.remove(element) != null) {
				elements_.get(i + 1).put(element, true);
				return;
			}
		}
		// else if element is in the last queue, reinsert it.
		final LinkedHashMap<E, Boolean> lastQueue = elements_
				.get(elements_.size() - 1);
		if (lastQueue.containsKey(element)) {
			lastQueue.put(element, true);
			return;
		}
		// else put it on the first queue.
		elements_.get(0).put(element, true);
	}

	@Override
	public Iterator<E> evict(final Predicate<E> retain) {
		Preconditions.checkNotNull(retain);

		/*
		 * Elements evicted from some level are added to the lower level. Only
		 * elements evicted from the first level are actually completely
		 * evicted.
		 */

		for (int i = capacities_.size() - 1; i > 0; i--) {
			final LinkedHashMap<E, Boolean> level = elements_.get(i);
			final LinkedHashMap<E, Boolean> lowerLevel = elements_.get(i - 1);
			final int capacity = capacities_.get(i);
			final double loadFactor = loadFactors_.get(i);

			if (level.size() <= capacity) {
				// Evict nothing.
				continue;
			}
			// else

			final int goalCapacity = (int) (capacity * loadFactor);
			final Iterator<E> iterator = level.keySet().iterator();
			while (iterator.hasNext() && level.size() > goalCapacity) {
				final E element = iterator.next();
				lowerLevel.put(element, true);
				iterator.remove();
			}

		}

		final List<E> evicted = new ArrayList<E>();

		final LinkedHashMap<E, Boolean> firstLevel = elements_.get(0);
		final int capacity = capacities_.get(0);
		final double loadFactor = loadFactors_.get(0);

		if (firstLevel.size() <= capacity) {
			// Evict nothing.
			return evicted.iterator();
		}
		// else

		final int goalCapacity = (int) (capacity * loadFactor);
		final Iterator<E> iterator = firstLevel.keySet().iterator();
		while (iterator.hasNext() && firstLevel.size() > goalCapacity) {
			final E element = iterator.next();
			if (!retain.apply(element)) {
				evicted.add(element);
				iterator.remove();
			}
		}

		return evicted.iterator();
	}

	public int getN() {
		return capacities_.size();
	}

	public int getCapacity(final int index) {
		return capacities_.get(index);
	}

	public int setCapacity(final int index, final int capacity) {
		if (0 > capacity) {
			throw new IllegalArgumentException("Capacity cannot be negative!");
		}
		return capacities_.set(index, capacity);
	}

	protected static abstract class ProtectedBuilder<B extends ProtectedBuilder<B>> {

		public static final int DEFAULT_CAPACITY = 128;
		public static final double DEFAULT_LOAD_FACTOR = 0.75;

		protected final List<Integer> capacities = new ArrayList<Integer>();
		protected final List<Double> loadFactors = new ArrayList<Double>();

		/**
		 * Adds a level with the specified capacity and load factor. The
		 * arguments have the same meaning as if the level was a
		 * {@link RecencyEvictor}.
		 * 
		 * @param capacity
		 *            the capacity of the added level
		 * @param loadFactor
		 *            the load factor of the added level
		 * @return This builder.
		 * @throws IllegalArgumentException
		 *             When capacity is negative or load factor is not between 0
		 *             and 1 inclusive.
		 */
		public B addLevel(final int capacity, final double loadFactor)
				throws IllegalArgumentException {
			if (0 > capacity) {
				throw new IllegalArgumentException(
						"Capacity cannot be negative!");
			}
			if (0 > loadFactor || loadFactor > 1) {
				throw new IllegalArgumentException(
						"Load factor must be between 0 and 1 inclusive!");
			}
			capacities.add(capacity);
			loadFactors.add(loadFactor);
			return convertThis();
		}

		/**
		 * Adds a level with the specified capacity and the default load factor
		 * {@link #DEFAULT_LOAD_FACTOR}. The arguments have the same meaning as
		 * if the level was a {@link RecencyEvictor}.
		 * 
		 * @param capacity
		 *            the capacity of the added level
		 * @return This builder.
		 * @throws IllegalArgumentException
		 *             When capacity is negative.
		 */
		public B addLevel(final int capacity) throws IllegalArgumentException {
			return addLevel(capacity, DEFAULT_LOAD_FACTOR);
		}

		/**
		 * Adds a level with the default capacity {@link #DEFAULT_CAPACITY} and
		 * the specified load factor. The arguments have the same meaning as if
		 * the level was a {@link RecencyEvictor}.
		 * 
		 * @param loadFactor
		 *            the load factor of the added level
		 * @return This builder.
		 * @throws IllegalArgumentException
		 *             When load factor is not between 0 and 1 inclusive.
		 */
		public B addLevel(final double loadFactor)
				throws IllegalArgumentException {
			return addLevel(DEFAULT_CAPACITY, loadFactor);
		}

		/**
		 * Adds a level with the default capacity {@link #DEFAULT_CAPACITY} and
		 * load factor {@link #DEFAULT_LOAD_FACTOR}. The arguments have the same
		 * meaning as if the level was a {@link RecencyEvictor}.
		 * 
		 * @return This builder.
		 */
		public B addLevel() {
			return addLevel(DEFAULT_CAPACITY);
		}

		/**
		 * Builds the {@link NQEvictor} with the added levels. If none were
		 * added, builds {@link NQEvictor} with one level with default capacity
		 * and load factor.
		 * 
		 * @param <E>
		 *            The type of the elements maintained by the evictor.
		 * 
		 * @return The built {@link NQEvictor}
		 */
		public <E> NQEvictor<E> build() {
			if (capacities.isEmpty()) {
				addLevel();
			}
			return new NQEvictor<E>(capacities, loadFactors);
		}

		protected abstract B convertThis();

	}

	public static class Builder extends ProtectedBuilder<Builder>
			implements Evictor.Builder {

		@Override
		protected Builder convertThis() {
			return this;
		}

		public static Builder valueOf(final String value) {
			final String[] args = Evictors.parseArgs(value, NQEvictor.class,
					null);
			if (args.length % 2 != 0) {
				throw new IllegalArgumentException(
						"The number of arguments must be even! value: "
								+ value);
			}
			final Builder builder = new Builder();
			for (int i = 0; i < args.length; i += 2) {
				final String capacityArg = args[i].trim();
				final String loadFactorArg = args[i + 1].trim();
				final int capacity = capacityArg.isEmpty() ? DEFAULT_CAPACITY
						: Integer.valueOf(capacityArg);
				final double loadFactor = loadFactorArg.isEmpty()
						? DEFAULT_LOAD_FACTOR : Double.valueOf(loadFactorArg);
				builder.addLevel(capacity < 0 ? Integer.MAX_VALUE : capacity,
						loadFactor);
			}
			return builder;
		}

		@Override
		public String toString() {
			final StringBuilder result = new StringBuilder(
					NQEvictor.class.getName()).append("(");
			for (int i = 0; i < capacities.size(); i++) {
				if (i > 0) {
					result.append(",");
				}
				result.append(capacities.get(i)).append(",")
						.append(loadFactors.get(i));
			}
			return result.append(")").toString();
		}

	}

	public static Builder builder() {
		return new Builder();
	}

	// Stats.
	protected class Stats {

		@Stat
		public String levelSizes() {
			final StringBuilder result = new StringBuilder(
					"" + elements_.get(0).size());
			for (int i = 1; i < elements_.size(); i++) {
				result.append("_").append(elements_.get(i).size());
			}
			return result.toString();
		}

		// Empty so far.
	}

}
