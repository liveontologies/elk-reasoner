/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;




/**
 * Represents a trivial inference of the from A => A
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ClassInitializationInference extends AbstractInference {

	@Override
	public String toString() {
		return "Inititialization inference";
	}

	@Override
	public <R> R accept(InferenceVisitor<R> visitor) {
		return visitor.visit(this);
	}
	
}
