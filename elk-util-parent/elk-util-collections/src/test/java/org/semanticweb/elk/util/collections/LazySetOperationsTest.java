/**
 * 
 */
package org.semanticweb.elk.util.collections;

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
		Set<Integer> set = new HashSet<Integer>();
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

