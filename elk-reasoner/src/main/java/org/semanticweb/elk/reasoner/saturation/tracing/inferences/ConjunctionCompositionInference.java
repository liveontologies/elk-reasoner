/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;

/**
 * Represents an inference of the form A => C_1, A => C_2, and C_1 \and C_2 occurs in the ontology, thus A => C_1 \and C_2.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ConjunctionCompositionInference extends AbstractInference {
	//TODO store this as ICE
	private final Conclusion first_;
	
	private final IndexedClassExpression second_;
	
	public ConjunctionCompositionInference(Conclusion first, IndexedClassExpression second) {
		first_ = first;
		second_ = second;
	}

	public Conclusion getFirstConjunct() {
		return first_;
	}
	
	public Conclusion getSecondConjunct() {
		return TracingUtils.getSubsumerWrapper(second_);
	}
	
	@Override
	public String toString() {
		return "Conjuncting " + first_ + " and " + second_;
	}
	
	@Override
	public <R> R accept(InferenceVisitor<R> visitor) {
		return visitor.visit(this);
	}
}
