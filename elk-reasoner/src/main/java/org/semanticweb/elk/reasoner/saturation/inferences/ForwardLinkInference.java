package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ForwardLinkInferenceVisitor;

public interface ForwardLinkInference extends ForwardLink, ClassInference {

	public <I, O> O accept(ForwardLinkInferenceVisitor<I, O> visitor, I input);

}
