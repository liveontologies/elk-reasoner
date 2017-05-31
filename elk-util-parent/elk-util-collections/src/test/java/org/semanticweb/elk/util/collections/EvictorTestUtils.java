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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Assert;

import com.google.common.base.Predicate;

class EvictorTestUtils {

	public static final double RETAIN_FOUR_FIFTHS_LOAD_FACTOR = 0.8;
	public static final double EVICT_EVERYTHING_LOAD_FACTOR = 0.0;
	public static final double RETAIN_FULL_CAPACITY_LOAD_FACTOR = 1;

	private EvictorTestUtils() {
		// Forbid instantiation of an utility class.
	}

	public static <E> void checkEvicted(final Collection<E> expected,
			final Iterator<E> actual) {

		final Set<E> expectedSet = new LinkedHashSet<E>(expected);

		final Set<E> actualSet = new LinkedHashSet<E>();
		while (actual.hasNext()) {
			actualSet.add(actual.next());
		}

		final Set<E> inExpectedNotInActual = new LinkedHashSet<E>(expectedSet);
		inExpectedNotInActual.removeAll(actualSet);

		final Set<E> inActualNotInExpected = new LinkedHashSet<E>(actualSet);
		inActualNotInExpected.removeAll(expectedSet);

		final String messageSuffix = "\nIn expected, not in actual: "
				+ inExpectedNotInActual + "\nIn actual, not in expected: "
				+ inActualNotInExpected + "\n";

		Assert.assertEquals(messageSuffix, expectedSet, actualSet);

	}

	public static <E> void checkNothingEvicted(final Iterator<E> evicted) {
		checkEvicted(Collections.<E> emptyList(), evicted);
	}

	public static interface TestEvictorFactory<E> {
		Evictor<E> newEvictor(int capacity, double loadFactor);
	}

	public static void testRecencyEviction(final TestEvictorFactory<Integer> factory) {

		final Evictor<Integer> evictor = factory.newEvictor(10,
				RETAIN_FOUR_FIFTHS_LOAD_FACTOR);

		Iterator<Integer> evicted;

		// While capacity is not exceeded, nothing is evicted.
		for (int element = 0; element < 10; element++) {
			evicted = evictor.addAndEvict(element);
			checkNothingEvicted(evicted);
		}

		// When capacity exceeded, the elements that are not the 8 most recent
		// ones are evicted.
		evicted = evictor.addAndEvict(10);
		checkEvicted(Arrays.asList(0, 1, 2), evicted);

		// Fill up the capacity again.
		evicted = evictor.addAndEvict(11);
		checkNothingEvicted(evicted);
		evicted = evictor.addAndEvict(12);
		checkNothingEvicted(evicted);

		// Re-adding elements does not exceed the capacity.
		evicted = evictor.addAndEvict(3);
		checkNothingEvicted(evicted);
		evicted = evictor.addAndEvict(4);
		checkNothingEvicted(evicted);
		evicted = evictor.addAndEvict(5);
		checkNothingEvicted(evicted);

		// Adding what was evicted exceeds the capacity.
		evicted = evictor.addAndEvict(0);
		checkEvicted(Arrays.asList(6, 7, 8), evicted);

	}

	public static void testRecencyRetainment(
			final TestEvictorFactory<Integer> factory) {

		final Evictor<Integer> evictor = factory.newEvictor(10,
				EVICT_EVERYTHING_LOAD_FACTOR);

		Iterator<Integer> evicted;

		// Fill up the capacity.
		for (int element = 0; element < 10; element++) {
			evicted = evictor.addAndEvict(element);
			checkNothingEvicted(evicted);
		}

		// Retaining even elements evicts the odd ones.
		evicted = evictor.addAndEvict(10, new Predicate<Integer>() {
			@Override
			public boolean apply(final Integer element) {
				return element % 2 == 0;
			}
		});
		checkEvicted(Arrays.asList(1, 3, 5, 7, 9), evicted);

		// Fill up the capacity again.
		for (int element = 11; element < 15; element++) {
			evicted = evictor.addAndEvict(element);
			checkNothingEvicted(evicted);
		}

		// Evict everything.
		evicted = evictor.addAndEvict(15);
		checkEvicted(Arrays.asList(0, 2, 4, 6, 8, 10, 11, 12, 13, 14, 15),
				evicted);

	}

}
