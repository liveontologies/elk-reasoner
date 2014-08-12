/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyConclusionVisitor;

/**
 * Represents a conclusion that a chain is a sub-property chain of another chain
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SubPropertyChain<R extends IndexedPropertyChain, S extends IndexedPropertyChain> implements ObjectPropertyConclusion {

	private final R chain_;
	
	private final S superProperty_;
	
	public SubPropertyChain(R chain, S sup) {
		chain_ = chain;
		superProperty_ = sup;
	}
	
	public R getSubPropertyChain() {
		return chain_;
	}
	
	public S getSuperPropertyChain() {
		return superProperty_;
	}

	@Override
	public String toString() {
		return "SubPropertyChain(" + chain_ + " " + superProperty_ + ")";
	}

	@Override
	public <I, O> O accept(ObjectPropertyConclusionVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}
	
}
