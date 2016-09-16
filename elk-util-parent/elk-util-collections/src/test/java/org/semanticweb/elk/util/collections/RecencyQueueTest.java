/*-
 * #%L
 * ELK Utilities Collections
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;

public class RecencyQueueTest {

	@Test
	public void testInsertionNoRepetition() {

		final RecencyQueue<Integer> queue = new RecencyQueue<Integer>();

		for (int i = 0; i < 5; i++) {
			queue.offer(i);
		}

		checkPoll(Arrays.asList(0, 1, 2, 3, 4), queue);
	}

	@Test
	public void testInsertionWithRepetition() {

		final RecencyQueue<Integer> queue = new RecencyQueue<Integer>();

		for (int i = 0; i < 5; i++) {
			queue.offer(i);
		}

		for (int i = 0; i < 2; i++) {
			queue.offer(i);
		}

		checkPoll(Arrays.asList(2, 3, 4, 0, 1), queue);
	}

	@Test
	public void testContainsAndRemove() {

		final RecencyQueue<Integer> queue = new RecencyQueue<Integer>();

		for (int i = 0; i < 5; i++) {
			queue.offer(i);
		}

		Assert.assertTrue(queue.contains(3));

		queue.remove(3);

		Assert.assertFalse(queue.contains(3));

		checkPoll(Arrays.asList(0, 1, 2, 4), queue);
	}

	@Test
	public void testIterator() {

		final RecencyQueue<Integer> queue = new RecencyQueue<Integer>();

		queue.offer(0);// readded later
		queue.offer(1);// readded later
		queue.offer(2);
		queue.offer(3);// readded later
		queue.offer(4);
		queue.offer(5);
		queue.offer(1);// readded later
		queue.offer(3);
		queue.offer(0);
		queue.offer(1);
		queue.offer(6);

		final Iterator<Integer> iter = queue.iterator();

		Assert.assertTrue(iter.hasNext());
		Assert.assertEquals(2, iter.next().intValue());
		Assert.assertTrue(iter.hasNext());
		Assert.assertEquals(4, iter.next().intValue());
		Assert.assertTrue(iter.hasNext());
		Assert.assertEquals(5, iter.next().intValue());
		Assert.assertTrue(iter.hasNext());
		Assert.assertEquals(3, iter.next().intValue());
		Assert.assertTrue(iter.hasNext());
		Assert.assertEquals(0, iter.next().intValue());
		Assert.assertTrue(iter.hasNext());
		Assert.assertEquals(1, iter.next().intValue());
		Assert.assertTrue(iter.hasNext());
		Assert.assertEquals(6, iter.next().intValue());

		Assert.assertFalse(iter.hasNext());
		boolean caught = false;
		try {
			iter.next();
		} catch (final NoSuchElementException e) {
			caught = true;
		}
		Assert.assertTrue(caught);

	}

	@Test
	public void testPeekAndSize() {

		final RecencyQueue<Integer> queue = new RecencyQueue<Integer>();

		for (int i = 0; i < 5; i++) {
			queue.offer(i);
		}

		for (int i = 0; i < 5; i++) {

			final int firstElement = queue.peek();
			Assert.assertEquals(i, firstElement);

			Assert.assertEquals(5 - i, queue.size());

			queue.poll();
		}

	}

	private static <E> void checkPoll(final Collection<E> expected,
			final RecencyQueue<E> actual) {

		final String messageSuffix = "\nexpected: " + expected + "\nactual: "
				+ actual;

		for (final E expectedElement : expected) {

			final E actualElement = actual.poll();
			Assert.assertNotNull("Actual misses elements!" + messageSuffix,
					actualElement);

			Assert.assertEquals("Elements do not match!" + messageSuffix,
					expectedElement, actualElement);

		}

		final E actualElement = actual.poll();
		Assert.assertNull("Actual has too many elements!" + messageSuffix,
				actualElement);

	}

}
