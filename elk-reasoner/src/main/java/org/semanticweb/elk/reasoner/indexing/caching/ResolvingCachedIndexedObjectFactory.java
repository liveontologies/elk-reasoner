package org.semanticweb.elk.reasoner.indexing.caching;

public class ResolvingCachedIndexedObjectFactory extends
		DelegatingCachedIndexedObjectFactory {

	private final ModifiableIndexedObjectCache cache_;

	public ResolvingCachedIndexedObjectFactory(
			CachedIndexedObjectFactory baseFactory,
			ModifiableIndexedObjectCache cache) {
		super(baseFactory);
		this.cache_ = cache;
	}

	@Override
	<T extends CachedIndexedObject<T>> T filter(T input) {
		return cache_.resolve(input);
	}

}
