package org.semanticweb.elk.reasoner.saturation.inferences.visitors;

import org.semanticweb.elk.reasoner.saturation.inferences.ComposedForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedExistentialForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedReflexiveForwardLink;

public interface ForwardLinkInferenceVisitor<I, O> {

	public O visit(ComposedForwardLink inference, I input);

	public O visit(DecomposedExistentialForwardLink inference, I input);

	public O visit(DecomposedReflexiveForwardLink inference, I input);

}
