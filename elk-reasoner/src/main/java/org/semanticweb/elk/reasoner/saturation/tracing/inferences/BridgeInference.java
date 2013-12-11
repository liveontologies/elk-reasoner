/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;

/**
 * This implementation is used as a "bridge" for auxiliary conclusions produced
 * as a part of a larger inference. The real underlying inferences can be
 * requested for the internally stored previously produced conclusion.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class BridgeInference extends AbstractInference {

	private final Conclusion previous_;
	
	/**
	 * 
	 */
	BridgeInference(Conclusion conclusion) {
		previous_ = conclusion;
	}

	public Conclusion getConclusion() {
		return previous_;
	}

	@Override
	public <R> R accept(InferenceVisitor<R> visitor) {
		return visitor.visit(this);
	}
}
