package org.semanticweb.elk.reasoner.indexing.collections;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitorEx;
import org.semanticweb.elk.util.collections.ArrayHashSet;

public class IndexedClassExpressionSet extends
		AbstractSet<IndexedClassExpression> implements
		Set<IndexedClassExpression> {

	private final Set<IndexedClassExpression> set_;

	public IndexedClassExpressionSet(int size) {
		set_ = new ArrayHashSet<IndexedClassExpression>(size);
	}

	private static final IndexedClassExpressionVisitorEx<Set<IndexedClassExpression>, IndexedClassExpressionSet> SELECTOR_ = new IndexedClassExpressionVisitorEx<Set<IndexedClassExpression>, IndexedClassExpressionSet>() {

		@Override
		public Set<IndexedClassExpression> visit(IndexedClass element,
				IndexedClassExpressionSet parameter) {
			return parameter.set_;
		}

		@Override
		public Set<IndexedClassExpression> visit(IndexedIndividual element,
				IndexedClassExpressionSet parameter) {
			return parameter.set_;
		}

		@Override
		public Set<IndexedClassExpression> visit(
				IndexedObjectIntersectionOf element,
				IndexedClassExpressionSet parameter) {
			return parameter.set_;
		}

		@Override
		public Set<IndexedClassExpression> visit(
				IndexedObjectSomeValuesFrom element,
				IndexedClassExpressionSet parameter) {
			return parameter.set_;
		}

		@Override
		public Set<IndexedClassExpression> visit(IndexedDataHasValue element,
				IndexedClassExpressionSet parameter) {
			return parameter.set_;
		}

	};

	@Override
	public int size() {
		return set_.size();
	}

	@Override
	public boolean contains(Object o) {
//		return set_.contains(o);
		if (o instanceof IndexedClassExpression)
			return ((IndexedClassExpression) o).accept(SELECTOR_, this)
					.contains(o);
		return false;
	}

	@Override
	public Iterator<IndexedClassExpression> iterator() {
		return set_.iterator();
	}

	@Override
	public boolean add(IndexedClassExpression e) {
		return e.accept(SELECTOR_, this).add(e);
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof IndexedClassExpression)
			return ((IndexedClassExpression) o).accept(SELECTOR_, this).remove(
					o);
		return false;
	}

	@Override
	public void clear() {
		set_.clear();
	}

}
