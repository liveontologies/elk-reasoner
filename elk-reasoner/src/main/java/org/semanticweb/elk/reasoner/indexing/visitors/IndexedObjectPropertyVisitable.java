package org.semanticweb.elk.reasoner.indexing.visitors;

/**
 * Interface for things that can be visited by
 * {@link IndexedObjectPropertyVisitor}.
 * 
 * @author "Yevgeny Kazakov"
 */
public interface IndexedObjectPropertyVisitable {

	public <O> O accept(IndexedObjectPropertyVisitor<O> visitor);

}
