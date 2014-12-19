package org.semanticweb.elk.reasoner.indexing.caching;

import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedClassExpression;

public interface CachedIndexedClassExpression<T extends CachedIndexedClassExpression<T>>
		extends CachedIndexedObject<T>, ModifiableIndexedClassExpression {

	T accept(CachedIndexedClassExpressionFilter filter);

}
