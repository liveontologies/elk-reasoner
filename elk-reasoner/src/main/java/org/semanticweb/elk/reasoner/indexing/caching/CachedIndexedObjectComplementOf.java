package org.semanticweb.elk.reasoner.indexing.caching;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectComplementOf;

public interface CachedIndexedObjectComplementOf extends
		ModifiableIndexedObjectComplementOf,
		CachedIndexedComplexClassExpression<CachedIndexedObjectComplementOf> {

	static class Helper extends CachedIndexedObject.Helper {

		public static int structuralHashCode(IndexedClassExpression negated) {
			return combinedHashCode(combinedHashCode(
					CachedIndexedObjectComplementOf.class, negated));
		}

		public static CachedIndexedObjectComplementOf structuralEquals(
				CachedIndexedObjectComplementOf first, Object second) {
			if (first == second) {
				return first;
			}
			if (second instanceof CachedIndexedObjectComplementOf) {
				CachedIndexedObjectComplementOf secondEntry = (CachedIndexedObjectComplementOf) second;
				if (first.getNegated().equals(secondEntry.getNegated()))
					return secondEntry;
			}
			// else
			return null;
		}

	}

}
