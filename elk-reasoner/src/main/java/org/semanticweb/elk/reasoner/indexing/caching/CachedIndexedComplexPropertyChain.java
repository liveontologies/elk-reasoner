package org.semanticweb.elk.reasoner.indexing.caching;

import org.semanticweb.elk.util.collections.entryset.Entry;

public interface CachedIndexedComplexPropertyChain<T extends CachedIndexedComplexPropertyChain<T>>
		extends CachedIndexedPropertyChain<T>,
		Entry<T, CachedIndexedComplexPropertyChain<?>> {

}
