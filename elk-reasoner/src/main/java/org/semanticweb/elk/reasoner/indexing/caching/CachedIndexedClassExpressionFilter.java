package org.semanticweb.elk.reasoner.indexing.caching;

public interface CachedIndexedClassExpressionFilter {

	public CachedIndexedClass filter(CachedIndexedClass element);

	public CachedIndexedIndividual filter(CachedIndexedIndividual element);

	public CachedIndexedObjectComplementOf filter(
			CachedIndexedObjectComplementOf element);

	public CachedIndexedObjectIntersectionOf filter(
			CachedIndexedObjectIntersectionOf element);

	public CachedIndexedObjectSomeValuesFrom filter(
			CachedIndexedObjectSomeValuesFrom element);

	public CachedIndexedObjectUnionOf filter(CachedIndexedObjectUnionOf element);

	public CachedIndexedDataHasValue filter(CachedIndexedDataHasValue element);
}
