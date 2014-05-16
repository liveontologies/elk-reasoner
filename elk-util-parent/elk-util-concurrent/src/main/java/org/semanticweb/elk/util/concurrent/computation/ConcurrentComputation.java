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

/**
 * An class for concurrent processing using the supplied
 * {@link ProcessorFactory}. Processing is performed concurrently by several
 * workers, each using a {@link Processor} created by the
 * {@link ProcessorFactory}. Processing starts by calling {@link #start()},
 * which creates a specified number of working threads and starts concurrent
 * processing. All workers can be interrupted by calling {@link #interrupt()}.
 * When {@link #finish()} is called, the current thread blocks until everything
 * is processed by the workers or all workers have been interrupted.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <F>
 *            the type of the factory for the input processors
 */
public class ConcurrentComputation<F extends ProcessorFactory<?>> {
	/**
	 * the factory for the input processor engines
	 */
	protected final F processorFactory;
	/**
	 * maximum number of concurrent workers
	 */
	protected final int maxWorkers;
	/**
	 * the executor used internally to run the jobs
	 */
	protected final ComputationExecutor executor;
	/**
	 * {@code true} if the finish of computation was requested using the
	 * function {@link #finish()}
	 */
	protected volatile boolean finishRequested;
	/**
	 * {@code true} if the computation has been interrupted
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
		this.processorFactory = inputProcessorFactory;
		this.finishRequested = false;
		this.interrupted = false;
		this.worker = getWorker();
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
	 * @param maxWorkers
	 *            the maximal number of concurrent workers processing the jobs
	 */
	public ConcurrentComputation(F inputProcessorFactory,
			ComputationExecutor executor, int maxWorkers) {
		this(inputProcessorFactory, executor, maxWorkers, 512 + 32 * maxWorkers);
	}

	/**
	 * Starts the workers to process the input.
	 * 
	 * @return {@code true} if the operation was successful
	 */
	public synchronized boolean start() {
		finishRequested = false;
		interrupted = false;
		return executor.start(worker, maxWorkers);
	}

	/**
	 * Request all currently running workers to stop; no input can be submitted
	 * after calling this method. When this method returns, no worker should be
	 * running.
	 * 
	 * @throws InterruptedException
	 */
	public synchronized Object interrupt() throws InterruptedException {
		if (!interrupted) {
			interrupted = true;
			executor.interrupt();
		}
		executor.waitDone();
		return null;
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
	public void finish() throws InterruptedException {
		if (!finishRequested) {
			finishRequested = true;
			executor.interrupt();
		}
		executor.waitDone();
		processorFactory.finish();
	}

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
			// we use one engine per worker run
			Processor inputProcessor = processorFactory.getEngine();

			try {
				for (;;) {
					if (interrupted) {
						break;
					}
					try {
						inputProcessor.process(); // can be interrupted
						if (!interrupted && Thread.interrupted())
							// spurious interrupt
							continue;
						break;
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
