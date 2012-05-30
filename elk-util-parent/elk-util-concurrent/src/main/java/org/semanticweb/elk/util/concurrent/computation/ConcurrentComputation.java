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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An class for concurrent processing of a number of tasks. The input for the
 * tasks are submitted, buffered, and processed in batches by concurrent workers
 * using the supplied {@link InputProcessor}. The implementation is loosely
 * based on a produce-consumer framework with one producer and many consumers.
 * The processing of the input should start by calling the {@link #start()}
 * method, following by {@link #submit(I)} method for submitting input to be
 * processed. All workers can be interrupted by calling the {@link #interrupt()}
 * method. If the workers are not interrupted, the method {@link #finish()}
 * could be used to request the workers to stop after all input will be
 * processed. Otherwise the workers will always wait for new input, unless
 * interrupted. To wait until all workers stop, a method
 * {@link #waitWorkersToStop()} can be used.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the type of the input to be processed.
 */
public class ConcurrentComputation<I> {
	/**
	 * the processor for the input to be executed by workers
	 */
	protected final InputProcessor<I> inputProcessor;
	/**
	 * the interrupter to interrupt and monitor task interruption
	 */
	protected final Interrupter interrupter;
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
	protected final Worker worker = new Worker(Thread.currentThread());
	/**
	 * The poison instance used to terminate the jobs
	 */
	protected final JobPoison<I> poison = new JobPoison<I>();
	/**
	 * increments every time a worker starts
	 */
	protected volatile int startedWorkers;
	/**
	 * increments every time a worker finishes
	 */
	protected final AtomicInteger finishedWorkers;
	/**
	 * <tt>true</tt> if the finish of computation was requested using the
	 * function {@link #finish()}
	 */
	protected boolean finishRequested;
	/**
	 * the number of poison elements submitted
	 */
	protected int poisonCount;

	/**
	 * Creating a computation instance.
	 * 
	 * @param inputProcessor
	 *            the processor for the input to be executed by workers
	 * @param interrupter
	 *            the interrupter to interrupt and monitor task interruption
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
			Interrupter interrupter, ExecutorService executor, int maxWorkers,
			int bufferCapacity, int batchSize) {
		this.inputProcessor = inputProcesor;
		this.interrupter = interrupter;
		this.executor = executor;
		this.maxWorkers = maxWorkers;
		this.buffer = new ArrayBlockingQueue<Job<I>>(bufferCapacity);
		this.batch = new JobBatch<I>(batchSize);
		this.batchSize = batchSize;
		this.startedWorkers = 0;
		this.finishedWorkers = new AtomicInteger(0);
		this.finishRequested = false;
		this.poisonCount = 0;
	}

	/**
	 * Starts {@link #maxWorkers} workers to process the input.
	 */
	public synchronized void start() {
		for (;;) {
			if (startedWorkers == finishedWorkers.get() + maxWorkers)
				break;
			executor.execute(worker);
			startedWorkers++;
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
	 * @return <tt>true</tt> if the input has been successfully submitted for
	 *         computation
	 * @throws InterruptedException
	 *             thrown if interrupted during waiting for space to be
	 *             available
	 */
	public synchronized boolean submit(I input) throws InterruptedException {
		if (finishRequested)
			return false;
		if (batch.size() == batchSize) {
			buffer.put(batch);
			batch = new JobBatch<I>(batchSize);
		}
		batch.add(input);
		return true;
	}

	/**
	 * The class that is used to process and run the input jobs.
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	protected final class Worker implements JobProcessor<I, Boolean>, Runnable {

		/**
		 * The thread that has created this worker
		 */
		protected final Thread creator;

		public Worker(Thread creator) {
			this.creator = creator;
		}

		@Override
		public final Boolean process(JobBatch<I> batch)
				throws InterruptedException {
			for (I input : batch) {
				// processing the input using the input processor
				inputProcessor.submit(input);
				inputProcessor.process();
			}
			return true;
		}

		@Override
		public final Boolean process(JobPoison<I> job) {
			return false;
		}

		@Override
		public final void run() {
			Job<I> nextJob;
			for (;;) {
				if (interrupter.isInterrupted()) {
					Thread.currentThread().interrupt();
					if (!creator.isInterrupted())
						creator.interrupt();
					break;
				}
				try {
					nextJob = buffer.take();
					if (!nextJob.accept(this)) {
						// the element is a poison; the worker should die
						break;
					}
				} catch (InterruptedException e) {
					// we interrupt all workers if one has been interrupted
					interrupter.interrupt();
				}
			}
			if (finishedWorkers.incrementAndGet() == startedWorkers) {
				// this might be the last worker running
				synchronized (finishedWorkers) {
					// notify the producer thread about it
					finishedWorkers.notify();
				}
			}
		}
	}

	/**
	 * Block and wait until all workers terminate. The workers terminate either
	 * if the finish request has been issued using
	 * {@link ConcurrentComputation#finish()} or the workers were interrupted,
	 * e.g., by calling the {@link Interrupter#interrupt()} method of the
	 * interrupter associated with this computation. If interrupted, this method
	 * can be executed again to wait until all workers terminate.
	 * 
	 * @throws InterruptedException
	 *             thrown if interrupted during waiting
	 */
	public synchronized void waitWorkersToStop() throws InterruptedException {
		for (;;) {
			if (finishedWorkers.get() == startedWorkers)
				break;
			synchronized (finishedWorkers) {
				if (finishedWorkers.get() == startedWorkers)
					break;
				finishedWorkers.wait();
			}
		}
	}

	/**
	 * Requests all workers to terminate when there is no input in the buffer.
	 * If this method is not called, normally, workers will wait for the new
	 * input. After calling this method, no new input can be submitted anymore,
	 * i.e., calling {@link #submit(I)} will always return <tt>false</tt>. When
	 * {@link #finish()} exits, it does not mean that all workers have been
	 * stopped. The method {@link #waitWorkersToStop()} should be called to wait
	 * until all workers are stopped. This method can be blocked if the buffer
	 * is full. If interrupted while blocked, this method should be called again
	 * in order to complete the termination request.
	 * 
	 * @throws InterruptedException
	 *             if interrupted during waiting for finish request
	 */
	public synchronized void finish() throws InterruptedException {
		if (!finishRequested) {
			buffer.put(batch);
			batch = null;
			finishRequested = true;
		}
		// poisoning the running workers
		for (;;) {
			if (poisonCount == maxWorkers)
				break;
			buffer.put(poison);
			poisonCount++;
		}
	}
}
