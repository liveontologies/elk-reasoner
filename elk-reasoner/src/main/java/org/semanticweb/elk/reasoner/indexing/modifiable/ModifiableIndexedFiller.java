package org.semanticweb.elk.reasoner.indexing.modifiable;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedFiller;

/**
 * An {@link IndexedFiller} that can be modified as a result of updating the
 * {@link ModifiableOntologyIndex} where this object is stored.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public interface ModifiableIndexedFiller extends IndexedFiller {

	@Override
	public ModifiableIndexedObjectProperty getProperty();

	@Override
	public ModifiableIndexedClassExpression getFillerConcept();

}
