/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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
	
	private final TraceStore.Reader traceReader_;
	
	private final UntracedConclusionListener listener_;
	
	RecursiveTraceExplorer(TraceStore.Reader reader) {
		this(reader, UntracedConclusionListener.DUMMY);
	}
	
	RecursiveTraceExplorer(TraceStore.Reader reader, UntracedConclusionListener listener) {
		traceReader_ = reader;
		listener_ = listener;
	}
	
	void accept(Context context, Conclusion conclusion, final ConclusionVisitor<Boolean, Context> visitor) {
		final Queue<InferenceWrapper> toDo = new LinkedList<InferenceWrapper>();
		final Set<TracedConclusion> seenInferences = new HashSet<TracedConclusion>();

		addToQueue(context, conclusion, toDo, seenInferences, visitor);

		for (;;) {
			InferenceWrapper next = toDo.poll();

			if (next == null) {
				break;
			}

			final Context infContext = next.context;

			next.inference.acceptTraced(
					new BaseTracedConclusionVisitor<Void, Void>() {

						@Override
						public Void visit(InitializationSubsumer inference,
								Void v) {
							return null;
						}

						@Override
						public Void visit(SubClassOfSubsumer inference, Void v) {
							addToQueue(infContext, inference.getPremise(),
									toDo, seenInferences, visitor);
							return null;
						}

						@Override
						public Void visit(ComposedConjunction inference, Void v) {
							addToQueue(infContext,
									inference.getFirstConjunct(), toDo,
									seenInferences, visitor);
							addToQueue(infContext,
									inference.getSecondConjunct(), toDo,
									seenInferences, visitor);
							return null;
						}

						@Override
						public Void visit(DecomposedConjunction inference,
								Void v) {
							addToQueue(infContext, inference.getConjunction(),
									toDo, seenInferences, visitor);
							return null;
						}

						@Override
						public Void visit(ComposedBackwardLink inference, Void v) {
							addToQueue(infContext, inference.getBackwardLink(),
									toDo,  seenInferences, visitor);
							addToQueue(infContext, inference.getForwardLink(),
									toDo, seenInferences, visitor);
							return null;
						}

						@Override
						public Void visit(ReflexiveSubsumer inference, Void v) {
							// TODO
							return null;
						}

						@Override
						public Void visit(PropagatedSubsumer inference, Void v) {
							addToQueue(infContext, inference.getBackwardLink(),
									toDo, seenInferences, visitor);
							addToQueue(infContext, inference.getPropagation(),
									toDo, seenInferences, visitor);
							return null;
						}

						@Override
						public Void visit(TracedPropagation inference, Void v) {
							addToQueue(infContext, inference.getPremise(),
									toDo, seenInferences, visitor);
							return null;
						}

					}, null);
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
		
		final AtomicBoolean traced = new AtomicBoolean(false);
		// finding all inferences that produced the given conclusion (if we are
		// here, the inference must have premises, i.e. it's not an
		// initialization inference)
		traceReader_.accept(context, conclusion,
				new BaseTracedConclusionVisitor<Void, Void>() {

					@Override
					protected Void defaultTracedVisit(TracedConclusion inference, Void v) {
						if (!seenInferences.contains(inference)) {
							seenInferences.add(inference);
							toDo.add(new InferenceWrapper(inference, inference.getInferenceContext(context)));
						}

						traced.set(true);

						return null;
					}

				});
		
		if (!traced.get()) {
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
