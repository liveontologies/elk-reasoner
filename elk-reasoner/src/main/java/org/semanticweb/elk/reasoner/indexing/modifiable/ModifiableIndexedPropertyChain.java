package org.semanticweb.elk.reasoner.indexing.modifiable;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;

public interface ModifiableIndexedPropertyChain extends
		ModifiableIndexedObject, IndexedPropertyChain,
		Comparable<ModifiableIndexedPropertyChain> {

	/**
	 * Adds the given {@link IndexedObjectProperty} as a super-role of this
	 * {@link IndexedPropertyChain}
	 * 
	 * @param superObjectProperty
	 *            the {@link IndexedObjectProperty} to be added
	 * @return {@code true} if the operation is successful or {@code false}
	 *         otherwise; if {@code false} is returned, this
	 *         {@link IndexedPropertyChain} does not change
	 */
	boolean addToldSuperObjectProperty(IndexedObjectProperty superObjectProperty);

	/**
	 * Removes the given {@link IndexedObjectProperty} from super-roles of this
	 * {@link IndexedPropertyChain}
	 * 
	 * @param superObjectProperty
	 *            the {@link IndexedObjectProperty} to be removed
	 * @return {@code true} if the operation is successful or {@code false}
	 *         otherwise; if {@code false} is returned, this
	 *         {@link IndexedPropertyChain} does not change
	 */
	boolean removeToldSuperObjectProperty(
			IndexedObjectProperty superObjectProperty);

	/**
	 * Adds the given {@link IndexedBinaryPropertyChain} to the list of
	 * {@link IndexedBinaryPropertyChain} that contains this
	 * {@link IndexedPropertyChain} in the right-hand-side
	 * 
	 * @param chain
	 *            the {@link IndexedBinaryPropertyChain} to be added
	 * @return {@code true} if the operation is successful or {@code false}
	 *         otherwise; if {@code false} is returned, this
	 *         {@link IndexedPropertyChain} does not change
	 */
	boolean addRightChain(IndexedBinaryPropertyChain chain);

	/**
	 * Adds the given {@link IndexedBinaryPropertyChain} from the list of
	 * {@link IndexedBinaryPropertyChain} that contain this
	 * {@link IndexedPropertyChain} in the right-hand-side
	 * 
	 * @param chain
	 *            the {@link IndexedBinaryPropertyChain} to be removed
	 * @return {@code true} if the operation is successful or {@code false}
	 *         otherwise; if {@code false} is returned, this
	 *         {@link IndexedPropertyChain} does not change
	 */
	boolean removeRightChain(IndexedBinaryPropertyChain chain);

}
