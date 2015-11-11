package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.DisjointSubsumerInferenceVisitor;

public interface DisjointSubsumerInference extends DisjointSubsumer,
		ClassInference {

	public <I, O> O accept(DisjointSubsumerInferenceVisitor<I, O> visitor,
			I input);

}
