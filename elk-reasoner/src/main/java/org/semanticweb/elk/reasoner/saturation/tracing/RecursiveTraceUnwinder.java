/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.collections.Pair;

/**
 * Recursively visits all conclusions which were used to produce a given
 * conclusion.
 * 
 * Works similarly to {@link RecursiveTraceExplorer} but is simpler. It does not
 * know anything about which contexts are traced or how inferences are read. It
 * uses the given {@link TraceStore.Reader} as an oracle providing access to
 * inferences.
 * 
 * TODO concurrently request and process inferences from the trace reader.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RecursiveTraceUnwinder {

	private final TraceStore.Reader traceReader_;
	
	private final static InferenceVisitor<?, Context> DUMMY_INFERENCE_VISITOR = new BaseInferenceVisitor<Void, Context>();

	public RecursiveTraceUnwinder(TraceStore.Reader reader) {
		traceReader_ = reader;
	}
	
	public void accept(Context context,
			final Conclusion conclusion,
			final ConclusionVisitor<Boolean, Context> premiseVisitor) {
		accept(context, conclusion, premiseVisitor, DUMMY_INFERENCE_VISITOR);
	}
	
	/**
	 * 
	 * @param context
	 * @param conclusion
	 * @param conclusionVisitor Visitor over all conclusions which were used as premises
	 * @param inferenceVisitor Visitor over all
	 */
	public void accept(Context context,
			final Conclusion conclusion,
			final ConclusionVisitor<Boolean, Context> conclusionVisitor,
			final InferenceVisitor<?, Context> inferenceVisitor) {
		final TraceUnwindingState unwindingState = new TraceUnwindingState();
		
		unwindingState.addToUnwindingQueue(conclusion, context);
		
		for (;;) {
			Pair<Conclusion, Context> next = unwindingState.pollFromUnwindingQueue();

			if (next == null) {
				break;
			}

			unwind(next.getFirst(), next.getSecond(), unwindingState, conclusionVisitor, inferenceVisitor);
		}
	}

	private void unwind(Conclusion conclusion, 
			final Context contextWhereStored,
			final TraceUnwindingState unwindingState,
			final ConclusionVisitor<Boolean, Context> conclusionVisitor,
			final InferenceVisitor<?, Context> inferenceVisitor) {
		
		final PremiseVisitor<?, Context> premiseVisitor = new PremiseVisitor<Void, Context>() {

			@Override
			protected Void defaultVisit(Conclusion premise, Context inferenceContext) {
				unwindingState.addToUnwindingQueue(premise, inferenceContext);
				return null;
			}
		};
		
		traceReader_.accept(contextWhereStored.getRoot(), conclusion,
				new BaseInferenceVisitor<Void, Void>() {

					@Override
					protected Void defaultTracedVisit(Inference inference, Void v) {
						if (unwindingState.addToProcessed(inference)) {
							Context inferenceContext = inference.getInferenceContext(contextWhereStored);
							//visit the premises so they can be put into the queue
							inference.acceptTraced(premiseVisitor, inferenceContext);
							//for the calling code
							inference.acceptTraced(inferenceVisitor, inferenceContext);
						}
					
						return null;
					}

				});
	}

}
