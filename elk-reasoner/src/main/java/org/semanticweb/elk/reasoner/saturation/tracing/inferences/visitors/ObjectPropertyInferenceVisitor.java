/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors;

import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.PropertyChainInitialization;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubObjectPropertyInference;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ObjectPropertyInferenceVisitor<I, O> {

	public O visit(SubObjectPropertyInference inference, I input);
	
	public O visit(PropertyChainInitialization inference, I input);
	
	public static ObjectPropertyInferenceVisitor<?, ?> DUMMY = new ObjectPropertyInferenceVisitor<Void, Void>() {

		@Override
		public Void visit(SubObjectPropertyInference inference, Void input) {
			// no-op
			return null;
		}

		@Override
		public Void visit(PropertyChainInitialization inference, Void input) {
			// no-op
			return null;
		}
		
	};
}
