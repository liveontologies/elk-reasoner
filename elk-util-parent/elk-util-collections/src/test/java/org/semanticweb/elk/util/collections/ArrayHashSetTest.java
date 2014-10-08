/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
/**
 * @author Yevgeny Kazakov, May 17, 2011
 */
package org.semanticweb.elk.util.collections;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ArrayHashSetTest extends TestCase {

	public ArrayHashSetTest(String testName) {
		super(testName);
	}

	/**
	 * Checking if two sets are equal
	 * 
	 * @param referenceSet
	 * @param testSet
	 */
	static <E> void testSetEquality(Set<E> referenceSet, Set<E> testSet) {
		int i = 0;
		for (E e : referenceSet) {
			assertTrue(testSet.contains(e));
			i++;
		}
		assertEquals(i, testSet.size());
		i = 0;
		for (E e : testSet) {
			assertTrue(referenceSet.contains(e));
			i++;
		}
		assertEquals(referenceSet.size(), i);
	}

	@Test
	public static void testAddRemoveContains() {
		// random number generator for elements
		Random generator = new Random(123);
		// number of test iterations
		final int noIterations = 55;
		// number of elements to generate in each iteration; will vary
		int noElements = 10;

		boolean expected, actual;

		int i;

		for (int j = 0; j < noIterations; j++) {
			// doubling the number of elements every 4 iteration
			if ((j & 3) == 3)
				noElements <<= 1;

			int initialSize = generator.nextInt(noElements);

			Set<Integer> testSet = new ArrayHashSet<Integer>(initialSize);
			Set<Integer> referenceSet = new HashSet<Integer>(noElements);

			// adding random elements
			for (i = 0; i < noElements; i++) {
				int element = generator.nextInt(noElements / 2);
				// System.out.println("adding " + element);
				expected = referenceSet.add(element);
				assertEquals(expected, !testSet.contains(element));
				actual = testSet.add(element);
				assertEquals(expected, actual);
				assertEquals(referenceSet.size(), testSet.size());
			}
			testSetEquality(referenceSet, testSet);

			// removing random elements
			for (i = 0; i < noElements; i++) {
				int element = generator.nextInt(noElements / 2);
				// System.out.println("removing " + element);
				expected = referenceSet.remove(element);
				assertEquals(expected, testSet.contains(element));
				actual = testSet.remove(element);
				assertEquals(expected, actual);
				assertEquals(referenceSet.size(), testSet.size());
			}
			testSetEquality(referenceSet, testSet);

			// removing through iterator
			Iterator<Integer> iterator = testSet.iterator();
			while (iterator.hasNext()) {
				Integer element = iterator.next();
				assertTrue(testSet.contains(element));
				assertTrue(referenceSet.contains(element));
				if (generator.nextBoolean()) {
					// removing
					// System.out.println("removing " + element);
					iterator.remove();
					referenceSet.remove(element);
					assertFalse(testSet.contains(element));
					// the second removal attempt should fail
					try {
						iterator.remove();
						fail();
					} catch (IllegalStateException e) {
						// this exception should always takes place
					}
				}
			}
			testSetEquality(referenceSet, testSet);

			// randomly adding and removing
			for (i = 0; i < noElements; i++) {
				int element = generator.nextInt(noElements / 2);
				if (generator.nextBoolean()) {
					// System.out.println("adding " + element);
					expected = referenceSet.add(element);
					assertEquals(expected, !testSet.contains(element));
					actual = testSet.add(element);
				} else {
					expected = referenceSet.remove(element);
					// System.out.println("removing " + element);
					assertEquals(expected, testSet.contains(element));
					actual = testSet.remove(element);
				}
				assertEquals(expected, actual);
				assertEquals(referenceSet.size(), testSet.size());
			}
			testSetEquality(referenceSet, testSet);

			testSet.clear();
			referenceSet.clear();
			testSetEquality(referenceSet, testSet);
		}

	}
}
