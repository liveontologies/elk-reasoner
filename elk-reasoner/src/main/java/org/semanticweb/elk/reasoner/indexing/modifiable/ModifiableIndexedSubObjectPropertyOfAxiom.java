package org.semanticweb.elk.reasoner.indexing.modifiable;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubObjectPropertyOfAxiom;

public interface ModifiableIndexedSubObjectPropertyOfAxiom extends
		ModifiableIndexedAxiom, IndexedSubObjectPropertyOfAxiom {

	@Override
	public ModifiableIndexedPropertyChain getSubPropertyChain();

	@Override
	public ModifiableIndexedObjectProperty getSuperProperty();

}
