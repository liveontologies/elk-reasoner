/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The factory for engines that use a {@link RuleApplicationFactory} to process
 * the corresponding {@link SaturationState} (
 * {@link RuleApplicationFactory#getSaturationState()}). The input for
 * processing is submitted in the form of a {@link SaturationJob} that are
 * forwarded to the corresponding engine of the {@link RuleApplicationFactory}
 * (see
 * {@link RuleApplicationFactory#getEngine(ContextCreationListener, ContextModificationListener)}
 * ). The jobs are submitted using the {@link Engine#submit(SaturationJob)}, and
 * all currently submitted jobs are processed using the {@link Engine#process()}
 * . To every {@link ClassExpressionSaturationFactory} it is possible to attach
 * a {@link ClassExpressionSaturationListener}, which can implement hook methods
 * that perform certain actions during the processing, e.g., notifying when the
 * jobs are finished.
 * 
 * The implementation relies heavily on the Java's concurrency package and uses
 * several atomic integer to monitor the progress of the computation.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <J>
 *            the type of the saturation jobs that can be processed by this
 *            saturation engine
 * @see ClassExpressionSaturationListener
 * 
 */
public class ClassExpressionSaturationFactory<J extends SaturationJob<? extends IndexedContextRoot>>
		implements
		InputProcessorFactory<J, ClassExpressionSaturationFactory<J>.Engine> {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ClassExpressionSaturationFactory.class);

	// TODO: switch to listener factory
	/**
	 * The listener object implementing callback functions for this engine
	 */
	private final ClassExpressionSaturationListener<J> listener_;
	/**
	 * The rule application engine used internally for execution of the
	 * saturation rules.
	 */
	private final RuleApplicationFactory<?, RuleApplicationInput> ruleApplicationFactory_;
	/**
	 * The cached {@link SaturationState} used by the
	 * {@link RuleApplicationFactory}
	 */
	private final SaturationState<?> saturationState_;
	/**
	 * The buffer for jobs that need to be processed, i.e., those for which the
	 * method {@link Engine#submit(SaturationJob)} was executed but processing
	 * of jobs has not been started yet.
	 */
	private final Queue<J> jobsToDo_;
	/**
	 * The buffer for jobs in progress, i.e., those for which processing has
	 * started but the method
	 * {@link ClassExpressionSaturationListener#notifyFinished(Object)} was not
	 * executed yet.
	 */
	private final Queue<J> jobsInProgress_;
	/**
	 * The upper bound on the number of submitted jobs, i.e., those for which
	 * the method {@link Engine#submit(SaturationJob)} was executed.
	 */
	private final AtomicInteger countJobsSubmittedUpper_ = new AtomicInteger(0);
	/**
	 * The lower number of processed jobs, i.e., for which saturation is already
	 * computed
	 */
	private final AtomicInteger countJobsProcessedLower_ = new AtomicInteger(0);
	/**
	 * The upper bound on the number of finished jobs, i.e., those for which
	 * {@link ClassExpressionSaturationListener#notifyFinished(Object)} is
	 * executed.
	 */
	private final AtomicInteger countJobsFinishedUpper_ = new AtomicInteger(0);
	/**
	 * The lower bound on the number of contexts that are saturated (but not
	 * necessarily marked as saturated yet).
	 */
	private final AtomicInteger countContextsSaturatedLower_ = new AtomicInteger(
			0);
	/**
	 * The threshold used to submit new jobs. The job is successfully submitted
	 * if the number of currently non-saturated contexts in the
	 * {@link SaturationState} does not exceed {@link #threshold_}; otherwise
	 * processing of new jobs is suspended, and resumes only when all possible
	 * rules are applied. The threshold has influence on the size of the batches
	 * of the input jobs that are processed simultaneously, which, in turn, has
	 * an effect on throughput and latency of the saturation: in general, the
	 * larger the threshold is, the faster it takes (in theory) to perform the
	 * overall processing of jobs, but it might take longer to process an
	 * individual job because it is possible to detect that the job is processed
	 * only when the whole batch of jobs is processed.
	 */
	private final int threshold_;
	/**
	 * {@code true} if some worker could be blocked because {{@link #threshold_}
	 * is exceeded.
	 */
	private volatile boolean workersWaiting_ = false;
	/**
	 * counter incremented every time a worker starts applying the rules
	 */
	private final AtomicInteger countStartedWorkers_ = new AtomicInteger(0);
	/**
	 * counter incremented every time a worker finishes applying the rules
	 */
	private final AtomicInteger countFinishedWorkers_ = new AtomicInteger(0);
	/**
	 * The statistics about this factory aggregated from statistics for all
	 * workers
	 */
	private final ThisStatistics aggregatedStats_;

	private final ReentrantLock stopWorkersLock_ = new ReentrantLock();

	private final Condition thereAreContextsToProcess_ = stopWorkersLock_
			.newCondition();

	/**
	 * Creates a new {@link ClassExpressionSaturationFactory} using the given
	 * {@link RuleApplicationFactory} for applying the rules, the maximal number
	 * of workers that can apply the rules concurrently, and
	 * {@link ClassExpressionSaturationListener} for reporting finished
	 * saturation jobs.
	 * 
	 * @param ruleAppFactory
	 *            specifies how the rules are applied to new
	 *            {@link ClassConclusion}s
	 * @param maxWorkers
	 *            the maximum number of workers that can use this factory
	 * @param listener
	 *            the listener object implementing callback functions
	 */
	public ClassExpressionSaturationFactory(
			RuleApplicationFactory<?, RuleApplicationInput> ruleAppFactory,
			int maxWorkers, ClassExpressionSaturationListener<J> listener) {
		this.threshold_ = 64 + 32 * maxWorkers;
		this.listener_ = listener;
		this.jobsToDo_ = new ConcurrentLinkedQueue<J>();
		this.jobsInProgress_ = new ConcurrentLinkedQueue<J>();
		this.ruleApplicationFactory_ = ruleAppFactory;
		this.saturationState_ = ruleAppFactory.getSaturationState();
		this.aggregatedStats_ = new ThisStatistics();
	}

	/**
	 * Creates a new {@link ClassExpressionSaturationFactory} using the given
	 * {@link RuleApplicationFactory}for applying the rules and the maximal
	 * number of workers that can apply the rules concurrently.
	 * 
	 * @param ruleAppFactory
	 * @param maxWorkers
	 *            the maximum number of workers that can use this factory
	 */
	public ClassExpressionSaturationFactory(
			RuleApplicationFactory<?, RuleApplicationInput> ruleAppFactory,
			int maxWorkers) {
		/* we use a dummy listener */
		this(ruleAppFactory, maxWorkers,
				new ClassExpressionSaturationListener<J>() {

					@Override
					public void notifyFinished(J job)
							throws InterruptedException {
						// dummy listener does not do anything
					}
				});
	}

	@Override
	public Engine getEngine() {
		return new Engine();
	}

	/**
	 * Print statistics about the saturation
	 */
	public void printStatistics() {
		ruleApplicationFactory_.getSaturationStatistics().print(LOGGER_);
		if (LOGGER_.isDebugEnabled()) {
			if (aggregatedStats_.jobsSubmittedNo > 0)
				LOGGER_.debug(
						"Saturation Jobs Submitted=Done+Processed: {}={}+{}",
						aggregatedStats_.jobsSubmittedNo,
						aggregatedStats_.jobsAlreadyDoneNo,
						aggregatedStats_.jobsProcessedNo);
			LOGGER_.debug("Locks: " + aggregatedStats_.locks);
		}
	}

	@Override
	public boolean isInterrupted() {
		return ruleApplicationFactory_.isInterrupted();
	}

	@Override
	public void finish() {
		checkStatistics();
	}

	/**
	 * Checks if the statistical values make sense and issues error messages if
	 * not
	 */
	private void checkStatistics() {
		if (aggregatedStats_.jobsSubmittedNo != aggregatedStats_.jobsAlreadyDoneNo
				+ aggregatedStats_.jobsProcessedNo) {
			LOGGER_.error("Some submitted saturation jobs were not processed!");
		}
	}

	public SaturationStatistics getRuleAndConclusionStatistics() {
		return ruleApplicationFactory_.getSaturationStatistics();
	}

	/**
	 * waking up all workers waiting for new saturated contexts
	 */
	private void wakeUpWorkers() {
		if (!workersWaiting_) {
			return;
		}
		stopWorkersLock_.lock();
		try {
			workersWaiting_ = false;
			thereAreContextsToProcess_.signalAll();
		} finally {
			stopWorkersLock_.unlock();
		}
	}

	/**
	 * Updates the counter for processed contexts and jobs
	 * 
	 * @param snapshotFinishedWorkers
	 */
	private void updateProcessedCounters(int snapshotFinishedWorkers) {
		if (isInterrupted()) {
			wakeUpWorkers();
			return;
		}
		if (countStartedWorkers_.get() > snapshotFinishedWorkers) {
			/*
			 * We are not the last worker processing the saturation state, so
			 * the current jobs and contexts may not be processed yet.
			 */
			return;
		}
		/*
		 * Otherwise we were the last worker processing the saturation state;
		 * take the values for current jobs and contexts and verify that we are
		 * still the last worker (thus the order is important here).
		 */
		int snapshotCountJobsSubmitted = countJobsSubmittedUpper_.get();
		int snapshotCountContextNonSaturated = saturationState_
				.getContextMarkNonSaturatedCount();
		int snapshotCountStartedWorkers = countStartedWorkers_.get();

		if (snapshotCountStartedWorkers > snapshotFinishedWorkers) {
			/* no longer the last worker */
			return;
		}
		/*
		 * If we arrive here, #snapshotCountJobsSubmitted and
		 * #snapshotCountContextNonSaturated represents at least the number of
		 * jobs processed and saturated contexts. Furthermore, since we took
		 * them in this order, we know that all contexts for the processed jobs
		 * were created, saturated, and counted. Now, we updated the
		 * corresponding counters for the processed contexts and jobs but in the
		 * reversed order to make sure that for every job considered to be
		 * processed all contexts were already considered to be processed.
		 */
		if (updateIfSmaller(countContextsSaturatedLower_,
				snapshotCountContextNonSaturated)) {
			/*
			 * Sleeping workers can now take new inputs.
			 */
			wakeUpWorkers();
		}
		updateIfSmaller(countJobsProcessedLower_, snapshotCountJobsSubmitted);
	}

	/**
	 * Check if the counter for saturated contexts and processed jobs can be
	 * increased and post-process the finished jobs
	 * 
	 * @throws InterruptedException
	 */
	private void updateFinishedCounters(ThisStatistics localStatistics)
			throws InterruptedException {
		int snapshotJobsProcessed = countJobsProcessedLower_.get();
		/*
		 * ensure that all contexts for processed jobs are marked as saturated
		 */
		for (;;) {
			int snapshotCountContextsSaturatedLower = countContextsSaturatedLower_
					.get();
			saturationState_
					.setContextsSaturated(snapshotCountContextsSaturatedLower);
			if (saturationState_
					.getContextSetSaturatedCount() < snapshotCountContextsSaturatedLower) {
				/*
				 * this means that some other worker also sets contexts as
				 * saturated, then it will mark the finished jobs instead
				 */
				return;
			}
			/*
			 * ensure that the counter for processed jobs is still up to date
			 */
			int updatedSnapshotJobsProcessed = countJobsProcessedLower_.get();
			if (updatedSnapshotJobsProcessed == snapshotJobsProcessed) {
				break;
			}
			/* else refresh counters */
			snapshotJobsProcessed = updatedSnapshotJobsProcessed;
		}
		/*
		 * ensure that all processed jobs are finished
		 */
		for (;;) {
			int snapshotJobsFinished = countJobsFinishedUpper_.get();
			if (snapshotJobsFinished >= snapshotJobsProcessed) {
				break;
			}
			/*
			 * update the finished context counter at least to the taken
			 * snapshot value and mark the corresponding number of jobs as
			 * processed
			 */
			if (!countJobsFinishedUpper_.compareAndSet(snapshotJobsFinished,
					snapshotJobsFinished + 1)) {
				/* retry */
				continue;
			}
			// else
			J nextJob = jobsInProgress_.poll();
			IndexedContextRoot root = nextJob.getInput();
			Context rootSaturation = saturationState_.getContext(root);
			if (rootSaturation.isInitialized()
					&& !rootSaturation.isSaturated()) {
				LOGGER_.error("{}: context for a finished job not saturated!",
						rootSaturation);
			}
			nextJob.setOutput(rootSaturation);

			LOGGER_.trace("{}: saturation finished", root);

			localStatistics.jobsProcessedNo++;
			listener_.notifyFinished(nextJob);// can be interrupted
		}

	}

	/**
	 * Update the counter to the value provided it is greater. Regardless of the
	 * returned value, it is guaranteed that the value of the counter after
	 * execution will be at least the input value.
	 * 
	 * @param counter
	 *            the counter that should be updated
	 * @param value
	 *            the value to which the counter should be updated
	 * @return {@code true} if the counter has been updated
	 */
	private static boolean updateIfSmaller(AtomicInteger counter, int value) {
		for (;;) {
			int snapshotCoutner = counter.get();
			if (snapshotCoutner >= value)
				return false;
			if (counter.compareAndSet(snapshotCoutner, value))
				return true;
		}
	}

	public class Engine implements InputProcessor<J> {

		private final InputProcessor<RuleApplicationInput> ruleApplicationEngine_ = ruleApplicationFactory_
				.getEngine(ContextCreationListener.DUMMY,
						ContextModificationListener.DUMMY);

		private final ThisStatistics stats_ = new ThisStatistics();

		// don't allow creating of engines directly; only through the factory
		private Engine() {
		}

		@Override
		public void submit(J job) {
			jobsToDo_.add(job);
			stats_.jobsSubmittedNo++;
		}

		@Override
		public void process() throws InterruptedException {
			/*
			 * This works as follows. We apply inference rules to the contexts
			 * created so far in batches: when the number of unsaturated
			 * contexts is below a certain threshold, we add a new saturation
			 * job and process the contexts. How do we know when contexts are
			 * completely processed, i.e., there could be nothing more derived
			 * in the context? This is very difficult to know. We apply the
			 * following strategy: we know that all created contexts are
			 * processed when (1) no worker is creating or processing contexts
			 * and (2) after every worker that was interrupted while processing
			 * contexts there was a worker that has started processing contexts.
			 * To check condition (1), we use two counters: first counter is
			 * incremented before a worker starts processing contexts, the
			 * second counter is incremented after a worker finishes processing
			 * contexts. Therefore, at the moment when the values of both
			 * counters coincide, we know that condition (1) is fulfilled. To
			 * check condition (2) we use a variable, where store the snapshot
			 * of the number of started workers at the moment when the last
			 * worker was interrupted, and when condition (1) is fulfilled, we
			 * check if the value of this snapshot is smaller then the number of
			 * started = the number of finished workers. This way we know that
			 * after the last interrupted worker there was a worker that was
			 * finished and not interrupted. To avoid deadlock, it is essential
			 * that whenever conditions (1) and (2) are satisfied, we can update
			 * the number of processed contexts, i.e., the computation was not
			 * interrupted in between processing of contexts and updating these
			 * counters.
			 */
			countStartedWorkers_.incrementAndGet();
			ruleApplicationEngine_.process();
			updateProcessedCounters(countFinishedWorkers_.incrementAndGet());
			updateFinishedCounters(stats_); // can throw InterruptedException

			for (;;) {
				if (isInterrupted()) {
					return;
				}
				int snapshotCountSaturated = countContextsSaturatedLower_.get();
				if (saturationState_.getContextMarkNonSaturatedCount()
						- snapshotCountSaturated > threshold_) {
					stopWorkersLock_.lock();
					try {
						workersWaiting_ = true;
						stats_.locks++;
						/*
						 * it is important to set waiting workers before
						 * checking processed contexts counters because it is
						 * tested in the other order when waking up the workers
						 */
						if (countContextsSaturatedLower_
								.get() > snapshotCountSaturated
								|| isInterrupted()) {
							/*
							 * new contexts were processed meanwhile; all
							 * workers should be notified
							 */
							workersWaiting_ = false;
							thereAreContextsToProcess_.signalAll();
							continue;
						}
						thereAreContextsToProcess_.await();
						continue;
					} finally {
						stopWorkersLock_.unlock();
					}
				}
				J nextJob = jobsToDo_.poll();
				if (nextJob == null)
					return;
				IndexedContextRoot root = nextJob.getInput();
				/*
				 * if the context is already assigned and saturated, this job is
				 * already complete
				 */
				Context rootContext = saturationState_.getContext(root);

				if (rootContext != null && rootContext.isInitialized()
						&& rootContext.isSaturated()) {
					nextJob.setOutput(rootContext);
					stats_.jobsAlreadyDoneNo++;
					// can throw InterruptedException
					listener_.notifyFinished(nextJob);
					continue;
				}

				LOGGER_.trace("{}: saturation started", root);
				/*
				 * submit the job to the rule engine and start processing it
				 */
				countStartedWorkers_.incrementAndGet();
				countJobsSubmittedUpper_.incrementAndGet();
				jobsInProgress_.add(nextJob);
				if (rootContext == null || !rootContext.isInitialized()) {
					// if context is assigned and initialized, saturation is already in progress or finished
					ruleApplicationEngine_
							.submit(new RuleApplicationInput(root));
				}
				ruleApplicationEngine_.process();
				updateProcessedCounters(
						countFinishedWorkers_.incrementAndGet());
				updateFinishedCounters(stats_);
			}
		}

		@Override
		public void finish() {
			ruleApplicationEngine_.finish();
			aggregatedStats_.merge(stats_);
		}

	}

	/**
	 * Counters accumulating statistical information about this factory.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private static class ThisStatistics {
		/**
		 * the number of submitted jobs
		 */
		int jobsSubmittedNo;
		/**
		 * submitted jobs that were already done when they started to be
		 * processed
		 */
		int jobsAlreadyDoneNo;
		/**
		 * submitted jobs that were finished by this engine
		 */
		int jobsProcessedNo;
		/**
		 * counts how many times workers have been waiting
		 */
		int locks;

		public synchronized void merge(ThisStatistics statistics) {
			this.jobsSubmittedNo += statistics.jobsSubmittedNo;
			this.jobsProcessedNo += statistics.jobsProcessedNo;
			this.jobsAlreadyDoneNo += statistics.jobsAlreadyDoneNo;
			this.locks += statistics.locks;
		}
	}

}
