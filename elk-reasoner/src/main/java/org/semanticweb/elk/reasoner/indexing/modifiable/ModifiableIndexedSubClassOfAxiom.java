package org.semanticweb.elk.reasoner.indexing.modifiable;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;

public interface ModifiableIndexedSubClassOfAxiom extends
		ModifiableIndexedAxiom, IndexedSubClassOfAxiom {

	@Override
	public ModifiableIndexedClassExpression getSubClass();

	@Override
	public ModifiableIndexedClassExpression getSuperClass();

}
