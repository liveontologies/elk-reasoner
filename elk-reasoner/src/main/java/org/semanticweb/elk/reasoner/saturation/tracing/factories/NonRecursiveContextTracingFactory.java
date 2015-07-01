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

import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationFactory;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationListener;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.SimpleInterrupter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class NonRecursiveContextTracingFactory<J extends ContextTracingJob>
		extends SimpleInterrupter implements ContextTracingFactory<J> {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(NonRecursiveContextTracingFactory.class);

	/**
	 * The factory for context saturation with the tracing-enabled rule
	 * application factory.
	 */
	private final ClassExpressionSaturationFactory<J> tracingFactory_;

	private final SaturationState<TracedContext> tracingState_;
	/**
	 * The queue of started jobs, indexed by the root. Useful because the same
	 * context can be submitted for tracing concurrently. It must be traced by
	 * only one worker but we should still notify all callers when tracing
	 * finished.
	 */
	private final Multimap<IndexedContextRoot, J> jobsInProgress_;

	private final ContextTracingListener tracingFinishedListener_;

	private final SaturationStatistics aggregateStatistics_;

	public NonRecursiveContextTracingFactory(
			SaturationState<?> saturationState,
			SaturationState<TracedContext> tracingState, TraceStore traceStore,
			int maxWorkers, ContextTracingListener listener) {
		// this factory applies all local rules (non-redundant and redundant)
		RuleApplicationFactory<TracedContext, RuleApplicationInput> ruleTracingFactory = new CycleBlockingRuleApplicationFactory(
				saturationState, tracingState, traceStore);

		tracingState_ = tracingState;
		tracingFactory_ = new ClassExpressionSaturationFactory<J>(
				ruleTracingFactory, maxWorkers, new ThisTracingListener());
		jobsInProgress_ = new HashListMultimap<IndexedContextRoot, J>();
		tracingFinishedListener_ = listener;
		aggregateStatistics_ = new SaturationStatistics();
	}

	@Override
	public Engine getEngine() {
		return new Engine();
	}

	@Override
	public void setInterrupt(boolean flag) {
		tracingFactory_.setInterrupt(flag);
	}

	@Override
	public void finish() {
		// aggregating statistics over both factories
		aggregateStatistics_.add(tracingFactory_
				.getRuleAndConclusionStatistics());
		tracingFactory_.finish();
	}

	@Override
	public SaturationStatistics getStatistics() {
		return aggregateStatistics_;
	}

	SaturationState<TracedContext> getTracingSaturationState() {
		return tracingState_;
	}

	private void finishTracing(J job) {
		IndexedContextRoot root = job.getInput();
		TracedContext context = tracingState_.getContext(root);

		LOGGER_.trace("{} finished tracing", root);
		// cleaning up the auxiliary data structures
		context.clearBlockedInferences();
		context.setSaturated(true);

		notifyCallers(job.getInput());
		context.beingTracedCompareAndSet(true, false);
	}

	private synchronized void notifyCallers(IndexedContextRoot root) {
		for (J job : jobsInProgress_.get(root)) {
			tracingFinishedListener_.notifyFinished(job);
			job.getCallback().notifyFinished(job);
		}

		jobsInProgress_.remove(root);
	}

	private synchronized void addTracingJobInProgress(J job) {
		jobsInProgress_.add(job.getInput(), job);
	}

	/**
	 * TODO doc
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	public class Engine implements InputProcessor<J> {

		private final ClassExpressionSaturationFactory<J>.Engine tracingEngine_ = tracingFactory_
				.getEngine();

		@Override
		public void submit(J job) {
			IndexedContextRoot root = job.getInput();
			TracedContext context = tracingState_.getContext(root);

			if (context != null && context.isInitialized()
					&& context.isSaturated()) {
				// do nothing, the context has finished tracing before
				notifyCallers(root);
			}

			addTracingJobInProgress(job);

			if (context != null
					&& !context.beingTracedCompareAndSet(false, true)) {
				LOGGER_.trace("{} is being traced by another worker", root);
				return;
			}

			LOGGER_.trace("{} first submitted for tracing", root);
			tracingEngine_.submit(job);
		}

		@Override
		public void process() throws InterruptedException {
			tracingEngine_.process();
		}

		@Override
		public void finish() {
			tracingEngine_.finish();
		}
	}

	/**
	 * 
	 * @author Pavel Klinov
	 *
	 *         pavel.klinov@uni-ulm.de
	 */
	private class ThisTracingListener implements
			ClassExpressionSaturationListener<J> {

		@Override
		public void notifyFinished(J job) throws InterruptedException {
			// done with tracing this context
			finishTracing(job);
		}
	}

}
