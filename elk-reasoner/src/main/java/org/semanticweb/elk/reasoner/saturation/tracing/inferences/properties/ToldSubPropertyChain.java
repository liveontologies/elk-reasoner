/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ToldSubPropertyChain extends SubPropertyChain<IndexedPropertyChain, IndexedObjectProperty>
		implements ObjectPropertyInference {

	private final IndexedPropertyChain premise_;
	
	public ToldSubPropertyChain(IndexedPropertyChain chain,
			IndexedObjectProperty sup, IndexedPropertyChain premise) {
		super(chain, sup);
		premise_ = premise;
	}

	public SubPropertyChain<?, ?> getPremise() {
		return new SubPropertyChain<IndexedPropertyChain, IndexedPropertyChain>(getSubPropertyChain(), premise_);
	}
	
	@Override
	public <I, O> O acceptTraced(ObjectPropertyInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}
	
	@Override
	public String toString() {
		return "Told sub-chain: " + getSubPropertyChain() + " => " + getSuperPropertyChain() + ", premise: " + premise_ + " => " + getSuperPropertyChain();
	}

}
