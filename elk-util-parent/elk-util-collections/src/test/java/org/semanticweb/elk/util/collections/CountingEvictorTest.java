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

import static org.semanticweb.elk.util.collections.EvictorTestUtils.checkEvicted;
import static org.semanticweb.elk.util.collections.EvictorTestUtils.checkNothingEvicted;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;

import com.google.common.base.Predicate;

public class CountingEvictorTest {

	public static final int EVICT_BEFORE_FIRST_ADD = 1;

	@Test
	public void testSimpleEviction() {

		EvictorTestUtils.testRecencyEviction(
				new EvictorTestUtils.TestEvictorFactory<Integer>() {
					@Override
					public Evictor<Integer> newEvictor(final int capacity,
							final double loadFactor) {
						final CountingEvictor.Builder b = new CountingEvictor.Builder();
						return b.capacity(capacity).loadFactor(loadFactor)
								.evictBeforeAddCount(EVICT_BEFORE_FIRST_ADD)
								.build();
					}
				});

	}

	@Test
	public void testSimpleRetainment() {

		EvictorTestUtils.testRecencyRetainment(
				new EvictorTestUtils.TestEvictorFactory<Integer>() {
					@Override
					public Evictor<Integer> newEvictor(final int capacity,
							final double loadFactor) {
						final CountingEvictor.Builder b = new CountingEvictor.Builder();
						return b.capacity(capacity).loadFactor(loadFactor)
								.evictBeforeAddCount(EVICT_BEFORE_FIRST_ADD)
								.build();
					}
				});

	}

	@Test
	public void testEvictionBefore3Adds() {

		final CountingEvictor.Builder b = new CountingEvictor.Builder();
		final Evictor<Integer> evictor = b.capacity(10)
				.loadFactor(EvictorTestUtils.RETAIN_FULL_CAPACITY_LOAD_FACTOR)
				.evictBeforeAddCount(3).build();

		Iterator<Integer> evicted;

		// What is added once is immediately evicted.
		for (int element = 0; element < 10; element++) {
			evicted = evictor.addAndEvict(element);
			checkEvicted(Arrays.asList(element), evicted);
		}

		// What is added second time is immediately evicted.
		for (int element = 0; element < 10; element++) {
			evicted = evictor.addAndEvict(element);
			checkEvicted(Arrays.asList(element), evicted);
		}

		// What is added third time is not evicted.
		for (int element = 0; element < 10; element++) {
			evicted = evictor.addAndEvict(element);
			checkNothingEvicted(evicted);
		}

		// When something new is added, it is evicted, ...
		evicted = evictor.addAndEvict(10);
		checkEvicted(Arrays.asList(10), evicted);
		evicted = evictor.addAndEvict(10);
		checkEvicted(Arrays.asList(10), evicted);
		// ... but on the third time old elements are evicted.
		evicted = evictor.addAndEvict(10);
		checkEvicted(Arrays.asList(0), evicted);

		// Re-adding 3-times added elements evicts nothing.
		evicted = evictor.addAndEvict(1);
		checkNothingEvicted(evicted);
		evicted = evictor.addAndEvict(2);
		checkNothingEvicted(evicted);
		evicted = evictor.addAndEvict(3);
		checkNothingEvicted(evicted);

		// Adding what was 3-times added and evicted evicts elements.
		evicted = evictor.addAndEvict(0);
		checkEvicted(Arrays.asList(4), evicted);

	}

	@Test
	public void testRetainmentWithEvictionBefore3Adds() {

		final CountingEvictor.Builder b = new CountingEvictor.Builder();
		final Evictor<Integer> evictor = b.capacity(10)
				.loadFactor(EvictorTestUtils.RETAIN_FULL_CAPACITY_LOAD_FACTOR)
				.evictBeforeAddCount(3).build();

		Iterator<Integer> evicted;

		// What is added once and should be retained is not evicted.
		for (int element = 0; element < 10; element++) {
			final int addedElement = element;
			evicted = evictor.addAndEvict(element, new Predicate<Integer>() {
				@Override
				public boolean apply(final Integer e) {
					return e <= addedElement;
				}
			});
			checkNothingEvicted(evicted);
		}

		// Everything is evicted the following add.
		evicted = evictor.addAndEvict(0);
		checkEvicted(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), evicted);

		// What is added second time is immediately evicted.
		for (int element = 1; element < 10; element++) {
			evicted = evictor.addAndEvict(element);
			checkEvicted(Arrays.asList(element), evicted);
		}

		// What is added third time is retained.
		for (int element = 0; element < 10; element++) {
			evicted = evictor.addAndEvict(element);
			checkNothingEvicted(evicted);
		}

		// When something new is added 3-times and everything should be
		// retained, nothing is evicted.
		evicted = evictor.addAndEvict(10);
		evicted = evictor.addAndEvict(10);
		evicted = evictor.addAndEvict(10, new Predicate<Integer>() {
			@Override
			public boolean apply(final Integer e) {
				return e <= 10;
			}
		});
		checkNothingEvicted(evicted);

		// Adding another thing 3-times without retainment, evicts elements.
		evicted = evictor.addAndEvict(11);
		evicted = evictor.addAndEvict(11);
		evicted = evictor.addAndEvict(11);
		checkEvicted(Arrays.asList(0, 1), evicted);

	}

}
