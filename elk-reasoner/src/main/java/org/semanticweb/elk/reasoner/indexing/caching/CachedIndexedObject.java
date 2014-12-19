package org.semanticweb.elk.reasoner.indexing.caching;

import java.util.List;

import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObject;
import org.semanticweb.elk.util.hashing.HashGenerator;

public interface CachedIndexedObject<T extends CachedIndexedObject<T>> extends
		ModifiableIndexedObject {

	boolean occurs();

	T accept(CachedIndexedObjectFilter filter);

	static class Helper {
		static int combinedHashCode(Object... objects) {
			return HashGenerator.combinedHashCode(objects);
		}

		static int combinedHashCode(List<?> objects) {
			return HashGenerator.combinedHashCode(objects);
		}
	}

}
