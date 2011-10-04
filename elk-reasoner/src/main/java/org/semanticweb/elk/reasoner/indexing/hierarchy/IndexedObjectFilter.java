package org.semanticweb.elk.reasoner.indexing.hierarchy;

public interface IndexedObjectFilter {

	IndexedClassExpression filter(IndexedClassExpression ice);

	IndexedObjectProperty filter(IndexedObjectProperty iop);

	IndexedPropertyComposition filter(IndexedPropertyComposition ipc);

}
