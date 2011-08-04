package org.semanticweb.elk.syntax;

/**
 * @author Frantisek Simancik
 *
 */
public abstract class ElkSubObjectPropertyExpression extends ElkObject {
	
	public abstract <O> O accept(ElkSubObjectPropertyExpressionVisitor<O> visitor);

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {

		return accept( (ElkSubObjectPropertyExpressionVisitor<O>) visitor);		
		
	}
}
