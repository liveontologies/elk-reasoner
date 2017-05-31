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

import org.junit.Assert;
import org.junit.Test;

public class CapacityBalancingEvictorTest {

	public static final double GET_FOUR_FIFTHS_OF_HITS_BALANCE = 0.8;
	public static final int NEVER_BALANCE_INTERVAL = Integer.MAX_VALUE;
	public static final int ALWAYS_BALANCE_INTERVAL = 1;

	@Test
	public void testEviction() {

		EvictorTestUtils.testRecencyEviction(
				new EvictorTestUtils.TestEvictorFactory<Integer>() {
					@Override
					public Evictor<Integer> newEvictor(final int capacity,
							final double loadFactor) {
						final CapacityBalancingEvictor.Builder b = new CapacityBalancingEvictor.Builder();
						return b.capacity(capacity)
								.balance(GET_FOUR_FIFTHS_OF_HITS_BALANCE)
								.balanceAfterNRepeatedQueries(
										NEVER_BALANCE_INTERVAL)
								.loadFactor(loadFactor).build();
					}
				});

	}

	@Test
	public void testRetainment() {

		EvictorTestUtils.testRecencyRetainment(
				new EvictorTestUtils.TestEvictorFactory<Integer>() {
					@Override
					public Evictor<Integer> newEvictor(final int capacity,
							final double loadFactor) {
						final CapacityBalancingEvictor.Builder b = new CapacityBalancingEvictor.Builder();
						return b.capacity(capacity)
								.balance(GET_FOUR_FIFTHS_OF_HITS_BALANCE)
								.balanceAfterNRepeatedQueries(
										NEVER_BALANCE_INTERVAL)
								.loadFactor(loadFactor).build();
					}
				});

	}

	@Test
	public void testCapacityDoesNotChange() {

		final CapacityBalancingEvictor.Builder b = new CapacityBalancingEvictor.Builder();
		final Evictor<Integer> evictor = b.capacity(10)
				.balance(GET_FOUR_FIFTHS_OF_HITS_BALANCE)
				.balanceAfterNRepeatedQueries(ALWAYS_BALANCE_INTERVAL)
				.loadFactor(EvictorTestUtils.RETAIN_FULL_CAPACITY_LOAD_FACTOR)
				.build();

		Iterator<Integer> evicted;

		// Fill up the capacity.
		for (int element = 0; element < 10; element++) {
			evicted = evictor.addAndEvict(element);
			checkNothingEvicted(evicted);
		}

		// Repeat the queries after number of queries that is the capacity.
		for (int element = 0; element < 10; element++) {
			evicted = evictor.addAndEvict(element);
			checkNothingEvicted(evicted);
		}

		// Check the capacity.
		Assert.assertTrue("Capacity changed!",
				((CapacityBalancingEvictor<Integer>) evictor)
						.getCapacity() == 10);

	}

	@Test
	public void testCapacityShrinks() {

		final CapacityBalancingEvictor.Builder b = new CapacityBalancingEvictor.Builder();
		final Evictor<Integer> evictor = b.capacity(10)
				.balance(GET_FOUR_FIFTHS_OF_HITS_BALANCE)
				.balanceAfterNRepeatedQueries(ALWAYS_BALANCE_INTERVAL)
				.loadFactor(EvictorTestUtils.RETAIN_FULL_CAPACITY_LOAD_FACTOR)
				.build();

		Iterator<Integer> evicted;

		// Fill up the capacity.
		for (int element = 0; element < 10; element++) {
			evicted = evictor.addAndEvict(element);
			checkNothingEvicted(evicted);
		}

		// Repeat queries after small number of queries.
		for (int element = 7; element < 10; element++) {
			evicted = evictor.addAndEvict(element);
		}
		for (int element = 7; element < 10; element++) {
			evicted = evictor.addAndEvict(element);
		}
		for (int element = 7; element < 10; element++) {
			evicted = evictor.addAndEvict(element);
		}

		// Check the capacity is smaller.
		Assert.assertTrue("Capacity did not shrink!",
				((CapacityBalancingEvictor<Integer>) evictor)
						.getCapacity() < 10);

	}

	@Test
	public void testCapacityGrows() {

		final CapacityBalancingEvictor.Builder b = new CapacityBalancingEvictor.Builder();
		final Evictor<Integer> evictor = b.capacity(10)
				.balance(GET_FOUR_FIFTHS_OF_HITS_BALANCE)
				.balanceAfterNRepeatedQueries(ALWAYS_BALANCE_INTERVAL)
				.loadFactor(EvictorTestUtils.RETAIN_FULL_CAPACITY_LOAD_FACTOR)
				.build();

		Iterator<Integer> evicted;

		// Fill up the capacity.
		for (int element = 0; element < 10; element++) {
			evicted = evictor.addAndEvict(element);
			checkNothingEvicted(evicted);
		}

		// Fill up the capacity with new elements.
		for (int element = 10; element < 2 * 10; element++) {
			evicted = evictor.addAndEvict(element);
			checkEvicted(Arrays.asList(element - 10), evicted);
		}

		// Repeat the old queries after big number of queries.
		for (int element = 0; element < 10; element++) {
			evicted = evictor.addAndEvict(element);
		}

		// Check the capacity grew.
		Assert.assertTrue("Capacity did not grow!",
				((CapacityBalancingEvictor<Integer>) evictor)
						.getCapacity() > 10);

	}

}
