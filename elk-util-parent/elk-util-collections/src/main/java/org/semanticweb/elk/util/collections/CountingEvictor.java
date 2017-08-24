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
import com.google.common.collect.Iterators;

/**
 * Acts as {@link RecencyEvictor} for elements that have been added more times
 * than the specified number. Before an element have been added the specified
 * number of times, this evictor acts as {@link RecencyEvictor} with capacity 0.
 * So elements that should be retained when they are added are never evicted
 * immediately.
 * 
 * TODO: The records about add counts grow indefinitely, which is a memory leak.
 * 
 * @author Peter Skocovsky
 *
 * @param <E>
 *            The type of the elements.
 */
public class CountingEvictor<E> extends RecencyEvictor<E> {

	private final Map<E, ElementRecord> elementRecords_ = new ArrayHashMap<E, ElementRecord>();
	private final RecencyEvictor<E> immediatelyEvicted_ = new RecencyEvictor<E>(
			0, 0.0);

	private final int evictBeforeAddCount_;

	CountingEvictor(final int capacity, final int evictBeforeAddCount,
			final double loadFactor) {
		super(capacity, loadFactor);
		this.evictBeforeAddCount_ = evictBeforeAddCount;

		this.stats = new Stats();
	}

	@Override
	public void add(final E element) {
		ElementRecord record = elementRecords_.get(element);
		if (record == null) {
			record = new ElementRecord();
			elementRecords_.put(element, record);
		}
		record.addCount++;
		if (record.addCount >= evictBeforeAddCount_) {
			super.add(element);
		} else {
			// Immediately evict, unless it should be retained.
			immediatelyEvicted_.add(element);
		}
	}

	@Override
	public Iterator<E> evict(final Predicate<E> retain) {
		return Iterators.concat(super.evict(retain),
				immediatelyEvicted_.evict(retain));
	}

	private static class ElementRecord {
		public int addCount = 0;
	}

	protected static abstract class ProtectedBuilder<B extends ProtectedBuilder<B>>
			extends RecencyEvictor.ProtectedBuilder<B> {

		public static final int DEFAULT_EVICT_BEFORE_ADD_COUNT = 3;

		private int evictBeforeAddCount_ = DEFAULT_EVICT_BEFORE_ADD_COUNT;

		/**
		 * The number of times an element must be added so that it is not
		 * evicted immediately. This number must not be negative!
		 * <p>
		 * If not provided, defaults to {@link #DEFAULT_EVICT_BEFORE_ADD_COUNT}.
		 * 
		 * @param evictBeforeAddCount
		 * @return This builder.
		 * @throws IllegalArgumentException
		 *             When the argument is negative.
		 */
		public B evictBeforeAddCount(final int evictBeforeAddCount) {
			if (0 > evictBeforeAddCount) {
				throw new IllegalArgumentException(
						"Cannot add an element negative number of times!");
			}
			this.evictBeforeAddCount_ = evictBeforeAddCount;
			return convertThis();
		}

		public <E> Evictor<E> build() {
			return new CountingEvictor<E>(capacity_, evictBeforeAddCount_,
					loadFactor_);
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
			return elementRecords_.size();
		}

	}

}
