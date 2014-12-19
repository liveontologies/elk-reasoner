package org.semanticweb.elk.reasoner.indexing.caching;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedBinaryPropertyChain;

public interface CachedIndexedBinaryPropertyChain extends
		ModifiableIndexedBinaryPropertyChain,
		CachedIndexedComplexPropertyChain<CachedIndexedBinaryPropertyChain> {

	static class Helper extends CachedIndexedObject.Helper {

		public static int structuralHashCode(IndexedObjectProperty leftProperty,
				IndexedPropertyChain rightProperty) {
			return combinedHashCode(CachedIndexedBinaryPropertyChain.class,
					leftProperty, rightProperty);
		}

		public static CachedIndexedBinaryPropertyChain structuralEquals(
				CachedIndexedBinaryPropertyChain first, Object second) {
			if (first == second) {
				return first;
			}
			if (second instanceof CachedIndexedBinaryPropertyChain) {
				CachedIndexedBinaryPropertyChain secondEntry = (CachedIndexedBinaryPropertyChain) second;
				if (first.getLeftProperty().equals(
						secondEntry.getLeftProperty())
						&& first.getRightProperty().equals(
								secondEntry.getRightProperty()))
					return secondEntry;
			}
			// else
			return null;
		}

	}

}
