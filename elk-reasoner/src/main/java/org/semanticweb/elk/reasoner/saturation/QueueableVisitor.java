package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;

/**
 * @author Frantisek Simancik
 *
 */
public interface QueueableVisitor<O> {
	O visit(BackwardLink backwardLink);
	O visit(DecomposedClassExpression compositeClassExpression);
	O visit(ForwardLink forwardLink);
	O visit(Propagation propagation);
	O visit(IndexedClassExpression indexedClassExpression);
}
