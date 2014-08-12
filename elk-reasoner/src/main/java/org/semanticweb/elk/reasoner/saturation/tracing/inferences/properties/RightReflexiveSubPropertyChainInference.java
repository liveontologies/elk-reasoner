/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class RightReflexiveSubPropertyChainInference extends ReflexiveSubPropertyChainInference {

	public RightReflexiveSubPropertyChainInference(IndexedPropertyChain subChain, IndexedBinaryPropertyChain chain) {
		super(subChain, chain);
	}

	@Override
	public ReflexivePropertyChain<IndexedPropertyChain> getReflexivePremise() {
		return new ReflexivePropertyChain<IndexedPropertyChain>(getSuperPropertyChain().getRightProperty());
	}

	@Override
	public <I, O> O acceptTraced(ObjectPropertyInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}
	
	

}
