/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;

/**
 * R <= S if R <= H and H <= S. This class stores H as the premise.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class BottomUpPropertySubsumptionInference extends SubPropertyChain<IndexedPropertyChain, IndexedObjectProperty>
		implements ObjectPropertyInference {

	private final IndexedPropertyChain premise_;
	
	public BottomUpPropertySubsumptionInference(IndexedPropertyChain chain,
			IndexedObjectProperty sup, IndexedPropertyChain premise) {
		super(chain, sup);
		premise_ = premise;
	}

	public SubPropertyChain<?, ?> getFirstPremise() {
		return new SubPropertyChain<IndexedPropertyChain, IndexedPropertyChain>(getSubPropertyChain(), premise_);
	}
	
	public SubPropertyChain<?, IndexedObjectProperty> getSecondPremise() {
		return new SubPropertyChain<IndexedPropertyChain, IndexedObjectProperty>(premise_, getSuperPropertyChain());
	}
	
	@Override
	public <I, O> O acceptTraced(ObjectPropertyInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}
	
	@Override
	public String toString() {
		return "Told sub-chain: " + getSubPropertyChain() + " => " + getSuperPropertyChain() + ", premise: " + getSubPropertyChain() + " => " + premise_;
	}

}
