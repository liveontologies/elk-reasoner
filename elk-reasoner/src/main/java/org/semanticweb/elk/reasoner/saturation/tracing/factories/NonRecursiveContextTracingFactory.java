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

import java.util.Collection;

import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationFactory;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationListener;
import org.semanticweb.elk.reasoner.saturation.ContextCreatingSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.SimpleInterrupter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basically a wrapper around a {@link ClassExpressionSaturationFactory} with a
 * {@link CycleDroppingRuleApplicationFactory} as the rule application factory
 * but transparently handles the situation when the context is being tracing
 * when another tracing job for the same root comes over. It puts it in a
 * pending queue and sends notifications once the context has been traced.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class NonRecursiveContextTracingFactory extends SimpleInterrupter
		implements ContextTracingFactory {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(NonRecursiveContextTracingFactory.class);

	/**
	 * The factory for context saturation with the tracing-enabled rule
	 * application factory.
	 */
	private final ClassExpressionSaturationFactory<ContextTracingJob> tracingFactory_;

	private final SaturationState<TracedContext> tracingState_;
	/**
	 * Pending tracing jobs indexed by the context roots (there could be more
	 * than one job for the same context)
	 */
	private final Multimap<IndexedContextRoot, ContextTracingJob> pendingJobsByRoot_;

	public NonRecursiveContextTracingFactory(
			SaturationState<?> saturationState,
			SaturationState<TracedContext> tracingState, TraceStore traceStore,
			int maxWorkers) {
		RuleApplicationFactory<TracedContext> ruleTracingFactory = new CycleBlockingRuleApplicationFactory(
				saturationState, tracingState, traceStore);

		tracingState_ = tracingState;
		tracingFactory_ = new ClassExpressionSaturationFactory<ContextTracingJob>(
				ruleTracingFactory, maxWorkers,
				new ThisClassExpressionSaturationListener());
		pendingJobsByRoot_ = new HashListMultimap<IndexedContextRoot, ContextTracingJob>();
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
		tracingFactory_.finish();
	}

	void notifyCallers(IndexedContextRoot root) {
		Collection<ContextTracingJob> jobs = removePendingJobs(root);

		for (ContextTracingJob job : jobs) {
			ContextTracingJob.getCallback().notifyFinished(job);
		}
	}

	private synchronized Collection<ContextTracingJob> removePendingJobs(
			IndexedContextRoot root) {
		return pendingJobsByRoot_.remove(root);
	}

	private synchronized void addPendingJob(ContextTracingJob job) {
		pendingJobsByRoot_.add(job.getInput(), job);
	}

	@Override
	public SaturationStatistics getStatistics() {
		return tracingFactory_.getRuleAndConclusionStatistics();
	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	public class Engine implements InputProcessor<ContextTracingJob> {

		private final ClassExpressionSaturationFactory<ContextTracingJob>.Engine tracingEngine_ = tracingFactory_
				.getEngine();

		private final ContextCreatingSaturationStateWriter<TracedContext> tracingContextWriter_ = tracingState_
				.getContextCreatingWriter(ContextCreationListener.DUMMY,
						ContextModificationListener.DUMMY);

		@Override
		public void submit(ContextTracingJob job) {
			IndexedContextRoot root = job.getInput();
			TracedContext context = tracingContextWriter_
					.getCreateContext(root);

			if (!context.isInitialized() || !context.isSaturated()) {
				addPendingJob(job);
				// if the context is being traced now (by the same factory), do
				// nothing as all notifications will be sent when tracing
				// finishes
				if (context.beingTracedCompareAndSet(false, true)) {

					LOGGER_.trace("{} submitted for tracing", root);

					tracingEngine_.submit(job);
				}
			} else {
				// if the context has been traced before, notify the caller
				// immediately
				ContextTracingJob.getCallback().notifyFinished(job);
			}
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
	private class ThisClassExpressionSaturationListener implements
			ClassExpressionSaturationListener<ContextTracingJob> {

		@Override
		public void notifyFinished(ContextTracingJob job)
				throws InterruptedException {
			IndexedContextRoot root = job.getInput();
			TracedContext context = tracingState_.getContext(root);

			LOGGER_.trace("{} finished tracing", root);
			context.beingTracedCompareAndSet(true, false);
			notifyCallers(job.getInput());
		}
	}

}
