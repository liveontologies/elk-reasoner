package org.semanticweb.elk.reasoner.indexing.caching;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectIntersectionOf;

public interface CachedIndexedObjectIntersectionOf extends
		ModifiableIndexedObjectIntersectionOf,
		CachedIndexedComplexClassExpression<CachedIndexedObjectIntersectionOf> {

	static class Helper extends CachedIndexedObject.Helper {

		public static int structuralHashCode(IndexedClassExpression firstConjunct,
				IndexedClassExpression secondConjunct) {
			return combinedHashCode(CachedIndexedObjectIntersectionOf.class,
					firstConjunct, secondConjunct);
		}

		public static CachedIndexedObjectIntersectionOf structuralEquals(
				CachedIndexedObjectIntersectionOf first, Object second) {
			if (first == second) {
				return first;
			}
			if (second instanceof CachedIndexedObjectIntersectionOf) {
				CachedIndexedObjectIntersectionOf secondEntry = (CachedIndexedObjectIntersectionOf) second;
				if (first.getFirstConjunct().equals(
						secondEntry.getFirstConjunct())
						&& first.getSecondConjunct().equals(
								secondEntry.getSecondConjunct()))
					return secondEntry;
			}
			// else
			return null;
		}
	}

}
