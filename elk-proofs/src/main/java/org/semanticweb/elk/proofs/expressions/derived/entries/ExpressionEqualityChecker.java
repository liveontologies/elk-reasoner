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
public interface ExpressionEqualityChecker {

	public boolean equal(Expression first, Expression second);
}
