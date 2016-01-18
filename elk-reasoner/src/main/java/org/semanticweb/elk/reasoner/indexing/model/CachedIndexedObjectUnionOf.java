package org.semanticweb.elk.reasoner.indexing.model;

import java.util.List;

/**
 * A {@link ModifiableIndexedObjectUnionOf} that can be used for memoization
 * (caching).
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <T>
 *            the type of the {@link CachedIndexedObjectUnionOf}
 */
public interface CachedIndexedObjectUnionOf extends
		ModifiableIndexedObjectUnionOf,
		CachedIndexedComplexClassExpression<CachedIndexedObjectUnionOf> {

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Factory {

		CachedIndexedObjectUnionOf getIndexedObjectUnionOf(
				List<? extends ModifiableIndexedClassExpression> disjuncts);

	}
	
	/**
	 * A filter for mapping objects
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	interface Filter {

		CachedIndexedObjectUnionOf filter(CachedIndexedObjectUnionOf element);

	}
	
	static class Helper extends CachedIndexedObject.Helper {

		public static int structuralHashCode(
				List<? extends ModifiableIndexedClassExpression> disjuncts) {
			return combinedHashCode(CachedIndexedObjectUnionOf.class, disjuncts);
		}

		public static CachedIndexedObjectUnionOf structuralEquals(
				CachedIndexedObjectUnionOf first, Object second) {
			if (first == second) {
				return first;
			}
			if (second instanceof CachedIndexedObjectUnionOf) {
				CachedIndexedObjectUnionOf secondEntry = (CachedIndexedObjectUnionOf) second;
				if (first.getDisjuncts().equals(secondEntry.getDisjuncts()))
					return secondEntry;
			}
			// else
			return null;
		}

	}

}
