package org.semanticweb.elk.reasoner.saturation.properties.inferences;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;

public class SubPropertyChainInferenceConclusionVisitor<O>
		implements
			SubPropertyChainInference.Visitor<O> {

	private final SubPropertyChain.Visitor<O> conclusionVisitor_;

	public SubPropertyChainInferenceConclusionVisitor(
			SubPropertyChain.Visitor<O> conclusionVisitor) {
		this.conclusionVisitor_ = conclusionVisitor;
	}

	@Override
	public O visit(SubPropertyChainExpandedSubObjectPropertyOf inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

	@Override
	public O visit(SubPropertyChainTautology inference) {
		return conclusionVisitor_.visit(inference.getConclusion());
	}

}
