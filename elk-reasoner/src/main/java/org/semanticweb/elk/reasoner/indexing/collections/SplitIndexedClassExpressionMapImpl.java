package org.semanticweb.elk.reasoner.indexing.collections;

import java.util.Collections;
import java.util.Map;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.util.collections.ArrayHashMap;

public class SplitIndexedClassExpressionMapImpl<V> extends
		AbstractSplitIndexedClassExpressionMap<V> {

	private Map<IndexedClassEntity, V> classEntities_;
	private Map<IndexedObjectIntersectionOf, V> objectIntersections_;
	private Map<IndexedObjectSomeValuesFrom, V> objectExistentials_;
	private Map<IndexedDataHasValue, V> dataExistentials_;

	public final <K> Map<K, V> emptyIfNull(Map<K, V> map) {
		if (map == null)
			return Collections.emptyMap();
		return map;
	}

	@Override
	public Map<IndexedClassEntity, V> getClassEntitiesMapReadOnly() {
		return emptyIfNull(classEntities_);
	}

	@Override
	public Map<IndexedClassEntity, V> getClassEntitiesMapReadWrite() {
		if (classEntities_ == null)
			return classEntities_ = new ArrayHashMap<IndexedClassEntity, V>(5);
		return classEntities_;
	}

	@Override
	public Map<IndexedObjectIntersectionOf, V> getObjectIntersectionsMapReadOnly() {
		return emptyIfNull(objectIntersections_);
	}

	@Override
	public Map<IndexedObjectIntersectionOf, V> getObjectIntersectionsMapReadWrite() {
		if (objectIntersections_ == null)
			return objectIntersections_ = new ArrayHashMap<IndexedObjectIntersectionOf, V>(
					5);
		return objectIntersections_;
	}

	public Map<IndexedObjectSomeValuesFrom, V> getObjectExistentialsMapReadOnly() {
		return emptyIfNull(objectExistentials_);
	}

	@Override
	public Map<IndexedObjectSomeValuesFrom, V> getObjectExistentialsMapReadWrite() {
		if (objectExistentials_ == null)
			return objectExistentials_ = new ArrayHashMap<IndexedObjectSomeValuesFrom, V>(
					5);
		return objectExistentials_;
	}

	public Map<IndexedDataHasValue, V> getDataExistentialsMapReadOnly() {
		return emptyIfNull(dataExistentials_);
	}

	@Override
	public Map<IndexedDataHasValue, V> getDataExistentialsMapReadWrite() {
		if (dataExistentials_ == null)
			return dataExistentials_ = new ArrayHashMap<IndexedDataHasValue, V>(
					5);
		return dataExistentials_;
	}

}
