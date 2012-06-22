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
 * @author Yevgeny Kazakov, May 26, 2011
 */
package org.semanticweb.elk.util.collections;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.semanticweb.elk.util.collections.LazySetIntersection;

import junit.framework.TestCase;

/**
 * @author Yevgeny Kazakov
 * 
 */
public class LazySetIntersectionTest extends TestCase {

	public LazySetIntersectionTest(String testName) {
		super(testName);
	}

	@SuppressWarnings("static-method")
	public void testLazySetIntersection() {
		// random number generator for elements
		Random generator = new Random(123);
		// number of iterations of filling in elements
		final int noIterations = 100;
		// number of elements to generate in each set
		final int noEntries = 10000;

		for (int i = 0; i < noIterations; i++) {
			// generating two random sets
			Set<Integer> firstSet = new HashSet<Integer>();
			Set<Integer> secondSet = new HashSet<Integer>();
			int firstNoGenerations = generator.nextInt(noEntries);
			for (int j = 0; j < firstNoGenerations; j++)
				firstSet.add(generator.nextInt(noEntries));
			int secondNoGenerations = generator.nextInt(noEntries);
			for (int j = 0; j < secondNoGenerations; j++)
				firstSet.add(generator.nextInt(noEntries));

			// computing the faithful intersection
			Set<Integer> referenceIntersection = new HashSet<Integer>(firstSet);
			referenceIntersection.retainAll(secondSet);

			// checking the lazy intersection
			for (Integer element : new LazySetIntersection<Integer>(firstSet,
					secondSet)) {
				assertTrue(referenceIntersection.remove(element));
			}
			assertTrue(referenceIntersection.isEmpty());
		}

	}

}
