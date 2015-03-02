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
 * the {@link InputProcessor} objects created by the supplied
 * {@link InputProcessorFactory}. The implementation is loosely based on a
 * producer-consumer framework with one producer and many consumers. The
 * processing of the input should start by calling the {@link #start()} method,
 * following by {@link #submit(Object)} method for submitting input to be
 * processed. The workers will always wait for new input, unless interrupted or
 * {@link #finish()} method is called. If {@link #finish()} is called then no
 * further input can be submitted and the workers will terminate when all input
 * has been processed or they are interrupted earlier, whichever is earlier.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the type of the input to be processed.
 * @param <F>
 *            the type of the factory for the input processors
 */
public class ConcurrentComputation<I, F extends InputProcessorFactory<I, ?>>
		extends SimpleInterrupter {
	/**
	 * the factory for the input processor engines
	 */
	private final F inputProcessorFactory_;
	/**
	 * maximum number of concurrent workers
	 */
	private final int maxWorkers_;
	/**
	 * the executor used internally to run the jobs
	 */
	private final ComputationExecutor executor_;
	/**
	 * the internal buffer for queuing input
	 */
	private final BlockingQueue<I> buffer_;
	/**
	 * the capacity of the buffer
	 */
	private final int bufferCapacity_;

	/**
	 * a special object to "wake up" worker threads waiting for the input
	 */
	@SuppressWarnings("unchecked")
	private final I poison_pill_ = (I) new Object();

	/**
	 * {@code true} the workers should stop either as soon as possible (if the
	 * computation has been terminated) or after there is no further input
	 * available in the buffer
	 */
	private volatile boolean termination_;
	/**
	 * the worker instance used to process the jobs
	 */
	private final Runnable worker_;

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
		this.inputProcessorFactory_ = inputProcessorFactory;
		if (bufferCapacity <= maxWorkers)
			// we need poisons from the workers plus one input to fit in the
			// buffer
			bufferCapacity = maxWorkers + 1;
		this.buffer_ = new ArrayBlockingQueue<I>(bufferCapacity);
		this.bufferCapacity_ = bufferCapacity;
		this.termination_ = false;
		this.worker_ = new Worker();
		this.executor_ = executor;
		this.maxWorkers_ = maxWorkers;
	}

	/**
	 * Creating a {@link ConcurrentComputation} instance.
	 * 
	 * @param inputProcessorFactory
	 *            the factory for input processors
	 * @param executor
	 *            the executor used internally to run the jobs
	 * @param maxWorkers
	 *            the maximal number of concurrent workers processing the jobs
	 */
	public ConcurrentComputation(F inputProcessorFactory,
			ComputationExecutor executor, int maxWorkers) {
		this(inputProcessorFactory, executor, maxWorkers, 512 + 32 * maxWorkers);
	}

	/**
	 * @return the {@link InputProcessorFactory} used by this
	 *         {@link ConcurrentComputation}
	 */
	public F getInputProcessorFactory() {
		return this.inputProcessorFactory_;
	}

	/**
	 * Starts the workers to process the input.
	 * 
	 * @return {@code true} if the operation was successful
	 */
	public synchronized boolean start() {
		return executor_.start(worker_, maxWorkers_);
	}

	/**
	 * Submitting a new input for processing. Submitted input jobs are first
	 * buffered, and then concurrently processed by workers. If the buffer is
	 * full, the method blocks until new space is available.
	 * 
	 * @param input
	 *            the input to be processed
	 * @return {@code true} if the input has been successfully submitted for
	 *         computation; the input cannot be submitted if {@link #finish()}
	 *         was called or the computation has been terminated
	 * @throws InterruptedException
	 *             thrown if interrupted during waiting for space to be
	 *             available
	 */
	public synchronized boolean submit(I input) throws InterruptedException {
		if (termination_)
			return false;
		buffer_.put(input);
		return true;
	}

	@Override
	public final void setInterrupt(boolean flag) {
		this.termination_ = flag;
		super.setInterrupt(flag);
		inputProcessorFactory_.setInterrupt(flag);
	}

	/**
	 * Wait until all currently running workers stop; no input can be submitted
	 * after calling this method. When this method returns, no worker should be
	 * running.
	 * 
	 * @throws InterruptedException
	 */
	protected synchronized void waitWorkers() throws InterruptedException {
		if (buffer_.isEmpty())
			// wake up blocked workers if not done already
			buffer_.offer(poison_pill_);
		executor_.waitDone();
		// remove all poison pills
		while (buffer_.peek() == poison_pill_) {
			buffer_.remove();
		}
	}

	/**
	 * Marks the end of the input and requests all workers to terminate when all
	 * currently submitted input has been processed. After calling this method,
	 * no new input can be submitted anymore, i.e., calling
	 * {@link #submit(Object)} will always return {@code false}. The method
	 * blocks until all workers have been stopped. If interrupted while blocked,
	 * this method can be called again in order to complete the termination
	 * request.
	 * 
	 * @throws InterruptedException
	 *             if interrupted during waiting for finish request
	 */
	public synchronized void finish() throws InterruptedException {
		termination_ = true;
		waitWorkers();
		if (!isInterrupted()) {
			termination_ = false;
			inputProcessorFactory_.finish();
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
			// we use one engine per worker run
			InputProcessor<I> inputProcessor = inputProcessorFactory_
					.getEngine();

			try {
				boolean previousProcessed = false;
				for (;;) {
					// make sure that all previously submitted inputs are
					// processed
					if (!previousProcessed) {
						inputProcessor.process(); // can be interrupted
						if (!isInterrupted())
							previousProcessed = true;
					}
					I nextInput = buffer_.take();
					if (nextInput != poison_pill_) {
						inputProcessor.submit(nextInput); // should not fail
						inputProcessor.process(); // can be interrupted
					}
					if (termination_) {
						if (buffer_.isEmpty()) {
							// wake up blocked workers if not done already
							buffer_.put(poison_pill_);
							break;
						}
						if (isInterrupted()) {
							if (buffer_.size() == bufferCapacity_) {
								// buffer is full, producer may be blocked,
								// need to consume one more input
								continue;
							}
							// else
							break;
						}
					}
				}
			} catch (InterruptedException e) {
				/*
				 * we don't know what is causing this but we need to obey;
				 * consistency of the computation for such interrupt is not
				 * guaranteed; restore the interrupt status and exit
				 */
				Thread.currentThread().interrupt();
			} finally {
				inputProcessor.finish();
			}
		}
	}
}
