package org.semanticweb.elk.reasoner.indexing.modifiable;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObject;

public interface ModifiableIndexedObject extends IndexedObject {

	boolean updateOccurrenceNumbers(ModifiableOntologyIndex index,
			int increment, int positiveIncrement, int negativeIncrement);

}
