/*
 * #%L
 * ELK Utilities for Concurrency
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A simple executor for starting a several copies of runnable tasks in
 * parallel, and waiting for it computation. New tasks can be started only if
 * all copies of the previous tasks were finished. All tasks run in a thread
 * pool of the given size. The threads in this pool are created on demand; its
 * size can be changed and idle threads can be terminated after a timeout.
 * 
 * @author "Yevgeny Kazakov"
 */
public class ComputationExecutor {

	/**
	 * a common prefix for the names of the threads created by this
	 * {@link ComputationExecutor}
	 */
	private final String threadPoolName_;

	/**
	 * the latch indicating that job processing is done
	 */
	private CountDownLatch jobDone_;

	/**
	 * {@code true} if new tasks can be started to be executed; this can happen
	 * only if no tasks are running in this executor
	 */
	private boolean jobsAccepted_ = true;

	/**
	 * To report uncaught exceptions thrown in the worker threads
	 */
	private ComputationRuntimeException exception_;

	/**
	 * {@code true} if shutdown is requested
	 */
	private boolean shutdown_ = false;

	/**
	 * the thread from which jobs are submitted
	 */
	private final Thread executorThread_;

	/**
	 * the last job submitted for the execution
	 */
	private Runnable nextJob_;

	/**
	 * the number of worker instances required to execute the last job
	 */
	private int nextJobNoInstances_;

	/**
	 * the number of jobs submitted so far
	 */
	private int submittedJobCount_ = 0;

	/**
	 * the threads in which the jobs are processed
	 */
	private Thread[] workerThreads_;

	/**
	 * used for synchronization between the executor and the workers
	 */
	private final ReentrantLock lock_;

	/**
	 * to signal that the {@link #nextJob_} can run by a worker
	 */
	private final Condition canRun_;

	/**
	 * {@code true} if idle threads can be terminated after the timeout
	 */
	private boolean timeOutEnabled_ = false;

	/**
	 * how long an idle worker thread should wait before termination if timeout
	 * is enabled
	 */
	private long timeout_ = 0L; // in nanoseconds

	/**
	 * Create a {@link ComputationExecutor} with a given number of threads and a
	 * name which is used to identify threads
	 * 
	 * @param poolSize
	 * @param name
	 */
	public ComputationExecutor(int poolSize, String name) {
		this.executorThread_ = Thread.currentThread();
		this.workerThreads_ = new Thread[poolSize];
		this.threadPoolName_ = name;
		this.exception_ = null;
		this.lock_ = new ReentrantLock();
		this.canRun_ = lock_.newCondition();
	}

	/**
	 * Create a {@link ComputationExecutor} with a given number of threads, a
	 * name which is used to identify threads, and the timeout after which idle
	 * threads should be terminated
	 * 
	 * @param poolSize
	 * @param name
	 * @param timeout
	 * @param unit
	 */
	public ComputationExecutor(int poolSize, String name, long timeout,
			TimeUnit unit) {
		this(poolSize, name);
		this.timeOutEnabled_ = true;
		this.timeout_ = unit.toNanos(timeout);
	}

	/**
	 * Sets the maximal number of threads used by this
	 * {@link ComputationExecutor}.
	 * 
	 * @param threadCount
	 */
	public synchronized boolean setPoolSize(int threadCount) {
		if (!jobsAccepted_)
			return false;
		Thread[] oldWorkerThreads = workerThreads_;
		workerThreads_ = Arrays.copyOf(oldWorkerThreads, threadCount);
		interrupt(oldWorkerThreads, threadCount);
		return true;
	}

	/**
	 * Starts a several copies of jobs. After that new jobs will not be accepted
	 * until the method {@link #waitDone()} is called
	 * 
	 * @param job
	 *            the job to be executed
	 * @param noInstances
	 *            in how many threads this job should be executed in parallel
	 * @return {@code true} if the jobs have been started or {@code false} if
	 *         the job was not accepted; a job may not be accepted if the
	 *         previous jobs were not finished or the number of instances
	 *         exceeds the current worker pool
	 */
	public synchronized boolean start(Runnable job, int noInstances) {
		if (!jobsAccepted_)
			return false;
		jobsAccepted_ = false;
		int processedJobeCount = submittedJobCount_;
		// setting up the fields shared with the workers
		this.nextJob_ = job;
		this.jobDone_ = new CountDownLatch(noInstances);
		this.nextJobNoInstances_ = noInstances;
		this.submittedJobCount_++;
		// waking up idle workers
		lock_.lock();
		try {
			canRun_.signalAll();
		} finally {
			lock_.unlock();
		}
		// creating missing workers
		for (int i = 0; i < noInstances; i++) {
			Thread workerThread = workerThreads_[i];
			if (workerThread == null) {
				workerThread = new Thread(new Worker(i, processedJobeCount),
						threadPoolName_ + "-thread-" + (i + 1));
				workerThreads_[i] = workerThread;
				workerThread.start();
			}
		}
		checkException();
		return true;
	}

	/**
	 * interrupting threads starting from the given number
	 */
	void interrupt(Thread[] threads, int first) {
		checkException();
		for (int i = first; i < threads.length; i++) {
			Thread workerThread = threads[i];
			if (workerThread == null)
				continue;
			workerThread.interrupt();
		}
	}

	/**
	 * interrupting all worker threads of this executor
	 */
	void interrupt() {
		interrupt(workerThreads_, 0);
	}

	/**
	 * Waits until all computations are done
	 * 
	 * @throws InterruptedException
	 *             if was interrupted while waiting
	 */
	public synchronized void waitDone() throws InterruptedException {
		try {
			jobDone_.await();
		} catch (InterruptedException e) {
			checkException();
			throw e;
		}
		jobsAccepted_ = true;
	}

	public synchronized boolean shutdown(long timeout, TimeUnit unit)
			throws InterruptedException {
		waitDone();
		shutdown_ = true;
		interrupt();
		return true;
	}

	/**
	 * Check if there was some exception in a worker thread and throw this
	 * exception
	 * 
	 * @throws ComputationRuntimeException
	 *             if there was an exception in some of the worker thread
	 */
	private void checkException() throws ComputationRuntimeException {
		if (exception_ != null)
			throw exception_;
	}

	/**
	 * the {@link Runnable} that wraps the job and allows to keep track of the
	 * counter for running jobs
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class Worker implements Runnable {

		/**
		 * the identifier of the worker
		 */
		private final int workerId_;

		/**
		 * incremented when the worker has processed a job
		 */
		private int processedJobCount_;

		Worker(int workerId, int startJobId) {
			this.workerId_ = workerId;
			this.processedJobCount_ = startJobId;
		}

		@Override
		public void run() {
			for (;;) {	
				// waiting until the next job is submitted or
				// timeout occurs
				try {
					lock_.lockInterruptibly();
					long nanos = timeout_;
					while (processedJobCount_ == submittedJobCount_) {						
						if (timeOutEnabled_) {
							if (nanos <= 0) {
								dispose();
								return;
							}
							nanos = canRun_.awaitNanos(nanos);
						} else {
							canRun_.await();
						}
					}
				} catch (InterruptedException e) {
					if (shutdown_) {
						dispose();
					} else if (workerId_ < workerThreads_.length) {
						handleUnexpectedException(e);
					}
					return;				
				} finally {
					lock_.unlock();
				}
				if (workerId_ >= workerThreads_.length) {
					// this worker is not needed anymore
					return;
				}
				// processing the next job
				try {					
					if (workerId_ < nextJobNoInstances_) {
						nextJob_.run();
					}
					processedJobCount_++;
				} catch (Throwable e) {
					handleUnexpectedException(e);
				} finally {
					jobDone_.countDown();
				}
			}
		}

		private void handleUnexpectedException(Throwable e) {
			exception_ = new ComputationRuntimeException(
					"Uncaught exception in a worker thread:", e);
			executorThread_.interrupt();
			dispose();
		}

		/**
		 * remove references for the thread of this worker
		 */
		private void dispose() {
			Thread[] currentWorkerThreads = workerThreads_;
			if (workerId_ < currentWorkerThreads.length) {
				currentWorkerThreads[workerId_] = null;
			}
		}
	}

}
