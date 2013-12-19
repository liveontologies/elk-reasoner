/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * A conclusion visitor which processes {@link OldTracedConclusion}s and saves their inferences using a {@link TraceStore.Writer}.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TracingConclusionInsertionVisitor extends BaseConclusionVisitor<Boolean, Context> {

	private final TraceStore.Writer traceWriter_;
	
	private final TracedConclusionVisitor<Boolean, Context> tracedVisitor_ = new BaseTracedConclusionVisitor<Boolean, Context>() {

		@Override
		protected Boolean defaultTracedVisit(TracedConclusion conclusion, Context context) {			
			traceWriter_.addInference(context, conclusion);
			
			return true;
		}
		
	};
	
	/**
	 * 
	 */
	public TracingConclusionInsertionVisitor(TraceStore.Writer traceWriter) {
		traceWriter_ = traceWriter;
	}

	@Override
	protected Boolean defaultVisit(Conclusion conclusion, Context cxt) {
		return ((TracedConclusion)conclusion).acceptTraced(tracedVisitor_, cxt);
	}
	
}
