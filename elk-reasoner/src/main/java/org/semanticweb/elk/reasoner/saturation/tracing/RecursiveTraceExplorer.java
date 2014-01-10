/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Recursively visits all conclusions which were used to produce a given conclusion
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class RecursiveTraceExplorer {

	//private static final Logger LOGGER_ = LoggerFactory.getLogger(RecursiveTraceExplorer.class);
	/**
	 * the expolorer won't explore more inferences for a conclusion as it unwinds the trace.
	 */
	private static final int INFERENCES_TO_UNWIND = Integer.MAX_VALUE;
	
	private final TraceStore.Reader traceReader_;
	/*
	 * TODO so far we use notifications that some conclusion isn't traced only
	 * for testing, perhaps we may get rid of this.
	 */
	private final UntracedConclusionListener listener_;

	public RecursiveTraceExplorer(TraceStore.Reader reader) {
		this(reader, UntracedConclusionListener.DUMMY);
	}
	
	RecursiveTraceExplorer(TraceStore.Reader reader, UntracedConclusionListener listener) {
		traceReader_ = reader;
		listener_ = listener;
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
			final ConclusionVisitor<Boolean, Context> premiseVisitor) {
		final Queue<InferenceWrapper> toDo = new LinkedList<InferenceWrapper>();
		final Set<TracedConclusion> seenInferences = new HashSet<TracedConclusion>();

		addToQueue(context, conclusion, toDo, seenInferences, premiseVisitor);

		for (;;) {
			final InferenceWrapper next = toDo.poll();

			if (next == null) {
				break;
			}
			//visiting all premises and putting them into the todo queue
			next.inference.acceptTraced(new PremiseVisitor<Void, Void>(new BaseConclusionVisitor<Void, Void>(){

				@Override
				protected Void defaultVisit(Conclusion premise, Void cxt) {
					addToQueue(next.context, premise, toDo, seenInferences, premiseVisitor);
					return null;
				}
				
			}), null);
		}
	}

	private void addToQueue(final Context context, final Conclusion conclusion,
			final Queue<InferenceWrapper> toDo,
			final Set<TracedConclusion> seenInferences,
			final ConclusionVisitor<Boolean, Context> visitor) {
		
		if (!conclusion.accept(visitor, context)) {
			//must stop here
			return;
		}
		
		final AtomicInteger traced = new AtomicInteger(0);
		// finding all inferences that produced the given conclusion (if we are
		// here, the inference must have premises, i.e. it's not an
		// initialization inference)
		traceReader_.accept(context, conclusion,
				new BaseTracedConclusionVisitor<Void, Void>() {

					@Override
					protected Void defaultTracedVisit(TracedConclusion inference, Void v) {
						if (!seenInferences.contains(inference) && traced.get() < INFERENCES_TO_UNWIND) {
							Context inferenceContext = inference.getInferenceContext(context);
							
							seenInferences.add(inference);
							toDo.add(new InferenceWrapper(inference, inferenceContext));
						}

						traced.incrementAndGet();
						
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
