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

import org.semanticweb.elk.util.collections.entryset.KeyEntryFactory;
import org.semanticweb.elk.util.collections.entryset.KeyEntryHashSet;
import org.semanticweb.elk.util.collections.entryset.KeyEntry;
import org.semanticweb.elk.util.collections.entryset.StrongKeyEntry;

import junit.framework.TestCase;

public class RecordHashSetTest extends TestCase {

	public RecordHashSetTest(String testName) {
		super(testName);
	}

	public void testAddContains() {

		// random number generator for elements
		Random generator = new Random(123);
		// number of iterations of filling in elements
		final int noIterations = 100;
		// number of elements to generate in each iteration
		final int noElements = 10000;

		int i;

		IntegerKeyRecordFactory integerKeyRecordFactory = new IntegerKeyRecordFactory();

		for (int j = 0; j < noIterations; j++) {

			int initialSize = generator.nextInt(noElements);

			KeyEntryHashSet<Int> set = new KeyEntryHashSet<Int>(
					integerKeyRecordFactory, initialSize);
			Set<Integer> referenceSet = new HashSet<Integer>(noElements);

			for (i = 0; i < noElements; i++) {
				Int element = new Int(generator.nextInt(noElements / 2));
				boolean isAdded = referenceSet.add(element.getValue());
				if (isAdded) {
					assertTrue(set.merge(element) == element);
					assertTrue(set.get(element) == element);
				} else {
					assertNotNull(set.get(element));
					assertFalse(set.merge(element) == element);
				}
				assertEquals(set.size(), referenceSet.size());
			}

			i = 0;
			for (Int element : set) {
				assertTrue(referenceSet.contains(element.getValue()));
				i++;
			}

			assertEquals(i, referenceSet.size());

			i = 0;
			for (int n : referenceSet) {
				assertNotNull(set.get(new Int(n)));
				i++;
			}
			assertEquals(i, referenceSet.size());

			set.clear();
			referenceSet.clear();
			assertEquals(set.size(), referenceSet.size());

		}

	}

	class Int {
		final int value;

		Int(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	class IntegerKeyRecord extends StrongKeyEntry<Int, Int> {

		public IntegerKeyRecord(Int key) {
			super(key);
		}

		@Override
		public int computeHashCode() {
			return this.getKey().getValue();
		}

		@Override
		public boolean equals(Object object) {
			if (object instanceof IntegerKeyRecord) {
				IntegerKeyRecord objectRecord = (IntegerKeyRecord) object;
				return objectRecord.getKey().getValue() == this.getKey()
						.getValue();
			}
			return false;
		}
	}

	class IntegerKeyRecordFactory implements KeyEntryFactory<Int> {

		public KeyEntry<Int, ? extends Int> createEntry(Int key) {
			return new IntegerKeyRecord(key);
		}
	}

}