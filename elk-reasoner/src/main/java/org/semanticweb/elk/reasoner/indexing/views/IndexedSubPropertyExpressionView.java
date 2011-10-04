package org.semanticweb.elk.reasoner.indexing.views;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubPropertyExpression;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * The wrapper class to define custom equality and hash functions for indexed
 * object property expressions.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the typed of the wrapped indexed object property expression
 */
public abstract class IndexedSubPropertyExpressionView<T extends IndexedSubPropertyExpression> {

	/**
	 * The wrapped indexed class expression
	 */
	protected final T representative;

	IndexedSubPropertyExpressionView(T representative) {
		this.representative = representative;
	}

	static int combinedHashCode(Object... objects) {
		return HashGenerator.combinedHashCode(objects);
	}

	public abstract int hashCode();

	public abstract boolean equals(Object other);

}
