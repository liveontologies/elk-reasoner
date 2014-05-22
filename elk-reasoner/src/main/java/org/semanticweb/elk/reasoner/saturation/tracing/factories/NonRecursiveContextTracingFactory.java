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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationFactory;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.SubConclusion;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class NonRecursiveContextTracingFactory<J extends ContextTracingJob> implements ContextTracingFactory<J> {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(NonRecursiveContextTracingFactory.class);

	/**
	 * The factory for context saturation with the tracing-enabled rule
	 * application factory.
	 */
	private final ClassExpressionSaturationFactory<J> tracingFactory_;
	/**
	 * This factory saturates sub-contexts which were found missing during tracing
	 */
	private final ContextSubContextSaturationFactory<SubContextSaturationJob<J>> subContextSaturationFactory_;

	private final SaturationState<TracedContext> tracingState_;

	private final Queue<SubContextSaturationJob<J>> jobsToSaturateSubContext_;
	/**
	 * The queue for jobs to resume saturation of a context after its context finished saturation.
	 */
	private final Queue<J> jobsToResume_;
	/**
	 * The queue of started jobs, indexed by the root. Useful because the same
	 * context can be submitted for tracing concurrently. It must be traced by
	 * only one worker but we should still notify all callers when tracing
	 * finished.
	 */
	private final Multimap<IndexedClassExpression, J> jobsInProgress_;
	
	private final ContextTracingListener tracingFinishedListener_;
	
	private final SaturationStatistics aggregateStatistics_;
	
	public NonRecursiveContextTracingFactory(
			SaturationState<?> saturationState,
			SaturationState<TracedContext> tracingState,
			TraceStore traceStore,
			int maxWorkers,
			ContextTracingListener listener
			) {
		// this factory applies all local rules (non-redundant and redundant)
		RuleApplicationFactory<TracedContext, RuleApplicationInput> ruleTracingFactory = new CycleBlockingRuleApplicationFactory(saturationState, tracingState, traceStore);
		
		tracingState_ = tracingState;
		tracingFactory_ = new ClassExpressionSaturationFactory<J>(
				ruleTracingFactory, maxWorkers, new ThisTracingListener());
		subContextSaturationFactory_ = new ContextSubContextSaturationFactory<SubContextSaturationJob<J>>(saturationState, maxWorkers, new ThisSubContextSaturationVisitor());
		jobsToSaturateSubContext_ = new ConcurrentLinkedQueue<SubContextSaturationJob<J>>();
		jobsToResume_ = new ConcurrentLinkedQueue<J>();
		jobsInProgress_ = new HashListMultimap<IndexedClassExpression, J>();
		tracingFinishedListener_ = listener;
		aggregateStatistics_ = new SaturationStatistics();
	}

	@Override
	public Engine getEngine() {
		return new Engine();
	}

	@Override
	public void finish() {
		// aggregating statistics over both factories
		aggregateStatistics_.add(tracingFactory_.getRuleAndConclusionStatistics());
		//TODO add stats from the sub-context saturation factory
		
		tracingFactory_.finish();
		subContextSaturationFactory_.finish();
	}

	@Override
	public SaturationStatistics getStatistics() {
		return aggregateStatistics_;
	}
	
	SaturationState<TracedContext> getTracingSaturationState() {
		return tracingState_;
	}
	
	private void finishTracing(J job) /*throws InterruptedException*/ {
		IndexedClassExpression root = job.getInput();
		TracedContext context = tracingState_.getContext(root);
		
		LOGGER_.info("{} finished tracing", root);
		// cleaning up the auxiliary data structures
		context.clearBlockedInferences();
		context.clearMissingConclusions();
		
		context.setSaturated(true);
		
		notifyCallers(job.getInput());
		context.beingTracedCompareAndSet(true, false);
	}	

	private synchronized void notifyCallers(IndexedClassExpression root) {
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
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	public class Engine implements InputProcessor<J> {

		private final ClassExpressionSaturationFactory<J>.Engine tracingEngine_ = tracingFactory_.getEngine();

		private final ContextSubContextSaturationFactory<SubContextSaturationJob<J>>.Engine subContextSaturationEngine_ = subContextSaturationFactory_.getEngine();
		
		@Override
		public void submit(J job) {
			IndexedClassExpression root = job.getInput();
			TracedContext context = tracingState_.getContext(root);
			
			if (context != null && context.isInitialized() && context.isSaturated()) {
				// do nothing, the context has finished tracing before
				notifyCallers(root);
			}
			
			addTracingJobInProgress(job);
			
			if (context != null && !context.beingTracedCompareAndSet(false, true)) {
				LOGGER_.trace("{} is being traced by another worker", root);
				return;
			}

			LOGGER_.trace("{} first submitted for tracing", root);
			tracingEngine_.submit(job);
		}

		@Override
		public void process() throws InterruptedException {
			for (;;) {
				if (Thread.currentThread().isInterrupted()) {
					LOGGER_.trace("Tracing has been interrupted");					
					break;
				}
				// first, do tracing
				tracingEngine_.process();
				
				if (jobsToSaturateSubContext_.isEmpty()) {
					break;
				}
				
				// second, saturate the relevant sub-contexts in the main saturation state
				submitSubContextSaturationJobs();
				subContextSaturationEngine_.process();
				// third, submit the jobs to resume tracing
				submitResumeTracingJobs();
			}
		}
		
		private void submitResumeTracingJobs() {
			// it's not interruptable
			for (;;) {
				J job = jobsToResume_.poll();

				if (job == null) {
					break;
				}

				tracingEngine_.submit(job);
			}
		}

		private void submitSubContextSaturationJobs() {
			// it's not interruptable
			for (;;) {
				SubContextSaturationJob<J> job = jobsToSaturateSubContext_.poll();
				
				if (job == null) {
					break;
				}
				
				subContextSaturationEngine_.submit(job);
			}
		}

		@Override
		public void finish() {
			tracingEngine_.finish();
			subContextSaturationEngine_.finish();
		}
	}

	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private class ThisTracingListener implements ClassExpressionSaturationListener<J> {

		@Override
		public void notifyFinished(J job) throws InterruptedException {
			TracedContext context = getTracingSaturationState().getContext(job.getInput());
			
			if (context.getMissingSubConclusions().isEmpty()) {
				// done with tracing this context
				finishTracing(job);
			}
			else {
				// initiating saturation of sub-contexts in the main state which were not initialized (or even existed) during tracing
				for (IndexedClassExpression root : context.getMissingSubConclusions().keySet()) {
					for (SubConclusion missing : context.getMissingSubConclusions().get(root)) {
						jobsToSaturateSubContext_.add(new SubContextSaturationJob<J>(root, missing.getSubRoot(), job));
					}
				}
				
				context.clearMissingConclusions();
			}
		}
		
	}
	
	/**
	 * Gets notifications when sub-contexts get saturated.
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private class ThisSubContextSaturationVisitor implements ClassExpressionSaturationListener<SubContextSaturationJob<J>> {

		@Override
		public void notifyFinished(SubContextSaturationJob<J> job) throws InterruptedException {
			jobsToResume_.add(job.getInitiatorJob());
		}
		
	}
}
