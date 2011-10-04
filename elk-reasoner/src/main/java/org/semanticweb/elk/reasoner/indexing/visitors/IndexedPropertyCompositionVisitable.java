package org.semanticweb.elk.reasoner.indexing.visitors;

/**
 * Interface for things that can be visited by
 * {@link IndexedPropertyCompositionVisitor}.
 * 
 * @author "Yevgeny Kazakov"
 */
public interface IndexedPropertyCompositionVisitable {

	public <O> O accept(IndexedPropertyCompositionVisitor<O> visitor);

}
