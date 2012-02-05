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

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.ReasonerJob;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.rules.RuleApplicationEngine;
import org.semanticweb.elk.util.concurrent.computation.AbstractJobManager;

/**
 * The engine for scheduling saturation jobs, detecting when results are ready,
 * and delegating the results for further post-processing. Each saturation job
 * consists of several saturation tasks requesting computing a saturation for
 * the input {@link IndexedClassExpression}. The jobs are submitted using the
 * method {@link #process(ReasonerJob)}. A hook for post-processing the result
 * when it is ready, is specified by the {@link #postProcess(ReasonerJob)}
 * method which should be implemented in subclasses accordingly.
 * 
 * The implementation relies heavily on the Java's concurrency package and uses
 * several atomic integer to monitor the progress of the computation.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <J>
 *            the type of the saturation jobs that can be processed in this
 *            saturation engine
 */
public class ClassExpressionSaturationEngine<J extends Iterable<? extends IndexedClassExpression>>
		extends AbstractJobManager<J> {

	protected final static Logger LOGGER_ = Logger
			.getLogger(ClassExpressionSaturationEngine.class);

	class RuleApplicationEngineForSaturation extends RuleApplicationEngine {

		public RuleApplicationEngineForSaturation(OntologyIndex ontologyIndex) {
			super(ontologyIndex);
		}

		@Override
		public void notifyCanProcess() {
			/* wake up all sleeping workers whenever new jobs are available */
			if (workersWaiting)
				synchronized (countTasksSubmitted) {
					workersWaiting = false;
					countTasksSubmitted.notifyAll();
				}
			ClassExpressionSaturationEngine.this.notifyCanProcess();
		}
	}

	/**
	 * The engine used for execution of rules.
	 */
	protected final RuleApplicationEngineForSaturation ruleApplicationEngine;
	/**
	 * The buffer for jobs in progress, i.e., those jobs for which the method
	 * {@link #process(ReasonerJob)} was executed but not
	 * {@link #postProcess(ReasonerJob)}.
	 */
	protected final Queue<J> buffer;
	/**
	 * This number of submitted jobs, i.e., those for which the method
	 * {@link #process(ReasonerJob)} was executed.
	 */
	protected final AtomicInteger countJobsSubmitted = new AtomicInteger(0);
	/**
	 * The number of processed jobs for which all tasks have been processed
	 */
	protected final AtomicInteger countJobsProcessed = new AtomicInteger(0);
	/**
	 * The number of finished jobs, i.e., for which
	 * {@link #postProcess(ReasonerJob)} is executed.
	 */
	protected final AtomicInteger countJobsFinished = new AtomicInteger(0);
	/**
	 * The number of submitted saturation tasks
	 */
	final AtomicInteger countTasksSubmitted = new AtomicInteger(0);
	/**
	 * The number of processed saturation tasks
	 */
	final AtomicInteger countTasksProcessed = new AtomicInteger(0);
	/**
	 * The maximal number of submitted but not processed saturation tasks; the
	 * difference between {@link #countTasksSubmitted} and
	 * {@link #countTasksProcessed} should never exceed the threshold.
	 */
	final int threshold;
	/**
	 * True if any worker is blocked from submitting the tasks because threshold
	 * is exceeded.
	 */
	volatile boolean workersWaiting = false;
	/**
	 * The number of workers applying the rules of the saturation engine. If the
	 * number of workers is zero, everything must be saturated.
	 */
	final AtomicInteger activeWorkers = new AtomicInteger(0);

	/**
	 * Creates a saturation engine using the given ontology index and the
	 * maximum number of concurrent saturation tasks. The limit has an effect on
	 * the size of batches in which the input is processed and has an effect on
	 * throughput and latency of the processing: in general, the larger the
	 * limit is, the faster it takes to perform the overall processing of jobs,
	 * but it might take longer to process an individual job because we can
	 * detect that the job is processed only when the whole batch is processed.
	 * 
	 * @param ruleApplicationEngine
	 *            the engine used to perform saturation
	 * @param threshold
	 *            the maximum number of unprocessed saturation tasks at any
	 *            given time
	 */
	public ClassExpressionSaturationEngine(OntologyIndex ontologyIndex,
			int threshold) {
		this.threshold = threshold;
		this.buffer = new ConcurrentLinkedQueue<J>();
		this.ruleApplicationEngine = new RuleApplicationEngineForSaturation(
				ontologyIndex);
	}

	/**
	 * Creates a saturation engine using a given ontology index.
	 * 
	 * @param ruleApplicationEngine
	 *            the engine used to perform saturation
	 */
	public ClassExpressionSaturationEngine(OntologyIndex ontologyIndex) {
		this(ontologyIndex, 64);
	}

	public final void submit(J job) throws InterruptedException {

		/*
		 * the iterator over the root indexed class expressions for which
		 * saturations are required to be computed as a part of this job
		 */
		Iterator<? extends IndexedClassExpression> roots = job.iterator();
		/* if there are more task of the job to be submitted */
		boolean moreTasks = true;
		while (moreTasks) {
			if (roots.hasNext()) {
				IndexedClassExpression root = roots.next();
				/*
				 * if saturation is already assigned, this task is already
				 * started or finished
				 */
				if (root.getSaturated() != null)
					continue;
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(root + ": saturation started");
				}
				/*
				 * incrementing the number of submitted tasks unless it reaches
				 * the number of processed tasks plus the threshold
				 */
				for (;;) {
					process();
					int snapshotCountTasksSubmitted = countTasksSubmitted.get();
					if (snapshotCountTasksSubmitted - countTasksProcessed.get() == threshold)
						synchronized (countTasksSubmitted) {
							if (countTasksSubmitted.get()
									- countTasksProcessed.get() == threshold
									&& !canProcess()) {
								workersWaiting = true;
								countTasksSubmitted.wait();
							}
						}
					else if (countTasksSubmitted.compareAndSet(
							snapshotCountTasksSubmitted,
							snapshotCountTasksSubmitted + 1))
						break;
				}
				/*
				 * process the submitted tasks; the counter of active workers
				 * overestimates the number of workers processing the tasks
				 * using the rule engine
				 */
				activeWorkers.incrementAndGet();
				ruleApplicationEngine.submit(root);
				ruleApplicationEngine.process();
				activeWorkers.decrementAndGet();
			} else {
				/*
				 * all tasks of this job have been submitted; we can add the job
				 * to the buffer to wait for completion
				 */
				moreTasks = false;
				buffer.add(job);
				countJobsSubmitted.incrementAndGet();
			}
		}
	}

	public final void process() throws InterruptedException {
		if (ruleApplicationEngine.canProcess()) {
			activeWorkers.incrementAndGet();
			ruleApplicationEngine.process();
			activeWorkers.decrementAndGet();
		}
		updateCounters();
	}

	@Override
	public boolean canProcess() {
		return ruleApplicationEngine.canProcess();
	}

	/**
	 * Updates the counters for processed and finished jobs.
	 * 
	 * @throws InterruptedException
	 */
	void updateCounters() throws InterruptedException {
		/*
		 * cache the current snapshot for submitted jobs and and tasks
		 */
		int snapshotCountJobsSubmitted = countJobsSubmitted.get();
		int snapshotCountTasksSubmitted = countTasksSubmitted.get();
		/*
		 * If there are no active workers then the snapshot values cached before
		 * represent the number of finished jobs and tasks.
		 */
		if (activeWorkers.get() == 0) {
			/*
			 * In this case we update the counter for processed jobs and tasks
			 * using the snapshot taken before; since several workers can enter
			 * this block with different values of snapshot, we make sure that
			 * the values of the counter will be updated to the largest of them.
			 */
			for (;;) {
				int snapshotCountJobsProcessed = countJobsProcessed.get();
				if (snapshotCountJobsSubmitted <= snapshotCountJobsProcessed)
					break;
				if (countJobsProcessed.compareAndSet(
						snapshotCountJobsProcessed, snapshotCountJobsSubmitted)) {
					break;
				}
			}
			for (;;) {
				int snapshotCountTasksProcessed = countTasksProcessed.get();
				if (snapshotCountTasksSubmitted <= snapshotCountTasksProcessed)
					break;
				if (countTasksProcessed.compareAndSet(
						snapshotCountTasksProcessed,
						snapshotCountTasksSubmitted)) {
					/*
					 * waking up workers, if any, waiting to submit the tasks
					 */
					if (workersWaiting)
						synchronized (countTasksSubmitted) {
							workersWaiting = false;
							countTasksSubmitted.notifyAll();
						}
					break;
				}
			}
		}
		/*
		 * Now we check if the counter for the finished jobs can be increased,
		 * and thus new finished jobs can be taken from the buffer; this is
		 * independent from whether the counter for the processed jobs has been
		 * updated or not in the previous step since this can happen in another
		 * thread.
		 */
		for (;;) {
			int shapshotOutputJobs = countJobsFinished.get();
			if (shapshotOutputJobs == countJobsProcessed.get()) {
				break;
			}
			/*
			 * at this place we know that the number of output jobs is smaller
			 * than the number of processed jobs if this counter has not been
			 * changed.
			 */
			if (countJobsFinished.compareAndSet(shapshotOutputJobs,
					shapshotOutputJobs + 1)) {
				/*
				 * It is safe to assume that the next job in the buffer is
				 * processed since a job is inserted in the buffer only after
				 * all tasks for the job are submitted.
				 */
				J nextJob = buffer.poll();
				/* mark all saturations as completed */
				for (IndexedClassExpression root : nextJob) {
					root.getSaturated().setSaturated();
					if (LOGGER_.isTraceEnabled()) {
						LOGGER_.trace(root + ": saturation finished");
					}
				}
				notifyProcessed(nextJob);
			}
		}
	}

}
