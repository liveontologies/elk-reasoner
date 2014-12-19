package org.semanticweb.elk.reasoner.indexing.factories;

import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedEntity;

public interface ModifiableIndexedDeclarationAxiomFactory {

	public ModifiableIndexedDeclarationAxiom getIndexedDeclarationAxiom(
			ModifiableIndexedEntity entity);

}
