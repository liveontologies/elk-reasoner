package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;

public interface ModifiableClassInferenceTracingState extends
		ClassInferenceTracingState {

	/**
	 * Assigns the given {@link ClassInference}s to the given
	 * {@link IndexedContextRoot}
	 * 
	 * @param inferenceOriginRoot
	 *            the origin {@link IndexedContextRoot} for which the
	 *            {@link ClassInference}s should be assigned
	 * 
	 * @param classInferences
	 *            the {@link ClassInference}s that should be assigned; the given
	 *            {@link IndexedContextRoot} should be the origin of all of them
	 * 
	 * @return the {@link ClassInferenceSet} that contain all the given
	 *         {@link ClassInference} that can be used in proofs (i.e., not
	 *         cyclic).
	 */
	public ClassInferenceSet setClassInferences(
			IndexedContextRoot inferenceOriginRoot,
			Iterable<? extends ClassInference> classInferences);

}
