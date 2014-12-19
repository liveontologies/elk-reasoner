package org.semanticweb.elk.reasoner.indexing.caching;

import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverter;

public interface ModifiableIndexedObjectCache extends IndexedObjectCache {

	<T extends CachedIndexedObject<T>> T resolve(CachedIndexedObject<T> input);

	void add(CachedIndexedObject<?> input);

	void remove(CachedIndexedObject<?> input);

	// TODO: make converter return unmodifiable indexed objects and move these
	// methods to IndexedObjectCache
	public ElkPolarityExpressionConverter getExpressionConverter();

	public ElkAxiomConverter getAxiomConverter();

}
