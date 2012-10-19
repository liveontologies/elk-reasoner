/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ReversibleRule<E> extends Rule<E> {

	/**
	 * 
	 * @param ruleEngine
	 * @param element
	 */
	public void deapply(RuleEngine ruleEngine, E element);
}
