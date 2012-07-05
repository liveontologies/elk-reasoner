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
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.classes.ContextClassSaturation;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Context;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationListener;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

/**
 * The factory for engines concurrently that submit, process, and post-process
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
	private final ClassExpressionSaturationListener<J, Engine> listener;
	/**
	 * The rule application engine used internally for execution of the
	 * saturation rules.
	 */
	private final RuleApplicationFactory ruleApplicationFactory;
	/**
	 * The buffer for jobs that need to be processed, i.e., those for which the
	 * method {@link #submit(J)} was executed but processing of jobs has not
	 * been started yet.
	 */
	private final Queue<J> jobsToDo;
	/**
	 * The buffer for jobs in progress, i.e., those for which processing has
	 * started but the method {@link #listener.notifyFinished(J)} was not
	 * executed yet.
	 */
	private final Queue<J> jobsInProgress;
	/**
	 * This number of submitted jobs, i.e., those for which the method
	 * {@link #submit(J)} was executed.
	 */
	private final AtomicInteger countJobsSubmitted = new AtomicInteger(0);
	/**
	 * The number of processed jobs, as determined by the procedure
	 */
	private final AtomicInteger countJobsProcessed = new AtomicInteger(0);
	/**
	 * The number of finished jobs, i.e., those for which
	 * {@link #listener.notifyFinished(J)} is executed.
	 */
	private final AtomicInteger countJobsFinished = new AtomicInteger(0);
	/**
	 * The number of processed contexts; used to control batches of jobs
	 */
	private final AtomicInteger countContextsProcessed = new AtomicInteger(0);
	/**
	 * The threshold used to submit new jobs. The job is successfully submitted
	 * if difference between the number of created contexts and processed
	 * contexts does not exceed this threshold; otherwise the computation is
	 * suspended, and will resume when all possible rules are applied.
	 */
	private final int threshold;
	/**
	 * <tt>true</tt> if any worker is blocked from submitting the jobs because
	 * threshold is exceeded.
	 */
	private volatile boolean workersWaiting = false;
	/**
	 * counter incremented every time a worker starts applying the rules
	 */
	private final AtomicInteger startedWorkers = new AtomicInteger(0);
	/**
	 * counter incremented every time a worker finishes applying the rules
	 */
	private final AtomicInteger finishedWorkers = new AtomicInteger(0);
	/**
	 * the largest started id of a worker that has been interrupted
	 */
	private final AtomicInteger lastInterruptedWorker = new AtomicInteger(0);

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
	public ClassExpressionSaturationFactory(OntologyIndex ontologyIndex,
			int maxWorkers,
			ClassExpressionSaturationListener<J, Engine> listener) {
		this.threshold = 64 + 32 * maxWorkers;
		this.listener = listener;
		this.jobsToDo = new ConcurrentLinkedQueue<J>();
		this.jobsInProgress = new ConcurrentLinkedQueue<J>();
		this.ruleApplicationFactory = new RuleApplicationFactory(ontologyIndex,
				new ThisRuleApplicationListener());
	}

	/**
	 * Creates a new saturation engine using the given ontology index.
	 * 
	 * @param ontologyIndex
	 *            the ontology index used to apply the rules
	 * @param maxWorkers
	 *            the maximum number of workers that can use this factory
	 */
	public ClassExpressionSaturationFactory(OntologyIndex ontologyIndex,
			int maxWorkers) {
		/* we use a dummy listener */
		this(ontologyIndex, maxWorkers,
				new ClassExpressionSaturationListener<J, Engine>() {

					@Override
					public void notifyCanProcess() {
					}

					@Override
					public void notifyFinished(J job)
							throws InterruptedException {
					}
				});
	}

	/**
	 * Check if the counter for processed jobs can be increased and post-process
	 * the finished jobs
	 * 
	 * @throws InterruptedException
	 */
	private void processFinishedJobs() throws InterruptedException {
		for (;;) {
			int shapshotJobsFinished = countJobsFinished.get();
			if (shapshotJobsFinished == countJobsProcessed.get()) {
				break;
			}
			/*
			 * at this place we know that the number of output jobs is smaller
			 * than the number of processed jobs; we try to increment this
			 * counter if it has not been changed.
			 */
			if (countJobsFinished.compareAndSet(shapshotJobsFinished,
					shapshotJobsFinished + 1)) {
				/*
				 * It is safe to assume that the next job in the buffer is
				 * processed since we increment the counter for the jobs only
				 * after the job is submitted, and the number of active workers
				 * remains positive until the job is processed.
				 */
				J nextJob = jobsInProgress.poll();
				IndexedClassExpression root = nextJob.getInput();
				Context rootSaturation = root.getContext();
				((ContextClassSaturation) rootSaturation).setSaturated();
				nextJob.setOutput(rootSaturation);
				if (LOGGER_.isTraceEnabled())
					LOGGER_.trace(root + ": saturation finished");
				listener.notifyFinished(nextJob);
			}
		}
	}

	/**
	 * Print statistics about the saturation
	 */
	public void printStatistics() {
		ruleApplicationFactory.printStatistics();
	}

	/**
	 * The listener class used for the rule application engine, which is used
	 * within this saturation engine
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class ThisRuleApplicationListener implements
			RuleApplicationListener {

		@Override
		public void notifyCanProcess() {
			/*
			 * the rule application engine can process; wake up all sleeping
			 * workers
			 */
			if (workersWaiting)
				synchronized (countContextsProcessed) {
					workersWaiting = false;
					countContextsProcessed.notifyAll();
				}
			/* tell also that the saturation engine can process */
			listener.notifyCanProcess();
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
	 * @return true if the counter has been updated
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

		// thread local objects
		private final RuleApplicationFactory.Engine ruleApplicationEngine = ruleApplicationFactory
				.getEngine();

		// don't allow creating of engines directly; only through the factory
		private Engine() {
		}

		@Override
		public void submit(J job) {
			jobsToDo.add(job);
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
			 * strategy: we know that all contexts are processed when (1) no
			 * worker is creating or processing contexts and (2) after every
			 * worker that was interrupted while processing contexts there was a
			 * worker that has started processing contexts. To check this
			 * condition, we use two counters: first counter is incremented
			 * before a worker starts processing contexts, the second counter is
			 * incremented after a worker finishes processing contexts.
			 * Therefore, at the moment when the values of both counters
			 * coincide, we know that condition (1) is fulfilled. Now, to check
			 * condition (2) we use a variable, where store the identifier of
			 * the last started worker that was interrupted and compare if this
			 * identifier is smaller than the identifier for the last finished
			 * worker (which should be equal to the identifier of the last
			 * started worker in case (1) is satisfied). To avoid deadlock, it
			 * is essential that whenever conditions (1) and (2) are satisfied,
			 * we can update the number of processed contexts, i.e., the
			 * computation was not interrupted in between processing of contexts
			 * and updating this counter.
			 */
			for (;;) {
				if (Thread.currentThread().isInterrupted())
					return;
				startedWorkers.incrementAndGet();
				ruleApplicationEngine.process();
				if (Thread.currentThread().isInterrupted())
					updateIfSmaller(lastInterruptedWorker, startedWorkers.get());
				updateProcessedCounters(finishedWorkers.incrementAndGet());
				processFinishedJobs(); // can be interrupted!
				int snapshotCountContextsProcessed = countContextsProcessed
						.get();
				if (ruleApplicationFactory.getApproximateContextNumber()
						- snapshotCountContextsProcessed > threshold) {
					synchronized (countContextsProcessed) {
						if (countContextsProcessed.get() > snapshotCountContextsProcessed)
							// new contexts were processed -- we need to check
							// again if we can process new jobs
							continue;
						workersWaiting = true;
						countContextsProcessed.wait();
						continue;
					}
				}
				J nextJob = jobsToDo.poll();
				if (nextJob == null)
					return;
				IndexedClassExpression root = nextJob.getInput();
				/*
				 * if saturation is already assigned, this job is already
				 * started or finished
				 */
				Context rootContext = root.getContext();
				if (rootContext != null
						&& ((ContextClassSaturation) rootContext).isSaturated()) {
					nextJob.setOutput(rootContext);
					listener.notifyFinished(nextJob); // can be interrupted!
					continue;
				}
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(root + ": saturation started");
				}
				/*
				 * submit the job to the rule engine and start processing it
				 */
				startedWorkers.incrementAndGet();
				jobsInProgress.add(nextJob);
				countJobsSubmitted.incrementAndGet();
				ruleApplicationEngine.submit(root);
				ruleApplicationEngine.process();
				if (Thread.currentThread().isInterrupted())
					updateIfSmaller(lastInterruptedWorker, startedWorkers.get());
				updateProcessedCounters(finishedWorkers.incrementAndGet());
				processFinishedJobs(); // can be interrupted!
			}

		}

		@Override
		public void finish() {
			ruleApplicationEngine.finish();
		}

		/**
		 * Updates the counter for processed contexts and jobs
		 */
		private void updateProcessedCounters(int snapshotFinishedWorkers) {
			if (lastInterruptedWorker.get() >= startedWorkers.get()) {
				/*
				 * after the last started worker has interrupted, no worker has
				 * started yet; in this case we cannot be sure that new
				 * submitted jobs are processed
				 */
				return;
			}
			/*
			 * cache the current snapshot for created contexts and jobs; it is
			 * important for correctness to measure the number of started
			 * workers only after that
			 */
			int snapshotContextNo = ruleApplicationFactory
					.getApproximateContextNumber();
			int snapshotCountJobsSubmitted = countJobsSubmitted.get();
			if (startedWorkers.get() > snapshotFinishedWorkers)
				// this means that some started worker did not finish yet
				return;
			/*
			 * if we arrived here, then at the period of time from the beginning
			 * of this function until the test we have: (1) there is no worker
			 * that started processing but did not finished, and (2) after every
			 * interrupted worker there was a started (and thus finished) worker
			 * that was not interrupted. This means that the taken snapshots
			 * represent at least the number of processed contexts and jobs. In
			 * this case we update the counter for processed jobs and tasks
			 * using the snapshot taken before, if it is smaller.
			 */
			updateIfSmaller(countJobsProcessed, snapshotCountJobsSubmitted);
			boolean updated = updateIfSmaller(countContextsProcessed,
					snapshotContextNo);
			if (updated && workersWaiting) {
				/*
				 * waking up all workers waiting to submit the jobs
				 */
				synchronized (countContextsProcessed) {
					workersWaiting = false;
					countContextsProcessed.notifyAll();
				}
				listener.notifyCanProcess();
			}
		}

	}

	@Override
	public Engine getEngine() {
		return new Engine();
	}

}
