package org.semanticweb.elk.reasoner.indexing.visitors;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedFiller;

/**
 * Visitor pattern interface for instances of {@link IndexedFiller}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <O>
 *            the type of the output of this visitor
 */
public interface IndexedFillerVisitor<O> {

	O visit(IndexedFiller element);

}
