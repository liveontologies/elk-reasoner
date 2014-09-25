/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.lemmas;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ElkComplexObjectSomeValuesFrom extends ElkComplexClassExpression {

	public ElkClassExpression getFiller();
	
	public ElkSubObjectPropertyExpression getPropertyChain();
}
