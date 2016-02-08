package org.semanticweb.elk.reasoner.saturation.inferences;

public interface SubClassInclusionInference extends ClassInference {

	public <O> O accept(Visitor<O> visitor);

	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public static interface Visitor<O>
			extends
				SubClassInclusionComposedInference.Visitor<O>,
				SubClassInclusionDecomposedInference.Visitor<O> {

		// combined interface

	}

}
