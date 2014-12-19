package org.semanticweb.elk.reasoner.indexing.visitors;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;

public class NoOpIndexedPropertyChainVisitor<O> implements
		IndexedPropertyChainVisitor<O> {

	@SuppressWarnings("unused")
	protected O defaultVisit(IndexedPropertyChain element) {
		return null;
	}

	@Override
	public O visit(IndexedObjectProperty element) {
		return defaultVisit(element);
	}

	@Override
	public O visit(IndexedBinaryPropertyChain element) {
		return defaultVisit(element);
	}

}
