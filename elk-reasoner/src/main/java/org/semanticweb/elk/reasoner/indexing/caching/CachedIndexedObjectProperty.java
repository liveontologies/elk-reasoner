package org.semanticweb.elk.reasoner.indexing.caching;

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObjectProperty;

public interface CachedIndexedObjectProperty extends
		ModifiableIndexedObjectProperty,
		CachedIndexedPropertyChain<CachedIndexedObjectProperty>,
		CachedIndexedEntity<CachedIndexedObjectProperty> {

	static class Helper extends CachedIndexedObject.Helper {

		public static int structuralHashCode(ElkObjectProperty entity) {
			return combinedHashCode(CachedIndexedObjectProperty.class,
					entity.getIri());
		}

		public static CachedIndexedObjectProperty structuralEquals(
				CachedIndexedObjectProperty first, Object second) {
			if (first == second) {
				return first;
			}
			if (second instanceof CachedIndexedObjectProperty) {
				CachedIndexedObjectProperty secondEntry = (CachedIndexedObjectProperty) second;
				if (first.getElkEntity().getIri()
						.equals(secondEntry.getElkEntity().getIri()))
					return secondEntry;
			}
			// else
			return null;
		}

	}

}
