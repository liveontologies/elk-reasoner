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
import java.util.Map;

import org.liveontologies.puli.statistics.Stat;

import com.google.common.base.Predicate;

/**
 * An {@link RecencyEvictor} that balances its capacity so that some proportion
 * of repeatedly added elements is not evicted. This proportion is called
 * <em>balance</em>.
 * 
 * <h3>Implementation idea</h3>
 * 
 * A query age of an element is the number of times some other element was added
 * since this element was added last time. This is an overestimation of the
 * position of this element in the recency queue if it had infinite capacity.
 * Consider a sequence of numbers that contains one number for each time add was
 * called on an element that was added before. This number would be its position
 * in infinite recency queue. Median of this sequence is the capacity with which
 * half of the elements would be not evicted. Analogously, x-percentile of this
 * sequence is the capacity at which x% of the elements would not be evicted.
 * 
 * TODO: The records about query ages grow indefinitely, which is a memory leak.
 * 
 * @author Peter Skocovsky
 *
 * @param <E>
 *            The type of the elements.
 */
public class CapacityBalancingEvictor<E> extends RecencyEvictor<E> {

	private final Map<E, Integer> lastQueryTicks_ = new ArrayHashMap<E, Integer>();

	private final QuantileEstimator quantileEstimator_;
	private final int balanceAfterNRepeatedQueries_;

	private int tick_ = Integer.MIN_VALUE;
	private int nRepeatedQueriesToBalance_;

	CapacityBalancingEvictor(final double balance,
			final int balanceAfterNRepeatedQueries, final double loadFactor,
			final int initialCapacity) {
		super(initialCapacity, loadFactor);
		this.quantileEstimator_ = new QuantileEstimator(balance,
				initialCapacity);
		this.balanceAfterNRepeatedQueries_ = balanceAfterNRepeatedQueries;
		this.nRepeatedQueriesToBalance_ = balanceAfterNRepeatedQueries;

		this.stats = new Stats();
	}

	@Override
	protected Iterator<E> protectedAddAndEvict(final E element,
			final Predicate<E> retain) {
		final Integer lastQueryTick = lastQueryTicks_.get(element);
		if (lastQueryTick != null) {
			nRepeatedQueriesToBalance_--;
			final int queryAge = tick_ - lastQueryTick;
			final double estimation = quantileEstimator_.next(queryAge);
			if (nRepeatedQueriesToBalance_ == 0) {
				nRepeatedQueriesToBalance_ = balanceAfterNRepeatedQueries_;
				// Balance capacity
				setCapacity((int) Math.min(Math.max(0.0, estimation),
						Integer.MAX_VALUE));
			}
		}
		lastQueryTicks_.put(element, tick_);
		tick_++;
		// Add and evict
		return super.protectedAddAndEvict(element, retain);
	}

	private static class QuantileEstimator {

		private final double q_;

		private boolean first_ = true;
		private double estimation_ = 0.0;
		private double absoluteDeviationSum_ = 0.0;
		private long count_ = 0;

		public QuantileEstimator(final double q) {
			this.q_ = q;
		}

		public QuantileEstimator(final double q, final double initialValue) {
			this(q);
			next(initialValue);
		}

		public double next(final double x) {
			count_++;
			if (first_) {
				estimation_ = x;
				first_ = false;
			} else {
				final double eta = 1.5 * absoluteDeviationSum_
						/ (count_ * count_);
				estimation_ += eta
						* (Math.signum(x - estimation_) + 2 * q_ - 1);
			}
			absoluteDeviationSum_ += Math.abs(x - estimation_);
			return estimation_;
		}

	}

	@Stat(name = "nDifferentQueries")
	public int getNumberOfDifferentQueries() {
		return lastQueryTicks_.size();
	}

	protected static abstract class ProtectedBuilder<B extends ProtectedBuilder<B>>
			extends RecencyEvictor.ProtectedBuilder<B> {

		public static final double DEFAULT_BALANCE = 0.8;
		public static final int DEFAULT_BALANCE_AFTER_N_REPEATED_QUERIES = 100;

		private double balance_ = DEFAULT_BALANCE;
		private int balanceAfterNRepeatedQueries_ = DEFAULT_BALANCE_AFTER_N_REPEATED_QUERIES;

		/**
		 * Capacity should be set so that this proportion of repeatedly added
		 * elements that should be not evicted. Must be between 0 and 1
		 * inclusive.
		 * <p>
		 * If not supplied, defaults to {@link #DEFAULT_BALANCE}.
		 * 
		 * @param balance
		 * @return This builder.
		 * @throws IllegalArgumentException
		 *             When the argument is not between 0 and 1 inclusive.
		 */
		public B balance(final double balance) {
			if (0 > balance || balance > 1) {
				throw new IllegalArgumentException(
						"Balance must be between 0 and 1 inclusive!");
			}
			this.balance_ = balance;
			return convertThis();
		}

		/**
		 * The capacity is balanced in the intervals of the provided number of
		 * additions. Must be positive.
		 * <p>
		 * If not supplied, defaults to
		 * {@link #DEFAULT_BALANCE_AFTER_N_REPEATED_QUERIES}.
		 * 
		 * @param balanceAfterNRepeatedQueries
		 * @return This builder.
		 * @throws IllegalArgumentException
		 *             If the value is not positive.
		 */
		public B balanceAfterNRepeatedQueries(
				final int balanceAfterNRepeatedQueries) {
			if (1 > balanceAfterNRepeatedQueries) {
				throw new IllegalArgumentException(
						"Capacity can be balanced only after positive number of repeated queries!");
			}
			this.balanceAfterNRepeatedQueries_ = balanceAfterNRepeatedQueries;
			return convertThis();
		}

		public <E> Evictor<E> build() {
			return new CapacityBalancingEvictor<E>(balance_,
					balanceAfterNRepeatedQueries_, loadFactor_, capacity_);
		}

		@Override
		protected abstract B convertThis();

	}

	public static class Builder extends ProtectedBuilder<Builder> {

		@Override
		protected Builder convertThis() {
			return this;
		}

	}

	// Stats.
	protected class Stats extends RecencyEvictor<E>.Stats {

		@Stat
		public int nDifferentQueries() {
			return lastQueryTicks_.size();
		}

	}

}
