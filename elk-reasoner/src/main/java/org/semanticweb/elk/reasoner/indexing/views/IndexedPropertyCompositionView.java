package org.semanticweb.elk.reasoner.indexing.views;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyComposition;

/**
 * Implements a view for instances of {@link IndexedObjectProperty}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped indexed object property composition
 */
public class IndexedPropertyCompositionView<T extends IndexedPropertyComposition>
		extends IndexedSubPropertyExpressionView<T> {

	IndexedPropertyCompositionView(T representative) {
		super(representative);
	}

	@Override
	public int hashCode() {
		return combinedHashCode(IndexedPropertyCompositionView.class,
				this.representative.getLeftProperty(),
				this.representative.getRightProperty(),
				this.representative.getSuperProperty());
	}

	@Override
	public boolean equals(Object other) {
		IndexedPropertyCompositionView<?> otherView = (IndexedPropertyCompositionView<?>) other;
		return this.representative.getLeftProperty().equals(
				otherView.representative.getLeftProperty())
				&& this.representative.getRightProperty().equals(
						otherView.representative.getRightProperty())
				&& this.representative.getSuperProperty().equals(
						otherView.representative.getSuperProperty());
	}
}
