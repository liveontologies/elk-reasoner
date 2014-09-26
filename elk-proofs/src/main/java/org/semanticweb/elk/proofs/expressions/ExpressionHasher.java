/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ExpressionHasher {

	public int hashCode(Expression expression);
}
