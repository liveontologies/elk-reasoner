package org.semanticweb.elk.reasoner.saturation.inferences.properties;

public interface SubPropertyChainInferenceVisitor<I, O> {

	public O visit(LeftReflexiveSubPropertyChainInference inference, I input);

	public O visit(PropertyChainInitialization inference, I input);

	public O visit(RightReflexiveSubPropertyChainInference inference, I input);

	public O visit(ToldSubProperty inference, I input);

}
