/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.lemmas;

import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ElkSubPropertyChainOfLemma extends ElkLemma {

	public ElkSubObjectPropertyExpression getSubPropertyChain();
	
	public ElkSubObjectPropertyExpression getSuperPropertyChain();
}
