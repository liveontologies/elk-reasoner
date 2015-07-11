package org.semanticweb.elk.reasoner.saturation.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;

public interface SubPropertyChainInference<R extends IndexedPropertyChain, S extends IndexedPropertyChain>
		extends SubPropertyChain<R, S>, ObjectPropertyInference {

	public <I, O> O accept(SubPropertyChainInferenceVisitor<I, O> visitor,
			I input);

}
