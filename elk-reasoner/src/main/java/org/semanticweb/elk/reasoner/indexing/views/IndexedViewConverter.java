package org.semanticweb.elk.reasoner.indexing.views;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyComposition;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedSubPropertyExpressionVisitor;

public class IndexedViewConverter
		implements
		IndexedClassExpressionVisitor<IndexedClassExpressionView<? extends IndexedClassExpression>>,
		IndexedSubPropertyExpressionVisitor<IndexedSubPropertyExpressionView<? extends IndexedSubPropertyExpression>> {

	private IndexedViewConverter() {
	}

	private static IndexedViewConverter instance_ = new IndexedViewConverter();

	public static IndexedViewConverter getInstance() {
		return instance_;
	}

	public IndexedClassExpressionView<IndexedClass> visit(IndexedClass element) {
		return new IndexedClassView<IndexedClass>(element);
	}

	public IndexedClassExpressionView<IndexedObjectIntersectionOf> visit(
			IndexedObjectIntersectionOf element) {
		return new IndexedObjectIntersectionOfView<IndexedObjectIntersectionOf>(
				element);
	}

	public IndexedClassExpressionView<IndexedObjectSomeValuesFrom> visit(
			IndexedObjectSomeValuesFrom element) {
		return new IndexedObjectSomeValuesFromView<IndexedObjectSomeValuesFrom>(
				element);
	}

	public IndexedSubPropertyExpressionView<? extends IndexedSubPropertyExpression> visit(
			IndexedObjectProperty element) {
		return new IndexedObjectPropertyView<IndexedObjectProperty>(element) {
		};
	}

	public IndexedSubPropertyExpressionView<? extends IndexedSubPropertyExpression> visit(
			IndexedPropertyComposition element) {
		return new IndexedPropertyCompositionView<IndexedPropertyComposition>(
				element);
	}
}
