package org.semanticweb.elk.reasoner.indexing.collections;

import java.util.Map;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;

public interface SplitIndexedClassExpressionMap<V> extends
		Map<IndexedClassExpression, V> {

	public Map<IndexedClassEntity, V> getClassEntitiesMapReadOnly();

	public Map<IndexedClassEntity, V> getClassEntitiesMapReadWrite();

	public Map<IndexedObjectIntersectionOf, V> getObjectIntersectionsMapReadOnly();

	public Map<IndexedObjectIntersectionOf, V> getObjectIntersectionsMapReadWrite();

	public Map<IndexedObjectSomeValuesFrom, V> getObjectExistentialsMapReadOnly();

	public Map<IndexedObjectSomeValuesFrom, V> getObjectExistentialsMapReadWrite();

	public Map<IndexedDataHasValue, V> getDataExistentialsMapReadOnly();

	public Map<IndexedDataHasValue, V> getDataExistentialsMapReadWrite();

}
