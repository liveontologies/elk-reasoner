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
 * A class for concurrent processing using the supplied {@link ProcessorFactory}
 * . Processing is performed concurrently by several workers, each using a
 * {@link Processor} created by the {@link ProcessorFactory}. Processing starts
 * by calling {@link #start()}, which creates a specified number of working
 * threads and starts concurrent processing. All workers can be interrupted by
 * calling {@link #setInterrupt(boolean)} with {@code false}. When
 * {@link #finish()} is called, the current thread blocks until everything is
 * processed by the workers or all workers have been interrupted.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <F>
 *            the type of the factory for the input processors
 */
public class ConcurrentComputation<F extends ProcessorFactory<?>> extends
		SimpleInterrupter {
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
	 * {@code true} the workers should stop either as soon as possible (if the
	 * computation has been terminated) or after {@link #finish()} is called
	 */
	protected boolean termination;
	/**
	 * the worker instance used to process the jobs
	 */
	protected final Runnable worker;

	/**
	 * Creating a {@link ConcurrentComputation} instance.
	 * 
	 * @param processorFactory
	 *            the factory for input processors
	 * @param executor
	 *            the executor used internally to run the jobs
	 * @param maxWorkers
	 *            the maximal number of concurrent workers processing the jobs
	 */
	public ConcurrentComputation(F processorFactory,
			ComputationExecutor executor, int maxWorkers) {
		this.processorFactory = processorFactory;
		this.termination = false;
		this.worker = getWorker();
		this.executor = executor;
		this.maxWorkers = maxWorkers;
	}

	/**
	 * Starts the workers to process the input.
	 * 
	 * @return {@code true} if the operation was successful
	 */
	public synchronized boolean start() {
		termination = false;
		return executor.start(worker, maxWorkers);
	}

	@Override
	public synchronized final void setInterrupt(boolean interrupt) {
		super.setInterrupt(interrupt);
		processorFactory.setInterrupt(interrupt);		
	}

	protected synchronized void waitWorkers() throws InterruptedException {
		executor.waitDone();
	}

	/**
	 * Requests all workers to terminate when processing is finished. The method
	 * blocks until all workers have been stopped. If interrupted while blocked,
	 * this method can be called again in order to complete the termination
	 * request.
	 * 
	 * @throws InterruptedException
	 *             if interrupted during waiting for finish request
	 */
	public synchronized void finish() throws InterruptedException {
		termination = true;
		waitWorkers();
		if (!isInterrupted()) {
			processorFactory.finish();
		}
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
					// FIXME: potential empty loop
					inputProcessor.process(); // can be interrupted
					if (termination || isInterrupted()) {
						break;
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
