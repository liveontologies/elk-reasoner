package org.semanticweb.elk.reasoner.indexing.visitors;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;

/**
 * Visitor pattern interface for instances of {@link IndexedObjectProperty}.
 * 
 * @author "Yevgeny Kazakov"
 */
public interface IndexedObjectPropertyVisitor<O> {

	O visit(IndexedObjectProperty element);

}
