package org.semanticweb.elk.reasoner.indexing.visitors;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

/**
 * Visitor pattern interface for instances of {@link IndexedClassExpression}.
 * 
 * @author "Yevgeny Kazakov"
 */
public interface IndexedSubPropertyExpressionVisitor<O> extends
		IndexedObjectPropertyVisitor<O>, IndexedPropertyCompositionVisitor<O> {

}
