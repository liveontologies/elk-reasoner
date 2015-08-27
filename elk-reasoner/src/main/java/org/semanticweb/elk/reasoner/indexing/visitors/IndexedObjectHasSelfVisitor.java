package org.semanticweb.elk.reasoner.indexing.visitors;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectHasSelf;

/**
 * Visitor pattern interface for instances of {@link IndexedObjectHasSelf}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <O>
 *            the type of the output of this visitor
 */
public interface IndexedObjectHasSelfVisitor<O> {

	O visit(IndexedObjectHasSelf element);

}
