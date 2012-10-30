package org.semanticweb.elk.reasoner.indexing.collections;

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.util.collections.LazySetIntersection;

public class LazySplitIndexedClassExpressionSetIntersection extends
		AbstractSplitIndexedClassExpressionSet {

	private final SplitIndexedClassExpressionSet firstSet_;
	private final SplitIndexedClassExpressionSet secondSet_;

	public LazySplitIndexedClassExpressionSetIntersection(
			SplitIndexedClassExpressionSet firstSet,
			SplitIndexedClassExpressionSet secondSet) {
		this.firstSet_ = firstSet;
		this.secondSet_ = secondSet;
	}

	@Override
	public Set<IndexedClassEntity> getClassEntitiesSetReadOnly() {
		return new LazySetIntersection<IndexedClassEntity>(
				firstSet_.getClassEntitiesSetReadOnly(),
				secondSet_.getClassEntitiesSetReadOnly());
	}

	@Override
	public Set<IndexedClassEntity> getClassEntitiesSetReadWrite() {
		return getClassEntitiesSetReadOnly();
	}

	@Override
	public Set<IndexedObjectIntersectionOf> getObjectIntersectionsSetReadOnly() {
		return new LazySetIntersection<IndexedObjectIntersectionOf>(
				firstSet_.getObjectIntersectionsSetReadOnly(),
				secondSet_.getObjectIntersectionsSetReadOnly());
	}

	@Override
	public Set<IndexedObjectIntersectionOf> getObjectIntersectionsSetReadWrite() {
		return getObjectIntersectionsSetReadOnly();
	}

	@Override
	public Set<IndexedObjectSomeValuesFrom> getObjectExistentialsSetReadOnly() {
		return new LazySetIntersection<IndexedObjectSomeValuesFrom>(
				firstSet_.getObjectExistentialsSetReadOnly(),
				secondSet_.getObjectExistentialsSetReadOnly());
	}

	@Override
	public Set<IndexedObjectSomeValuesFrom> getObjectExistentialsSetReadWrite() {
		return getObjectExistentialsSetReadOnly();
	}

	@Override
	public Set<IndexedDataHasValue> getDataExistentialsSetReadOnly() {
		return new LazySetIntersection<IndexedDataHasValue>(
				firstSet_.getDataExistentialsSetReadOnly(),
				secondSet_.getDataExistentialsSetReadOnly());
	}

	@Override
	public Set<IndexedDataHasValue> getDataExistentialsSetReadWrite() {
		return getDataExistentialsSetReadOnly();
	}

}
