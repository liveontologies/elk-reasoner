/**
 * 
 */
package org.semanticweb.elk.proofs.expressions;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ExpressionVisitor<I, O> {

	public O visit(AxiomExpression expr, I input);
	
	public O visit(LemmaExpression expr, I input);
}
