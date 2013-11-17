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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import junit.framework.TestCase;

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
	 * a large number of nodes with 1 value per each.
	 */
	public void testWideTree() {
		final int datasetSize = 1000;
		final long seed = System.currentTimeMillis();
		Random rnd = new Random(seed);

		//IntervalTree to test
		IntervalTree<IntegerInterval, IntegerInterval, Integer> testTree = new IntervalTree<IntegerInterval, IntegerInterval, Integer>(IntegerInterval.INT_COMPARATOR);

		//Reference list of intervals (for comparison)
		ArrayList<IntegerInterval> intervals = new ArrayList<IntegerInterval>(datasetSize);

		//Reference list of values
		ArrayList<IntegerInterval> values = new ArrayList<IntegerInterval>(datasetSize);

		//initial tree construction 

		for (int i = 0; i < datasetSize; i++) {
			int a = rnd.nextInt();
			int b = rnd.nextInt();
			IntegerInterval testInterval;
			if (a == b) {
				testInterval = new IntegerInterval(a, true, b, true);
			} else {
				testInterval = new IntegerInterval(a > b ? b : a, rnd.nextBoolean(), a > b ? a : b, rnd.nextBoolean());
			}
			testTree.add(testInterval, testInterval);
			intervals.add(testInterval);
			values.add(testInterval);
			
			//assertTrue(testInterval.getLow().compareTo(testInterval.getHigh()) <= 0);
		}


		//remove 25% of intervals and repopulate

		int changeSetSize = Math.round(datasetSize * 0.25f);

		for (int i = 0; i < changeSetSize; i++) {
			IntegerInterval del = intervals.get(rnd.nextInt(intervals.size()));
			testTree.remove(del, del);
			intervals.remove(del);
			values.remove(del);
		}

		assertEquals(testTree.size(), datasetSize - changeSetSize);
		assertEquals(testTree.size(), intervals.size());

		for (int i = 0; i < changeSetSize; i++) {
			int a = rnd.nextInt();
			int b = rnd.nextInt();
			IntegerInterval testInterval;
			if (a == b) {
				testInterval = new IntegerInterval(a, true, b, true);
			} else {
				testInterval = new IntegerInterval(a > b ? b : a, rnd.nextBoolean(), a > b ? a : b, rnd.nextBoolean());
			}
			testTree.add(testInterval, testInterval);
			intervals.add(testInterval);
			values.add(testInterval);
		}

		//test that all intervals are present in the tree and reference list

		assertEquals(testTree.size(), datasetSize);
		assertEquals(testTree.size(), intervals.size());

		//test that all values are present in the tree and reference list

		Collection<IntegerInterval> treeValues = testTree.values();
		assertEquals(treeValues.size(), values.size());

		for (IntegerInterval value : values) {
			assertTrue(treeValues.contains(value));
		}

		//test that every stored interval can be retrieved

		for (IntegerInterval testInterval : intervals) {
			assertTrue(testTree.searchIncludes(testInterval).contains(testInterval));
		}

		//test that every interval was retrieved for every stored interval

		for (IntegerInterval testInterval : intervals) {
			Collection<IntegerInterval> treeAnswer = testTree.searchIncludes(testInterval);
			Collection<IntegerInterval> listAnswer = getAllIncludes(intervals, testInterval);
			assertEquals(treeAnswer.size(), listAnswer.size());
			assertTrue(listAnswer.containsAll(treeAnswer));
		}

		//test that every interval was retrieved for any random query interval

		for (int i = 0; i < datasetSize; i++) {
			int a = rnd.nextInt();
			int b = rnd.nextInt();
			IntegerInterval testInterval;
			if (a == b) {
				testInterval = new IntegerInterval(a, true, b, true);
			} else {
				testInterval = new IntegerInterval(a > b ? b : a, rnd.nextBoolean(), a > b ? a : b, rnd.nextBoolean());
			}
			Collection<IntegerInterval> treeAnswer = testTree.searchIncludes(testInterval);
			Collection<IntegerInterval> listAnswer = getAllIncludes(intervals, testInterval);
			assertEquals(treeAnswer.size(), listAnswer.size());
			assertTrue(listAnswer.containsAll(treeAnswer));
		}
	}

	/**
	 * Test interval tree with small spread of intervals. Produces tree with
	 * small amount of nodes but many values for each.
	 * @throws IOException 
	 */
	public void testNarrowTree() throws Exception {
		final int datasetSize = 1000;
		final long seed = System.currentTimeMillis();
		Random rnd = new Random(seed);

		//IntervalTree to test
		IntervalTree<IntegerInterval, IntegerInterval, Integer> testTree = new IntervalTree<IntegerInterval, IntegerInterval, Integer>(IntegerInterval.INT_COMPARATOR);

		//Reference list of intervals (for comparison)
		ArrayList<IntegerInterval> intervals = new ArrayList<IntegerInterval>(datasetSize);

		//Reference list of values
		ArrayList<IntegerInterval> values = new ArrayList<IntegerInterval>(datasetSize);

		//initial tree construction 

		for (int i = 0; i < datasetSize; i++) {
			int a = rnd.nextInt(20);
			int b = rnd.nextInt(20);
			IntegerInterval testInterval;
			
			if (a == b) {
				testInterval = new IntegerInterval(a, true, b, true);
			} else {
				testInterval = new IntegerInterval(a > b ? b : a, rnd.nextBoolean(), a > b ? a : b, rnd.nextBoolean());
			}
			
			testTree.add(testInterval, testInterval);
			intervals.add(testInterval);
			values.add(testInterval);
			
			//assertTrue(testInterval.getLow().compareTo(testInterval.getHigh()) <= 0);
		}

		//remove 25% of intervals and repopulate

		int changeSetSize = Math.round(datasetSize * 0.25f);

		for (int i = 0; i < changeSetSize; i++) {
			IntegerInterval del = intervals.get(rnd.nextInt(intervals.size()));
			testTree.remove(del, del);
			intervals.remove(del);
			values.remove(del);
		}

		assertEquals(testTree.size(), datasetSize - changeSetSize);
		assertEquals(testTree.size(), intervals.size());

		for (int i = 0; i < changeSetSize; i++) {
			int a = rnd.nextInt(20);
			int b = rnd.nextInt(20);
			IntegerInterval testInterval;
			if (a == b) {
				testInterval = new IntegerInterval(a, true, b, true);
			} else {
				testInterval = new IntegerInterval(a > b ? b : a, rnd.nextBoolean(), a > b ? a : b, rnd.nextBoolean());
			}
			testTree.add(testInterval, testInterval);
			intervals.add(testInterval);
			values.add(testInterval);
		}

		//test that all intervals are present in the tree and reference list

		assertEquals(testTree.size(), datasetSize);
		assertEquals(testTree.size(), intervals.size());

		//test that all values are present in the tree and reference list

		Collection<IntegerInterval> treeValues = testTree.values();
		
		assertEquals(treeValues.size(), values.size());

		for (IntegerInterval value : values) {
			assertTrue(treeValues.contains(value));
		}

		//test that every stored interval can be retrieved

		for (IntegerInterval testInterval : intervals) {
				
				/*OutputStreamWriter writer = new OutputStreamWriter(System.out);
				
				testTree.print(writer);
				writer.flush();*/
				Collection<IntegerInterval> retrieved = testTree.searchIncludes(testInterval);
				
				assertTrue(retrieved + ". " + testInterval.toString(), retrieved.contains(testInterval));
		}

		//test that every interval was retrieved for every stored interval

		for (IntegerInterval testInterval : intervals) {
			Collection<IntegerInterval> treeAnswer = testTree.searchIncludes(testInterval);
			Collection<IntegerInterval> listAnswer = getAllIncludes(intervals, testInterval);
			
			assertEquals(treeAnswer.size(), listAnswer.size());
			assertTrue(listAnswer.containsAll(treeAnswer));
		}


		//test that every interval was retrieved for any random query interval

		for (int i = 0; i < datasetSize; i++) {
			int a = rnd.nextInt(30) - 10;
			int b = rnd.nextInt(30) - 10;
			IntegerInterval testInterval;
			
			if (a == b) {
				testInterval = new IntegerInterval(a, true, b, true);
			} else {
				testInterval = new IntegerInterval(a > b ? b : a, rnd.nextBoolean(), a > b ? a : b, rnd.nextBoolean());
			}
			
			Collection<IntegerInterval> treeAnswer = testTree.searchIncludes(testInterval);
			Collection<IntegerInterval> listAnswer = getAllIncludes(intervals, testInterval);
			assertEquals(treeAnswer.size(), listAnswer.size());
			assertTrue(listAnswer.containsAll(treeAnswer));
		}
	}

	private Collection<IntegerInterval> getAllIncludes(Collection<IntegerInterval> intervals, IntegerInterval i) {
		ArrayList<IntegerInterval> ret = new ArrayList<IntegerInterval>(50);
		
		for (IntegerInterval testInterval : intervals) {
			if (testInterval.contains(i)) {
				ret.add(testInterval);
			}
		}
		return ret;
	}

}
