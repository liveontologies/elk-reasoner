package org.semanticweb.elk.reasoner.indexing.modifiable;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;

public interface ModifiableIndexedBinaryPropertyChain extends
		ModifiableIndexedPropertyChain, IndexedBinaryPropertyChain {

	@Override
	public ModifiableIndexedObjectProperty getLeftProperty();

	@Override
	public ModifiableIndexedPropertyChain getRightProperty();

}
