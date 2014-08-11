/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyConclusionVisitor;

/**
 * TODO merge with {@link PropertyChainInitialization}? 
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SubPropertyChain implements ObjectPropertyConclusion {

	private final IndexedBinaryPropertyChain chain_;
	
	public SubPropertyChain(IndexedBinaryPropertyChain chain) {
		chain_ = chain;
	}
	
	public IndexedBinaryPropertyChain getPropertyChain() {
		return chain_;
	}

	@Override
	public String toString() {
		return "SubPropertyChain(" + chain_ + " " + chain_ + ")";
	}

	@Override
	public <I, O> O accept(ObjectPropertyConclusionVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}
	
}
