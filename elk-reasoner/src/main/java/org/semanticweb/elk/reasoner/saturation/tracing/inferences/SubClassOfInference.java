/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;

/**
 * Represents an inference of the form A => B, B => C is in the the ontology, thus A => C
 * 
 * TODO represent the side condition explicitly
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SubClassOfInference extends AbstractInference {
	
	/**
	 * This is B in the example above.
	 */
	private final Conclusion subsumer_;

	public SubClassOfInference(Conclusion subsumer) {
		subsumer_ = subsumer;
	}
	
	public Conclusion getPremise() {
		return subsumer_;
	}

	
	
}
