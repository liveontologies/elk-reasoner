/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Subsumer;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.collections.Pair;

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
	
	private final TracingSaturationState tracingState_;
	/*
	 * TODO so far we use notifications that some conclusion isn't traced only
	 * for testing, perhaps we may get rid of this.
	 */
	private final UntracedConclusionListener listener_;

	public RecursiveTraceExplorer(TraceStore.Reader reader, TracingSaturationState state) {
		this(reader, state, UntracedConclusionListener.DUMMY);
	}
	
	RecursiveTraceExplorer(TraceStore.Reader reader, TracingSaturationState state, UntracedConclusionListener listener) {
		traceReader_ = reader;
		listener_ = listener;
		tracingState_ = state;
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
		// this visitor visits all premises and putting them into the todo queue
		PremiseVisitor<Void, Context> tracedVisitor = new PremiseVisitor<Void, Context>/*SideConditionCollector*/(new BaseConclusionVisitor<Void, Context>(){
			@Override
			protected Void defaultVisit(Conclusion premise, Context cxt) {
				// the context passed into this method is the context where the inference has been made
				addToQueue(cxt, premise, toDo, seenInferences, premiseVisitor);
				return null;
			}
			
		});

		for (;;) {
			final InferenceWrapper next = toDo.poll();

			if (next == null) {
				break;
			}

			next.inference.acceptTraced(tracedVisitor, next.context);
		}
		
		/*for (Pair<Conclusion, Conclusion> pair : tracedVisitor.getSubClassOfAxioms()) {
			LOGGER_.info("{} => {}", pair.getFirst(), pair.getSecond());
		}*/
		
		//LOGGER_.info("{} subclassof axioms used", tracedVisitor.getSubClassOfAxioms().size());
		//LOGGER_.info("{} fillers occurred in existentials", tracedVisitor.getFillers().size());
	}

	private void addToQueue(final Context context, 
			final Conclusion conclusion,
			final Queue<InferenceWrapper> toDo,
			final Set<TracedConclusion> seenInferences,
			final ConclusionVisitor<Boolean, Context> visitor) {
		
		Context tracedContext = tracingState_.getContext(conclusion.getSourceContext(context).getRoot());
		
		conclusion.accept(visitor, tracedContext);
		
		if (!tracedContext.isSaturated()) {
			//must stop unwinding because the context to which the next conclusion belongs isn't yet traced
			//LOGGER_.trace("Stopping unwinding because {} isn't yet traced", tracedContext);			
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
							//Context inferenceContext = inference.getSourceContext(context);
							
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
	
	/*
	 * FIXME currently used for testing only
	 */
	private static class SideConditionCollector extends PremiseVisitor<Void, Context> {

		private final Set<Pair<Conclusion, Conclusion>> subclassAxioms_ = new HashSet<Pair<Conclusion, Conclusion>>();
		
		private final Set<IndexedClassExpression> fillers_ = new HashSet<IndexedClassExpression>();
		
		public SideConditionCollector(ConclusionVisitor<Void, Context> v) {
			super(v);
		}

		@Override
		public Void visit(SubClassOfSubsumer conclusion, Context cxt) {
			subclassAxioms_.add(new Pair<Conclusion, Conclusion>(conclusion.getPremise(), conclusion));
			
			checkIfExistential(conclusion.getPremise());
			checkIfExistential(conclusion);
			
			return super.visit(conclusion, cxt);
		}
		
		//collecting all fillers used in some existential restrictions
		private void checkIfExistential(Conclusion c) {
			if (c instanceof Subsumer) {
				IndexedClassExpression ice = ((Subsumer)c).getExpression();
				
				ice.accept(new IndexedClassExpressionVisitor<Void>() {

					@Override
					public Void visit(IndexedClass element) {
						return null;
					}

					@Override
					public Void visit(IndexedIndividual element) {
						return null;
					}

					@Override
					public Void visit(IndexedObjectComplementOf element) {
						return element.getNegated().accept(this);
					}

					@Override
					public Void visit(IndexedObjectIntersectionOf element) {
						element.getFirstConjunct().accept(this);
						element.getSecondConjunct().accept(this);
						return null;
					}

					@Override
					public Void visit(IndexedObjectSomeValuesFrom element) {
						fillers_.add(element.getFiller());
						element.getFiller().accept(this);
						return null;
					}

					@Override
					public Void visit(IndexedObjectUnionOf element) {
						for (IndexedClassExpression disjunct : element.getDisjuncts()) {
							disjunct.accept(this);
						}
						return null;
					}

					@Override
					public Void visit(IndexedDataHasValue element) {
						return null;
					}
					
				});
			}
		}

		public Set<Pair<Conclusion, Conclusion>> getSubClassOfAxioms() {
			return subclassAxioms_;
		}
		
		public Set<IndexedClassExpression> getFillers() {
			return fillers_;
		}
	}

}
