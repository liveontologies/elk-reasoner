package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Propagation;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.PropagationInferenceVisitor;

public interface PropagationInference extends Propagation, ClassInference {

	public <I, O> O accept(PropagationInferenceVisitor<I, O> visitor, I input);

}
