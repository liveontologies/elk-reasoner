/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.util.InferencePrinter;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;

/**
 * Represents an inference based on a ReflexiveObjectProperty axiom in the ontology.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ToldReflexiveProperty extends ReflexivePropertyChain<IndexedObjectProperty> implements ObjectPropertyInference {

	public ToldReflexiveProperty(IndexedObjectProperty property) {
		super(property);
	}
	
	public SubObjectProperty getPropertyInitialization() {
		return new SubObjectProperty(getPropertyChain(), getPropertyChain());
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
