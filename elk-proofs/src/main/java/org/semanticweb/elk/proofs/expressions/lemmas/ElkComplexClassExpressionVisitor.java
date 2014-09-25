/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.lemmas;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ElkComplexClassExpressionVisitor<I, O> {

	public O visit(ElkComplexObjectSomeValuesFrom ce, I input);
	
	public O visit(ElkClassExpressionWrap ce, I input);
}
