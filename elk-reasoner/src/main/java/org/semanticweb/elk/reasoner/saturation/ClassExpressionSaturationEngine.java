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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.ReasonerJob;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.classes.RuleApplicationEngine;
import org.semanticweb.elk.reasoner.saturation.classes.SaturatedClassExpression;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * The engine for scheduling saturations of class expressions, detecting when
 * results are ready, and delegating the results for further post-processing.
 * The jobs initialized with input class expressions are submitted using the
 * method {@link #process(ReasonerJob)}. A hook for post-processing the result
 * when it is ready, is specified by the {@link #processOutput(ReasonerJob)}
 * method which should be implemented accordingly.
 * 
 * The implementation relies heavily on Java's concurrency package and contains
 * a complicated machinery to achieve thread safety and contract guarantees.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <J>
 *            the type of the saturation jobs to be processed
 */
public class ClassExpressionSaturationEngine<J extends SaturationJob<? extends IndexedClassExpression>>
		implements InputProcessor<J> {

	protected final static Logger LOGGER_ = Logger
			.getLogger(ClassExpressionSaturationEngine.class);

	protected final RuleApplicationEngine ruleApplicationEngine;

	/**
	 * The buffer for jobs in progress, i.e., for those jobs for which the
	 * method {@link #processOutput(ReasonerJob)} is not yet executed.
	 */
	protected final BlockingQueue<J> buffer;

	/**
	 * The size of the job buffer.
	 */
	final int bufferSize;

	/**
	 * A counter for the number of worker instances processing the active
	 * contexts queue
	 */
	final AtomicInteger activeWorkers = new AtomicInteger(0);

	/**
	 * The counter incremented with every inserted job to the buffer
	 */
	protected final AtomicInteger countJobs = new AtomicInteger(0);

	/**
	 * The counter incremented whenever a job is processed, i.e., the input is
	 * saturated; it should never exceed the counter for the number of the
	 * inserted jobs and should reach that when all computations are over
	 */
	protected final AtomicInteger countProcessedJobs = new AtomicInteger(0);

	/**
	 * The counter incremented whenever the processed job is submitted for
	 * post-processing; it should never exceed the counter for processed jobs
	 * and should reach that in the limit when all computations are over
	 */
	protected final AtomicInteger countOutputJobs = new AtomicInteger(0);

	/**
	 * Indicates that some workers are waiting for new processed jobs
	 */
	protected volatile boolean waitingForNewProcessedJobs = false;

	/**
	 * Indicates that there are new processed jobs that are not yet submitted
	 * for post-processing
	 */
	protected volatile boolean newProcessedJobsAvailable = false;

	/**
	 * Creates a saturation engine using a given ontology index for executing
	 * the rules and the upper limit for the number unprocessed input. The limit
	 * has an effect on the size of batches in which the input is processed and
	 * has an effect on throughput and latency of the processing: in general,
	 * the larger the limit is, the faster it takes to perform the overall
	 * processing of jobs, but it might take longer to process an individual
	 * individual job because the jobs are processed in batches.
	 * 
	 * @param ontologyIndex
	 *            the index used for executing the rules
	 * @param maxUnprocessed
	 *            the maximum number of unprocessed jobs at any given time
	 */
	public ClassExpressionSaturationEngine(OntologyIndex ontologyIndex,
			int maxUnprocessed) {
		this.bufferSize = maxUnprocessed;
		this.buffer = new ArrayBlockingQueue<J>(maxUnprocessed);
		this.ruleApplicationEngine = new RuleApplicationEngine(ontologyIndex);
	}

	/**
	 * Creates a saturation engine using a given ontology index for executing
	 * the rules.
	 * 
	 * @param ontologyIndex
	 *            the index used for executing the rules
	 */
	public ClassExpressionSaturationEngine(OntologyIndex ontologyIndex) {
		this(ontologyIndex, 64);
	}

	/**
	 * Submits a job initialized with an indexed class expression for computing
	 * the saturation. Once the job is processed, a method
	 * {@link #processOutput(ReasonerJob)} will be called to post-process this
	 * job. This method is thread safe and different jobs can be executed
	 * concurrently from different threads, however, it is not safe to submit
	 * the same job object several times. It is not guaranteed that
	 * {@link #processOutput(ReasonerJob)} will be called from the same thread
	 * in which the job was submitted; it can be processed by any of the
	 * concurrently running workers since the job pool is shared. It is
	 * guaranteed that all submitted jobs will be processed when no instances of
	 * {@link #process(ReasonerJob)} of the same engine object are running.
	 * 
	 * @param job
	 *            the job initialized with the the indexed class expression for
	 *            which the saturation should be computed
	 * @throws InterruptedException
	 *             if interrupted when the thread was idle
	 */
	public void process(J job) throws InterruptedException {
		IndexedClassExpression root = job.getInput();
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace(root + ": saturation started");
		}
		SaturatedClassExpression rootSaturation = root.getSaturated();
		if (rootSaturation != null && rootSaturation.isSaturated()) {
			job.setOutput(rootSaturation);
			processOutput(job);
			activeWorkers.incrementAndGet();
		} else {
			ruleApplicationEngine.getCreateContext(root);
			/*
			 * invariant: for every buffered job, the context is created and
			 * scheduled for processing
			 */
			while (!this.buffer.offer(job)) {
				/* wait until there are new processed jobs */
				if (!newProcessedJobsAvailable) {
					synchronized (buffer) {
						if (!newProcessedJobsAvailable) {
							waitingForNewProcessedJobs = true;
							buffer.wait();
						}
					}
				}
				outputNewProcessedJobs();
			}
			activeWorkers.incrementAndGet();
			/*
			 * it is important to increment the counter for the buffered jobs
			 * only when the number of active workers is positive
			 */
			countJobs.incrementAndGet();
		}
		ruleApplicationEngine.processActiveContexts();
		if (activeWorkers.decrementAndGet() == 0) {
			int snapshotCountJobs = countJobs.get();
			if (activeWorkers.get() == 0) {
				/*
				 * We increment the counter for the jobs only after a context
				 * for a job was created and it has been added to the buffer,
				 * and only when some worker is active, i.e., the counter for
				 * active workers is greater than zero. At the moment when the
				 * counter for active workers becomes zero, no worker is
				 * processing the active contexts, thus at the time the last
				 * worker finishing processing active contexts, all context
				 * created before become saturated. So at that very time, the
				 * value for the counter for the jobs is equal to the number of
				 * finished jobs. We take a snapshot of the counter between two
				 * such moments, which means that 1) now the value of the
				 * snapshot does not exceed the number of processed jobs and 2)
				 * after every time the last active worker is finished, we will
				 * have a snapshot value (perhaps taken in a different thread)
				 * which will represent at least the number of processed jobs at
				 * that time.
				 * 
				 * Now we update the counter for processed jobs to this
				 * snapshot, provided it is greater. It may happen that the
				 * snapshot value is not greater because several threads can
				 * simultaneously enter this block for different snapshot
				 * values. It is guaranteed, however, that the counter will be
				 * updated to the largest of these values.
				 */
				for (;;) {
					int snapshotCountProcessedJobs = countProcessedJobs.get();
					if (snapshotCountJobs <= snapshotCountProcessedJobs)
						break;
					if (countProcessedJobs.compareAndSet(
							snapshotCountProcessedJobs, snapshotCountJobs)) {
						notifyAboutNewProcessedJobs();
						outputNewProcessedJobs();
						break;
					}
				}
			}
		}
	}

	/**
	 * Notifies, if necessary, that new processed jobs are available for the
	 * output
	 */
	void notifyAboutNewProcessedJobs() {
		newProcessedJobsAvailable = true;
		if (waitingForNewProcessedJobs) {
			synchronized (buffer) {
				waitingForNewProcessedJobs = false;
				buffer.notifyAll();
			}
		}
	}

	/**
	 * Post-processing the newly processed jobs
	 * 
	 * @throws InterruptedException
	 */
	void outputNewProcessedJobs() throws InterruptedException {
		for (;;) {
			int shapshotOutputJobs = countOutputJobs.get();
			if (shapshotOutputJobs == countProcessedJobs.get()) {
				newProcessedJobsAvailable = false;
				/*
				 * before exiting, check to make sure that no new processed jobs
				 * have appeared after the last test and before the variable was
				 * set
				 */
				if (shapshotOutputJobs == countProcessedJobs.get())
					break;
				else
					notifyAboutNewProcessedJobs();
			}
			/*
			 * at this place we know that the number of output jobs is smaller
			 * than the number of processed jobs, if this counter has not been
			 * changed.
			 */
			if (countOutputJobs.compareAndSet(shapshotOutputJobs,
					shapshotOutputJobs + 1)) {
				/*
				 * Since the contexts are created and therefore saturated in the
				 * order they appear in the buffer, it is safe to assume that
				 * the next job in the buffer is processed.
				 */
				J nextJob = buffer.poll();
				SaturatedClassExpression output = nextJob.getInput()
						.getSaturated();
				output.setSaturated();
				nextJob.setOutput(output);
				processOutput(nextJob);
			}
		}
	}

	/**
	 * The hook for post-processing the finished jobs
	 * 
	 * @param job
	 *            the processed job
	 * @throws InterruptedException
	 */
	protected void processOutput(J job) throws InterruptedException {
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace(job.getInput() + ": saturation finished");
		}
	}

}
