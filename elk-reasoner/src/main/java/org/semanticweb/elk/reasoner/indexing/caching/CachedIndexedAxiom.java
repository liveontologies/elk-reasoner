package org.semanticweb.elk.reasoner.indexing.caching;

import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedAxiom;
import org.semanticweb.elk.util.collections.entryset.Entry;

public interface CachedIndexedAxiom<T extends CachedIndexedAxiom<T>> extends
		ModifiableIndexedAxiom, CachedIndexedObject<T>,
		Entry<T, CachedIndexedAxiom<?>> {

}
