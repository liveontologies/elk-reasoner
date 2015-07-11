package org.semanticweb.elk.reasoner.saturation.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;

public interface ReflexivePropertyChainInference<P extends IndexedPropertyChain>
		extends ReflexivePropertyChain<P>, ObjectPropertyInference {

	public <I, O> O accept(
			ReflexivePropertyChainInferenceVisitor<I, O> visitor, I input);

}
