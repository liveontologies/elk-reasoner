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

public class NQEvictorTest {

	@Test
	public void testSingleLevelEviction() {

		EvictorTestUtils.testRecencyEviction(
				new EvictorTestUtils.TestEvictorFactory<Integer>() {
					@Override
					public Evictor<Integer> newEvictor(final int capacity,
							final double loadFactor) {
						final NQEvictor.Builder b = new NQEvictor.Builder();
						return b.addLevel(capacity, loadFactor).build();
					}
				});

	}

	@Test
	public void testSingleLevelRetainment() {

		EvictorTestUtils.testRecencyRetainment(
				new EvictorTestUtils.TestEvictorFactory<Integer>() {
					@Override
					public Evictor<Integer> newEvictor(final int capacity,
							final double loadFactor) {
						final NQEvictor.Builder b = new NQEvictor.Builder();
						return b.addLevel(capacity, loadFactor).build();
					}
				});

	}

	@Test
	public void testTwoLevelEviction() {

		final NQEvictor.Builder b = new NQEvictor.Builder();
		final Evictor<Integer> evictor = b
				.addLevel(10, EvictorTestUtils.RETAIN_FULL_CAPACITY_LOAD_FACTOR)
				.addLevel(5, EvictorTestUtils.RETAIN_FULL_CAPACITY_LOAD_FACTOR)
				.build();

		Iterator<Integer> evicted;

		// While capacity is not exceeded, nothing is evicted.
		for (int element = 0; element < 10; element++) {
			evicted = evictor.addAndEvict(element);
			checkNothingEvicted(evicted);
		}

		// When capacity exceeded, the least recent element is evicted.
		evicted = evictor.addAndEvict(10);
		checkEvicted(Arrays.asList(0), evicted);

		// Re-adding elements moves them to higher level.
		evicted = evictor.addAndEvict(1);
		checkNothingEvicted(evicted);
		evicted = evictor.addAndEvict(2);
		checkNothingEvicted(evicted);
		evicted = evictor.addAndEvict(3);
		checkNothingEvicted(evicted);

		// So they will not be evicted even when the capacity is exceeded.
		evicted = evictor.addAndEvict(11);
		checkNothingEvicted(evicted);
		evicted = evictor.addAndEvict(12);
		checkNothingEvicted(evicted);
		evicted = evictor.addAndEvict(13);
		checkNothingEvicted(evicted);
		for (int element = 14; element < 20; element++) {
			evicted = evictor.addAndEvict(element);
			checkEvicted(Arrays.asList(element - 10), evicted);
		}

		// Exceeding second level capacity evicts elements.
		evicted = evictor.addAndEvict(10);
		checkNothingEvicted(evicted);
		evicted = evictor.addAndEvict(11);
		checkNothingEvicted(evicted);
		evicted = evictor.addAndEvict(12);
		checkEvicted(Arrays.asList(1), evicted);

		// Re-adding elements in the top level evicts nothing ...
		evicted = evictor.addAndEvict(2);
		checkNothingEvicted(evicted);
		evicted = evictor.addAndEvict(3);
		checkNothingEvicted(evicted);

		// ... and makes them most recent.
		evicted = evictor.addAndEvict(13);
		checkEvicted(Arrays.asList(10), evicted);
		evicted = evictor.addAndEvict(14);
		checkEvicted(Arrays.asList(11), evicted);

	}

	@Test
	public void testThirdLevelEviction() {

		final NQEvictor.Builder b = new NQEvictor.Builder();
		final Evictor<Integer> evictor = b
				.addLevel(1, EvictorTestUtils.RETAIN_FULL_CAPACITY_LOAD_FACTOR)
				.addLevel(1, EvictorTestUtils.RETAIN_FULL_CAPACITY_LOAD_FACTOR)
				.addLevel(10, EvictorTestUtils.RETAIN_FOUR_FIFTHS_LOAD_FACTOR)
				.build();

		Iterator<Integer> evicted;

		// While capacity is not exceeded, nothing is evicted.
		for (int element = 0; element < 10; element++) {
			evicted = add3Times(evictor, element);
			checkNothingEvicted(evicted);
		}

		// When capacity exceeded, the elements that are not the 8 most recent
		// ones are evicted.
		evicted = add3Times(evictor, 10);
		checkEvicted(Arrays.asList(0, 1, 2), evicted);

		// Fill up the capacity again.
		evicted = add3Times(evictor, 11);
		checkNothingEvicted(evicted);
		evicted = add3Times(evictor, 12);
		checkNothingEvicted(evicted);

		// Re-adding elements does not exceed the capacity.
		evicted = add3Times(evictor, 3);
		checkNothingEvicted(evicted);
		evicted = add3Times(evictor, 4);
		checkNothingEvicted(evicted);
		evicted = add3Times(evictor, 5);
		checkNothingEvicted(evicted);

		// Adding what was evicted exceeds the capacity.
		evicted = add3Times(evictor, 0);
		checkEvicted(Arrays.asList(6, 7, 8), evicted);

	}

	private static Iterator<Integer> add3Times(final Evictor<Integer> evictor,
			final int element) {
		Iterator<Integer> evicted = null;
		for (int i = 0; i < 3; i++) {
			evicted = evictor.addAndEvict(element);
		}
		return evicted;
	}

}
