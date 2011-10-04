package org.semanticweb.elk.reasoner.indexing.views;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * The wrapper class to define custom equality and hash functions for indexed
 * class expressions.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the typed of the wrapped indexed class expression
 */
public abstract class IndexedClassExpressionView<T extends IndexedClassExpression> {

	/**
	 * The wrapped indexed class expression
	 */
	protected final T representative;

	IndexedClassExpressionView(T representative) {
		this.representative = representative;
	}

	static int combinedHashCode(Object... objects) {
		return HashGenerator.combinedHashCode(objects);
	}

	public abstract int hashCode();

	public abstract boolean equals(Object other);

}
