/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors;

import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexivePropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubObjectProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubPropertyChain;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ObjectPropertyConclusionVisitor<I, O> {

	public O visit(SubObjectProperty conclusion, I input);
	
	public O visit(SubPropertyChain conclusion, I input);
	
	public O visit(ReflexivePropertyChain conclusion, I input);
	
	public static ObjectPropertyConclusionVisitor<?, ?> DUMMY = new ObjectPropertyConclusionVisitor<Void, Void>() {

		@Override
		public Void visit(SubObjectProperty conclusion, Void input) {
			// no-op
			return null;
		}

		@Override
		public Void visit(ReflexivePropertyChain conclusion, Void input) {
			// no-op
			return null;
		}

		@Override
		public Void visit(SubPropertyChain conclusion, Void input) {
			// no-op
			return null;
		}

	};
}
