package org.semanticweb.elk.reasoner.indexing.views;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;

/**
 * Implements a view for instances of {@link IndexedObjectProperty}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped indexed object property
 */
public abstract class IndexedObjectPropertyView<T extends IndexedObjectProperty>
		extends IndexedSubPropertyExpressionView<T> {

	public IndexedObjectPropertyView(T representative) {
		super(representative);
	}

	@Override
	public int hashCode() {
		return combinedHashCode(IndexedClassView.class, this.representative
				.getElkObjectProperty().getIri());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof IndexedObjectPropertyView<?>) {
			IndexedObjectPropertyView<?> otherView = (IndexedObjectPropertyView<?>) other;
			return this.representative
					.getElkObjectProperty()
					.getIri()
					.equals(otherView.representative.getElkObjectProperty()
							.getIri());
		}
		return false;
	}
}
