package org.semanticweb.elk.reasoner.indexing.modifiable;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedReflexiveObjectPropertyAxiom;

public interface ModifiableIndexedReflexiveObjectPropertyAxiom extends
		ModifiableIndexedAxiom, IndexedReflexiveObjectPropertyAxiom {

	@Override
	public ModifiableIndexedObjectProperty getProperty();

}
