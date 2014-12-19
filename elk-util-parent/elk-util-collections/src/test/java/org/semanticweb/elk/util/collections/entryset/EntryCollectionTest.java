/*
 * #%L
 * ELK Utilities Collections
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.util.collections.entryset;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

public class EntryCollectionTest extends TestCase {

	public EntryCollectionTest(String testName) {
		super(testName);
	}

	/**
	 * Checking if two sets are equal
	 * 
	 * @param referenceSet
	 * @param testSet
	 */
	static void testSetEquality(Set<Integer> referenceSet,
			EntryCollection<Int> testSet) {
		int i = 0;
		for (int n : referenceSet) {
			Int canonical = testSet.findStructural(new Int(n));
			assertNotNull(canonical);
			assertEquals(n, canonical.getValue());
			i++;
		}
		assertEquals(i, testSet.size());
		i = 0;
		for (Int e : testSet) {
			assertTrue(referenceSet.contains(e.getValue()));
			i++;
		}
		assertEquals(referenceSet.size(), i);
	}

	@Test
	public static void testAddRemoveContains() {

		// random number generator for elements
		Random generator = new Random(123);
		// number of iterations of filling in elements
		final int noIterations = 55;
		// number of elements to generate in each iteration; will vary
		int noElements = 10;

		int i;

		for (int j = 0; j < noIterations; j++) {
			// doubling the number of elements every 4 iteration
			if ((j & 3) == 3)
				noElements <<= 1;

			int initialSize = generator.nextInt(noElements);

			EntryCollection<Int> testSet = new EntryCollection<Int>(initialSize);
			Set<Integer> referenceSet = new HashSet<Integer>(noElements);

			// adding random elements
			for (i = 0; i < noElements; i++) {
				int n = generator.nextInt(noElements / 2);
				if (referenceSet.add(n)) {
					assertNull(testSet.findStructural(new Int(n)));
					testSet.addStructural(new Int(n));
					assertNotNull(testSet.findStructural(new Int(n)));
				} else {
					assertNotNull(testSet.findStructural(new Int(n)));
				}
				assertEquals(testSet.size(), referenceSet.size());
			}
			testSetEquality(referenceSet, testSet);

			// removing random elements
			for (i = 0; i < noElements; i++) {
				int n = generator.nextInt(noElements / 2);
				if (referenceSet.remove(n)) {
					assertNotNull(testSet.findStructural(new Int(n)));
					testSet.removeStructural(new Int(n));
					assertNull(testSet.findStructural(new Int(n)));
				} else {
					assertNull(testSet.findStructural(new Int(n)));
				}
				assertEquals(referenceSet.size(), testSet.size());
			}
			testSetEquality(referenceSet, testSet);

			// randomly adding and removing
			for (i = 0; i < noElements; i++) {
				int n = generator.nextInt(noElements / 2);
				if (generator.nextBoolean()) {
					if (referenceSet.add(n)) {
						assertNull(testSet.findStructural(new Int(n)));
						testSet.addStructural(new Int(n));
						assertNotNull(testSet.findStructural(new Int(n)));
					} else {
						assertNotNull(testSet.findStructural(new Int(n)));
					}
				} else {
					if (referenceSet.remove(n)) {
						assertNotNull(testSet.findStructural(new Int(n)));
						testSet.removeStructural(new Int(n));
						assertNull(testSet.findStructural(new Int(n)));
					} else {
						assertNull(testSet.findStructural(new Int(n)));
					}
				}
				assertEquals(referenceSet.size(), testSet.size());
			}
			testSetEquality(referenceSet, testSet);

			testSet.clear();
			referenceSet.clear();
			testSetEquality(referenceSet, testSet);

			testSet.clear();
			referenceSet.clear();
			testSetEquality(referenceSet, testSet);

		}

	}

	static class Int implements Entry<Int, Int> {

		private final int value_;

		private Int next_;

		Int(int value) {
			this.value_ = value;
		}

		public int getValue() {
			return this.value_;
		}

		@Override
		public void setNext(Int next) {
			next_ = next;
		}

		@Override
		public Int getNext() {
			return next_;
		}

		@Override
		public Int structuralEquals(Object other) {
			if (other instanceof Int) {
				Int otherEntry = (Int) other;
				if (this.value_ == otherEntry.value_)
					return otherEntry;
			}
			// else
			return null;
		}

		@Override
		public int structuralHashCode() {
			return value_;
		}

	}

}