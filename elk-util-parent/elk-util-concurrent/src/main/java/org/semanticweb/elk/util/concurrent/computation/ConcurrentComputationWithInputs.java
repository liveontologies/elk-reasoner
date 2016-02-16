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
public class ConcurrentComputationWithInputs<I, F extends InputProcessorFactory<I, ?>>
		extends ConcurrentComputation<F> {
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
	 * Creating a {@link ConcurrentComputationWithInputs} instance.
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
	public ConcurrentComputationWithInputs(F inputProcessorFactory,
			ComputationExecutor executor, int maxWorkers, int bufferCapacity) {
		super(inputProcessorFactory, executor, maxWorkers);
		if (bufferCapacity <= maxWorkers) {
			// we need poisons from the workers plus one input to fit in the
			// buffer
			bufferCapacity = maxWorkers + 1;
		}
		this.bufferCapacity_ = bufferCapacity;
		this.buffer_ = new ArrayBlockingQueue<I>(bufferCapacity);
	}

	/**
	 * Creating a {@link ConcurrentComputationWithInputs} instance.
	 * 
	 * @param inputProcessorFactory
	 *            the factory for input processors
	 * @param executor
	 *            the executor used internally to run the jobs
	 * @param maxWorkers
	 *            the maximal number of concurrent workers processing the jobs
	 */
	public ConcurrentComputationWithInputs(F inputProcessorFactory,
			ComputationExecutor executor, int maxWorkers) {
		this(inputProcessorFactory, executor, maxWorkers, 512 + 32 * maxWorkers);
	}

	/**
	 * Submitting a new input for processing. Submitted input jobs are first
	 * buffered, and then concurrently processed by workers. If the buffer is
	 * full, the method blocks until new space is available.
	 * 
	 * @param input
	 *            the input to be processed
	 * @return {@code true} if the input has been successfully submitted for
	 *         computation; the input cannot be submitted, e.g., if
	 *         {@link #finish()} has been called
	 * @throws InterruptedException
	 *             thrown if interrupted during waiting for space to be
	 *             available
	 */
	public synchronized boolean submit(I input) throws InterruptedException {
		if (termination || isInterrupted())
			return false;
		buffer_.put(input);
		return true;
	}

	@Override
	protected synchronized void waitWorkers() throws InterruptedException {
		if (buffer_.isEmpty())
			// wake up blocked workers if not done already
			buffer_.offer(poison_pill_);
		super.waitWorkers();
		// remove all poison pills		
		while (buffer_.peek() == poison_pill_) {
			buffer_.remove();
		}
	}

	@Override
	Runnable getWorker() {
		return new Worker();
	}

	/**
	 * The {@link Runnable} for workers processing the input
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class Worker implements Runnable {

		/**
		 * Exceptions produced by worker
		 */
		private RuntimeException workerException_ = null;

		@Override
		public final void run() {			
			// TODO: reuse the code from the superclass
			// we use one engine per worker run
			InputProcessor<I> inputProcessor = processorFactory.getEngine();

			try {
				boolean doneProcess = false;
				for (;;) {
					// make sure that all previously submitted inputs are
					// processed
					if (!doneProcess) {
						inputProcessor.process(); // can be interrupted
						doneProcess = true;
					}
					I nextInput = buffer_.take();
					if (nextInput != poison_pill_) {						
						inputProcessor.submit(nextInput); // should not fail
						inputProcessor.process(); // can be interrupted
					}
					if (termination || isInterrupted()) {
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
			} catch (Throwable e) {
				workerException_ = new RuntimeException(
						"Exception in worker thread: ", e);
			} finally {
				if (workerException_ != null)
					throw workerException_;
				inputProcessor.finish();
			}
		}
	}
}
