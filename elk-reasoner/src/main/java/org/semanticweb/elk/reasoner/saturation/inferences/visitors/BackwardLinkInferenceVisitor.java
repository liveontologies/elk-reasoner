package org.semanticweb.elk.reasoner.saturation.inferences.visitors;

import org.semanticweb.elk.reasoner.saturation.inferences.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedExistentialBackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.ReversedForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.SuperReversedForwardLink;

public interface BackwardLinkInferenceVisitor<I, O> {

	public O visit(ComposedBackwardLink inference, I input);

	public O visit(DecomposedExistentialBackwardLink inference, I input);

	public O visit(ReversedForwardLink inference, I input);

	public O visit(SuperReversedForwardLink inference, I input);

}
