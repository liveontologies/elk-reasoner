/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;

/**
 * Represents an inference of the form A => C_1 \and C_2, thus A => C_i
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ConjunctionDecompositionInference extends AbstractInference {

	private final IndexedObjectIntersectionOf conjunction_;

	public ConjunctionDecompositionInference(IndexedObjectIntersectionOf subsumer) {
		conjunction_ = subsumer;
	}
	
	public Conclusion getConjunction() {
		return TracingUtils.getSubsumerWrapper(conjunction_);
	}

	@Override
	public String toString() {
		return "Decomposing " + conjunction_;
	}
	
	@Override
	public <R> R accept(InferenceVisitor<R> visitor) {
		return visitor.visit(this);
	}
}
