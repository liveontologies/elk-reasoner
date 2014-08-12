/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class LeftReflexiveSubPropertyChainInference extends ReflexiveSubPropertyChainInference {

	public LeftReflexiveSubPropertyChainInference(IndexedPropertyChain sub, IndexedBinaryPropertyChain chain) {
		super(sub, chain);
	}

	@Override
	public ReflexivePropertyChain<IndexedObjectProperty> getReflexivePremise() {
		return new ReflexivePropertyChain<IndexedObjectProperty>(getSuperPropertyChain().getLeftProperty());
	}

	@Override
	public <I, O> O acceptTraced(ObjectPropertyInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}
	
	

}
