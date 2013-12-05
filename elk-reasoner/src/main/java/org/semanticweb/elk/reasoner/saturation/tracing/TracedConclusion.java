/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class TracedConclusion<S extends Conclusion> {

	/**
	 * How this conclusion was produced
	 */
	private final Inference inference_;
	/**
	 * The actual conclusion
	 */
	protected final S conclusion;
	
	/**
	 * 
	 */
	public TracedConclusion(Inference inf, S cnl) {
		inference_ = inf;
		conclusion = cnl;
	}
	
	public Inference getInference() {
		return inference_;
	}

	public S getConclusion() {
		return conclusion;
	}
	
	
}
