package org.semanticweb.elk.reasoner.saturation.inferences.visitors;

import org.semanticweb.elk.reasoner.saturation.inferences.GeneratedPropagation;

public interface PropagationInferenceVisitor<I, O> {

	public O visit(GeneratedPropagation inference, I input);

}
