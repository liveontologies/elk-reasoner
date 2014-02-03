/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.MutableInteger;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Recursively visits all conclusions which were used to produce a given
 * conclusion. Never traces a non-traced context but stops there without
 * attempting to read traces from it.
 * 
 * Works analogously to {@link RecursiveTraceUnwinder} except that, first, it
 * can notify the caller if some visited conclusion has not been traced (this is
 * useful for testing) and, second, it will not attempt to visit inferences for
 * a conclusion which belongs to not yet traced context.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RecursiveTraceExplorer {

	private final TraceStore.Reader traceReader_;
	
	private final LocalTracingSaturationState tracingState_;
	/*
	 * TODO so far we use notifications that some conclusion isn't traced only
	 * for testing, perhaps we may get rid of this.
	 */
	private final UntracedConclusionListener listener_;
	
	private final static TracedConclusionVisitor<?, Context> DUMMY_INFERENCE_VISITOR = new BaseTracedConclusionVisitor<Void, Context>();

	public RecursiveTraceExplorer(TraceStore.Reader reader, LocalTracingSaturationState state) {
		this(reader, state, UntracedConclusionListener.DUMMY);
	}
	
	RecursiveTraceExplorer(TraceStore.Reader reader, LocalTracingSaturationState state, UntracedConclusionListener listener) {
		traceReader_ = reader;
		listener_ = listener;
		tracingState_ = state;
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
	 * @param premiseVisitor Visitor over all conclusions which were used as premises
	 * @param inferenceVisitor Visitor over all
	 */
	public void accept(Context context,
			final Conclusion conclusion,
			final ConclusionVisitor<Boolean, Context> premiseVisitor,
			final TracedConclusionVisitor<?, Context> inferenceVisitor) {
		final Queue<InferenceWrapper> toDo = new LinkedList<InferenceWrapper>();
		final Set<TracedConclusion> seenInferences = new HashSet<TracedConclusion>();

		addToQueue(context, conclusion, toDo, seenInferences, premiseVisitor, inferenceVisitor);
		// this visitor visits all premises and putting them into the todo queue
		PremiseVisitor<?, Context> tracedVisitor = new PremiseVisitor<Void, Context>() {

			@Override
			protected Void defaultVisit(Conclusion premise, Context cxt) {
				// the context passed into this method is the context where the inference has been made
				addToQueue(cxt, premise, toDo, seenInferences, premiseVisitor, inferenceVisitor);
				return null;
			}
		};

		for (;;) {
			final InferenceWrapper next = toDo.poll();

			if (next == null) {
				break;
			}

			next.inference.acceptTraced(tracedVisitor, next.context);
		}
	}

	private void addToQueue(final Context context, 
			final Conclusion conclusion,
			final Queue<InferenceWrapper> toDo,
			final Set<TracedConclusion> seenInferences,
			final ConclusionVisitor<Boolean, Context> visitor,
			final TracedConclusionVisitor<?, Context> inferenceVisitor) {
		
		Context tracedContext = tracingState_.getContext(conclusion.getSourceContext(context).getRoot());
		
		conclusion.accept(visitor, tracedContext);
		
		if (!tracedContext.isSaturated()) {
			//must stop unwinding because the context to which the next conclusion belongs isn't yet traced
			return;
		}
		
		final MutableInteger traced = new MutableInteger(0);
		// finding all inferences that produced the given conclusion (if we are
		// here, the inference must have premises, i.e. it's not an
		// initialization inference)
		traceReader_.accept(context, conclusion,
				new BaseTracedConclusionVisitor<Void, Void>() {

					@Override
					protected Void defaultTracedVisit(TracedConclusion inference, Void v) {
						if (!seenInferences.contains(inference)) {
							Context inferenceContext = inference.getInferenceContext(context);
							
							inference.acceptTraced(inferenceVisitor, inferenceContext);
							
							seenInferences.add(inference);
							toDo.add(new InferenceWrapper(inference, inferenceContext));
						}

						traced.increment();
						
						return null;
					}

				});
		
		if (traced.get() == 0) {
			listener_.notifyUntraced(conclusion, context);
		}
	}

	/*
	 * used to propagate context which normally isn't stored inside each traced conclusion
	 */
	private static class InferenceWrapper {

		final TracedConclusion inference;
		final Context context;

		InferenceWrapper(TracedConclusion inf, Context cxt) {
			inference = inf;
			context = cxt;
		}

		@Override
		public String toString() {
			return inference + " stored in " + context;
		}

	}

}
