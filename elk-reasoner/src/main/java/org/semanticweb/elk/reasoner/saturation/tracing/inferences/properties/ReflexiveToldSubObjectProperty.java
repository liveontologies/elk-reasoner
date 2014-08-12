/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.util.InferencePrinter;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;

/**
 * Represents an inference that the property is reflexive if one of its told sub-chains is reflexive.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ReflexiveToldSubObjectProperty extends ReflexivePropertyChain<IndexedObjectProperty> implements ObjectPropertyInference {

	private final IndexedPropertyChain subChain_;
	
	public ReflexiveToldSubObjectProperty(IndexedObjectProperty property, IndexedPropertyChain subProperty) {
		super(property);
		subChain_ = subProperty;
	}
	
	public ReflexivePropertyChain<?> getSubProperty() {
		return new ReflexivePropertyChain<IndexedPropertyChain>(subChain_);
	}

	@Override
	public <I, O> O acceptTraced(ObjectPropertyInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}
	
	@Override
	public String toString() {
		return new InferencePrinter().visit(this, null);
	}
}
