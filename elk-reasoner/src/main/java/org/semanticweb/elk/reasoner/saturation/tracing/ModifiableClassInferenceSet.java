package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;

public interface ModifiableClassInferenceSet extends ClassInferenceSet {

	public void add(ClassInference inference);

}
