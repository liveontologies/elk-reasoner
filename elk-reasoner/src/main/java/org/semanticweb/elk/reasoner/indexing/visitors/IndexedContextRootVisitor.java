package org.semanticweb.elk.reasoner.indexing.visitors;

import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;

/**
 * Visitor pattern interface for instances of {@link IndexedContextRoot}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <O>
 *            the type of the output of this visitor
 */
public interface IndexedContextRootVisitor<O> extends
		IndexedClassExpressionVisitor<O>, IndexedFillerVisitor<O> {

	// combined visitor

}
