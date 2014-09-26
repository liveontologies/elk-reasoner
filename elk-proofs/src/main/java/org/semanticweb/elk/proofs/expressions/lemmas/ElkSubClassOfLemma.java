/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.lemmas;

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ElkSubClassOfLemma extends ElkLemma {

	public ElkClassExpression getSubClass();
	
	public ElkComplexClassExpression getSuperClass();
}
