package org.semanticweb.elk.reasoner.indexing.caching;

import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassEntity;

public interface CachedIndexedClassEntity<T extends CachedIndexedClassEntity<T>>
		extends ModifiableIndexedClassEntity, CachedIndexedClassExpression<T>,
		CachedIndexedEntity<T> {

}
