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
import org.semanticweb.elk.reasoner.ReasonerJob;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.rules.RuleApplicationEngine;
import org.semanticweb.elk.reasoner.rules.RuleApplicationListener;
import org.semanticweb.elk.reasoner.rules.SaturatedClassExpression;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

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
public final class ClassExpressionSaturationEngine<J extends SaturationJob<? extends IndexedClassExpression>>
		implements InputProcessor<J>, RuleApplicationListener {

	protected final static Logger LOGGER_ = Logger
			.getLogger(ClassExpressionSaturationEngine.class);

	/**
	 * The listener for saturation callbacks
	 */
	protected final ClassExpressionSaturationListener<J> listener;
	/**
	 * The rule application engine used for execution of the rules.
	 */
	protected final RuleApplicationEngine ruleApplicationEngine;
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
	 * The number of processed contexts; used to control batches of jobs
	 */
	final AtomicInteger countContextsProcessed = new AtomicInteger(0);
	/**
	 * The threshold used to submit new jobs. The job is successfully submitted
	 * if difference between the number of created contexts and processed
	 * contexts does not exceed this threshold; otherwise the computation is
	 * suspended, and will resume when new contexts are processed.
	 */
	final int threshold;
	/**
	 * True if any worker is blocked from submitting the tasks because threshold
	 * is exceeded.
	 */
	volatile boolean workersWaiting = false;
	/**
	 * The number of workers applying the rules of the saturation engine. If the
	 * number of workers is zero, every context must be saturated.
	 */
	final AtomicInteger activeWorkers = new AtomicInteger(0);

	/**
	 * Creates a saturation engine using the given ontology index and the give
	 * threshold for submitting the jobs. The threshold has an effect on the
	 * size of the batches of the input jobs that are processed simultaneously,
	 * which, in turn, has an effect on throughput and latency of the
	 * processing: in general, the larger the threshold is, the faster it takes
	 * (in theory) to perform the overall processing of jobs, but it might take
	 * longer to process an individual job because we can detect that the job is
	 * processed only when the whole batch is processed.
	 * 
	 * @param ruleApplicationEngine
	 *            the engine used to perform saturation
	 * @param listener
	 *            the listener for saturation callbacks
	 * @param threshold
	 *            the maximal difference between unprocessed and processed
	 *            contexts under which new jobs can be submitted.
	 */
	public ClassExpressionSaturationEngine(OntologyIndex ontologyIndex,
			ClassExpressionSaturationListener<J> listener, int threshold) {
		this.threshold = threshold;
		this.listener = listener;
		this.buffer = new ConcurrentLinkedQueue<J>();
		this.ruleApplicationEngine = new RuleApplicationEngine(ontologyIndex,
				this);
	}

	/**
	 * Creates a saturation engine using a given ontology index and the
	 * listener.
	 * 
	 * @param ruleApplicationEngine
	 *            the engine used to perform saturation
	 */
	public ClassExpressionSaturationEngine(OntologyIndex ontologyIndex,
			ClassExpressionSaturationListener<J> listener) {
		this(ontologyIndex, listener, 256);
	}

	/**
	 * Creates a saturation engine using a given ontology index.
	 * 
	 * @param ruleApplicationEngine
	 *            the engine used to perform saturation
	 */
	public ClassExpressionSaturationEngine(OntologyIndex ontologyIndex) {
		this(ontologyIndex, new ClassExpressionSaturationListener<J>() {

			public void notifyCanProcess() {
			}

			public void notifyProcessed(J job) throws InterruptedException {
			}
		});
	}

	public void submit(J job) throws InterruptedException {

		IndexedClassExpression root = job.getInput();
		/*
		 * if saturation is already assigned, this task is already started or
		 * finished
		 */
		SaturatedClassExpression rootSaturation = root.getSaturated();
		if (rootSaturation != null && rootSaturation.isSaturated()) {
			listener.notifyProcessed(job);
			return;
		}
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace(root + ": saturation started");
		}
		/*
		 * if the number of unprocessed contexts exceeds the threshold, suspend
		 * the computation
		 */
		for (;;) {
			process();
			if (ruleApplicationEngine.getContextNo()
					- countContextsProcessed.get() <= threshold)
				break;
			synchronized (countContextsProcessed) {
				if (canProcess())
					continue;
				if (ruleApplicationEngine.getContextNo()
						- countContextsProcessed.get() <= threshold)
					break;
				workersWaiting = true;
				countContextsProcessed.wait();
			}
		}
		/*
		 * submit the job and start processing it; the counter of active workers
		 * overestimates the number of workers processing the tasks using the
		 * rule engine so that when there are no active workers, we know that
		 * all submitted jobs are processed
		 */
		activeWorkers.incrementAndGet();
		buffer.add(job);
		countJobsSubmitted.incrementAndGet();
		ruleApplicationEngine.submit(root);
		ruleApplicationEngine.process();
		if (activeWorkers.decrementAndGet() == 0)
			updateProcessedCounters();
		processFinishedJobs();
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

	public void notifyCanProcess() {
		/* wake up all sleeping workers whenever new jobs are available */
		if (workersWaiting)
			synchronized (countContextsProcessed) {
				workersWaiting = false;
				countContextsProcessed.notifyAll();
			}
		listener.notifyCanProcess();
	}

	/**
	 * Print statistics about the saturation
	 */
	public void printStatistics() {
		ruleApplicationEngine.printStatistics();
	}

	/**
	 * Decrements the number of active workers and updates the counter for
	 * processed contexts and jobs
	 */
	void updateProcessedCounters() {
		/*
		 * cache the current snapshot for submitted jobs
		 */
		int snapshotContextNo = ruleApplicationEngine.getContextNo();
		int snapshotCountJobsSubmitted = countJobsSubmitted.get();
		if (activeWorkers.get() > 0)
			return;
		boolean updated = false;
		/*
		 * In this case we update the counter for processed jobs and tasks using
		 * the snapshot taken before; since several workers can enter this block
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
			 * waking up workers, if any, waiting to submit the tasks
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
			 * than the number of processed jobs if this counter has not been
			 * changed.
			 */
			if (countJobsFinished.compareAndSet(shapshotJobsFinished,
					shapshotJobsFinished + 1)) {
				/*
				 * It is safe to assume that the next job in the buffer is
				 * processed since a job is inserted in the buffer only after
				 * some worker starts processing the saturation for this job.
				 */
				J nextJob = buffer.poll();
				IndexedClassExpression root = nextJob.getInput();
				SaturatedClassExpression rootSaturation = root.getSaturated();
				rootSaturation.setSaturated();
				nextJob.setOutput(rootSaturation);
				if (LOGGER_.isTraceEnabled()) {
					LOGGER_.trace(root + ": saturation finished");
				}
				listener.notifyProcessed(nextJob);
			}
		}
	}

}
