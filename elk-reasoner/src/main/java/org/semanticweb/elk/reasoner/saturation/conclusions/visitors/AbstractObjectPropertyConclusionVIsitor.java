/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexivePropertyChain;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.SubPropertyChain;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class AbstractObjectPropertyConclusionVIsitor<I, O> implements
		ObjectPropertyConclusionVisitor<I, O> {

	protected abstract O defaultVisit(ObjectPropertyConclusion conclusion, I input);
	
	@Override
	public O visit(SubPropertyChain<?, ?> conclusion, I input) {
		return defaultVisit(conclusion, input);
	}

	@Override
	public O visit(ReflexivePropertyChain<?> conclusion, I input) {
		return defaultVisit(conclusion, input);
	}

}
