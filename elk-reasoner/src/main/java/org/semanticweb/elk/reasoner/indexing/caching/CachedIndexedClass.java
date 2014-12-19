package org.semanticweb.elk.reasoner.indexing.caching;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClass;

public interface CachedIndexedClass extends ModifiableIndexedClass,
		CachedIndexedClassEntity<CachedIndexedClass> {

	static class Helper extends CachedIndexedObject.Helper {

		public static int structuralHashCode(ElkClass entity) {
			return combinedHashCode(CachedIndexedClass.class, entity.getIri());
		}

		public static CachedIndexedClass structuralEquals(
				CachedIndexedClass first, Object second) {
			if (first == second) {
				return first;
			}
			if (second instanceof CachedIndexedClass) {
				CachedIndexedClass secondEntry = (CachedIndexedClass) second;
				if (first.getElkEntity().getIri()
						.equals(secondEntry.getElkEntity().getIri()))
					return secondEntry;
			}
			return null;
		}

	}
}
