/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.MutableBoolean;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionByContextStore;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tries to unwind the trace without cycles using the following recursive logic:
 * 
 * i) a conclusion has an acyclic trace iff at least one inference can be
 * unwound acyclically ii) an inference has an acyclic trace iff all premises
 * can be unwound acyclically
 * 
 * A trace for a conclusion is cyclic when it contains a premise which was used
 * to derive the conclusion.
 * 
 * Unfortunately this algorithm is worst-case exponential because each inference
 * can be visited exponentially many times (once for each combination of
 * previously visited conclusions).
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class AcyclicRecursiveTraceUnwinder implements TraceUnwinder {

	private static final Logger LOGGER_ = LoggerFactory.getLogger(AcyclicRecursiveTraceUnwinder.class);
	
	private final TraceStore.Reader traceReader_;

	private final static TracedConclusionVisitor<?, Context> DUMMY_INFERENCE_VISITOR = new BaseTracedConclusionVisitor<Void, Context>();

	public AcyclicRecursiveTraceUnwinder(TraceStore.Reader reader) {
		traceReader_ = reader;
	}

	public void accept(Context context, final Conclusion conclusion,
			final ConclusionVisitor<?, Context> premiseVisitor) {
		accept(context, conclusion, premiseVisitor, DUMMY_INFERENCE_VISITOR);
	}
	
	@Override
	public void accept(Context context, 
			final Conclusion conclusion,
			final ConclusionVisitor<?, Context> conclusionVisitor,
			final TracedConclusionVisitor<?, Context> inferenceVisitor) {
		
		ConclusionByContextStore conclusionsToBeAvoided = new ConclusionByContextStore();
		
		traceConclusion(context, conclusion, conclusionsToBeAvoided, conclusionVisitor, inferenceVisitor);
	}

	private boolean traceConclusion(
			final Context context, 
			final Conclusion conclusion,
			final ConclusionByContextStore conclusionsToBeAvoided,
			final ConclusionVisitor<?, Context> conclusionVisitor,
			final TracedConclusionVisitor<?, Context> inferenceVisitor) {
		
		if (conclusionsToBeAvoided.add(context, conclusion)) {
			
			LOGGER_.trace("Tracing conclusion {} in {}", conclusion, context);
			
			final MutableBoolean traced = new MutableBoolean(false);

			traceReader_.accept(context, conclusion,
					new BaseTracedConclusionVisitor<Void, Void>() {

						@Override
						protected Void defaultTracedVisit(TracedConclusion inference, Void ignored) {
							Context inferenceContext = inference.getInferenceContext(context);
							
							if (traceInference(inferenceContext, inference, conclusionsToBeAvoided, conclusionVisitor, inferenceVisitor)) {
								traced.set(true);
							}

							return null;
						}

					});

			conclusionsToBeAvoided.delete(context, conclusion);
			
			if (traced.get()) {
				LOGGER_.trace("Acyclic trace found for conclusion {} in {}", conclusion, context);
			} else {
				LOGGER_.trace("Acyclic trace NOT found for conclusion {} in {}", conclusion, context);
			}
			
			return traced.get();
		}

		return false;
	}

	private boolean traceInference(
			final Context inferenceContext,
			final TracedConclusion inference,
			final ConclusionByContextStore conclusionsToBeAvoided,
			final ConclusionVisitor<?, Context> conclusionVisitor,
			final TracedConclusionVisitor<?, Context> inferenceVisitor) {
		
		final MutableBoolean traced = new MutableBoolean(true);
		
		LOGGER_.trace("Tracing inference {} in {}", inference, inferenceContext);
		
		inference.acceptTraced(new PremiseVisitor<Void, Context>(){
			
			@Override
			protected Void defaultVisit(Conclusion premise, Context inferenceContext) {
				if (!traced.get()) {
					LOGGER_.trace("Tracing skipped for conclusion {} in {}", premise, inferenceContext);
					
					return null;
				}
				
				if (!traceConclusion(inferenceContext, premise, conclusionsToBeAvoided, conclusionVisitor, inferenceVisitor)) {
					traced.set(false);
				}
				
				return null;
			}
			
		}, inferenceContext);
		
		if (traced.get()) {
			LOGGER_.trace("Acyclic trace found for inference {} in {}", inference, inferenceContext);
		} else {
			LOGGER_.trace("Acyclic trace NOT found for inference {} in {}", inference, inferenceContext);
		}
		
		return traced.get();
	}

}
