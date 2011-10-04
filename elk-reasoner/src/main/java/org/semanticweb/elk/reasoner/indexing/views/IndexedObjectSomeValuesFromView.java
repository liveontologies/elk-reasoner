package org.semanticweb.elk.reasoner.indexing.views;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;

/**
 * Implements a view for instances of {@link IndexedObjectSomeValuesFrom}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped indexed class expression
 */
public class IndexedObjectSomeValuesFromView<T extends IndexedObjectSomeValuesFrom>
		extends IndexedClassExpressionView<T> {

	IndexedObjectSomeValuesFromView(T representative) {
		super(representative);
	}

	@Override
	public int hashCode() {
		return combinedHashCode(IndexedObjectSomeValuesFromView.class,
				this.representative.getFiller(), this.representative.getClass());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof IndexedObjectSomeValuesFromView<?>) {
			IndexedObjectSomeValuesFromView<?> otherView = (IndexedObjectSomeValuesFromView<?>) other;
			return this.representative.getFiller().equals(
					otherView.representative.getFiller())
					&& this.representative.getClass().equals(
							otherView.representative.getClass());
		}
		return false;
	}
}
