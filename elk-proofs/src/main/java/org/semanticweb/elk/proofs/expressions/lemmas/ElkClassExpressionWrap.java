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
public interface ElkClassExpressionWrap extends ElkComplexClassExpression {

	public ElkClassExpression getClassExpression();
}
