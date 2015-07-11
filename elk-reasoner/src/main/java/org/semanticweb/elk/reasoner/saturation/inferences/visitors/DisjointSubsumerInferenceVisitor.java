package org.semanticweb.elk.reasoner.saturation.inferences.visitors;

import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerFromSubsumer;

public interface DisjointSubsumerInferenceVisitor<I, O> {

	public O visit(DisjointSubsumerFromSubsumer inference, I input);

}
