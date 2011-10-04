package org.semanticweb.elk.reasoner.indexing.views;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;

/**
 * Implements a view for instances of {@link IndexedClass}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped indexed object
 */
public class IndexedClassView<T extends IndexedClass> extends
		IndexedClassExpressionView<T> {

	public IndexedClassView(T representative) {
		super(representative);
	}

	@Override
	public int hashCode() {
		return combinedHashCode(IndexedClassView.class, this.representative
				.getElkClass().getIri());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof IndexedClassView<?>) {
			IndexedClassView<?> otherView = (IndexedClassView<?>) other;
			return this.representative.getIri().equals(
					otherView.representative.getIri());
		}
		return false;
	}
}