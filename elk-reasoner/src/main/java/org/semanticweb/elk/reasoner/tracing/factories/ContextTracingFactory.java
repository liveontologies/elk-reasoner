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

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationFactory;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.tracing.TracingInferenceProducer;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The factory for engines for tracing of {@link ClassInference}s applied to
 * conclusions in a given {@link SaturationState}. The engines accept instances
 * of {@link ContextTracingJob} with the input {@link IndexedContextRoot}
 * origin; when this job is processed, its output will contain all inferences
 * with the given origin that are applicable to {@link ClassConclusion}s stored in
 * the {@link SaturationState} and produce {@link ClassConclusion}s present in the
 * {@link SaturationState}.
 * 
 * As usual, to this engine factory it is possible to attach a
 * {@link ContextTracingListener} using which one can perform actions upon
 * completion of {@link ContextTracingJob}s.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class ContextTracingFactory<R extends IndexedContextRoot, J extends ContextTracingJob<R>>
		implements
		InputProcessorFactory<J, ContextTracingFactory<R, J>.Engine> {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ContextTracingFactory.class);

	/**
	 * An object were to save the inferences
	 */
	private final ConcurrentMap<IndexedContextRoot, Queue<ClassInference>> tracedInferences_;

	/**
	 * The factory for context saturation with the tracing-enabled rule
	 * application factory.
	 */
	private final ClassExpressionSaturationFactory<SaturationJobForContextTracing<R, J>> saturationFactory_;

	/**
	 * The listener object implementing callback functions for this engine
	 */
	private final ContextTracingListener<R, J> listener_;

	public ContextTracingFactory(final InterruptMonitor interrupter,
			SaturationState<?> saturationState, int maxWorkers,
			ContextTracingListener<R, J> listener) {
		// applying all local rules, saving the inferences using the class inference producer
		this.saturationFactory_ = new ClassExpressionSaturationFactory<SaturationJobForContextTracing<R, J>>(
				new ContextTracingRuleApplicationFactory(interrupter,
						saturationState, new ThisClassInferenceProducer()),
				maxWorkers, new ThisClassExpressionSaturationListener());
		this.listener_ = listener;
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

	/**
	 * TODO doc
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	public class Engine implements InputProcessor<J> {

		private final ClassExpressionSaturationFactory<SaturationJobForContextTracing<R, J>>.Engine saturationEngine_ = saturationFactory_
				.getEngine();

		@Override
		public void submit(J job) {
			LOGGER_.trace("{}: job submitted", job);
			saturationEngine_.submit(new SaturationJobForContextTracing<R, J>(
					job));
		}

		@Override
		public void process() throws InterruptedException {
			saturationEngine_.process();
		}

		@Override
		public void finish() {
			saturationEngine_.finish();
		}
	}

	private class ThisClassInferenceProducer implements TracingInferenceProducer<ClassInference> {

		@Override
		public void produce(ClassInference inference) {
			IndexedContextRoot originRoot = inference.getTraceRoot();
			Queue<ClassInference> inferencesByOrigin = tracedInferences_
					.get(originRoot);
			if (inferencesByOrigin == null) {
				inferencesByOrigin = new ConcurrentLinkedQueue<ClassInference>();
				Queue<ClassInference> previous = tracedInferences_.putIfAbsent(
						originRoot, inferencesByOrigin);
				if (previous != null)
					inferencesByOrigin = previous;
			}
			inferencesByOrigin.add(inference);
		}
	}

	/**
	 * 
	 * The {@link ClassExpressionSaturationListener} for the
	 * {@link ClassExpressionSaturationFactory} used within this
	 * {@link ContextTracingFactory}
	 * 
	 * @author Pavel Klinov
	 *
	 *         pavel.klinov@uni-ulm.de
	 * @author "Yevgeny Kazakov"
	 */
	private class ThisClassExpressionSaturationListener
			implements
			ClassExpressionSaturationListener<SaturationJobForContextTracing<R, J>> {

		@Override
		public void notifyFinished(
				SaturationJobForContextTracing<R, J> tracingJob)
				throws InterruptedException {
			// all inferences for this origin are computed
			J job = tracingJob.getInitiatorJob();
			job.setOutput(tracedInferences_.get(job.getInput()));
			LOGGER_.trace("{}: job finished", job);
			listener_.notifyFinished(job);
		}
	}

}
