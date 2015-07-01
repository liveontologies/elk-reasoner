/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.factories;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ObjectPropertyConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AbstractConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AbstractObjectPropertyConclusionVIsitor;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.OnDemandTracingReader;
import org.semanticweb.elk.reasoner.saturation.tracing.RecursiveTraceUnwinder;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceUnwindingState;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.PremiseVisitor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.SimpleInterrupter;
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
public class RecursiveContextTracingFactory extends SimpleInterrupter implements
		ContextTracingFactory<RecursiveContextTracingJob> {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(RecursiveContextTracingFactory.class);

	private final NonRecursiveContextTracingFactory<ContextTracingJob> singleContextTracingFactory_;

	private final TraceStore traceStore_;

	private final Queue<RecursiveContextTracingJob> jobsToDo_;

	private final Queue<TraceUnwindingState> jobsInProgress_;

	public RecursiveContextTracingFactory(
			SaturationState<?> mainSaturationState,
			SaturationState<TracedContext> tracingSaturationState,
			TraceStore traceStore, int maxWorkers) {
		traceStore_ = traceStore;
		singleContextTracingFactory_ = new NonRecursiveContextTracingFactory<ContextTracingJob>(
				mainSaturationState, tracingSaturationState, traceStore,
				maxWorkers, ContextTracingListener.DUMMY);
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
				if (isInterrupted()) {
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
				currentState_.addToClassUnwindingQueue(job.getTarget());

				unwindCurrentState();
			}
		}

		private void unwindCurrentState() {
			for (;;) {
				if (isInterrupted()) {
					LOGGER_.trace("Unwinding interrupted");
					return;
				}
				Conclusion next = currentState_.pollFromClassUnwindingQueue();

				if (next == null) {
					// end of class conclusion unwinding
					break;
				}

				LOGGER_.trace("Unwinding of {}", next);

				unwindClassConclusion(next);
				
			}
			// we're done with class conclusions, moving to object property
			// conclusions
			for (;;) {
				ObjectPropertyConclusion next = currentState_
						.pollFromPropertyUnwindingQueue();

				if (next == null) {
					// end of property conclusion unwinding
					currentState_ = null;
					break;
				}

				LOGGER_.trace("Unwinding of {}", next);

				unwindPropertyConclusion(next);

				if (isInterrupted()) {
					LOGGER_.trace("Unwinding interrupted at {}", next);
					// will re-start from this point later
					currentState_.addToPropertyUnwindingQueue(next);

					break;
				}
			}
		}

		private void unwindClassConclusion(final Conclusion conclusion) {
			final PremiseVisitor<IndexedContextRoot, ?> premiseVisitor = new PremiseVisitor<IndexedContextRoot, Void>(
					new AbstractConclusionVisitor<IndexedContextRoot, Void>() {
						@Override
						protected Void defaultVisit(Conclusion premise,
								IndexedContextRoot _ignored) {
							currentState_.addToClassUnwindingQueue(premise);
							return null;
						}
					},
					new AbstractObjectPropertyConclusionVIsitor<IndexedContextRoot, Void>() {
						@Override
						protected Void defaultVisit(
								ObjectPropertyConclusion premise,
								IndexedContextRoot _ignored) {
							// property conclusions are put into another queue
							currentState_.addToPropertyUnwindingQueue(premise);
							return null;
						}
					});

			reader_.accept(
					conclusion,
					new AbstractClassInferenceVisitor<IndexedContextRoot, Void>() {

						@Override
						protected Void defaultTracedVisit(
								ClassInference inference,
								IndexedContextRoot _ignored) {														
							if (currentState_.addToProcessed(inference)) {
								IndexedContextRoot inferenceRoot = inference
										.getInferenceRoot();
								// visit the premises to put into the queue								
								inference.acceptTraced(premiseVisitor,
										inferenceRoot);
							}

							return null;
						}

					});
		}

		private void unwindPropertyConclusion(
				final ObjectPropertyConclusion conclusion) {
			final PremiseVisitor<?, ?> premiseVisitor = new PremiseVisitor<Void, Void>(
					new AbstractObjectPropertyConclusionVIsitor<Void, Void>() {
						@Override
						protected Void defaultVisit(
								ObjectPropertyConclusion premise, Void _ignored) {
							// property conclusions are put into another queue
							currentState_.addToPropertyUnwindingQueue(premise);
							return null;
						}
					});

			reader_.accept(conclusion,
					new AbstractObjectPropertyInferenceVisitor<Void, Void>() {

						@Override
						protected Void defaultTracedVisit(
								ObjectPropertyInference inference, Void _ignored) {
							if (currentState_.addToProcessed(inference)) {
								// visit the premises to put into the queue
								inference.acceptTraced(premiseVisitor, null);
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
			} else {
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
