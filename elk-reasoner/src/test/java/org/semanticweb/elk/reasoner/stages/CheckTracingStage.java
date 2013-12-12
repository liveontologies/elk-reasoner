/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore.Reader;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingSaturationState;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.BaseInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.BridgeInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInitializationInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ConjunctionCompositionInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ConjunctionDecompositionInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ExistentialInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.PropertyChainInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ReflexiveInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.SubClassOfInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.TracingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performs checks to verify that tracing information is correct 
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class CheckTracingStage extends BasePostProcessingStage {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(CheckTracingStage.class);
	
	/**
	 * 
	 */
	public CheckTracingStage(AbstractReasonerState r) {
		super(r);
	}

	@Override
	public String getName() {
		return "Check tracing";
	}

	@Override
	public void execute() throws ElkException {
		if (reasoner.saturationState instanceof TracingSaturationState) {
			
			TracingSaturationState tracingState = (TracingSaturationState) reasoner.saturationState;
			TraceStore.Reader traceReader = tracingState.getTraceStoreReader();
			
			for (Context context : reasoner.saturationState.getContexts()) {
				for (IndexedClassExpression subsumer : context.getSubsumers()) {
					checkTrace(context, TracingUtils.getSubsumerWrapper(subsumer), traceReader);
				}
			}
		}
		
	}

	private void checkTrace(Context context, Conclusion conclusion, final Reader traceReader) {
		final Queue<InferenceWrapper> toDo = new LinkedList<InferenceWrapper>();
		final Set<Inference> seenInferences = new HashSet<Inference>();
		
		addToQueue(context, conclusion, toDo, traceReader, seenInferences);
		
		for (;;) {
			InferenceWrapper next = toDo.poll();
			
			if (next == null) {
				break;
			}
			
			final Context infContext = next.context;			
			
			next.inference.accept(new BaseInferenceVisitor<Void>() {

				@Override
				public Void visit(ClassInitializationInference inference) {
					return null;
				}

				@Override
				public Void visit(SubClassOfInference inference) {
					addToQueue(infContext, inference.getPremise(), toDo, traceReader, seenInferences);
					return null;
				}

				@Override
				public Void visit(ConjunctionCompositionInference inference) {
					addToQueue(infContext, inference.getFirstConjunct(), toDo, traceReader, seenInferences);
					addToQueue(infContext, inference.getSecondConjunct(), toDo, traceReader, seenInferences);
					return null;
				}

				@Override
				public Void visit(ConjunctionDecompositionInference inference) {
					addToQueue(infContext, inference.getConjunction(), toDo, traceReader, seenInferences);
					return null;
				}

				@Override
				public Void visit(PropertyChainInference inference) {
					addToQueue(infContext, inference.getFirstChain(), toDo, traceReader, seenInferences);
					addToQueue(infContext, inference.getSecondChain(), toDo, traceReader, seenInferences);
					return null;
				}

				@Override
				public Void visit(ReflexiveInference inference) {
					//TODO
					return null;
				}

				@Override
				public Void visit(ExistentialInference inference) {
					addToQueue(infContext, inference.getBackwardLink(), toDo, traceReader, seenInferences);
					addToQueue(infContext, inference.getSubsumer(), toDo, traceReader, seenInferences);
					return null;
				}

				@Override
				public Void visit(BridgeInference inference) {
					addToQueue(infContext, inference.getConclusion(), toDo, traceReader, seenInferences);
					return null;
				}
				
			});
		}
	}
	
	private void addToQueue(final Context context, final Conclusion conclusion, final Queue<InferenceWrapper> toDo, final TraceStore.Reader traceReader, final Set<Inference> seenInferences) {
		//just need a mutable flag that can be set from inside the visitor 
		final AtomicBoolean infFound = new AtomicBoolean(false);
		
		traceReader.accept(context, conclusion, new BaseInferenceVisitor<Void>(){

			@Override
			protected Void defaultVisit(Inference premiseInference) {
				if (!seenInferences.contains(premiseInference)) {
					seenInferences.add(premiseInference);
					toDo.add(new InferenceWrapper(premiseInference, premiseInference.getContext(context)));
				}
				
				infFound.set(true);
				
				return null;
			}
			
		});
		
		if (!infFound.get()) {
			LOGGER_.error("No inferences for a conclusion {} in context {}", conclusion, context);
		}
	}

	/*
	 * used to propagate context which normally isn't stored within the inference.
	 */
	private static class InferenceWrapper {
		
		final Inference inference;
		final Context context;
		
		InferenceWrapper(Inference inf, Context cxt) {
			inference = inf;
			context = cxt;
		}
		
		/*Context getContext() {
			return inference.getContext(context);
		}*/

		@Override
		public String toString() {
			return inference + " stored in " + context;
		}
		
	}
}
