/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ReversedBackwardLink extends ForwardLinkImpl implements TracedConclusion {

	private final BackwardLink sourceLink_;
	
	/**
	 * 
	 */
	public ReversedBackwardLink(BackwardLink backwardLink, Context target) {
		super(backwardLink.getRelation(),target);
		sourceLink_ = backwardLink;
	}

	@Override
	public <R, C> R acceptTraced(TracedConclusionVisitor<R, C> visitor, C parameter) {
		return visitor.visit(this, parameter);
	}
	
	public BackwardLink getSourceLink() {
		return sourceLink_;
	}

	@Override
	public Context getInferenceContext(Context defaultContext) {
		return getTarget();
	}
}
