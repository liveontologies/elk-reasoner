/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.lemmas;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ElkSubClassOfLemma extends ElkLemma {

	public ElkComplexClassExpression getSubClass();
	
	public ElkComplexClassExpression getSuperClass();
}
