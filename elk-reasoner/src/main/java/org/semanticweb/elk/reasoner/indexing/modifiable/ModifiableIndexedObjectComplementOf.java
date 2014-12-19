package org.semanticweb.elk.reasoner.indexing.modifiable;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;

public interface ModifiableIndexedObjectComplementOf extends
		ModifiableIndexedClassExpression, IndexedObjectComplementOf {

	@Override
	public ModifiableIndexedClassExpression getNegated();

}
