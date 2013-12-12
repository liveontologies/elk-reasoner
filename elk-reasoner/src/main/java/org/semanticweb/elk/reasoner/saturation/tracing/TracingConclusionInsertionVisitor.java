/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A conclusion visitor which processes {@link TracedConclusion}s and saves their inferences using a {@link TraceStore.Writer}.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TracingConclusionInsertionVisitor extends BaseConclusionVisitor<Boolean, Context> {

	private final static Logger LOGGER_ = LoggerFactory.getLogger(TracingConclusionInsertionVisitor.class);
	
	private final TraceStore.Writer traceWriter_;
	
	/**
	 * 
	 */
	public TracingConclusionInsertionVisitor(TraceStore.Writer traceWriter) {
		traceWriter_ = traceWriter;
	}

	private boolean addInference(Conclusion conclusion, Context context) {
		//TODO need a good idea of how to get rid of this cast
		if (conclusion instanceof TracedConclusion<?>) {
			Inference inf = ((TracedConclusion<?>) conclusion).getInference();
			
			traceWriter_.addInference(context, conclusion, inf);
		}
		else {
			LOGGER_.warn("Expecting instances of TracedConclusion when tracing is on");
		}
		
		return true;
	}
	
	@Override
	public Boolean visit(NegativeSubsumer negSCE, Context context) {
		return addInference(negSCE, context);
	}

	@Override
	public Boolean visit(PositiveSubsumer posSCE, Context context) {
		return addInference(posSCE, context);
	}

	@Override
	public Boolean visit(BackwardLink link, Context context) {
		return addInference(link, context);
	}
	
	@Override
	public Boolean visit(ForwardLink link, Context context) {
		return addInference(link, context);
	}

	@Override
	protected Boolean defaultVisit(Conclusion conclusion, Context cxt) {
		return true;
	}
	
}
