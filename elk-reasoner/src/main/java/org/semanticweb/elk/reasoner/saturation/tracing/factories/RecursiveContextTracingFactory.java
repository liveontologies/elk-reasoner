/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import java.util.LinkedList;
import java.util.Queue;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.PremiseVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.OnDemandTracingReader;
import org.semanticweb.elk.reasoner.saturation.tracing.RecursiveTraceUnwinder;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceUnwindingState;
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
	
	private final NonRecursiveContextTracingFactory singleContextTracingFactory_;
	
	private final TraceStore traceStore_;
	
	public RecursiveContextTracingFactory(SaturationState<?> mainSaturationState, SaturationState<TracedContext> tracingSaturationState, TraceStore traceStore) {
		traceStore_ = traceStore;
		singleContextTracingFactory_ = new NonRecursiveContextTracingFactory(mainSaturationState, tracingSaturationState, traceStore);
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
	public class Engine implements InputProcessor<RecursiveContextTracingJob> {
		
		private final Queue<RecursiveContextTracingJob> jobsToDo_;

		private final TraceStore.Reader reader_;
		
		private TraceUnwindingState currentJobState_;
		
		private Engine() {
			jobsToDo_ = new LinkedList<RecursiveContextTracingJob>();
			reader_ = new OnDemandTracingReader(
					singleContextTracingFactory_.getTracingSaturationState(),
					traceStore_.getReader(), singleContextTracingFactory_);
		}

		@Override
		public void process() throws InterruptedException {
			for (;;) {
				LOGGER_.trace("Recursive tracing started");
				
				if (currentJobState_ != null) {
					// this is a leftover from the previous run
					unwind();
				}
				else {
					RecursiveContextTracingJob nextJob = jobsToDo_.poll();
					
					if (nextJob == null) {
						break;
					}
					
					currentJobState_ = new TraceUnwindingState();
					currentJobState_.addToUnwindingQueue(nextJob.getTarget(), nextJob.getInput());
					unwind();
				}
				
				if (Thread.currentThread().isInterrupted()) {
					LOGGER_.trace("Recursive tracing interrupted");
					break;
				}
				// finished the current job, clearing the state
				currentJobState_ = null;
			}
		}

		private void unwind() throws InterruptedException {
			for (;;) {
				Pair<Conclusion, IndexedClassExpression> next = currentJobState_.pollFromUnwindingQueue();

				if (next == null) {
					LOGGER_.trace("Nothing to unwind");
					break;
				}

				LOGGER_.trace("Unwinding started: {} {}", next.getSecond(), next.getFirst());
				
				unwind(next.getFirst(), next.getSecond());
				
				if (Thread.currentThread().isInterrupted()) {
					LOGGER_.trace("Unwinding interrupted, putting the last element back to the queue");
					currentJobState_.addToUnwindingQueue(next.getFirst(), next.getSecond());
					break;
				}

			}
		}

		private void unwind(final Conclusion conclusion, final IndexedClassExpression rootWhereStored) {
			final PremiseVisitor<IndexedClassExpression, ?> premiseVisitor = new PremiseVisitor<IndexedClassExpression, Void>() {

				@Override
				protected Void defaultVisit(Conclusion premise, IndexedClassExpression inferenceContext) {
					currentJobState_.addToUnwindingQueue(premise, inferenceContext);
					return null;
				}
			};

			reader_.accept(rootWhereStored, conclusion,
					new AbstractInferenceVisitor<Void, Void>() {

						@Override
						protected Void defaultTracedVisit(Inference inference, Void _ignored) {
							if (currentJobState_.addToProcessed(inference)) {
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
		}

		@Override
		public void submit(RecursiveContextTracingJob job) {
			jobsToDo_.add(job);
		}
		
	}
}
