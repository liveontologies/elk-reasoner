package org.semanticweb.elk.util.collections;

/**
 * Objects that can be subsumed by other objects.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the subsumed objects
 */
public interface Subsumable<T> {

	/**
	 * @param o
	 *            the object to be tested on the subsumption
	 * @return {@code true} if this object is subsumed by the given object
	 */
	public boolean isSubsumedBy(T o);

}
