package org.semanticweb.elk.reasoner.indexing.visitors;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyComposition;

/**
 * Visitor pattern interface for instances of {@link IndexedPropertyComposition}
 * .
 * 
 * @author "Yevgeny Kazakov"
 */
public interface IndexedPropertyCompositionVisitor<O> {

	O visit(IndexedPropertyComposition element);

}
