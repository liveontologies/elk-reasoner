package org.semanticweb.elk.reasoner.indexing.modifiable;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedAxiom;

/**
 * An {@link IndexedAxiom} that can be modified as a result of updating the
 * {@link ModifiableOntologyIndex} where this object is stored.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public interface ModifiableIndexedAxiom extends ModifiableIndexedObject,
		IndexedAxiom {

	/**
	 * Adds this {@link ModifiableIndexedAxiom} once to the given
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param index
	 *            the {@link ModifiableOntologyIndex} to which this
	 *            {@link ModifiableIndexedAxiom} should be added
	 *            
	 * @return {@code true} if this operation was successful and {@code false}
	 *         otherwise; if {@code false} is returned, the index should not
	 *         logically change as the result of calling this method
	 */
	boolean addOccurrence(ModifiableOntologyIndex index);

	/**
	 * Removes this {@link ModifiableIndexedAxiom} once from the given
	 * {@link ModifiableOntologyIndex}
	 * 
	 * @param index
	 *            the {@link ModifiableOntologyIndex} from which this
	 *            {@link ModifiableIndexedAxiom} should be removed
	 *            
	 * @return {@code true} if this operation was successful and {@code false}
	 *         otherwise; if {@code false} is returned, the index should not
	 *         logically change as the result of calling this method
	 */
	boolean removeOccurrence(ModifiableOntologyIndex index);

}
