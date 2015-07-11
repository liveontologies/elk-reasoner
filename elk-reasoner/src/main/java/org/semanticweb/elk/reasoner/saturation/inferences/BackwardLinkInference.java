package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.BackwardLinkInferenceVisitor;

public interface BackwardLinkInference extends BackwardLink, ClassInference {

	public <I, O> O accept(BackwardLinkInferenceVisitor<I, O> visitor, I input);

}
