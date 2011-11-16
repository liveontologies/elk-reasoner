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
	 * An upper bound on the the number of workers modifying the active
	 * saturations queue the saturations themselves; if the value of this
	 * counter is zero, this should guarantee that all saturated class
	 * expressions created thus far are processed.
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
		/*
		 * invariant: if the counter for active workers = 0, then for every job
		 * in the buffer, a saturated class expression has been initialized and
		 * processed (together with all dependent saturations). Note that it is
		 * possible that a job has been inserted into the buffer, and the
		 * assigned saturation is being initialized by another worker and not
		 * yet finished or processed. It is guaranteed in this case, however,
		 * that the counter for active workers remains positive until the
		 * initialization and processing will be over.
		 */
		activeWorkers.incrementAndGet();
		ruleApplicationEngine.getCreateContext(root);
		ruleApplicationEngine.processActiveContexts();
		activeWorkers.decrementAndGet();
		/*
		 * this check is to make sure we do not stuck inserting things into the
		 * buffer when there are processed jobs available that can be removed
		 */
		checkProcessedJobs();
		buffer.put(job);
		/*
		 * we increment the counter only after the job is inserted to the buffer
		 */
		countJobs.incrementAndGet();
		/* check that all jobs are processed before we finish */
		checkProcessedJobs();
	}

	void checkProcessedJobs() throws InterruptedException {
		int snapshotCountJobs = countJobs.get();
		/*
		 * At the time the counter of active workers becomes zero, we know that
		 * for every job in the buffer, a saturation was assigned, initialized
		 * and processed, together with all the dependent saturations.
		 * Therefore, at that time, every job in the buffer is finished. Since
		 * the counter for the jobs never exceeds the number of jobs ever
		 * inserted into the buffer, the snapshot value, taken before the active
		 * worker counter becomes zero, is at most the number of finished jobs
		 * in the buffer coming consecutively from the head + the number of jobs
		 * that were taken from the buffer before.
		 */
		if (activeWorkers.get() == 0) {
			/*
			 * Now we update the counter for processed jobs to the taken
			 * snapshot, provided it is greater. It may happen that the snapshot
			 * value is not greater because several threads can simultaneously
			 * enter this block for different snapshot values. It is guaranteed,
			 * however, that the counter will be updated to the largest of them.
			 */
			for (;;) {
				int snapshotCountProcessedJobs = countProcessedJobs.get();
				if (snapshotCountJobs <= snapshotCountProcessedJobs)
					break;
				if (countProcessedJobs.compareAndSet(
						snapshotCountProcessedJobs, snapshotCountJobs)) {
					break;
				}
			}
		}
		/*
		 * now we check if the counter for output jobs can be increased and new
		 * jobs can be taken from the buffer; this is independent from whether
		 * the counter for the processed jobs has been updated or not in the
		 * previous step since this can happen in another thread.
		 */
		for (;;) {
			int shapshotOutputJobs = countOutputJobs.get();
			if (shapshotOutputJobs == countProcessedJobs.get()) {
				break;
			}
			/*
			 * at this place we know that the number of output jobs is smaller
			 * than the number of processed jobs if this counter has not been
			 * changed.
			 */
			if (countOutputJobs.compareAndSet(shapshotOutputJobs,
					shapshotOutputJobs + 1)) {
				/*
				 * We have updated the counter for processed jobs only when we
				 * know how many *consecutive* jobs in the buffer are processed.
				 * Therefore, it is safe to assume that the next job in the
				 * buffer is processed.
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
