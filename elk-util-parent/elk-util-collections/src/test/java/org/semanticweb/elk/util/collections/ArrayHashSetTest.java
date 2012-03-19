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

import org.semanticweb.elk.util.collections.ArrayHashSet;

import junit.framework.TestCase;

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
	 * @param firstSet
	 * @param secondSet
	 */
	<E> void testSetEquality(Set<E> firstSet, Set<E> secondSet) {
		int i = 0;
		for (E e : firstSet) {
			assertTrue(secondSet.contains(e));
			i++;
		}
		assertEquals(i, secondSet.size());
		i = 0;
		for (E e : secondSet) {
			assertTrue(firstSet.contains(e));
			i++;
		}
		assertEquals(i, secondSet.size());
	}

	public void testAddRemoveContains() {

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

			Set<Integer> set = new ArrayHashSet<Integer>(initialSize);
			Set<Integer> referenceSet = new HashSet<Integer>(noElements);

			// adding random elements
			for (i = 0; i < noElements; i++) {
				int element = generator.nextInt(noElements / 2);
				expected = referenceSet.add(element);
				actual = set.add(element);
				assertEquals(expected, actual);
				assertEquals(set.size(), referenceSet.size());
			}
			testSetEquality(set, referenceSet);

			// removing random elements
			for (i = 0; i < noElements; i++) {
				int element = generator.nextInt(noElements / 2);
				expected = referenceSet.remove(element);
				actual = set.remove(element);
				assertEquals(expected, actual);
				assertEquals(set.size(), referenceSet.size());
			}
			testSetEquality(set, referenceSet);

			// randomly adding and removing
			for (i = 0; i < noElements; i++) {
				int element = generator.nextInt(noElements / 2);
				if (generator.nextBoolean()) {
					expected = referenceSet.add(element);
					actual = set.add(element);
				} else {
					expected = referenceSet.remove(element);
					actual = set.remove(element);
				}
				assertEquals(expected, actual);
				assertEquals(set.size(), referenceSet.size());
			}
			testSetEquality(set, referenceSet);

			set.clear();
			referenceSet.clear();
			testSetEquality(set, referenceSet);
		}

	}
}
