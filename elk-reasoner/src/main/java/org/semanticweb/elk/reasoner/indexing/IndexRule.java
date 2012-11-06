/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing;


/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface IndexRule<T, R> {

	/**
	 * Applying the rule to an indexed element
	 * 
	 * @param element
	 *            the element to which the rule is applied
	 */
	public R apply(T element);
	
	public R deapply(T element);

}
