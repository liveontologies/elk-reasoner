package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;

public interface ClassInferenceTracingState {

	/**
	 * @param root
	 *            the {@link IndexedContextRoot} for which to find the
	 *            {@link Context}
	 * @return the {@link ClassInferenceSet} containing all
	 *         {@link ClassInference}s for the given origin
	 *         {@link IndexedContextRoot}; {@code null} if not computed yet.
	 */
	public ClassInferenceSet getInferencesForOrigin(
			IndexedContextRoot inferenceOriginRoot);

}
