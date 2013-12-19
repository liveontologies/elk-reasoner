/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.util.TracingUtils;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ComposedBackwardLink extends BackwardLinkImpl implements TracedConclusion {

	private final Context inferenceContext_;
	
	private final IndexedPropertyChain backwardLinkRelation_;
	
	private final IndexedPropertyChain forwardLinkRelation_;
	
	private final Context forwardLinkTarget_;
	
	public ComposedBackwardLink(IndexedPropertyChain chain, Context inferenceContext, ForwardLink forwardLink, IndexedPropertyChain backwardLinkChain, Context linkSource) {
		super(linkSource, chain);
		backwardLinkRelation_ = backwardLinkChain;
		forwardLinkRelation_ = forwardLink.getRelation();
		forwardLinkTarget_ = forwardLink.getTarget();
		inferenceContext_ = inferenceContext;
	}
	
	public ComposedBackwardLink(IndexedPropertyChain chain, Context inferenceContext, BackwardLink backwardLink, IndexedPropertyChain forwardLinkChain, Context forwardLinkTarget) {
		super(backwardLink.getSource(), chain);
		backwardLinkRelation_ = backwardLink.getRelation();
		forwardLinkRelation_ = forwardLinkChain;
		forwardLinkTarget_ = forwardLinkTarget;
		inferenceContext_ = inferenceContext;
	}

	@Override
	public <R, C> R acceptTraced(TracedConclusionVisitor<R, C> visitor, C parameter) {
		return visitor.visit(this, parameter);
	}

	public BackwardLink getBackwardLink() {
		return TracingUtils.getBackwardLinkWrapper(backwardLinkRelation_, getSource());
	}
	
	public ForwardLink getForwardLink() {
		return TracingUtils.getForwardLinkWrapper(forwardLinkRelation_, forwardLinkTarget_);
	}
	
	@Override
	public Context getInferenceContext(Context defaultContext) {
		return inferenceContext_;
	}


}
