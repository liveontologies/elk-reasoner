package org.semanticweb.elk.reasoner.indexing.collections;

import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;

public interface SplitIndexedClassExpressionSet extends
		Set<IndexedClassExpression> {

	public Set<IndexedClassEntity> getClassEntitiesSetReadOnly();

	public Set<IndexedClassEntity> getClassEntitiesSetReadWrite();

	public Set<IndexedObjectIntersectionOf> getObjectIntersectionsSetReadOnly();

	public Set<IndexedObjectIntersectionOf> getObjectIntersectionsSetReadWrite();

	public Set<IndexedObjectSomeValuesFrom> getObjectExistentialsSetReadOnly();

	public Set<IndexedObjectSomeValuesFrom> getObjectExistentialsSetReadWrite();

	public Set<IndexedDataHasValue> getDataExistentialsSetReadOnly();

	public Set<IndexedDataHasValue> getDataExistentialsSetReadWrite();

}
