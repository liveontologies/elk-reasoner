/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.util.InferencePrinter;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;

/**
 * Represents an inference that a property chain is reflexive if it is composed of reflexive property sub-chains.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ReflexivePropertyChainInference extends ReflexivePropertyChain<IndexedBinaryPropertyChain> implements ObjectPropertyInference {

	public ReflexivePropertyChainInference(IndexedBinaryPropertyChain chain) {
		super(chain);
	}
	
	public PropertyChainInitialization getPropertyChainInitialization() {
		return new PropertyChainInitialization(getPropertyChain());
	}
	
	public ReflexivePropertyChain<IndexedObjectProperty> getLeftReflexiveProperty() {
		return new ReflexivePropertyChain<IndexedObjectProperty>(getPropertyChain().getLeftProperty());
	}
	
	public ReflexivePropertyChain<?> getRightReflexivePropertyChain() {
		return new ReflexivePropertyChain<IndexedPropertyChain>(getPropertyChain().getRightProperty());
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
