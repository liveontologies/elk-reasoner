package org.semanticweb.elk.reasoner.indexing.modifiable;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;

public interface ModifiableIndexedObjectSomeValuesFrom extends
		ModifiableIndexedClassExpression, IndexedObjectSomeValuesFrom {

	@Override
	public ModifiableIndexedObjectProperty getProperty();

	@Override
	public ModifiableIndexedClassExpression getFiller();

}
