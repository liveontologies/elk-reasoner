package org.semanticweb.elk.reasoner.indexing.caching;

import org.semanticweb.elk.util.collections.entryset.Entry;

public interface CachedIndexedComplexClassExpression<T extends CachedIndexedComplexClassExpression<T>>
		extends CachedIndexedClassExpression<T>,
		Entry<T, CachedIndexedComplexClassExpression<?>> {

}
