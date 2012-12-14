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

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

/**
 * The factory for engines that concurrently submit, process, and post-process
 * saturation jobs. Each saturation job requires to compute implied
 * super-classes of a given indexed class expression. The jobs are submitted
 * using the {@link Engine#submit(SaturationJob)}, and all currently submitted
 * jobs are processed using the {@link Engine#process()}. To every
 * {@link ClassExpressionSaturationFactory} it is possible to attach a
 * {@link ClassExpressionSaturationListener}, which can implement hook methods
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
public class ClassExpressionSaturationFactory<J extends SaturationJob<? extends IndexedClassExpression>>
		implements
		InputProcessorFactory<J, ClassExpressionSaturationFactory<J>.Engine> {

	// logger for this class
	private static final Logger LOGGER_ = Logger
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
	private final RuleApplicationFactory ruleApplicationFactory_;
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
	 * This number of submitted jobs, i.e., those for which the method
	 * {@link Engine#submit(SaturationJob)} was executed.
	 */
	private final AtomicInteger countJobsSubmitted_ = new AtomicInteger(0);
	/**
	 * The number of processed jobs, as determined by the procedure
	 */
	private final AtomicInteger countJobsProcessed_ = new AtomicInteger(0);
	/**
	 * The number of finished jobs, i.e., those for which
	 * {@link ClassExpressionSaturationListener#notifyFinished(Object)} is
	 * executed.
	 */
	private final AtomicInteger countJobsFinished_ = new AtomicInteger(0);

	/**
	 * The buffer for not saturated contexts, i.e., those for which method
	 * {@link Context#isSaturated()} not necessarily returns {@code true}
	 */
	private final Queue<Context> nonSaturatedContexts_;
	/**
	 * The number of contexts created
	 */
	private final AtomicInteger countContextsCreated_ = new AtomicInteger(0);
	/**
	 * The counter of contexts processed, as determined by the procedure
	 */
	private final AtomicInteger countContextsProcessed_ = new AtomicInteger(0);
	/**
	 * The number of created contexts, which are marked as saturated, i.e., for
	 * which {@link Context#isSaturated()} returns {@code true}
	 */
	private final AtomicInteger countContextsFinished_ = new AtomicInteger(0);
	/**
	 * The threshold used to submit new jobs. The job is successfully submitted
	 * if difference between {@link #countContextsCreated_} and {
	 * {@link #countContextsProcessed_} is less than {@link #threshold_};
	 * otherwise the computation is suspended, and will resume only when all
	 * possible rules are applied.
	 */
	private final int threshold_;
	/**
	 * {@code true} if any worker is blocked from submitting the jobs because
	 * threshold is exceeded.
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
	private final AtomicInteger lastInterruptStartedWorkersSnapshot_ = new AtomicInteger(
			0);
	/**
	 * The statistics about this factory aggregated from statistics for all
	 * workers
	 */
	private final ThisStatistics aggregatedStats_;

	/**
	 * Creates a new saturation engine using the given ontology index, listener
	 * for callback functions, and threshold for the number of unprocessed
	 * contexts. The threshold has influence on the size of the batches of the
	 * input jobs that are processed simultaneously, which, in turn, has an
	 * effect on throughput and latency of the saturation: in general, the
	 * larger the threshold is, the faster it takes (in theory) to perform the
	 * overall processing of jobs, but it might take longer to process an
	 * individual job because it is possible to detect that the job is processed
	 * only when the whole batch of jobs is processed.
	 * 
	 * @param ontologyIndex
	 *            the ontology index used to apply the rules
	 * @param maxWorkers
	 *            the maximum number of workers that can use this factory
	 * @param listener
	 *            the listener object implementing callback functions
	 */
	public ClassExpressionSaturationFactory(SaturationState saturationState,
			int maxWorkers, ClassExpressionSaturationListener<J> listener) {
		this(new RuleApplicationFactory(saturationState), maxWorkers, listener);
	}

	public ClassExpressionSaturationFactory(
			RuleApplicationFactory ruleAppFactory, int maxWorkers,
			ClassExpressionSaturationListener<J> listener) {
		this.threshold_ = 64 + 32 * maxWorkers;
		this.listener_ = listener;
		this.jobsToDo_ = new ConcurrentLinkedQueue<J>();
		this.jobsInProgress_ = new ConcurrentLinkedQueue<J>();
		this.ruleApplicationFactory_ = ruleAppFactory;
		this.aggregatedStats_ = new ThisStatistics();
		this.nonSaturatedContexts_ = new ConcurrentLinkedQueue<Context>();
	}

	/**
	 * Creates a new saturation engine using the given ontology index.
	 * 
	 * @param ontologyIndex
	 *            the ontology index used to apply the rules
	 * @param maxWorkers
	 *            the maximum number of workers that can use this factory
	 */
	public ClassExpressionSaturationFactory(
			RuleApplicationFactory ruleAppFactory, int maxWorkers) {
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
		ruleApplicationFactory_.getStatistics().print(LOGGER_);
		checkStatistics();
		if (LOGGER_.isDebugEnabled()) {
			if (aggregatedStats_.jobsSubmittedNo > 0)
				LOGGER_.debug("Saturation Jobs Submitted=Done+Processed: "
						+ aggregatedStats_.jobsSubmittedNo + "="
						+ aggregatedStats_.jobsAlreadyDoneNo + "+"
						+ aggregatedStats_.jobsProcessedNo);
			LOGGER_.debug("Locks: " + aggregatedStats_.locks);
		}
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
				+ +aggregatedStats_.jobsProcessedNo)
			LOGGER_.error("Some submitted saturation jobs were not processed!");
	}
	
	public RuleAndConclusionStatistics getRuleAndConclusionStatistics() {
		return ruleApplicationFactory_.getStatistics();
	}

	/**
	 * Check if the counter for processed jobs can be increased and post-process
	 * the finished jobs
	 * 
	 * @throws InterruptedException
	 */
	private void processFinishedCounters(ThisStatistics localStatistics)
			throws InterruptedException {
		for (;;) {
			int shapshotContextsFinished = countContextsFinished_.get();
			if (shapshotContextsFinished == countContextsProcessed_.get()) {
				break;
			}
			if (countContextsFinished_.compareAndSet(shapshotContextsFinished,
					shapshotContextsFinished + 1)) {
				Context nextContext = nonSaturatedContexts_.poll();
				nextContext.setSaturated(true);
			}
		}
		for (;;) {
			int shapshotJobsFinished = countJobsFinished_.get();
			if (shapshotJobsFinished == countJobsProcessed_.get()) {
				break;
			}
			/*
			 * at this place we know that the number of output jobs is smaller
			 * than the number of processed jobs; we try to increment this
			 * counter if it has not been changed.
			 */
			if (countJobsFinished_.compareAndSet(shapshotJobsFinished,
					shapshotJobsFinished + 1)) {
				/*
				 * It is safe to assume that the next job in the buffer is
				 * processed since we increment the counter for the jobs only
				 * after the job is submitted, and the number of active workers
				 * remains positive until the job is processed.
				 */
				J nextJob = jobsInProgress_.poll();
				IndexedClassExpression root = nextJob.getInput();
				Context rootSaturation = root.getContext();
				rootSaturation.setSaturated(true);
				nextJob.setOutput(rootSaturation);
				if (LOGGER_.isTraceEnabled())
					LOGGER_.trace(root + ": saturation finished");
				localStatistics.jobsProcessedNo++;
				listener_.notifyFinished(nextJob);
			}
		}
	}

	/**
	 * Updates the counter for processed contexts and jobs
	 */
	private void updateProcessedCounters(int snapshotFinishedWorkers) {
		if (lastInterruptStartedWorkersSnapshot_.get() >= countStartedWorkers_
				.get()) {
			/*
			 * after the last started worker was interrupted, no worker has
			 * started yet; in this case we cannot be sure whether submitted
			 * jobs are processed
			 */
			return;
		}
		/*
		 * otherwise, cache the current snapshot for created contexts and jobs;
		 * it is important for correctness to measure the number of started
		 * workers only after that
		 */
		int snapshotCountContextCreated = countContextsCreated_.get();
		int snapshotCountJobsSubmitted = countJobsSubmitted_.get();
		if (countStartedWorkers_.get() > snapshotFinishedWorkers)
			// this means that some started worker did not finish yet
			return;
		/*
		 * if we arrive here, then at the period of time from the beginning of
		 * this function until the test we have: (1) there is no worker that
		 * started processing but did not finished, and (2) after the last
		 * interrupted worker there was a started (and thus finished) worker
		 * that was not interrupted. This means that the taken snapshots
		 * represent at least the number of processed contexts and jobs. In this
		 * case we make sure that the counter for processed jobs and tasks have
		 * at least the values of the corresponding snapshots.
		 */
		updateIfSmaller(countJobsProcessed_, snapshotCountJobsSubmitted);
		if (updateIfSmaller(countContextsProcessed_,
				snapshotCountContextCreated) && workersWaiting_) {
			/*
			 * waking up all workers waiting for new processed contexts
			 */
			synchronized (countContextsProcessed_) {
				workersWaiting_ = false;
				countContextsProcessed_.notifyAll();
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

		private final RuleApplicationFactory.Engine ruleApplicationEngine_ = ruleApplicationFactory_
				.getEngine(new ContextCreationListener() {
					@Override
					public void notifyContextCreation(Context newContext) {
						nonSaturatedContexts_.add(newContext);
						countContextsCreated_.incrementAndGet();
					}
				});

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
			 * created so far in batches: when the number of unprocessed
			 * contexts is below a certain threshold, we add a new saturation
			 * job and process the contexts. How do we know when contexts are
			 * completely processed, i.e., there will be nothing more derived in
			 * a context? This is very difficult to know. We apply the following
			 * strategy: we know that all created contexts are processed when
			 * (1) no worker is creating or processing contexts and (2) after
			 * every worker that was interrupted while processing contexts there
			 * was a worker that has started processing contexts. To check
			 * condition (1), we use two counters: first counter is incremented
			 * before a worker starts processing contexts, the second counter is
			 * incremented after a worker finishes processing contexts.
			 * Therefore, at the moment when the values of both counters
			 * coincide, we know that condition (1) is fulfilled. Now, to check
			 * condition (2) we use a variable, where store the snapshot of the
			 * number of started workers at the moment when the last worker was
			 * interrupted, and when condition (1) is fulfilled, we check if the
			 * value of this snapshot is smaller then the number of started =
			 * the number of finished workers. This way we know that after last
			 * interrupted worker there was a worker that was finished and not
			 * interrupted. To avoid deadlock, it is essential that whenever
			 * conditions (1) and (2) are satisfied, we can update the number of
			 * processed contexts, i.e., the computation was not interrupted in
			 * between processing of contexts and updating this counter.
			 */
			countStartedWorkers_.incrementAndGet();
			ruleApplicationEngine_.process();
			if (Thread.currentThread().isInterrupted())
				updateIfSmaller(lastInterruptStartedWorkersSnapshot_,
						countStartedWorkers_.get());
			updateProcessedCounters(countFinishedWorkers_.incrementAndGet());
			processFinishedCounters(stats_); // can throw InterruptedException
			for (;;) {
				if (Thread.currentThread().isInterrupted())
					return;
				int snapshotCountContextsProcessed = countContextsProcessed_
						.get();
				if (countContextsCreated_.get()
						- snapshotCountContextsProcessed > threshold_) {
					synchronized (countContextsProcessed_) {
						if (countContextsProcessed_.get() > snapshotCountContextsProcessed)
							/*
							 * new contexts were processed meanwhile -- we need
							 * to check again if we can submit a new job
							 */
							continue;
						workersWaiting_ = true;
						stats_.locks++;
						countContextsProcessed_.wait();
						continue;
					}
				}
				J nextJob = jobsToDo_.poll();
				if (nextJob == null)
					return;
				IndexedClassExpression root = nextJob.getInput();
				/*
				 * if the context is already assigned and saturated, this job is
				 * already complete
				 */
				Context rootContext = root.getContext();
				if (rootContext != null && rootContext.isSaturated()) {
					nextJob.setOutput(rootContext);
					stats_.jobsAlreadyDoneNo++;
					listener_.notifyFinished(nextJob); // can throw
														// InterruptedException
					continue;
				}
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(root + ": saturation started");
				}
				/*
				 * submit the job to the rule engine and start processing it
				 */
				countStartedWorkers_.incrementAndGet();
				jobsInProgress_.add(nextJob);
				countJobsSubmitted_.incrementAndGet();
				ruleApplicationEngine_.submit(root);
				ruleApplicationEngine_.process();
				if (Thread.currentThread().isInterrupted())
					updateIfSmaller(lastInterruptStartedWorkersSnapshot_,
							countStartedWorkers_.get());
				updateProcessedCounters(countFinishedWorkers_.incrementAndGet());
				processFinishedCounters(stats_); // can throw
													// InterruptedException
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
