/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyConclusionVisitor;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ReflexivePropertyChain<P extends IndexedPropertyChain> implements ObjectPropertyConclusion {

	private final P propertyChain_;
	
	public ReflexivePropertyChain(P chain) {
		propertyChain_ = chain;
	}

	public P getPropertyChain() {
		return propertyChain_;
	}
	
	@Override
	public <I, O> O accept(ObjectPropertyConclusionVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

	@Override
	public String toString() {
		return propertyChain_.toString();
	}

	
}
