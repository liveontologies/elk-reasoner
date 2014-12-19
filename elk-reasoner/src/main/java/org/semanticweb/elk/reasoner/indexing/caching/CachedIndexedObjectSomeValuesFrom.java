package org.semanticweb.elk.reasoner.indexing.caching;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectSomeValuesFrom;

public interface CachedIndexedObjectSomeValuesFrom extends
		ModifiableIndexedObjectSomeValuesFrom,
		CachedIndexedComplexClassExpression<CachedIndexedObjectSomeValuesFrom> {

	static class Helper extends CachedIndexedObject.Helper {

		public static int structuralHashCode(IndexedObjectProperty property,
				IndexedClassExpression filler) {
			return combinedHashCode(CachedIndexedObjectSomeValuesFrom.class,
					property, filler);
		}

		public static CachedIndexedObjectSomeValuesFrom structuralEquals(
				CachedIndexedObjectSomeValuesFrom first, Object second) {
			if (first == second) {
				return first;
			}
			if (second instanceof CachedIndexedObjectSomeValuesFrom) {
				CachedIndexedObjectSomeValuesFrom secondEntry = (CachedIndexedObjectSomeValuesFrom) second;
				if (first.getProperty().equals(secondEntry.getProperty())
						&& first.getFiller().equals(secondEntry.getFiller()))
					return secondEntry;
			}
			// else
			return null;
		}

	}

}
