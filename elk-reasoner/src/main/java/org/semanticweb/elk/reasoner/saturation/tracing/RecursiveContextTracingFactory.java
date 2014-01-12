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
import org.semanticweb.elk.reasoner.saturation.tracing.TracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingSaturationState.TracingWriter;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Traces contexts using a non-recursive factory and then unwinds the computed
 * trace until it hits a conclusion which belongs to a non-traced contexts. Then
 * the latter is recursively submitted for tracing.
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
		RuleApplicationFactory ruleTracingFactory = new ContextTracingFactory(saturationState, traceState);
		
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
	
	private void scheduleTraceUnwinding(TracedContext context, Conclusion target) {
		LOGGER_.trace("{}: scheduling unwinding for {}", context, target);
		
		context.addConclusionToTrace(target);
	}	

	/**
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
			// here we decide if the context needs to be traced or just some
			// traces need to be unwound
			if (context.beingTracedCompareAndSet(false, true)) {
				LOGGER_.trace("{}: recursive context tracing started, target: {}", root, target);

				tracingEngine_.submit(job);	
			}
			else {
				scheduleTraceUnwinding(context, target);
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
		//this explorer should be thread-safe, it only keeps a reference to a thread-safe trace reader.
		private final RecursiveTraceExplorer traceExplorer_ = new RecursiveTraceExplorer(traceState_.getTraceStore().getReader(), traceState_.getSaturationState());
	
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
						Context tracingContext = traceState_.getSaturationState().getContext(conclusionContext.getRoot());
						
						if (!tracingContext.isSaturated()) {
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
			scheduleTraceUnwinding(context, job.getConclusion());
			traceUnwinder_.process(context);
		}		
	}
	
}
