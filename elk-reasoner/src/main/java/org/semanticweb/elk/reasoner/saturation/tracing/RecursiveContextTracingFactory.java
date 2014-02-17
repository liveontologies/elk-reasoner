/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationFactory;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationListener;
import org.semanticweb.elk.reasoner.saturation.ExtendedSaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.BaseConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionOccurranceCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.tracing.RecursiveContextTracingFactory.Engine;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracingWriter;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Traces contexts using a non-recursive factory and then unwinds the computed
 * trace until it hits a conclusion which belongs to a non-traced contexts. Then
 * the latter is recursively submitted for tracing.
 * 
 * This factory supports concurrent tracing of contexts during processing of the
 * same tracing job.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RecursiveContextTracingFactory implements InputProcessorFactory<TracingJob, Engine> {
	
	private static final Logger LOGGER_ = LoggerFactory.getLogger(RecursiveContextTracingFactory.class);
	
	/**
	 * Factory for non-recursive context tracing.
	 */
	private final ClassExpressionSaturationFactory<TracingJob> tracingFactory_;
	/**
	 * queue of tracing jobs to complete
	 */
	private final Queue<TracingJob> toTraceQueue_;
	
	private final TraceState traceState_;
	
	private final TracedContextProcessor traceUnwinder_;

	/**
	 * 
	 */
	public RecursiveContextTracingFactory(
			ExtendedSaturationState saturationState, TraceState traceState, int maxWorkers) {
		RuleApplicationFactory ruleTracingFactory = new CycleDroppingRuleApplicationFactory(saturationState, traceState.getSaturationState(), traceState.getTraceStore());
		
		toTraceQueue_ = new ConcurrentLinkedQueue<TracingJob>();
		traceState_ = traceState;
		tracingFactory_ = new ClassExpressionSaturationFactory<TracingJob>(
				ruleTracingFactory, maxWorkers,
				new ThisClassExpressionSaturationListener());
		traceUnwinder_ = new TracedContextProcessor();
	}
	
	@Override
	public Engine getEngine() {
		return new Engine();
	}

	@Override
	public void finish() {
		tracingFactory_.finish();
	}
	
	/**
	 * Print statistics about the saturation
	 */
	public void printStatistics() {
		tracingFactory_.printStatistics();
	}

	public SaturationStatistics getRuleAndConclusionStatistics() {
		return tracingFactory_.getRuleAndConclusionStatistics();
	}	
	
	private boolean scheduleTraceUnwinding(TracedContext context, Conclusion target) {
		if (context.addConclusionToTrace(target) ) {
			LOGGER_.trace("{}: scheduling trace unwinding for {}", context, target);
			
			return true;
		}
		else {
			return false;
		}
	}	

	/**
	 * TODO
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	public class Engine implements InputProcessor<TracingJob> {

		private final ClassExpressionSaturationFactory<TracingJob>.Engine tracingEngine_ = tracingFactory_.getEngine();
		
		private final TracingWriter tracingContextWriter_ = traceState_.getSaturationState().getTracingWriter();
		
		@Override
		public void submit(TracingJob job) {
			IndexedClassExpression root = job.getInput();
			Conclusion target = job.getConclusion();
			
			if (entailed(root, target)) {
				submitInternal(job);
			}
			else {
				LOGGER_.info("{}: tracing skipped, target not entailed: {}", root, target);
			}
		}
		
		private void submitInternal(TracingJob job) {
			IndexedClassExpression root = job.getInput();
			Conclusion target = job.getConclusion();
			TracedContext context = tracingContextWriter_.getCreateContext(root);
			
			if (scheduleTraceUnwinding(context, target)) {
				// here we decide if the context needs to be traced or just some
				// traces need to be unwound
				if (context.beingTracedCompareAndSet(false, true)) {
					LOGGER_.trace("{} submitted for tracing, target: {}", root, target);

					tracingEngine_.submit(job);	
				}
			}
		}

		private boolean entailed(IndexedClassExpression root, Conclusion target) {
			return target.accept(new ConclusionOccurranceCheckingVisitor(), root.getContext());
		}

		@Override
		public void process() throws InterruptedException {
			for (;;) {
				if (Thread.currentThread().isInterrupted())
					return;

				tracingEngine_.process();
				
				TracingJob nextJob = toTraceQueue_.poll();
				
				if (nextJob == null) {
					break;
				}
				
				submitInternal(nextJob);
			}
		}

		@Override
		public void finish() {
			tracingEngine_.finish();
		}

	}

	/**
	 * Processes contexts that have been traced and unwinds its traced
	 * conclusions.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class TracedContextProcessor {

		private final RecursiveTraceExplorer traceExplorer_;
	
		TracedContextProcessor() {
			TraceStore.Reader traceReader = traceState_.getTraceStore().getReader();
			LocalTracingSaturationState traceState = traceState_.getSaturationState();
			
			traceExplorer_ = new RecursiveTraceExplorer(traceReader, traceState);
		}
		
		void process(final TracedContext context) {
			for (;;) {
				final IndexedClassExpression root = context.getRoot();
				final Conclusion next = context.pollForConclusionToTrace();
				
				if (next == null) {
					break;
				}
				
				LOGGER_.trace("{}: unwinding the trace for {}", root, next);
				
				traceExplorer_.accept(root.getContext(), next, new BaseConclusionVisitor<Boolean, Context>() {

					@Override
					protected Boolean defaultVisit(Conclusion conclusion, 	Context cxt) {
						if (cxt.getRoot() == root) {
							return true;
						}
						
						Context conclusionContext = conclusion.getSourceContext(cxt);
						TracedContext tracingContext = traceState_.getSaturationState().getContext(conclusionContext.getRoot());
						//schedule tracing if the context (to which this conclusion belongs) hasn't been traced. 
						//the second condition isn't necessary for correctness but helps to avoid multiple stack unwindings
						//for the same conclusion.
						if (!tracingContext.isSaturated() && !tracingContext.isTraced(conclusion)) {
							LOGGER_.trace("{}: recursively submitted for tracing, target: {}", tracingContext, conclusion);
							
							toTraceQueue_.add(new TracingJob(tracingContext.getRoot(), conclusion));
						}

						return true;
					}
					
				});
			}
		}
	}
	
	/**
	 * Used to trigger unwinding of the context which just finished tracing. 
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private class ThisClassExpressionSaturationListener implements ClassExpressionSaturationListener<TracingJob> {

		@Override
		public void notifyFinished(TracingJob job) throws InterruptedException {
			final IndexedClassExpression root = job.getInput();
			final TracedContext context = traceState_.getSaturationState().getContext(root);
			
			context.beingTracedCompareAndSet(true, false);
			traceUnwinder_.process(context);
		}		
	}
	
}
