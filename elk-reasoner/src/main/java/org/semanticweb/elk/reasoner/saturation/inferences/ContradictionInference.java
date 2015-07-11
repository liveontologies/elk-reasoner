package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Contradiction;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ContradictionInferenceVisitor;

public interface ContradictionInference extends Contradiction, ClassInference {

	public <I, O> O accept(ContradictionInferenceVisitor<I, O> visitor, I input);

}
