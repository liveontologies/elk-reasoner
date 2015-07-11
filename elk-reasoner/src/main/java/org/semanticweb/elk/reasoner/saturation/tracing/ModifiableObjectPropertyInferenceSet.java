package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.inferences.properties.ObjectPropertyInference;

public interface ModifiableObjectPropertyInferenceSet extends
		ObjectPropertyInferenceSet {

	public void add(ObjectPropertyInference inference);

}
