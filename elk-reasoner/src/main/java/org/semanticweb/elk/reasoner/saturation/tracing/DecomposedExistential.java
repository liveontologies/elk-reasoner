/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.util.TracingUtils;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class DecomposedExistential extends BackwardLinkImpl implements TracedConclusion, BackwardLink {

	private final Context inferenceContext_;
	
	private final IndexedObjectSomeValuesFrom existential_;
	
	/**
	 * 
	 */
	public DecomposedExistential(IndexedObjectSomeValuesFrom subsumer, Context source) {
		super(source, subsumer.getRelation());
		existential_ = subsumer;
		inferenceContext_ = source;
	}
	
	@Override
	public <R, C> R acceptTraced(TracedConclusionVisitor<R, C> visitor, C parameter) {
		return visitor.visit(this, parameter);
	}

	public Subsumer getExistential() {
		return TracingUtils.getSubsumerWrapper(existential_);
	}

	@Override
	public Context getInferenceContext(Context defaultContext) {
		return inferenceContext_;
	}
}
