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
 * @author Yevgeny Kazakov, May 23, 2011
 */
package org.semanticweb.elk.util.collections;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import junit.framework.TestCase;

/**
 * @author Yevgeny Kazakov
 * 
 */
public class ArrayHashMapTest extends TestCase {

	public ArrayHashMapTest(String testName) {
		super(testName);
	}

	/**
	 * Checking if two maps are equal
	 * 
	 * @param referenceMap
	 * @param testMap
	 */
	<K, V> void testMapEquality(Map<K, V> referenceMap, Map<K, V> testMap) {
		int i = 0;
		for (Entry<K, V> entry : referenceMap.entrySet()) {
			assertEquals(entry.getValue(), testMap.get(entry.getKey()));
			assertTrue(testMap.keySet().contains(entry.getKey()));
			i++;
		}
		assertEquals(i, testMap.size());
		i = 0;
		for (Entry<K, V> entry : testMap.entrySet()) {
			assertEquals(referenceMap.get(entry.getKey()), entry.getValue());
			i++;
		}
		assertEquals(referenceMap.size(), i);
	}


	public void testPutGet() {
		// random number generator for elements
		Random generator = new Random(123);
		// number of iterations of filling in elements
		final int noIterations = 55;
		// number of entries to generate in each iteration; will vary
		int noEntries = 10;

		int i;

		for (int j = 0; j < noIterations; j++) {
			// doubling the number of entries every 4 iteration
			if ((j & 3) == 3)
				noEntries <<= 1;

			int initialSize = generator.nextInt(noEntries);
			Map<Integer, Integer> testMap = new ArrayHashMap<Integer, Integer>(
					initialSize);
			Map<Integer, Integer> referenceMap = new HashMap<Integer, Integer>(
					noEntries);

			// adding random additions
			for (i = 0; i < noEntries; i++) {
				int key = generator.nextInt(noEntries / 2);
				int value = i;
				Integer previousReference = referenceMap.put(key, value);
				assertEquals(previousReference == null,
						!testMap.containsKey(key));
				Integer previous = testMap.put(key, value);
				assertEquals(previousReference, previous);
				assertEquals(referenceMap.size(), testMap.size());
			}
			testMapEquality(referenceMap, testMap);

			// adding random deletions
			for (i = 0; i < noEntries; i++) {
				int key = generator.nextInt(noEntries / 2);
				Integer previousReference = referenceMap.remove(key);
				assertEquals(previousReference == null,
						!testMap.containsKey(key));
				Integer previous = testMap.remove(key);
				assertEquals(previousReference, previous);
				assertEquals(referenceMap.size(), testMap.size());
			}
			testMapEquality(referenceMap, testMap);

			// randomly adding and removing
			for (i = 0; i < noEntries; i++) {
				int key = generator.nextInt(noEntries / 2);
				Integer previousReference, previous;
				if (generator.nextBoolean()) {
					int value = i;
					previousReference = referenceMap.put(key, value);
					assertEquals(previousReference == null,
							!testMap.containsKey(key));
					previous = testMap.put(key, value);
				} else {
					previousReference = referenceMap.remove(key);
					assertEquals(previousReference == null,
							!testMap.containsKey(key));
					previous = testMap.remove(key);
				}
				assertEquals(previousReference, previous);
				assertEquals(referenceMap.size(), testMap.size());
			}
			testMapEquality(referenceMap, testMap);

			testMap.clear();
			referenceMap.clear();
			testMapEquality(referenceMap, testMap);

		}
	}
}
