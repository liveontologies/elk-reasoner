package org.semanticweb.elk.reasoner.indexing.caching;

public interface CachedIndexedPropertyChainFilter {

	CachedIndexedObjectProperty filter(CachedIndexedObjectProperty element);

	CachedIndexedBinaryPropertyChain filter(
			CachedIndexedBinaryPropertyChain element);

}
