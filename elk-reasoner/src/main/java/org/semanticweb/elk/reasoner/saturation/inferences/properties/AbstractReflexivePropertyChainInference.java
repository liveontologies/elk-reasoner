package org.semanticweb.elk.reasoner.saturation.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;

public abstract class AbstractReflexivePropertyChainInference<P extends IndexedPropertyChain>
		extends ReflexivePropertyChainImpl<P> implements
		ReflexivePropertyChainInference<P> {

	public AbstractReflexivePropertyChainInference(P chain) {
		super(chain);
	}

	@Override
	public <I, O> O accept(ObjectPropertyInferenceVisitor<I, O> visitor, I input) {
		return accept((ReflexivePropertyChainInferenceVisitor<I, O>) visitor,
				input);
	}

}
