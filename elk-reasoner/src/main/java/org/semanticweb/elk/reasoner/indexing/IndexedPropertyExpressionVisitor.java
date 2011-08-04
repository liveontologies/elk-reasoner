package org.semanticweb.elk.reasoner.indexing;

/**
 * @author Frantisek Simancik
 *
 */
public interface IndexedPropertyExpressionVisitor<O> {
	O visit(IndexedObjectProperty indexedObjectProperty);
	O visit(IndexedPropertyChain indexedPropertyChain);
}
