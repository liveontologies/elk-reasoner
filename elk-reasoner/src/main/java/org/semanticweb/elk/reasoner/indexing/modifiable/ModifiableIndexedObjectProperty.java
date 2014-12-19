package org.semanticweb.elk.reasoner.indexing.modifiable;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;

public interface ModifiableIndexedObjectProperty extends
		ModifiableIndexedPropertyChain, ModifiableIndexedEntity,
		IndexedObjectProperty {

	/**
	 * Adds the given {@link IndexedBinaryPropertyChain} to the list of
	 * {@link IndexedBinaryPropertyChain} that contains this
	 * {@link IndexedPropertyChain} in the left-hand-side
	 * 
	 * @param chain
	 *            the {@link IndexedBinaryPropertyChain} to be added
	 * @return {@code true} if the operation is successful or {@code false}
	 *         otherwise; if {@code false} is returned, this
	 *         {@link IndexedObjectProperty} does not change
	 */
	boolean addLeftChain(IndexedBinaryPropertyChain chain);

	/**
	 * Adds the given {@link IndexedBinaryPropertyChain} from the list of
	 * {@link IndexedBinaryPropertyChain} that contain this
	 * {@link IndexedPropertyChain} in the left-hand-side
	 * 
	 * @param chain
	 *            the {@link IndexedBinaryPropertyChain} to be removed
	 * @return {@code true} if successfully removed
	 */
	boolean removeLeftChain(IndexedBinaryPropertyChain chain);

	/**
	 * Adds the given {@link IndexedPropertyChain} as a sub-role of this
	 * {@link IndexedObjectProperty}
	 * 
	 * @param subObjectProperty
	 *            the {@link IndexedPropertyChain} to be added
	 * @return {@code true} if the operation is successful or {@code false}
	 *         otherwise; if {@code false} is returned, this
	 *         {@link IndexedObjectProperty} does not change
	 */
	boolean addToldSubPropertyChain(IndexedPropertyChain subObjectProperty);

	/**
	 * Removes the given {@link IndexedPropertyChain} from sub-roles of this
	 * {@link IndexedObjectProperty}
	 * 
	 * @param subObjectProperty
	 *            the {@link IndexedPropertyChain} to be removed
	 * @return {@code true} if the operation is successful or {@code false}
	 *         otherwise; if {@code false} is returned, this
	 *         {@link IndexedObjectProperty} does not change
	 */
	boolean removeToldSubPropertyChain(IndexedPropertyChain subObjectProperty);

	boolean updateReflexiveOccurrenceNumber(int increment);

}
