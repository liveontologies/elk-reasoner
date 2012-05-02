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

	@SuppressWarnings("static-method")
	public void testAddContains() {
		
		// random number generator for elements
		Random generator = new Random(123);
		// number of iterations of filling in elements
		final int noIterations = 100;
		// number of elements to generate in each iteration
		final int noElements = 10000;
		
		int i;

		for (int j = 0; j < noIterations; j++) {

			int initialSize = generator.nextInt(noElements);

			Set<Integer> set = new ArrayHashSet<Integer>(initialSize);
			Set<Integer> referenceSet = new HashSet<Integer>(noElements);

			for (i = 0; i < noElements; i++) {
				int element = generator.nextInt(noElements / 2);
				boolean isAdded = referenceSet.add(element);
				if (isAdded)
					assertTrue(set.add(element));
				else
					assertFalse(set.add(element));
				assertEquals(set.size(), referenceSet.size());
			}

			i = 0;
			for (int element : set) {
				assertTrue(referenceSet.contains(element));
				i++;
			}

			assertEquals(i, referenceSet.size());

			i = 0;
			for (int element : referenceSet) {
				assertTrue(set.contains(element));
				i++;
			}
			assertEquals(i, referenceSet.size());

			set.clear();
			referenceSet.clear();
			assertEquals(set.size(), referenceSet.size());

		}

	}
}
