package org.semanticweb.elk.reasoner.indexing.caching;

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectUnionOf;

public interface CachedIndexedObjectUnionOf extends
		ModifiableIndexedObjectUnionOf,
		CachedIndexedComplexClassExpression<CachedIndexedObjectUnionOf> {

	static class Helper extends CachedIndexedObject.Helper {

		public static int structuralHashCode(
				Set<ModifiableIndexedClassExpression> disjuncts) {
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
