/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;

/**
 * Represents an inference that one {@link IndexedObjectProperty} is a sub-property of another.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SubObjectPropertyInference extends SubObjectProperty implements ObjectPropertyInference {

	private final IndexedObjectProperty premise_;
	
	public SubObjectPropertyInference(IndexedObjectProperty sub, IndexedObjectProperty sup, IndexedObjectProperty premise) {
		super(sub, sup);
		premise_ = premise;
	}
	
	public SubObjectProperty getPremise() {
		return new SubObjectProperty(getSubProperty(), premise_);
	}

	@Override
	public <I, O> O acceptTraced(ObjectPropertyInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}
	
	@Override
	public String toString() {
		return "Sub-property inference: " + getSubProperty() + " => " + getSuperProperty() + ", premise: " + premise_ + " => " + getSuperProperty();
	}

}
