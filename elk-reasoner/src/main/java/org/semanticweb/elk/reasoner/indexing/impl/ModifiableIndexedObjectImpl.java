package org.semanticweb.elk.reasoner.indexing.impl;

import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedObject;

/**
 * Implements {@link ModifiableIndexedObject}.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public abstract class ModifiableIndexedObjectImpl implements
		ModifiableIndexedObject {

	@Override
	public final String toString() {
		// use in debugging to identify the object uniquely (more or less)
		return toStringStructural() + "#" + hashCode();
	}

}
