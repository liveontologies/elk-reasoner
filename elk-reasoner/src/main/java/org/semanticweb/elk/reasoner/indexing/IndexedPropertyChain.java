package org.semanticweb.elk.reasoner.indexing;

import org.semanticweb.elk.syntax.ElkObjectPropertyChain;

/**
 * @author Frantisek Simancik
 *
 */
public class IndexedPropertyChain extends IndexedPropertyExpression {
	
	protected final IndexedObjectProperty leftComponent;
	protected final IndexedPropertyExpression rightComponent;

	protected IndexedPropertyChain(IndexedObjectProperty leftComponent,
			IndexedPropertyExpression rightComponent, ElkObjectPropertyChain elkObjectPropertyChain) {
		super(elkObjectPropertyChain);
		this.leftComponent = leftComponent;
		this.rightComponent = rightComponent;
	}
	
	public IndexedObjectProperty getLeftComponent() {
		return leftComponent;
	}

	public IndexedPropertyExpression getRightComponent() {
		return rightComponent;
	}

	@Override
	public <O> O accept(IndexedPropertyExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
