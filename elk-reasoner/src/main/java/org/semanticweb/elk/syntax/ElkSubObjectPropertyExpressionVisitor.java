package org.semanticweb.elk.syntax;

/**
 * Visitor pattern interface for instances of {@link ElkSubObjectPropertyExpression}.
 * @author Frantisek
 *
 */
public interface ElkSubObjectPropertyExpressionVisitor<O> 
		extends ElkObjectPropertyExpressionVisitor<O> {
	
	O visit(ElkObjectPropertyChain elkObjectPropertyChain);

}