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
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * An class for concurrent processing of a number of tasks. The input for the
 * tasks are submitted, buffered, and processed by concurrent workers using the
 * supplied {@link InputProcessor}. The implementation is loosely based on a
 * produce-consumer framework with one producer and many consumers. The
 * processing of the input should start by calling the {@link #start()} method,
 * following by {@link #submit(I)} method for submitting input to be processed.
 * The workers will always wait for new input, unless interrupted or
 * {@link #finish()} method is called. The workers can be interrupted by calling
 * the {@link Interrupter#interrupt()} method of the supplied interrupter. If
 * {@link #finish()} is called then no further input can be submitted and the
 * workers will terminate when all input has been processed, unless they are
 * interrupt
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the type of the input to be processed.
 * @param <P>
 *            the type of the processor for the input
 */
public class ConcurrentComputation<I, P extends InputProcessor<I>> {
	/**
	 * the processor for the input to be executed by workers
	 */
	protected final P inputProcessor;
	/**
	 * the interrupter to interrupt and monitor task interruption
	 */
	protected final Interrupter interrupter;
	/**
	 * maximum number of concurrent workers
	 */
	protected final int maxWorkers;
	/**
	 * the executor used internally to run the jobs
	 */
	protected volatile ExecutorService executor; // TODO: use one thread pool
	/**
	 * the internal buffer for queuing input
	 */
	protected final BlockingQueue<I> buffer;
	/**
	 * <tt>true</tt> if the finish of computation was requested using the
	 * function {@link #finish()}
	 */
	protected volatile boolean finishRequested;
	/**
	 * the worker instance used to process the jobs
	 */
	protected final Runnable worker;

	/**
	 * Creating a {@link ConcurrentComputation} instance.
	 * 
	 * @param inputProcessor
	 *            the processor for the input to be executed by workers
	 * @param interrupter
	 *            the interrupter to interrupt and monitor task interruption
	 * @param maxWorkers
	 *            the maximal number of concurrent workers processing the jobs
	 * @param bufferCapacity
	 *            the size of the buffer for scheduled jobs; if the buffer is
	 *            full, submitting new jobs will block until new space is
	 *            available
	 */
	public ConcurrentComputation(P inputProcessor, Interrupter interrupter,
			int maxWorkers, int bufferCapacity) {
		this.inputProcessor = inputProcessor;
		this.interrupter = interrupter;
		this.maxWorkers = maxWorkers;
		this.buffer = new ArrayBlockingQueue<I>(bufferCapacity);
		this.finishRequested = false;
		this.worker = new Worker();
	}

	/**
	 * Creating a {@link ConcurrentComputation} instance.
	 * 
	 * @param inputProcessor
	 *            the processor for the input to be executed by workers
	 * @param interrupter
	 *            the interrupter to interrupt and monitor task interruption
	 * @param maxWorkers
	 *            the maximal number of concurrent workers processing the jobs
	 */
	public ConcurrentComputation(P inputProcesor, Interrupter interrupter,
			int maxWorkers) {
		this(inputProcesor, interrupter, maxWorkers, 64);
	}

	/**
	 * Starts {@link #maxWorkers} workers to process the input.
	 */
	public synchronized void start() {
		executor = Executors.newFixedThreadPool(maxWorkers);
		finishRequested = false;
		for (int i = 0; i < maxWorkers; i++) {
			executor.execute(worker);
		}
	}

	/**
	 * Submitting a new input for processing. Submitted input jobs are first
	 * buffered, and then concurrently processed by workers. If the buffer is
	 * full, the method blocks until new space is available.
	 * 
	 * @param input
	 *            the input to be processed
	 * @return <tt>true</tt> if the input has been successfully submitted for
	 *         computation; the input cannot be submitted, e.g., if
	 *         {@link #finish()} has been called
	 * @throws InterruptedException
	 *             thrown if interrupted during waiting for space to be
	 *             available
	 */
	public synchronized boolean submit(I input) throws InterruptedException {
		if (finishRequested)
			return false;
		buffer.put(input);
		return true;
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

	/**
	 * Marks the end of the input and requests all workers to terminate when all
	 * currently submitted input has been processed. After calling this method,
	 * no new input can be submitted anymore, i.e., calling {@link #submit(I)}
	 * will always return <tt>false</tt>. The method blocks until all workers
	 * have been stopped. If the computation has been interrupted through
	 * {@link Interrupter#interrupt()} either before or after calling this
	 * method, the method will return the submitted input elements that have not
	 * been yet submitted to the {@link InputProcessor}. If interrupted while
	 * blocked, this method should be called again in order to complete the
	 * termination request.
	 * 
	 * @return the submitted input elements that have not been yet submitted to
	 *         the supplied {@link InputProcessor}
	 * @throws InterruptedException
	 *             if interrupted during waiting for finish request
	 */
	public synchronized Iterable<I> finish() throws InterruptedException {
		if (!finishRequested) {
			finishRequested = true;
			// make sure all waiting workers wake up
			executor.shutdownNow();
		}
		for (;;) {
			executor.awaitTermination(60, TimeUnit.SECONDS);
			if (executor.isTerminated())
				return buffer;
		}
	}

	/**
	 * The {@link Runnable} for workers processing the input
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class Worker implements Runnable {
		@Override
		public final void run() {
			I nextInput;
			for (;;) {
				if (interrupter.isInterrupted()) {
					// FIXME: not safe when workers start
					// if (!executor.isShutdown())
					// executor.shutdownNow();
					break;
				}
				try {
					if (finishRequested) {
						nextInput = buffer.poll();
						if (nextInput == null) {
							// make sure nothing is left unprocessed
							inputProcessor.process();
							break;
						}
					} else
						nextInput = buffer.take();
					inputProcessor.submit(nextInput); // should never fail
					inputProcessor.process(); // can be interrupted
				} catch (InterruptedException e) {
					/*
					 * something has happened: either the whole execution was
					 * interrupted in which case we need to quit as soon as
					 * possible, or finish has been requested, in which case we
					 * need to drain the buffer before exiting
					 */
				}
			}
		}
	}
}
