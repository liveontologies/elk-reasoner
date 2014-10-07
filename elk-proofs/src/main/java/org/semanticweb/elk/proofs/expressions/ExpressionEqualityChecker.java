/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;


/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ExpressionEqualityChecker {

	public boolean equal(Expression first, Expression second);
}
