/**
 * 
 */
package org.semanticweb.elk.proofs.expressions.lemmas;

/**
 * Class expressions not representable in the OWL syntax, e.g. existentials with a complex property chain.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ElkComplexClassExpression {

	public <I, O> O accept(ElkComplexClassExpressionVisitor<I, O> visitor, I input);
}
