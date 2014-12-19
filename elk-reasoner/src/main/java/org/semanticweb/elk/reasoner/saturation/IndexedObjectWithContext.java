package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObject;

public interface IndexedObjectWithContext extends IndexedObject {

	/**
	 * @return The corresponding context or {@code null} if none was assigned.
	 */
	ExtendedContext getContext();

	/**
	 * Resets the corresponding context to {@code null}.
	 */
	void resetContext();

	/**
	 * Assign the given {@link ExtendedContext} to this {@link IndexedObject} if
	 * none was yet assigned.
	 * 
	 * @param context
	 *            the {@link ExtendedContext} which will be assigned to this
	 *            {@link IndexedObject}
	 * 
	 * @return the previously assigned {@link ExtendedContext} or {@code null}
	 *         if none was assigned (in which case the new
	 *         {@link ExtendedContext} will be assigned)
	 */
	ExtendedContext setContextIfAbsent(ExtendedContext context);

}
