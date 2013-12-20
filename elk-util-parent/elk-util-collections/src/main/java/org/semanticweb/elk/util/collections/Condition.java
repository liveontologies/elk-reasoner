package org.semanticweb.elk.util.collections;

/**
 * Boolean conditions over some type.
 * 
 * @param <T>
 *            the type of elements which can be used with this condition
 * 
 */
public interface Condition<T> {
	/**
	 * Checks if the condition holds for an element
	 * 
	 * @param element
	 *            the element for which to check the condition
	 * @return {@code true} if the condition holds for the element and
	 *         otherwise {@code false}
	 */
	public boolean holds(T element);
}