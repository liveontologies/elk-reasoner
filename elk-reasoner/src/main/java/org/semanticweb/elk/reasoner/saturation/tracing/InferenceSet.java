package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ObjectPropertyInference;

/**
 * 
 * An object containing {@link ClassInference}s and
 * {@link ObjectPropertyInference}s, which can be used to retrieve inferences
 * producing a given {@link ClassConclusion} and {@link ObjectPropertyConclusion}
 * respectively.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public interface InferenceSet extends ClassInferenceSet,
		ObjectPropertyInferenceSet {
	// a simple combination of two interfaces

}
