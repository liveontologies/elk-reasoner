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
package org.semanticweb.elk.reasoner.tracing.factories;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import org.liveontologies.puli.Producer;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationFactory;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.tracing.TraceState;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;
import org.semanticweb.elk.util.concurrent.computation.Processor;
import org.semanticweb.elk.util.concurrent.computation.ProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The factory for engines for tracing of {@link ClassInference}s deriving
 * {@link ClassConclusion}s queued up in {@link TraceState}. Processing one
 * {@link ClassConclusion} results in {@link ClassInference}s with origin that
 * is {@link ClassConclusion#getTraceRoot()}, are applicable to
 * {@link ClassConclusion}s stored in the provided {@link SaturationState} and
 * produce {@link ClassConclusion}s present in the {@link SaturationState}.
 * {@link TraceState#getTracingListener()} is notified about these inferences.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 */
public class SingleContextTracingFactory
		implements ProcessorFactory<SingleContextTracingFactory.Engine> {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(SingleContextTracingFactory.class);

	private final TraceState tracingState_;

	/**
	 * Inferences per finished contexts.
	 */
	private final ConcurrentMap<IndexedContextRoot, Queue<ClassInference>> tracedInferences_;

	/**
	 * The factory for context saturation with the tracing-enabled rule
	 * application factory.
	 */
	private final ClassExpressionSaturationFactory<SingleContextTracingJob> saturationFactory_;

	public SingleContextTracingFactory(final TraceState tracingState,
			final InterruptMonitor interrupter,
			final SaturationState<?> saturationState, final int maxWorkers) {
		this.tracingState_ = tracingState;
		// applying all local rules, saving the inferences using the class
		// inference producer
		this.saturationFactory_ = new ClassExpressionSaturationFactory<SingleContextTracingJob>(
				new ContextTracingRuleApplicationFactory(interrupter,
						saturationState, new ThisClassInferenceProducer()),
				maxWorkers, new ThisClassExpressionSaturationListener());
		this.tracedInferences_ = new ConcurrentHashMap<IndexedContextRoot, Queue<ClassInference>>();
	}

	@Override
	public Engine getEngine() {
		return new Engine();
	}

	@Override
	public void finish() {
		saturationFactory_.finish();
	}

	@Override
	public boolean isInterrupted() {
		return saturationFactory_.isInterrupted();
	}

	public void printStatistics() {
		saturationFactory_.printStatistics();
	}

	public SaturationStatistics getRuleAndConclusionStatistics() {
		return saturationFactory_.getRuleAndConclusionStatistics();
	}

	public class Engine implements Processor {

		private final ClassExpressionSaturationFactory<SingleContextTracingJob>.Engine saturationEngine_ = saturationFactory_
				.getEngine();

		@Override
		public void process() throws InterruptedException {
			for (;;) {
				if (isInterrupted()) {
					return;
				}
				saturationEngine_.process();
				final ClassConclusion conclusion = tracingState_.pollToTrace();
				if (conclusion == null) {
					break;
				}
				// else
				saturationEngine_
						.submit(new SingleContextTracingJob(conclusion));
			}
		}

		@Override
		public void finish() {
			saturationEngine_.finish();
		}

	}

	private class ThisClassInferenceProducer
			implements Producer<ClassInference> {

		@Override
		public void produce(final ClassInference inference) {
			final IndexedContextRoot originRoot = inference.getTraceRoot();
			Queue<ClassInference> inferencesByOrigin = tracedInferences_
					.get(originRoot);
			if (inferencesByOrigin == null) {
				inferencesByOrigin = new ConcurrentLinkedQueue<ClassInference>();
				final Queue<ClassInference> previous = tracedInferences_
						.putIfAbsent(originRoot, inferencesByOrigin);
				if (previous != null)
					inferencesByOrigin = previous;
			}
			inferencesByOrigin.add(inference);
		}

	}

	private class ThisClassExpressionSaturationListener implements
			ClassExpressionSaturationListener<SingleContextTracingJob> {

		@Override
		public void notifyFinished(final SingleContextTracingJob job)
				throws InterruptedException {
			// all inferences for this job are computed
			final IndexedContextRoot root = job.getInput();
			tracedInferences_.get(root);
			LOGGER_.trace("{}: job finished", job);
			tracingState_.getTracingListener().notifyFinished(
					job.getGoalConclusion(), tracedInferences_.get(root));
		}

	}

}
