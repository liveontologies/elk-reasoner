/*
 * #%L
 * ELK Utilities for Concurrency
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
package org.semanticweb.elk.util.concurrent.computation;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

/**
 * An class for concurrent processing of a number of tasks. The input for the
 * tasks are submitted, buffered, and processed in batches by concurrent workers
 * using an {@link InputProcessor} . The implementation is loosely based on a
 * produce-consumer framework with one producer and many consumers.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the type of the input to be processed.
 */
public class ConcurrentComputation<I> {
	/**
	 * processor for the input
	 */
	protected final InputProcessor<I> inputProcessor;
	/**
	 * executor used to run the jobs
	 */
	protected final ExecutorService executor;
	/**
	 * maximum number of concurrent workers
	 */
	protected final int maxWorkers;
	/**
	 * the buffer for queuing jobs
	 */
	protected final BlockingQueue<Job<I>> buffer;
	/**
	 * the maximum number of jobs in a batch
	 */
	protected final int batchSize;
	/**
	 * the current batch
	 */
	protected JobBatch<I> batch;
	/**
	 * The worker instance used to process the jobs
	 */
	protected final Worker worker = new Worker();
	/**
	 * The poison instance used to terminate the jobs
	 */
	protected final JobPoison<I> poison = new JobPoison<I>();
	/**
	 * counts the number of poison values submitted to terminate the jobs
	 */
	protected int poisonCounter;

	/**
	 * Creating a computation instance.
	 * 
	 * @param inputProcessor
	 *            the processor for the input to be executed by workers
	 * @param executor
	 *            the executor used to execute the concurrent jobs
	 * @param maxWorkers
	 *            the maximal number of concurrent workers processing the jobs
	 * @param bufferCapacity
	 *            the size of the buffer for scheduled jobs; if the buffer is
	 *            full, submitting new jobs will block until new space is
	 *            available
	 */
	public ConcurrentComputation(InputProcessor<I> inputProcesor,
			ExecutorService executor, int maxWorkers, int bufferCapacity,
			int batchSize) {
		this.inputProcessor = inputProcesor;
		this.executor = executor;
		this.maxWorkers = maxWorkers;
		this.buffer = new ArrayBlockingQueue<Job<I>>(bufferCapacity);
		this.batchSize = batchSize;
	}

	/**
	 * Start processing of the input.
	 */
	public final void start() {
		buffer.clear();
		batch = new JobBatch<I>(batchSize);
		poisonCounter = 0;
		for (int i = 0; i < maxWorkers; i++) {
			executor.execute(worker);
		}
	}

	/**
	 * Submitting a new input for processing. Submitted input jobs are first
	 * added to a buffer, after which they are concurrently processed by
	 * workers. If the buffer is full, the method blocks until new space is
	 * available.
	 * 
	 * @param input
	 *            the input to be processed
	 * @throws InterruptedException
	 *             thrown if interrupted during waiting for space to be
	 *             available
	 */
	public final void submit(I input) throws InterruptedException {
		batch.add(input);
		if (batch.size() == batchSize) {
			buffer.put(batch);
			batch = new JobBatch<I>(batchSize);
		}
	}

	/**
	 * The class that is used to process and run the input jobs.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	protected final class Worker implements JobProcessor<I, Boolean>, Runnable {

		public final Boolean process(JobBatch<I> batch)
				throws InterruptedException {
			for (I input : batch) {
				// processing the input using the input processor
				inputProcessor.submit(input);
				inputProcessor.process();
			}
			return true;
		}

		public final Boolean process(JobPoison<I> job) {
			return false;
		}

		public final void run() {
			for (;;) {
				try {
					Job<I> nextJob = buffer.take();
					if (!nextJob.accept(this)) {
						// the element is a poison; the worker should die
						break;
					}
				} catch (InterruptedException e) {
				}
			}
			if (buffer.isEmpty()) {
				// if the buffer is empty, this might be the last worker running
				synchronized (buffer) {
					// notify the producer thread about it
					buffer.notify();
				}
			}

		}
	}

	/**
	 * Block until all pending jobs are processed. If interrupted, this method
	 * can be executed again to wait until the jobs are finished.
	 * 
	 * @throws InterruptedException
	 *             thrown if interrupted during waiting
	 */
	public final void waitCompletion() throws InterruptedException {
		// submit the remaining jobs
		buffer.put(batch);
		while (poisonCounter < maxWorkers) {
			// for each worker we put a poison to die
			buffer.put(poison);
			poisonCounter++;
		}
		// wait until the buffer becomes empty; it will mean that all the
		// workers are dead
		synchronized (buffer) {
			while (!buffer.isEmpty()) {
				buffer.wait();
			}
		}

	}
}
