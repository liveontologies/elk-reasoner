/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

import org.semanticweb.elk.proofs.expressions.derived.DerivedAxiomExpression;
import org.semanticweb.elk.proofs.expressions.derived.DerivedLemmaExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ExpressionVisitor<I, O> {

	public O visit(DerivedAxiomExpression expr, I input);
	
	public O visit(DerivedLemmaExpression expr, I input);
}
