package org.semanticweb.elk.reasoner.indexing.caching;

import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedEntity;
import org.semanticweb.elk.util.collections.entryset.Entry;

public interface CachedIndexedEntity<T extends CachedIndexedEntity<T>> extends
		ModifiableIndexedEntity, CachedIndexedObject<T>, Entry<T, T> {

}
