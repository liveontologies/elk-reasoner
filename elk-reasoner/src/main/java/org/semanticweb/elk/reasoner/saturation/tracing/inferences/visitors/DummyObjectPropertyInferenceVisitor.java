/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors;

import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class DummyObjectPropertyInferenceVisitor<I, O> extends AbstractObjectPropertyInferenceVisitor<I, O> {

	@Override
	protected O defaultTracedVisit(ObjectPropertyInference inference, I input) {
		// no-op
		return null;
	}

}
