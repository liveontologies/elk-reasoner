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
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ArraySlicedSetTest extends TestCase {

	private static int SLICES_ = 8;

	public ArraySlicedSetTest(String testName) {
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
			// System.out.println("refs: " + e);
			assertTrue(testSet.contains(e));
			i++;
		}
		assertEquals(i, testSet.size());
		i = 0;
		for (E e : testSet) {
			// System.out.println("test: " + e);
			assertTrue(referenceSet.contains(e));
			i++;
		}
		assertEquals(referenceSet.size(), i);
	}

	static <E> void testSetsEquality(Set<E>[] referenceSets,
			ArraySlicedSet<E> slicedSet) {
		for (int i = 0; i < SLICES_; i++) {
			// System.out.println("Test equality for: " + i);
			testSetEquality(referenceSets[i], slicedSet.getSlice(i));
		}
	}

	@SuppressWarnings("static-method")
	@Test
	public void testAddRemoveContains() {
		// random number generator for elements
		Random generator = new Random(123);
		// number of test iterations
		final int noIterations = 50;
		// number of elements to generate in each iteration; will vary
		int noElements = 10;

		boolean expected, actual;

		int i;

		for (int j = 0; j < noIterations; j++) {
			// doubling the number of elements every 4 iteration
			if ((j & 3) == 3)
				noElements <<= 1;

			int initialSize = generator.nextInt(noElements);
			ArraySlicedSet<Integer> slicedSet = new ArraySlicedSet<Integer>(
					initialSize);

			@SuppressWarnings("unchecked")
			Set<Integer>[] referenceSets = new Set[SLICES_];
			for (int s = 0; s < SLICES_; s++)
				referenceSets[s] = new HashSet<Integer>(noElements);
			Set<Integer> testSet;
			Set<Integer> referenceSet;

			// adding random elements
			for (i = 0; i < noElements; i++) {
				int element = generator.nextInt(noElements / 2);
				int s = generator.nextInt(SLICES_);
				// System.out.println(s + ": adding " + element);
				testSet = slicedSet.getSlice(s);
				referenceSet = referenceSets[s];
				expected = referenceSet.add(element);
				assertEquals(!expected, testSet.contains(element));
				actual = testSet.add(element);
				assertEquals(expected, actual);
				assertEquals(referenceSet.size(), testSet.size());
			}
			testSetsEquality(referenceSets, slicedSet);

			// removing random elements
			for (i = 0; i < noElements; i++) {
				int element = generator.nextInt(noElements / 2);
				int s = generator.nextInt(SLICES_);
				// System.out.println(s + ": removing " + element);
				testSet = slicedSet.getSlice(s);
				referenceSet = referenceSets[s];
				expected = referenceSet.remove(element);
				assertEquals(expected, testSet.contains(element));
				actual = testSet.remove(element);
				assertEquals(expected, actual);
				assertEquals(referenceSet.size(), testSet.size());
			}
			testSetsEquality(referenceSets, slicedSet);

			// randomly adding and removing
			for (i = 0; i < noElements; i++) {
				int element = generator.nextInt(noElements / 2);
				int s = generator.nextInt(SLICES_);
				testSet = slicedSet.getSlice(s);
				referenceSet = referenceSets[s];
				if (generator.nextBoolean()) {
					// System.out.println(s + ": adding " + element);
					expected = referenceSet.add(element);
					assertEquals(!expected, testSet.contains(element));
					actual = testSet.add(element);
				} else {
					// System.out.println(s + ": removing " + element);
					expected = referenceSet.remove(element);
					assertEquals(expected, testSet.contains(element));
					actual = testSet.remove(element);
				}
				assertEquals(expected, actual);
				assertEquals(referenceSet.size(), testSet.size());
			}
			testSetsEquality(referenceSets, slicedSet);
			// clear random sets
			for (int s = 0; s < SLICES_; s++) {
				if (generator.nextBoolean())
					continue;
				// else
				// System.out.println(s + ": clear");
				referenceSets[s].clear();
				slicedSet.getSlice(s).clear();
			}
			testSetsEquality(referenceSets, slicedSet);
		}

	}
}
