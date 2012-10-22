/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.saturation.SaturationState;

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
	public void deapply(SaturationState state, E element);
}
