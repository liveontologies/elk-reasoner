package org.semanticweb.elk.reasoner.indexing.modifiable;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;

public interface ModifiableIndexedObjectIntersectionOf extends
		ModifiableIndexedClassExpression, IndexedObjectIntersectionOf {

	@Override
	public ModifiableIndexedClassExpression getFirstConjunct();

	@Override
	public ModifiableIndexedClassExpression getSecondConjunct();

}
