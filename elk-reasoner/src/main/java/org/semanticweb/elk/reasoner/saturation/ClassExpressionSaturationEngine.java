/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationEngine;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationListener;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * The engine for submitting, processing, and post-processing of saturation
 * jobs. Each saturation job requires to compute implied super-classes of a
 * given indexed class expression. The jobs are submitted using the
 * {@link #submit(SaturationJob)} method, and all currently submitted jobs are
 * processed using the {@link #process()} method. To every saturation engine it
 * is possible to attach a {@link ClassExpressionSaturationListener}, which can
 * implement hook methods that perform certain actions during the processing,
 * e.g., notifying when the jobs are finished.
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
public class ClassExpressionSaturationEngine<J extends SaturationJob<? extends IndexedClassExpression>>
		implements InputProcessor<J> {

	protected final static Logger LOGGER_ = Logger
			.getLogger(ClassExpressionSaturationEngine.class);

	/**
	 * The listener object implementing callback functions for this engine
	 */
	protected final ClassExpressionSaturationListener<J, ClassExpressionSaturationEngine<J>> listener;
	/**
	 * The rule application engine used internally for execution of the
	 * saturation rules.
	 */
	protected final RuleApplicationEngine ruleApplicationEngine;
	/**
	 * The buffer for jobs that need to be processed, i.e., those for which the
	 * method {@link #submit(J)} was executed but processing of jobs has not
	 * been started yet.
	 */
	protected final Queue<J> jobsToDo;
	/**
	 * The buffer for jobs in progress, i.e., those for which processing has
	 * started but the method {@link #listener.notifyFinished(J)} was not
	 * executed yet.
	 */
	protected final Queue<J> jobsInProgress;
	/**
	 * This number of submitted jobs, i.e., those for which the method
	 * {@link #submit(J)} was executed.
	 */
	protected final AtomicInteger countJobsSubmitted = new AtomicInteger(0);
	/**
	 * The number of processed jobs, as determined by the procedure
	 */
	protected final AtomicInteger countJobsProcessed = new AtomicInteger(0);
	/**
	 * The number of finished jobs, i.e., those for which
	 * {@link #listener.notifyFinished(J)} is executed.
	 */
	protected final AtomicInteger countJobsFinished = new AtomicInteger(0);
	/**
	 * The number of processed contexts; used to control batches of jobs
	 */
	final AtomicInteger countContextsProcessed = new AtomicInteger(0);
	/**
	 * The threshold used to submit new jobs. The job is successfully submitted
	 * if difference between the number of created contexts and processed
	 * contexts does not exceed this threshold; otherwise the computation is
	 * suspended, and will resume when all possible rules are applied.
	 */
	final int threshold;
	/**
	 * <tt>true</tt> if any worker is blocked from submitting the jobs because
	 * threshold is exceeded.
	 */
	volatile boolean workersWaiting = false;
	/**
	 * The number of workers applying the rules of the rule application engine.
	 * If the number of workers is zero, all rules must have been applied.
	 */
	final AtomicInteger activeWorkers = new AtomicInteger(0);

	/**
	 * counter incremented every time a worker starts applying the rules
	 */
	final AtomicInteger startedWorkers = new AtomicInteger(0);
	/**
	 * counter incremented every time a worker finishes applying the rules
	 */
	final AtomicInteger finishedWorkers = new AtomicInteger(0);
	/**
	 * the largest started id of a worker that has been interrupted
	 */
	final AtomicInteger lastInterruptedWorker = new AtomicInteger(0);

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
	 * @param listener
	 *            the listener object implementing callback functions
	 * @param threshold
	 *            the maximal difference between unprocessed and processed
	 *            contexts under which new jobs can be submitted.
	 */
	public ClassExpressionSaturationEngine(
			OntologyIndex ontologyIndex,
			ClassExpressionSaturationListener<J, ClassExpressionSaturationEngine<J>> listener,
			int threshold) {
		this.threshold = threshold;
		this.listener = listener;
		this.jobsToDo = new ConcurrentLinkedQueue<J>();
		this.jobsInProgress = new ConcurrentLinkedQueue<J>();
		this.ruleApplicationEngine = new RuleApplicationEngine(ontologyIndex,
				new ThisRuleApplicationListener());
	}

	/**
	 * Creates a new saturation engine using the given ontology index and the
	 * listener for callback functions.
	 * 
	 * @param ontologyIndex
	 *            the ontology index used to apply the rules
	 * @param listener
	 *            The listener object implementing callback functions
	 */
	public ClassExpressionSaturationEngine(
			OntologyIndex ontologyIndex,
			ClassExpressionSaturationListener<J, ClassExpressionSaturationEngine<J>> listener) {
		this(ontologyIndex, listener, 256);
	}

	/**
	 * Creates a new saturation engine using the given ontology index.
	 * 
	 * @param ontologyIndex
	 *            the ontology index used to apply the rules
	 */
	public ClassExpressionSaturationEngine(OntologyIndex ontologyIndex) {
		/* we use a dummy listener */
		this(
				ontologyIndex,
				new ClassExpressionSaturationListener<J, ClassExpressionSaturationEngine<J>>() {

					@Override
					public void notifyCanProcess() {
					}

					@Override
					public void notifyFinished(J job)
							throws InterruptedException {
					}
				});
	}

	@Override
	public void submit(J job) {
		jobsToDo.add(job);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.util.concurrent.computation.InputProcessor#process()
	 */
	@Override
	public void process() throws InterruptedException {
		/*
		 * if the number of unprocessed contexts exceeds the threshold, suspend
		 * the computation; whenever workers wake up, try to process the jobs
		 */
		for (;;) {
			if (Thread.currentThread().isInterrupted())
				return;
			/*
			 * try to process the jobs in progress; the counter of active
			 * workers overestimates the number of workers processing the tasks
			 * using the rule engine, so that when there are no active workers
			 * and the process has not been interrupted, we know that all
			 * submitted jobs in progress are processed
			 */
			int snapshotFinishedWorkers;
			// if (ruleApplicationEngine.canProcess()) {
			startedWorkers.incrementAndGet();
			ruleApplicationEngine.process();
			if (Thread.currentThread().isInterrupted())
				updateLastInterruptedWorkerId(startedWorkers.get());
			snapshotFinishedWorkers = finishedWorkers.incrementAndGet();
			updateProcessedCounters(snapshotFinishedWorkers);
			processFinishedJobs();
			if (ruleApplicationEngine.getContextNumber()
					- countContextsProcessed.get() > threshold) {
				synchronized (countContextsProcessed) {
					if (canProcess())
						continue;
					if (ruleApplicationEngine.getContextNumber()
							- countContextsProcessed.get() > threshold) {
						workersWaiting = true;
						countContextsProcessed.wait();
						continue;
					}
				}
			}
			// }
			J nextJob = jobsToDo.poll();
			if (nextJob == null)
				return;
			IndexedClassExpression root = nextJob.getInput();
			/*
			 * if saturation is already assigned, this job is already started or
			 * finished
			 */
			Context rootContext = root.getContext();
			if (rootContext != null
					&& ((ContextClassSaturation) rootContext).isSaturated()) {
				nextJob.setOutput(rootContext);
				listener.notifyFinished(nextJob);
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
				updateLastInterruptedWorkerId(startedWorkers.get());
			snapshotFinishedWorkers = finishedWorkers.incrementAndGet();
			updateProcessedCounters(snapshotFinishedWorkers);
			processFinishedJobs();
		}

	}

	@Override
	public boolean canProcess() {
		return ruleApplicationEngine.canProcess()
				|| countJobsFinished.get() > countJobsProcessed.get();
	}

	/**
	 * Print statistics about the saturation
	 */
	public void printStatistics() {
		ruleApplicationEngine.printStatistics();
	}

	/**
	 * Updates the id of the last interrupted worker to the given value provided
	 * it is not greater
	 * 
	 * @param snapshotStartedWorkers
	 */
	void updateLastInterruptedWorkerId(int snapshotStartedWorkers) {
		for (;;) {
			int snapshotLastInterruptedWorker = lastInterruptedWorker.get();
			if (snapshotStartedWorkers <= snapshotLastInterruptedWorker)
				return;
			if (lastInterruptedWorker.compareAndSet(
					snapshotLastInterruptedWorker, snapshotStartedWorkers))
				return;
		}
	}

	/**
	 * Updates the counter for processed contexts and jobs
	 */
	void updateProcessedCounters(int snapshotFinishedWorkers) {
		if (lastInterruptedWorker.get() >= startedWorkers.get()) {
			// after the last started worker has interrupted, no worker
			// has started yet; in this case the result might not be computed
			return;
		}
		/*
		 * cache the current snapshot for created contexts and jobs
		 */
		int snapshotContextNo = ruleApplicationEngine.getContextNumber();
		int snapshotCountJobsSubmitted = countJobsSubmitted.get();
		if (startedWorkers.get() > snapshotFinishedWorkers) {
			// some started worker did not finish yet
			return;
		}

		/* the value will be true if any of the counters are updated */
		boolean updated = false;

		/*
		 * right before this test (1) no worker is processing anything and (2)
		 * after every interrupted worker there was a started and finished
		 * worker that was not interrupted. This means that the taken snapshots
		 * represent at least the number of processed contexts and jobs. In this
		 * case we update the counter for processed jobs and tasks using the
		 * snapshot taken before; since several workers can enter this block
		 * with different values of snapshot, we make sure that the values of
		 * the counter will be updated to the largest of them.
		 */
		for (;;) {
			int snapshotContextsProcessed = countContextsProcessed.get();
			if (snapshotContextNo <= snapshotContextsProcessed)
				break;
			if (countContextsProcessed.compareAndSet(snapshotContextsProcessed,
					snapshotContextNo)) {
				updated = true;
				break;
			}
		}
		for (;;) {
			int snapshotCountJobsProcessed = countJobsProcessed.get();
			if (snapshotCountJobsSubmitted <= snapshotCountJobsProcessed)
				break;
			if (countJobsProcessed.compareAndSet(snapshotCountJobsProcessed,
					snapshotCountJobsSubmitted)) {
				updated = true;
				break;
			}
		}
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

	/**
	 * Check if the counter for processed jobs can be increased and post-process
	 * the finished jobs
	 * 
	 * @throws InterruptedException
	 */
	void processFinishedJobs() throws InterruptedException {
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
	 * The listener class used for the rule application engine, which is used
	 * within this saturation engine
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	class ThisRuleApplicationListener implements RuleApplicationListener {

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

}
