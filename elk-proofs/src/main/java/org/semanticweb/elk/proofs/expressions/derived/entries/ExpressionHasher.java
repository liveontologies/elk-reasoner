/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.derived.entries;

import org.semanticweb.elk.proofs.expressions.Expression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ExpressionHasher {

	public int hashCode(Expression expression);
}
