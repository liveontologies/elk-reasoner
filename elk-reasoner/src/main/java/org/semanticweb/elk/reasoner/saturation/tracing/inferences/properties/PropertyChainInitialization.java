/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;

/**
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class PropertyChainInitialization implements ObjectPropertyInference {

	private final IndexedPropertyChain chain_;
	
	public PropertyChainInitialization(IndexedPropertyChain prop) {
		chain_ = prop;
	}
	
	public IndexedPropertyChain getPropertyChain() {
		return chain_;
	}
	
	@Override
	public String toString() {
		return "Initialization( " + chain_ + " )";
	}

	@Override
	public <I, O> O acceptTraced(ObjectPropertyInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}


}
