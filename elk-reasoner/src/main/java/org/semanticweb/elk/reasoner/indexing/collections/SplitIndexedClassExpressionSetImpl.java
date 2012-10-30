package org.semanticweb.elk.reasoner.indexing.collections;

import java.util.Collections;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.util.collections.ArrayHashSet;

public class SplitIndexedClassExpressionSetImpl extends
		AbstractSplitIndexedClassExpressionSet {

	private Set<IndexedClassEntity> classEntities_;
	private Set<IndexedObjectIntersectionOf> objectIntersections_;
	private Set<IndexedObjectSomeValuesFrom> objectExistentials_;
	private Set<IndexedDataHasValue> dataExistentials_;

	public static final <T> Set<T> emptyIfNull(Set<T> set) {
		if (set == null)
			return Collections.emptySet();
		return set;
	}

	@Override
	public Set<IndexedClassEntity> getClassEntitiesSetReadOnly() {
		return emptyIfNull(classEntities_);
	}

	@Override
	public Set<IndexedClassEntity> getClassEntitiesSetReadWrite() {
		if (classEntities_ == null)
			return classEntities_ = new ArrayHashSet<IndexedClassEntity>(5);
		return classEntities_;
	}

	@Override
	public Set<IndexedObjectIntersectionOf> getObjectIntersectionsSetReadOnly() {
		return emptyIfNull(objectIntersections_);
	}

	@Override
	public Set<IndexedObjectIntersectionOf> getObjectIntersectionsSetReadWrite() {
		if (objectIntersections_ == null)
			return objectIntersections_ = new ArrayHashSet<IndexedObjectIntersectionOf>(
					5);
		return objectIntersections_;
	}

	public Set<IndexedObjectSomeValuesFrom> getObjectExistentialsSetReadOnly() {
		return emptyIfNull(objectExistentials_);
	}

	@Override
	public Set<IndexedObjectSomeValuesFrom> getObjectExistentialsSetReadWrite() {
		if (objectExistentials_ == null)
			return objectExistentials_ = new ArrayHashSet<IndexedObjectSomeValuesFrom>(
					5);
		return objectExistentials_;
	}

	public Set<IndexedDataHasValue> getDataExistentialsSetReadOnly() {
		return emptyIfNull(dataExistentials_);
	}

	@Override
	public Set<IndexedDataHasValue> getDataExistentialsSetReadWrite() {
		if (dataExistentials_ == null)
			return dataExistentials_ = new ArrayHashSet<IndexedDataHasValue>(5);
		return dataExistentials_;
	}

}
