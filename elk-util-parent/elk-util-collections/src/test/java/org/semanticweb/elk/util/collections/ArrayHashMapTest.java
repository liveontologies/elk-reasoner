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

import org.semanticweb.elk.util.collections.ArrayHashMap;

import junit.framework.TestCase;

/**
 * @author Yevgeny Kazakov
 * 
 */
public class ArrayHashMapTest extends TestCase {

	public ArrayHashMapTest(String testName) {
		super(testName);
	}

	public void testPutGet() {
		// random number generator for elements
		Random generator = new Random(123);
		// number of iterations of filling in elements
		final int noIterations = 100;
		// number of entries to generate in each iteration
		final int noEntries = 10000;		
		
		int i;

		for (int j = 0; j < noIterations; j++) {

			int initialSize = generator.nextInt(noEntries);
			Map<Integer, Integer> map = new ArrayHashMap<Integer, Integer>(initialSize);
			Map<Integer, Integer> referenceMap = new HashMap<Integer, Integer>(noEntries);

			for (i = 0; i < noEntries; i++) {
				int key = generator.nextInt(noEntries / 2);
				int value = i;
				Integer previousReference = referenceMap.put(key, value);
				if (previousReference == null)
					assertFalse(map.containsKey(key));
				else
					assertTrue(map.containsKey(key));
				Integer previous = map.put(key, value);
				assertEquals(previous, previousReference);
				assertEquals(map.size(), referenceMap.size());
			}

			i = 0;
			for (Integer key : map.keySet()) {
				assertTrue(referenceMap.containsKey(key));
				i++;
			}
			assertEquals(i, referenceMap.size());

			i = 0;
			for (Entry<Integer, Integer> entry : map.entrySet()) {
				assertEquals(referenceMap.get(entry.getKey()), entry.getValue());
				i++;
			}
			assertEquals(i, referenceMap.size());

			i = 0;
			for (Entry<Integer, Integer> entry : referenceMap.entrySet()) {
				assertEquals(map.get(entry.getKey()), entry.getValue());
				assertTrue(map.keySet().contains(entry.getKey()));
				i++;
			}
			assertEquals(i, map.size());
			
			map.clear();
			referenceMap.clear();
			assertEquals(map.size(), referenceMap.size());

		}
	}

}
