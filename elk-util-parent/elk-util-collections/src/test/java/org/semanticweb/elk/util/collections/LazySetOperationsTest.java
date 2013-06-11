/**
 * 
 */
package org.semanticweb.elk.util.collections;
/*
 * #%L
 * ELK Utilities Collections
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class LazySetOperationsTest {

	private Set<Integer> generateSet(Random rnd, int numOfElements) {
		// TODO: generate sets using different classes
		Set<Integer> set = new ArrayHashSet<Integer>();
		int firstNoGenerations = rnd.nextInt(numOfElements);

		for (int j = 0; j < firstNoGenerations; j++) {
			set.add(rnd.nextInt(numOfElements));
		}

		return set;
	}

	private void assertCorrectness(Collection<Integer> refSet, Collection<Integer> lazySet, int numOfElements) {
		// checking emptiness
		assertEquals(lazySet.isEmpty(), refSet.isEmpty());

		// checking membership
		for (int k = 0; k < numOfElements; k++)
			assertEquals(refSet.contains(k), lazySet.contains(k));

		// checking the lazy union
		for (Integer element : lazySet) {
			assertTrue(refSet.remove(element));
		}
		assertTrue(refSet.isEmpty());
	}
	
	private void test(Operation faithful, Operation lazy) {
		// random number generator for elements
		Random generator = new Random(456);
		// number of iterations of filling in elements
		final int noIterations = 100;
		// number of elements to generate in each set
		final int noEntries = 1000;

		for (int i = 0; i < noIterations; i++) {
			Set<Integer> firstSet = generateSet(generator, 1 + generator.nextInt(noEntries));
			Set<Integer> secondSet = generateSet(generator, 1 + generator.nextInt(noEntries));

			Collection<Integer> refSet = faithful.create(firstSet, secondSet);
			Collection<Integer> lazySet = lazy.create(firstSet, secondSet);

			assertCorrectness(refSet, lazySet, noEntries);
		}
	}
	
	@Test
	public void testLazyIntersection() {
		test(new Operation() {

			@Override
			public Collection<Integer> create(Set<Integer> set1, Set<Integer> set2) {
				Set<Integer> referenceIntersection = new HashSet<Integer>(set1);
				
				referenceIntersection.retainAll(set2);
				
				return referenceIntersection;
			}},
			
			new Operation() {

				@Override
				public Collection<Integer> create(Set<Integer> set1, Set<Integer> set2) {
					return new LazySetIntersection<Integer>(set1, set2);
				}});
	}
	
	@Test
	public void testLazyUnion() {
		test(new Operation() {

			@Override
			public Collection<Integer> create(Set<Integer> set1, Set<Integer> set2) {
				Set<Integer> ref = new HashSet<Integer>(set1);
				
				ref.addAll(set2);
				
				return ref;
			}},
			
			new Operation() {

				@Override
				public Collection<Integer> create(Set<Integer> set1, Set<Integer> set2) {
					return new LazySetUnion<Integer>(set1, set2);
				}});
	}	
	
	@Test
	public void testLazyDifference() {
		test(new Operation() {

			@Override
			public Collection<Integer> create(Set<Integer> set1, Set<Integer> set2) {
				Set<Integer> ref = new HashSet<Integer>(set1);
				
				ref.removeAll(set2);
				
				return ref;
			}},
			
			new Operation() {

				@Override
				public Collection<Integer> create(Set<Integer> set1, Set<Integer> set2) {
					return new LazyCollectionMinusSet<Integer>(set1, set2);
				}});
	}		
	
	private static interface Operation {
		
		public Collection<Integer> create(Set<Integer> set1, Set<Integer> set2);
	}
}

