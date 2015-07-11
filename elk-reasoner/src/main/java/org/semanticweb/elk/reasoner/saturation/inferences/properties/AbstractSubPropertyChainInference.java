package org.semanticweb.elk.reasoner.saturation.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;

public abstract class AbstractSubPropertyChainInference<R extends IndexedPropertyChain, S extends IndexedPropertyChain>
		extends SubPropertyChainImpl<R, S> implements
		SubPropertyChainInference<R, S> {

	public AbstractSubPropertyChainInference(R subChain, S superChain) {
		super(subChain, superChain);
	}

	@Override
	public <I, O> O accept(ObjectPropertyInferenceVisitor<I, O> visitor, I input) {
		return accept((SubPropertyChainInferenceVisitor<I, O>) visitor, input);
	}

}
