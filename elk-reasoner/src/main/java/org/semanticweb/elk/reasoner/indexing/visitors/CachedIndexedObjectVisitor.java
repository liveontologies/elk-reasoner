package org.semanticweb.elk.reasoner.indexing.visitors;

public interface CachedIndexedObjectVisitor<O> extends
		IndexedClassExpressionVisitor<O>, IndexedPropertyChainVisitor<O>,
		IndexedDisjointnessAxiomVisitor<O> {

	// combined visitor

}
