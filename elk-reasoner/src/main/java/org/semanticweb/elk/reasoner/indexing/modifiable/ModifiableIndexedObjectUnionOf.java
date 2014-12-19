package org.semanticweb.elk.reasoner.indexing.modifiable;

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectUnionOf;

public interface ModifiableIndexedObjectUnionOf extends
		ModifiableIndexedClassExpression, IndexedObjectUnionOf {

	@Override
	public Set<? extends ModifiableIndexedClassExpression> getDisjuncts();

}
