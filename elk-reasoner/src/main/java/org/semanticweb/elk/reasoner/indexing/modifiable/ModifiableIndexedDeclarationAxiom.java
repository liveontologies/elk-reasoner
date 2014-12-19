package org.semanticweb.elk.reasoner.indexing.modifiable;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDeclarationAxiom;

public interface ModifiableIndexedDeclarationAxiom extends
		ModifiableIndexedAxiom, IndexedDeclarationAxiom {

	@Override
	public ModifiableIndexedEntity getEntity();

}
