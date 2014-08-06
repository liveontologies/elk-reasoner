/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.OnDemandTracingReader;
import org.semanticweb.elk.reasoner.saturation.tracing.RecursiveTraceUnwinder;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceUnwindingState;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.PremiseVisitor;
import org.semanticweb.elk.util.collections.Pair;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Recursively unwinds the trace and submits all necessary contexts for tracing
 * using {@link OnDemandTracingReader}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RecursiveContextTracingFactory implements ContextTracingFactory<RecursiveContextTracingJob> {

	private static final Logger LOGGER_ = LoggerFactory.getLogger(RecursiveContextTracingFactory.class);
	
	private final NonRecursiveContextTracingFactory<ContextTracingJob> singleContextTracingFactory_;
	
	private final TraceStore traceStore_;
	
	private final Queue<RecursiveContextTracingJob> jobsToDo_;
	
	private final Queue<TraceUnwindingState> jobsInProgress_;
	
	public RecursiveContextTracingFactory(SaturationState<?> mainSaturationState, SaturationState<TracedContext> tracingSaturationState, TraceStore traceStore, int maxWorkers) {
		traceStore_ = traceStore;
		singleContextTracingFactory_ = new NonRecursiveContextTracingFactory<ContextTracingJob>(
				mainSaturationState,
				tracingSaturationState,
				traceStore,
				maxWorkers,
				ContextTracingListener.DUMMY);
		jobsToDo_ = new ConcurrentLinkedQueue<RecursiveContextTracingJob>();
		jobsInProgress_ = new ConcurrentLinkedQueue<TraceUnwindingState>();
	}
	
	@Override
	public InputProcessor<RecursiveContextTracingJob> getEngine() {
		return new Engine();
	}

	@Override
	public void finish() {
		singleContextTracingFactory_.finish();
	}

	@Override
	public SaturationStatistics getStatistics() {
		return singleContextTracingFactory_.getStatistics();
	}
	
	/**
	 * A simple wrapper around {@link RecursiveTraceUnwinder} which does all the
	 * heavy lifting.
	 */
	class Engine implements InputProcessor<RecursiveContextTracingJob> {
		
		private final TraceStore.Reader reader_;
		
		private TraceUnwindingState currentState_;
		
		private Engine() {
			reader_ = new OnDemandTracingReader(
					singleContextTracingFactory_.getTracingSaturationState(),
					traceStore_.getReader(), singleContextTracingFactory_);
		}

		@Override
		public void process() throws InterruptedException {
			LOGGER_.trace("Recursive tracing started");
			
			for (;;) {
				if (Thread.currentThread().isInterrupted()) {
					LOGGER_.trace("Recursive tracing interrupted");
					finish();
					
					break;
				}
				// first, continue some existing unwinding
				currentState_ = jobsInProgress_.poll();
				
				if (currentState_ != null) {
					unwindCurrentState();
					continue;
				}
				// second, start new unwinding
				RecursiveContextTracingJob job = jobsToDo_.poll();
				
				if (job == null) {
					LOGGER_.trace("Recursive tracing finished");
					
					break;
				}
				
				currentState_ = new TraceUnwindingState();
				currentState_.addToUnwindingQueue(job.getTarget(), job.getInput());
				
				unwindCurrentState();
			}
		}

		private void unwindCurrentState() throws InterruptedException {
			for (;;) {
				Pair<Conclusion, IndexedClassExpression> next = currentState_.pollFromUnwindingQueue();

				if (next == null) {
					// end of unwinding
					currentState_ = null;
					break;
				}

				LOGGER_.trace("Unwinding of {} in {}", next.getFirst(), next.getSecond());
				
				unwind(next.getFirst(), next.getSecond());
				
				if (Thread.currentThread().isInterrupted()) {
					LOGGER_.trace("Unwinding interrupted at {} in {}", next.getFirst(), next.getSecond());
					// will re-start from this point later
					currentState_.addToUnwindingQueue(next.getFirst(), next.getSecond());
					
					break;
				}
			}
		}

		private void unwind(final Conclusion conclusion, final IndexedClassExpression rootWhereStored) {
			final PremiseVisitor<IndexedClassExpression, ?> premiseVisitor = new PremiseVisitor<IndexedClassExpression, Void>() {

				@Override
				protected Void defaultVisit(Conclusion premise, IndexedClassExpression inferenceContext) {
					currentState_.addToUnwindingQueue(premise, inferenceContext);
					return null;
				}
			};

			reader_.accept(rootWhereStored, conclusion,
					new AbstractInferenceVisitor<Void, Void>() {

						@Override
						protected Void defaultTracedVisit(Inference inference, Void _ignored) {
							if (currentState_.addToProcessed(inference)) {
								IndexedClassExpression inferenceContextRoot = inference.getInferenceContextRoot(rootWhereStored);
								// visit the premises to put into the queue
								inference.acceptTraced(premiseVisitor, inferenceContextRoot);
							}

							return null;
						}

					});
		}

		@Override
		public void finish() {
			// saving the unfinished job
			if (currentState_ != null) {
				LOGGER_.trace("Unwinding interrupted but will be resumed");
				
				jobsInProgress_.add(currentState_);
			}
			else {
				LOGGER_.trace("Unwinding finished normally");
			}
			
			currentState_ = null;
		}

		@Override
		public void submit(RecursiveContextTracingJob job) {
			jobsToDo_.add(job);
		}
		
	}
}
