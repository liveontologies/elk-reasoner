package org.semanticweb.elk.reasoner.indexing;

import org.semanticweb.elk.util.Triple;

/**
 * Binary role inclusion axiom.
 * 
 * @author Frantisek
 *
 */
public class ComplexRoleInclusion extends 
		Triple<IndexedObjectProperty, IndexedPropertyExpression, IndexedPropertyExpression> {

	protected boolean isSafe;
	
	public ComplexRoleInclusion(IndexedObjectProperty leftSubProperty,
			IndexedPropertyExpression rightSubProperty,
			IndexedPropertyExpression superProperty) {
		super(leftSubProperty, rightSubProperty, superProperty);
	}
	
	public IndexedObjectProperty getLeftSubProperty() {
		return first;
	}
	
	public IndexedPropertyExpression getRightSubProperty() {
		return second;
	}
	
	public IndexedPropertyExpression getSuperProperty() {
		return third;
	}

	public boolean isSafe() {
		return isSafe;
	}

	public void setSafe(boolean isSafe) {
		this.isSafe = isSafe;
	}
}