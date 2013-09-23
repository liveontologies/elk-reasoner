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
package org.semanticweb.elk.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import org.semanticweb.elk.util.collections.intervals.Interval;
import org.semanticweb.elk.util.collections.intervals.IntervalTree;

/**
 *
 * @author Pospishnyi Oleksandr
 */
public class IntervalTreeTest extends TestCase {

	public IntervalTreeTest(String name) {
		super(name);
	}

	/**
	 * Test interval tree with wide spread of intervals. Produces tree with
	 * large amount of nodes with 1 value per each.
	 */
	public void testWideTree() {
		final int datasetSize = 1000;

		Random rnd = new Random(System.currentTimeMillis());

		//IntervalTree to test
		IntervalTree<TestInterval, TestInterval> testTree = new IntervalTree<TestInterval, TestInterval>();

		//Reference list of intervals (for comparison)
		ArrayList<TestInterval> intervals = new ArrayList<TestInterval>(datasetSize);

		//Reference list of values
		ArrayList<TestInterval> values = new ArrayList<TestInterval>(datasetSize);

		//initial tree construction 

		for (int i = 0; i < datasetSize; i++) {
			int a = rnd.nextInt();
			int b = rnd.nextInt();
			TestInterval testInterval;
			if (a == b) {
				testInterval = new TestInterval(a, true, b, true);
			} else {
				testInterval = new TestInterval(a > b ? b : a, rnd.nextBoolean(), a > b ? a : b, rnd.nextBoolean());
			}
			testTree.add(testInterval, testInterval);
			intervals.add(testInterval);
			values.add(testInterval);
			assertTrue(testInterval.getLow().compareTo(testInterval.getHigh()) <= 0);
		}


		//remove 25% of intervals and repopulate

		int changeSetSize = Math.round(datasetSize * 0.25f);

		for (int i = 0; i < changeSetSize; i++) {
			TestInterval del = intervals.get(rnd.nextInt(intervals.size()));
			testTree.remove(del, del);
			intervals.remove(del);
			values.remove(del);
		}

		assertEquals(testTree.size(), datasetSize - changeSetSize);
		assertEquals(testTree.size(), intervals.size());

		for (int i = 0; i < changeSetSize; i++) {
			int a = rnd.nextInt();
			int b = rnd.nextInt();
			TestInterval testInterval;
			if (a == b) {
				testInterval = new TestInterval(a, true, b, true);
			} else {
				testInterval = new TestInterval(a > b ? b : a, rnd.nextBoolean(), a > b ? a : b, rnd.nextBoolean());
			}
			testTree.add(testInterval, testInterval);
			intervals.add(testInterval);
			values.add(testInterval);
		}

		//test that all intervals are present in the tree and reference list

		assertEquals(testTree.size(), datasetSize);
		assertEquals(testTree.size(), intervals.size());

		//test that all values are present in the tree and reference list

		Collection<TestInterval> treeValues = testTree.values();
		assertEquals(treeValues.size(), values.size());

		for (TestInterval value : values) {
			assertTrue(treeValues.contains(value));
		}

		//test that every stored interval can be retrieved

		for (TestInterval testInterval : intervals) {
			assertTrue(testTree.searchIncludes(testInterval).contains(testInterval));
		}

		//test that every interval was retrieved for every stored interval

		for (TestInterval testInterval : intervals) {
			Collection<TestInterval> treeAnswer = testTree.searchIncludes(testInterval);
			Collection<TestInterval> listAnswer = getAllIncludes(intervals, testInterval);
			assertEquals(treeAnswer.size(), listAnswer.size());
			assertTrue(listAnswer.containsAll(treeAnswer));
		}

		//test that every interval was retrieved for any random query interval

		for (int i = 0; i < datasetSize; i++) {
			int a = rnd.nextInt();
			int b = rnd.nextInt();
			TestInterval testInterval;
			if (a == b) {
				testInterval = new TestInterval(a, true, b, true);
			} else {
				testInterval = new TestInterval(a > b ? b : a, rnd.nextBoolean(), a > b ? a : b, rnd.nextBoolean());
			}
			Collection<TestInterval> treeAnswer = testTree.searchIncludes(testInterval);
			Collection<TestInterval> listAnswer = getAllIncludes(intervals, testInterval);
			assertEquals(treeAnswer.size(), listAnswer.size());
			assertTrue(listAnswer.containsAll(treeAnswer));
		}
	}

	/**
	 * Test interval tree with small spread of intervals. Produces tree with
	 * small amount of nodes but many values for each.
	 */
	public void testNarrowTree() {
		final int datasetSize = 1000;

		Random rnd = new Random(System.currentTimeMillis());

		//IntervalTree to test
		IntervalTree<TestInterval, TestInterval> testTree = new IntervalTree<TestInterval, TestInterval>();

		//Reference list of intervals (for comparison)
		ArrayList<TestInterval> intervals = new ArrayList<TestInterval>(datasetSize);

		//Reference list of values
		ArrayList<TestInterval> values = new ArrayList<TestInterval>(datasetSize);

		//initial tree construction 

		for (int i = 0; i < datasetSize; i++) {
			int a = rnd.nextInt(20);
			int b = rnd.nextInt(20);
			TestInterval testInterval;
			if (a == b) {
				testInterval = new TestInterval(a, true, b, true);
			} else {
				testInterval = new TestInterval(a > b ? b : a, rnd.nextBoolean(), a > b ? a : b, rnd.nextBoolean());
			}
			testTree.add(testInterval, testInterval);
			intervals.add(testInterval);
			values.add(testInterval);
			assertTrue(testInterval.getLow().compareTo(testInterval.getHigh()) <= 0);
		}

		//remove 25% of intervals and repopulate

		int changeSetSize = Math.round(datasetSize * 0.25f);

		for (int i = 0; i < changeSetSize; i++) {
			TestInterval del = intervals.get(rnd.nextInt(intervals.size()));
			testTree.remove(del, del);
			intervals.remove(del);
			values.remove(del);
		}

		assertEquals(testTree.size(), datasetSize - changeSetSize);
		assertEquals(testTree.size(), intervals.size());

		for (int i = 0; i < changeSetSize; i++) {
			int a = rnd.nextInt(20);
			int b = rnd.nextInt(20);
			TestInterval testInterval;
			if (a == b) {
				testInterval = new TestInterval(a, true, b, true);
			} else {
				testInterval = new TestInterval(a > b ? b : a, rnd.nextBoolean(), a > b ? a : b, rnd.nextBoolean());
			}
			testTree.add(testInterval, testInterval);
			intervals.add(testInterval);
			values.add(testInterval);
		}

		//test that all intervals are present in the tree and reference list

		assertEquals(testTree.size(), datasetSize);
		assertEquals(testTree.size(), intervals.size());

		//test that all values are present in the tree and reference list

		Collection<TestInterval> treeValues = testTree.values();
		assertEquals(treeValues.size(), values.size());

		for (TestInterval value : values) {
			assertTrue(treeValues.contains(value));
		}

		//test that every stored interval can be retrieved

		for (TestInterval testInterval : intervals) {
			assertTrue(testTree.searchIncludes(testInterval).contains(testInterval));
		}

		//test that every interval was retrieved for every stored interval

		for (TestInterval testInterval : intervals) {
			Collection<TestInterval> treeAnswer = testTree.searchIncludes(testInterval);
			Collection<TestInterval> listAnswer = getAllIncludes(intervals, testInterval);
			assertEquals(treeAnswer.size(), listAnswer.size());
			assertTrue(listAnswer.containsAll(treeAnswer));
		}


		//test that every interval was retrieved for any random query interval

		for (int i = 0; i < datasetSize; i++) {
			int a = rnd.nextInt(30) - 10;
			int b = rnd.nextInt(30) - 10;
			TestInterval testInterval;
			if (a == b) {
				testInterval = new TestInterval(a, true, b, true);
			} else {
				testInterval = new TestInterval(a > b ? b : a, rnd.nextBoolean(), a > b ? a : b, rnd.nextBoolean());
			}
			Collection<TestInterval> treeAnswer = testTree.searchIncludes(testInterval);
			Collection<TestInterval> listAnswer = getAllIncludes(intervals, testInterval);
			assertEquals(treeAnswer.size(), listAnswer.size());
			assertTrue(listAnswer.containsAll(treeAnswer));
		}
	}

	private Collection<TestInterval> getAllIncludes(Collection<TestInterval> intervals, TestInterval i) {
		ArrayList<TestInterval> ret = new ArrayList<TestInterval>(50);
		for (TestInterval testInterval : intervals) {
			if (testInterval.contains(i)) {
				ret.add(testInterval);
			}
		}
		return ret;
	}

	class TestInterval implements Interval<Endpoint> {

		private Endpoint low;
		private Endpoint high;

		public TestInterval(int low, int high) {
			this.low = new Endpoint(low, true, true);
			this.high = new Endpoint(high, true, false);
		}

		public TestInterval(int low, boolean lowInclusive, int high, boolean highInclusive) {
			this.low = new Endpoint(low, lowInclusive, true);
			this.high = new Endpoint(high, highInclusive, false);
		}

		@Override
		public Endpoint getLow() {
			return low;
		}

		@Override
		public Endpoint getHigh() {
			return high;
		}

		@Override
		public boolean contains(Interval<Endpoint> interval) {
			return low.compareTo(interval.getLow()) <= 0 && high.compareTo(interval.getHigh()) >= 0;
		}

		@Override
		public int compareTo(Interval<Endpoint> o) {
			int cmp = low.compareTo(o.getLow());
			if (cmp == 0) {
				return high.compareTo(o.getHigh());
			} else {
				return cmp;
			}
		}

		@Override
		public int hashCode() {
			int hash = 7;
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj instanceof TestInterval) {
				TestInterval i = (TestInterval) obj;
				return this.low.equals(i.low) && this.high.equals(i.high);
			} else {
				return false;
			}
		}

		@Override
		public String toString() {
			return "" + low + ',' + high;
		}
	}

	class Endpoint implements Comparable<Endpoint> {

		private int value;
		private boolean inclusive;
		private boolean low;

		public Endpoint(int value, boolean inclusive, boolean low) {
			this.value = value;
			this.inclusive = inclusive;
			this.low = low;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj instanceof Endpoint) {
				Endpoint e = (Endpoint) obj;
				return this.value == e.value && this.inclusive == e.inclusive && this.low == e.low;
			} else {
				return false;
			}
		}

		@Override
		public int compareTo(Endpoint o) {
			int cmp = Integer.compare(value, o.value);
			if (cmp == 0 && inclusive != o.inclusive) {
				return (low ^ inclusive) ? 1 : -1;
			} else {
				return cmp;
			}
		}

		@Override
		public String toString() {
			if (low) {
				return "" + (inclusive ? "[" : "(") + value;
			} else {
				return "" + value + (inclusive ? "]" : ")");
			}
		}
	}
}
