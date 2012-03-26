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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.rules.RuleApplicationEngine;
import org.semanticweb.elk.reasoner.rules.RuleApplicationListener;
import org.semanticweb.elk.reasoner.rules.SaturatedClassExpression;
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
	 * The buffer for jobs in progress, i.e., those jobs for which the method
	 * {@link #submit(J)} was executed but not
	 * {@link #listener.notifyFinished(J)}.
	 */
	protected final Queue<J> buffer;
	/**
	 * This number of submitted jobs, i.e., those for which the method
	 * {@link #submit(J)} was executed.
	 */
	protected final AtomicInteger countJobsSubmitted = new AtomicInteger(0);
	/**
	 * The number of processed jobs, as determined by the procedure; should
	 * never exceed {@link #countJobsSubmitted}.
	 */
	protected final AtomicInteger countJobsProcessed = new AtomicInteger(0);
	/**
	 * The number of finished jobs, i.e., those for which
	 * {@link #listener.notifyFinished(J)} is executed; should never exceed
	 * {@link #countJobsProcessed}
	 */
	protected final AtomicInteger countJobsFinished = new AtomicInteger(0);
	/**
	 * The queue to collect the context created during the saturation; emptied
	 * every time the rule application finishes
	 */
	protected final Queue<SaturatedClassExpression> contextCreated;
	/**
	 * The counter for created contexts; used to control batches of jobs
	 */
	protected final AtomicInteger contextCreatedNo = new AtomicInteger(0);
	/**
	 * The counter for processed contexts, should never exceed
	 * {@link #contextCreatedNo}
	 */
	protected final AtomicInteger contextProcessedNo = new AtomicInteger(0);
	/**
	 * The number of contexts marked as saturated saturated contexts, i.e.,
	 * those marked as saturated; should never exceed
	 * {@link #contextProcessedNo}
	 */
	protected final AtomicInteger contextSaturatedNo = new AtomicInteger(0);

	/**
	 * The threshold used to submit new jobs. The job is successfully submitted
	 * if difference between the number of created contexts and processed
	 * contexts does not exceed this threshold; otherwise the computation is
	 * suspended, and will resume when all possible rules are applied.
	 */
	final int threshold;
	/**
	 * True if any worker is blocked from submitting the jobs because threshold
	 * is exceeded.
	 */
	volatile boolean workersWaiting = false;
	/**
	 * The number of workers applying the rules of the rule application engine.
	 * If the number of workers is zero, all rules must have been applied.
	 */
	final AtomicInteger activeWorkers = new AtomicInteger(0);
	/**
	 * The lock used to suspend workers until a sufficient number of jobs are
	 * processed
	 */
	final Lock suspendLock = new ReentrantLock();
	/**
	 * The lock condition using which one signal when jobs can be processed or
	 * submitted
	 */
	final Condition canProcessOrSubmit = suspendLock.newCondition();

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
		this.buffer = new ConcurrentLinkedQueue<J>();
		this.contextCreated = new ConcurrentLinkedQueue<SaturatedClassExpression>();
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

					public void notifyCanProcess() {
					}

					public void notifyFinished(J job)
							throws InterruptedException {
					}
				});
	}

	public void submit(J job) throws InterruptedException {

		IndexedClassExpression root = job.getInput();
		/*
		 * if saturation is already assigned, this job is already started or
		 * finished
		 */
		SaturatedClassExpression rootSaturation = root.getSaturated();
		if (rootSaturation != null && rootSaturation.isSaturated()) {
			listener.notifyFinished(job);
			return;
		}
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace(root + ": saturation started");
		}
		/*
		 * checking if the number of unprocessed jobs does not exceed the
		 * threshold
		 */
		checkThreshold();
		/*
		 * submit the job to the rule engine and start processing it; the
		 * counter of active workers overestimates the number of workers
		 * processing the tasks using the rule engine, so that when there are no
		 * active workers, we know that all submitted jobs are processed
		 */
		activeWorkers.incrementAndGet();
		// activeWorkers.incrementAndGet();
		buffer.add(job);
		countJobsSubmitted.incrementAndGet();
		ruleApplicationEngine.submit(root);
		ruleApplicationEngine.process();
		// activeWorkers.decrementAndGet();
		if (activeWorkers.decrementAndGet() == 0)
			updateProcessedCounters();
		processFinishedJobs();
	}

	/**
	 * Check whether the number of unprocessed jobs exceeds the threshold. If it
	 * does, then suspend the computation until the jobs are processed or other
	 * jobs can be processed
	 * 
	 * @throws InterruptedException
	 */
	void checkThreshold() throws InterruptedException {
		/*
		 * if the number of unprocessed contexts exceeds the threshold, suspend
		 * the computation; whenever workers wake up, try to process the jobs
		 */
		for (;;) {
			if (contextCreatedNo.get() - contextProcessedNo.get() <= threshold)
				return;
			suspendLock.lock();
			try {
				for (;;) {
					if (canProcess())
						break;
					if (contextCreatedNo.get() - contextProcessedNo.get() <= threshold)
						return;
					workersWaiting = true;
					canProcessOrSubmit.await();
				}
			} finally {
				suspendLock.unlock();
			}
			process();
		}
	}

	public void process() throws InterruptedException {
		if (ruleApplicationEngine.canProcess()) {
			activeWorkers.incrementAndGet();
			ruleApplicationEngine.process();
			if (activeWorkers.decrementAndGet() == 0)
				updateProcessedCounters();
		}
		processFinishedJobs();
	}

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
	 * Updates the counter for processed contexts and jobs
	 */
	void updateProcessedCounters() {
		/*
		 * cache the current snapshot for created contexts and jobs
		 */
		int snapshotContextNo = contextCreatedNo.get();
		int snapshotCountJobsSubmitted = countJobsSubmitted.get();
		if (activeWorkers.get() > 0)
			return;
		/* the value will be true if any of the counters are updated */
		boolean updated = false;
		/*
		 * At this point we know that there was a time when there was no active
		 * workers after the snapshots were taken. This means that the snapshots
		 * represent at least the number of processed contexts and jobs. In this
		 * case we update the counter for processed jobs and tasks using the
		 * snapshot taken before; since several workers can enter this block
		 * with different values of snapshot, we make sure that the values of
		 * the counter will be updated to the largest of them.
		 */
		for (;;) {
			int snapshotContextsProcessed = contextProcessedNo.get();
			if (snapshotContextNo <= snapshotContextsProcessed)
				break;
			if (contextProcessedNo.compareAndSet(snapshotContextsProcessed,
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
			suspendLock.lock();
			try {
				workersWaiting = false;
				canProcessOrSubmit.signalAll();
			} finally {
				suspendLock.unlock();
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
			int shapshotContextSaturatedNo = contextSaturatedNo.get();
			if (shapshotContextSaturatedNo == contextProcessedNo.get()) {
				break;
			}
			/*
			 * at this place we know that the number of saturated contexts is
			 * smaller than the number of processed contexts; we try to
			 * increment this counter if it has not been changed.
			 */
			if (contextSaturatedNo.compareAndSet(shapshotContextSaturatedNo,
					shapshotContextSaturatedNo + 1)) {
				/*
				 * It is safe to assume that the next context in the buffer is
				 * saturated since we increment the counter for the processed
				 * context number only after the corresponding number of
				 * contexts are processed
				 */
				SaturatedClassExpression nextContext = contextCreated.poll();
				nextContext.setSaturated();
			}
		}
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
				J nextJob = buffer.poll();
				IndexedClassExpression root = nextJob.getInput();
				// this should be set as saturated in the previous loop
				SaturatedClassExpression rootSaturation = root.getSaturated();
				nextJob.setOutput(rootSaturation);
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(root + ": saturation finished");
				}
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

		public void notifyCanProcess() {
			/*
			 * the rule application engine can process; wake up all sleeping
			 * workers
			 */
			if (workersWaiting) {
				suspendLock.lock();
				try {
					workersWaiting = false;
					canProcessOrSubmit.signalAll();
				} finally {
					suspendLock.unlock();
				}
			}
			/* tell also that the saturation engine can process */
			listener.notifyCanProcess();
		}

		public void notifyCreated(SaturatedClassExpression context) {
			contextCreatedNo.incrementAndGet();
			contextCreated.add(context);
		}

		public void notifyMofidified(SaturatedClassExpression context) {
			// TODO Auto-generated method stub

		}
	}

}
