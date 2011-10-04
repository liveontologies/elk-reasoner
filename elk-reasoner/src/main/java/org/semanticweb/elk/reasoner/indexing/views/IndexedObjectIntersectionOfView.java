package org.semanticweb.elk.reasoner.indexing.views;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;

/**
 * Implements a view for instances of {@link IndexedClassIndexedClass}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped indexed class expression
 */
public class IndexedObjectIntersectionOfView<T extends IndexedObjectIntersectionOf>
		extends IndexedClassExpressionView<T> {

	IndexedObjectIntersectionOfView(T representative) {
		super(representative);
	}

	@Override
	public int hashCode() {
		return combinedHashCode(IndexedObjectIntersectionOfView.class,
				this.representative.getFirstConjunct(),
				this.representative.getSecondConjunct());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof IndexedObjectIntersectionOfView<?>) {
			IndexedObjectIntersectionOfView<?> otherView = (IndexedObjectIntersectionOfView<?>) other;
			return this.representative.getFirstConjunct().equals(
					otherView.representative.getFirstConjunct())
					&& this.representative.getSecondConjunct().equals(
							otherView.representative.getSecondConjunct());
		}
		return false;
	}

}
