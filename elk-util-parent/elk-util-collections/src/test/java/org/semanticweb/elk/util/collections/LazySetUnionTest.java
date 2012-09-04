package org.semanticweb.elk.util.collections;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;

public class LazySetUnionTest extends TestCase {

	public LazySetUnionTest(String testName) {
		super(testName);
	}

	@SuppressWarnings("static-method")
	public void testLazySetUnion() {
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
			Set<Integer> referenceUnion = new HashSet<Integer>(firstSet);
			referenceUnion.addAll(secondSet);

			Set<Integer> lazyUnion = new LazySetUnion<Integer>(firstSet,
					secondSet);

			// checking emptiness
			assertEquals(lazyUnion.isEmpty(), referenceUnion.isEmpty());

			// checking membership
			for (int k = 0; k < noEntries; k++)
				assertEquals(referenceUnion.contains(k), lazyUnion.contains(k));

			// checking the lazy union
			for (Integer element : lazyUnion) {
				assertTrue(referenceUnion.remove(element));
			}
			assertTrue(referenceUnion.isEmpty());
		}

	}
}
