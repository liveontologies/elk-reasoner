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
	protected final BlockingQueue<I> buffer;
	// we are never going to call any method on this object
	@SuppressWarnings("unchecked")
	private I POISION_PILL = (I) new Object();

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
		this.buffer = new ArrayBlockingQueue<I>(bufferCapacity);
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
		if (finishRequested)
			return false;
		buffer.put(input);
		return true;
	}

	/**
	 * Request all currently running workers to stop; no input can be submitted
	 * after calling this method. When this method returns, no worker should be
	 * running.
	 * 
	 * @return the submitted inputs that were not processed by the workers
	 * @throws InterruptedException
	 */
	@Override
	public synchronized Iterable<I> interrupt() throws InterruptedException {
		if (!interrupted) {
			interrupted = true;
			executor.interrupt();
		}
		executor.waitDone();
		return buffer;
	}

	@Override
	Runnable getWorker() {
		return new Worker();
	}

	@Override
	public void finish() throws InterruptedException {
		if (!finishRequested) {
			finishRequested = true;
			buffer.put(POISION_PILL);
		}
		executor.waitDone();
		processorFactory.finish();
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
			I nextInput = null;
			// we use one engine per worker run
			InputProcessor<I> inputProcessor = processorFactory.getEngine();

			try {
				boolean doneProcess = false;
				for (;;) {
					if (interrupted) {
						break;
					}
					try {
						// make sure that all previously submitted inputs are
						// processed
						if (!doneProcess) {
							inputProcessor.process(); // can be interrupted
							doneProcess = true;
						}
						if (finishRequested) {
							// do not poll if we've already eaten the poison pill
							nextInput = buffer.poll();
							//if (nextInput == null) {
							if (nextInput == POISION_PILL || nextInput == null) {
								// let's poison the workers blocked by the empty buffer
								buffer.put(POISION_PILL);
								// make sure nothing is left unprocessed
								inputProcessor.process(); // can be interrupted
								if (!interrupted && Thread.interrupted())
									continue;
								break;
							}
						} else {
							nextInput = buffer.take();
							
							if (nextInput == POISION_PILL) {
								continue;
							}
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
