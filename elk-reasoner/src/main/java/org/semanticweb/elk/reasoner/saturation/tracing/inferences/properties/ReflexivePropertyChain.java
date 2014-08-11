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
public class ReflexivePropertyChain implements ObjectPropertyConclusion {

	private final IndexedPropertyChain propertyChain_;
	
	public ReflexivePropertyChain(IndexedPropertyChain chain) {
		propertyChain_ = chain;
	}

	public IndexedPropertyChain getPropertyChain() {
		return propertyChain_;
	}
	
	@Override
	public <I, O> O accept(ObjectPropertyConclusionVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}
	
	/*@Override
	public <I, O> O accept(ConclusionVisitor<I, O> visitor, I input) {
		return null;
	}

	@Override
	public IndexedClassExpression getSourceRoot(
			IndexedClassExpression rootWhereStored) {
		// FIXME
		return rootWhereStored;
	}*/

}
