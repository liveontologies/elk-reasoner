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

/**
 * An class for concurrent processing of a number of tasks. The input for the
 * tasks are submitted, buffered, and processed by concurrent workers using the
 * supplied {@link InputProcessor}. The implementation is loosely based on a
 * produce-consumer framework with one producer and many consumers. The
 * processing of the input should start by calling the {@link #start()} method,
 * following by {@link #submit(I)} method for submitting input to be processed.
 * The workers will always wait for new input, unless interrupted or
 * {@link #finish()} method is called. If {@link #finish()} is called then no
 * further input can be submitted and the workers will terminate when all input
 * has been processed, unless they are interrupt
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the type of the input to be processed.
 * @param <P>
 *            the type of the processor for the input
 * @param <F>
 *            the type of the factory for the input processors
 */
public class ConcurrentComputation<I, P extends InputProcessor<I>, F extends InputProcessorFactory<I, P>> {
	/**
	 * the factory for input processors
	 */
	protected final F inputProcessorFactory;
	/**
	 * maximum number of concurrent workers
	 */
	protected final int maxWorkers;
	/**
	 * the executor used internally to run the jobs
	 */
	protected final ComputationExecutor executor;
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
	 * <tt>true</tt> if the computation has been interrupted
	 */
	protected volatile boolean interrupted;
	/**
	 * the worker instance used to process the jobs
	 */
	protected final Runnable worker;

	/**
	 * Creating a {@link ConcurrentComputation} instance.
	 * 
	 * @param inputProcessorFactory
	 *            the factory for input processors
	 * @param executor
	 *            the executor used internally to run the jobs
	 * @param maxWorkers
	 *            the maximal number of concurrent workers processing the jobs
	 * @param bufferCapacity
	 *            the size of the buffer for scheduled jobs; if the buffer is
	 *            full, submitting new jobs will block until new space is
	 *            available
	 */
	public ConcurrentComputation(F inputProcessorFactory,
			ComputationExecutor executor, int maxWorkers, int bufferCapacity) {
		this.inputProcessorFactory = inputProcessorFactory;
		this.buffer = new ArrayBlockingQueue<I>(bufferCapacity);
		this.finishRequested = false;
		this.interrupted = false;
		this.worker = new Worker();
		this.executor = executor;
		this.maxWorkers = maxWorkers;
	}

	/**
	 * Creating a {@link ConcurrentComputation} instance.
	 * 
	 * @param inputProcessorFactory
	 *            the factory for input processors
	 * @param executor
	 *            the executor used internally to run the jobs
	 * @param interrupter
	 *            the interrupter to interrupt and monitor task interruption
	 * @param maxWorkers
	 *            the maximal number of concurrent workers processing the jobs
	 */
	public ConcurrentComputation(F inputProcessorFactory,
			ComputationExecutor executor, int maxWorkers) {
		this(inputProcessorFactory, executor, maxWorkers, 64);
	}

	/**
	 * Starts the workers to process the input.
	 */
	public synchronized void start() {
		finishRequested = false;
		interrupted = false;
		executor.start(worker, maxWorkers);
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

	public synchronized Iterable<I> interrupt() throws InterruptedException {
		if (!interrupted) {
			interrupted = true;
			executor.interrupt();
		}
		executor.waitDone();
		return buffer;
	}

	/**
	 * Marks the end of the input and requests all workers to terminate when all
	 * currently submitted input has been processed. After calling this method,
	 * no new input can be submitted anymore, i.e., calling {@link #submit(I)}
	 * will always return <tt>false</tt>. The method blocks until all workers
	 * have been stopped. If the computation of workers has been interrupted,
	 * the method will return the submitted input elements that have not been
	 * yet submitted to the {@link InputProcessor}. If interrupted while
	 * blocked, this method should be called again in order to complete the
	 * termination request.
	 * 
	 * @return the submitted input elements that have not been yet submitted to
	 *         the supplied {@link InputProcessor}
	 * @throws InterruptedException
	 *             if interrupted during waiting for finish request
	 */
	public void finish() throws InterruptedException {
		if (!finishRequested) {
			finishRequested = true;
			executor.interrupt();
		}
		executor.waitDone();
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
			P inputProcessor = inputProcessorFactory.getEngine();
			for (;;) {
				if (interrupted)
					break;
				try {
					if (finishRequested) {
						nextInput = buffer.poll();
						if (nextInput == null) {
							// make sure nothing is left unprocessed
							inputProcessor.process();
							if (!interrupted && Thread.interrupted())
								continue;
							break;
						}
					} else {
						nextInput = buffer.take();
					}
					inputProcessor.submit(nextInput); // should never fail
					inputProcessor.process(); // can be interrupted
				} catch (InterruptedException e) {
					if (interrupted) {
						// restore the interrupt status and exit
						Thread.currentThread().interrupt();
						break;
					}
				}
			}
			inputProcessor.finish();
		}
	}
}
