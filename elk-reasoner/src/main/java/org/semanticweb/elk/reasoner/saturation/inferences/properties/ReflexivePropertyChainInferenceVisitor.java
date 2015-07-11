package org.semanticweb.elk.reasoner.saturation.inferences.properties;

public interface ReflexivePropertyChainInferenceVisitor<I, O> {

	public O visit(ComposedReflexivePropertyChain inference, I input);

	public O visit(ReflexiveToldSubObjectProperty inference, I input);

	public O visit(ToldReflexiveProperty inference, I input);

}
