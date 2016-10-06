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
	 * The upper bound on the overall number of contexts, which are marked as
	 * saturated by the {@link SaturationState} (not necessarily within this
	 * factory), i.e., the upper bound on the number of calls of
	 * {@link SaturationState#setNextContextSaturated()}
	 */
	private final AtomicInteger countContextsSetSaturatedUpper_ = new AtomicInteger(
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
	 * the number of the started workers at the moment the last worker was
	 * interrupted
	 */
	private final AtomicInteger lastInterruptCountStartedWorkers_ = new AtomicInteger(
			0);
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
	 * {@link RuleApplicationFactory}for applying the rules, the maximal number
	 * of workers that can apply the rules concurrently, and
	 * {@link ClassExpressionSaturationListener} for reporting finished
	 * saturation jobs.
	 * 
	 * saturation state, listener for callback functions, and threshold for the
	 * number of unprocessed contexts.
	 * 
	 * @param ruleAppFactory
	 *            specifies how the rules are applied to new {@link ClassConclusion}s
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
		this.countContextsSetSaturatedUpper_.set(saturationState_
				.getContextSetSaturatedCount());
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
		checkStatistics();
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
	public void setInterrupt(boolean flag) {
		ruleApplicationFactory_.setInterrupt(flag);
		/*
		 * waking up all waiting workers
		 */
		stopWorkersLock_.lock();
		try {
			if (workersWaiting_) {
				workersWaiting_ = false;
				thereAreContextsToProcess_.signalAll();
			}
		} finally {
			stopWorkersLock_.unlock();
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
		if (isInterrupted())
			return;
		// else
		if (aggregatedStats_.jobsSubmittedNo != aggregatedStats_.jobsAlreadyDoneNo
				+ +aggregatedStats_.jobsProcessedNo)
			LOGGER_.error("Some submitted saturation jobs were not processed!");
	}

	public SaturationStatistics getRuleAndConclusionStatistics() {
		return ruleApplicationFactory_.getSaturationStatistics();
	}

	/**
	 * Updates the counter for processed contexts and jobs
	 */
	/**
	 * @param snapshotFinishedWorkers
	 */
	private void updateProcessedCounters(int snapshotFinishedWorkers) {
		if (lastInterruptCountStartedWorkers_.get() >= snapshotFinishedWorkers) {
			/*
			 * This means that this worker might not be the one that has started
			 * after the last worker was interrupted. Since interrupting a
			 * worker interrupts the computation, we cannot be sure that all
			 * submitted jobs are processed unless some worker started after
			 * interrupt and was not interrupted. If such worker exists, then
			 * there should also be a worker for which the counter for finished
			 * workers becomes greater than the number of started workers during
			 * the last interrupt. Thus, this condition becomes false for some
			 * worker. Conversely, if the counter for finished workers becomes
			 * greater, then the counter for started workers is also grater and
			 * so, there must be a worker started after the interrupt. If this
			 * condition does not hold, then this worker is one of them.
			 */
			return;
		}
		/*
		 * take a snapshot for the jobs submitted
		 */
		int snapshotCountJobsSubmitted = countJobsSubmittedUpper_.get();

		if (countStartedWorkers_.get() > snapshotFinishedWorkers)
			/*
			 * this means that some started worker did not finish yet we cannot
			 * say which jobs or contexts are processed
			 */
			return;

		/*
		 * Otherwise, the taken snapshot represents at least the number of jobs
		 * processed (all of them were submitted by the time no worker was
		 * processing the jobs). Now, similarly, take the snapshot for the
		 * counter of non-saturated contexts.
		 */
		int snapshotCountContextNonSaturated = saturationState_
				.getContextMarkNonSaturatedCount();

		if (countStartedWorkers_.get() > snapshotFinishedWorkers)
			/*
			 * some worker have started, our previous counter may have spoiled.
			 */
			return;
		/*
		 * If we arrive here, the taken snapshot represents at least the number
		 * of concepts that become saturated. Furthermore, since we took it
		 * after we counted processed jobs, we know that all contexts for the
		 * processed jobs were created, and thus, have been counted. Now, we
		 * make sure that the counter for processed contexts and jobs have at
		 * least the values of the corresponding snapshots. We first update the
		 * counter for context to make sure that for every processed job the
		 * context was already identified to be processed.
		 */
		if (updateIfSmaller(countContextsSaturatedLower_,
				snapshotCountContextNonSaturated) && workersWaiting_) {
			/*
			 * waking up all workers waiting for new saturated contexts
			 */
			stopWorkersLock_.lock();
			try {
				workersWaiting_ = false;
				thereAreContextsToProcess_.signalAll();
			} finally {
				stopWorkersLock_.unlock();
			}
		}
		/*
		 * It is important to update it last
		 */
		updateIfSmaller(countJobsProcessedLower_, snapshotCountJobsSubmitted);
	}

	/**
	 * Check if the counter for saturated contexts and processed jobs can be
	 * increased and post-process the finished jobs
	 * 
	 * @throws InterruptedException
	 */
	private void processFinishedCounters(ThisStatistics localStatistics)
			throws InterruptedException {

		/*
		 * take snapshots for the number of processed jobs and contexts; the
		 * order is important here to make sure that for every processed job the
		 * context is already considered to be processed
		 */
		int snapshotJobsProcessed = countJobsProcessedLower_.get();
		int snapshotContextsSaturated = countContextsSaturatedLower_.get();
		/*
		 * update the finished context counter at least to the taken snapshot
		 * value and mark the corresponding number of contexts as saturated
		 */
		int shapshotContextsFinished = countContextsSetSaturatedUpper_.get();
		for (;;) {
			if (shapshotContextsFinished >= snapshotContextsSaturated) {
				/*
				 * take the snapshots again so that when we are done we have the
				 * latest snapshots; this is needed when other worker that also
				 * updates these counters (possibly to larger values found) will
				 * not be able to process finished jobs (see the condition
				 * later) because we still set contexts as saturated here. In
				 * this case we will need to take over.
				 */
				snapshotJobsProcessed = countJobsProcessedLower_.get();
				snapshotContextsSaturated = countContextsSaturatedLower_.get();
				if (shapshotContextsFinished >= snapshotContextsSaturated)
					/*
					 * terminate only if we really marked the corresponding
					 * number of contexts as saturated
					 */
					break;
			}
			if (countContextsSetSaturatedUpper_.compareAndSet(
					shapshotContextsFinished, ++shapshotContextsFinished)) {
				saturationState_.setNextContextSaturated();
			} else {
				/*
				 * the counter has changed by a different worker; refresh the
				 * counter and start over
				 */
				shapshotContextsFinished = countContextsSetSaturatedUpper_
						.get();
			}
		}

		int snapshotContextSetSaturated = saturationState_
				.getContextSetSaturatedCount();
		if (countContextsSetSaturatedUpper_.get() > snapshotContextSetSaturated)
			/*
			 * some other may still mark concepts as saturated, we cannot
			 * process finished jobs yet since we do not know which contexts it
			 * marks (all of them are shared between workers)
			 */
			return;

		int snapshotJobsFinished = countJobsFinishedUpper_.get();
		for (;;) {
			if (snapshotJobsFinished >= snapshotJobsProcessed)
				break;
			/*
			 * update the finished context counter at least to the taken
			 * snapshot value and mark the corresponding number of jobs as
			 * processed
			 */
			if (countJobsFinishedUpper_.compareAndSet(snapshotJobsFinished,
					++snapshotJobsFinished)) {
				J nextJob = jobsInProgress_.poll();
				IndexedContextRoot root = nextJob.getInput();
				Context rootSaturation = saturationState_.getContext(root);
				/*
				 * rootSaturation should be already marked as saturated but we
				 * test anyway for debugging purpose.
				 */
				if (rootSaturation.isInitialized()
						&& !rootSaturation.isSaturated()) {
					LOGGER_.error(
							"{}: context for a finished job not saturated!",
							rootSaturation);
				}
				nextJob.setOutput(rootSaturation);

				LOGGER_.trace("{}: saturation finished", root);

				localStatistics.jobsProcessedNo++;
				listener_.notifyFinished(nextJob);// can be interrupted
			} else {
				snapshotJobsFinished = countJobsFinishedUpper_.get();
			}

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
			if (isInterrupted())
				updateIfSmaller(lastInterruptCountStartedWorkers_,
						countStartedWorkers_.get());
			updateProcessedCounters(countFinishedWorkers_.incrementAndGet());
			processFinishedCounters(stats_); // can throw InterruptedException

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
						if (countContextsSaturatedLower_.get() > snapshotCountSaturated
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
				ruleApplicationEngine_.submit(new RuleApplicationInput(root));
				ruleApplicationEngine_.process();

				if (isInterrupted())
					updateIfSmaller(lastInterruptCountStartedWorkers_,
							countStartedWorkers_.get());
				updateProcessedCounters(countFinishedWorkers_.incrementAndGet());
				processFinishedCounters(stats_);
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
