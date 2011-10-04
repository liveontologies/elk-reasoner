package org.semanticweb.elk.reasoner.indexing.visitors;

/**
 * Interface for things that can be visited by
 * {@link IndexedSubPropertyExpressionVisitor}.
 * 
 * @author "Yevgeny Kazakov"
 */
public interface IndexedSubPropertyExpressionVisitable {

	public <O> O accept(IndexedSubPropertyExpressionVisitor<O> visitor);

}
