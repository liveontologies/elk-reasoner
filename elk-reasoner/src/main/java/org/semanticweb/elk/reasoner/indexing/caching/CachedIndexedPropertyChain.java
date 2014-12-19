package org.semanticweb.elk.reasoner.indexing.caching;

import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedPropertyChain;

public interface CachedIndexedPropertyChain<T extends CachedIndexedPropertyChain<T>>
		extends CachedIndexedObject<T>, ModifiableIndexedPropertyChain {

	T accept(CachedIndexedPropertyChainFilter filter);

}
