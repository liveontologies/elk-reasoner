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
	 * the last job submitted for the execution
	 */
	private Runnable job_;

	/**
	 * the number of worker instances required to execute the last job
	 */
	private int jobWorkerRequired_;

	/**
	 * the number of workers that have processed the last job
	 */
	private int jobWorkerFinished_;

	/**
	 * a counter that increments with every new job
	 */
	private int jobId_ = 0;

	/**
	 * to report uncaught exceptions thrown in the worker threads
	 */
	private ComputationRuntimeException exception_ = null;

	/**
	 * {@code true} if shutdown is requested
	 */
	private boolean shutdown_ = false;

	/**
	 * the threads in which the jobs are processed
	 */
	private Thread[] workerThreads_;

	/**
	 * used for synchronization between the executor and the workers
	 */
	private final ReentrantLock lock_ = new ReentrantLock();

	/**
	 * to signal when workers need to wake up
	 */
	private final Condition workersNeedAttention_ = lock_.newCondition();

	/**
	 * to signal when the control thread needs to wake up
	 */
	private final Condition masterNeedsAttention_ = lock_.newCondition();

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
		this.workerThreads_ = new Thread[poolSize];
		this.threadPoolName_ = name;
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
	 * 
	 * @return {@code true} if this operation was successful; {@code false} is
	 *         returned if some jobs have been processed yet
	 */
	public boolean setPoolSize(int threadCount) {
		lock_.lock();
		try {
			if (jobWorkerFinished_ < jobWorkerRequired_) {
				// last job is not yet processed
				return false;
			}
			workerThreads_ = Arrays.copyOf(workerThreads_, threadCount);
			workersNeedAttention_.signalAll();
			return true;
		} finally {
			lock_.unlock();
		}
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
	 *         previous jobs were not finished, the number of instances exceeds
	 *         the current worker pool, or the executor has been
	 */
	public boolean start(Runnable job, int noInstances) {
		lock_.lock();
		try {
			if (shutdown_ || noInstances > workerThreads_.length
					|| jobWorkerFinished_ < jobWorkerRequired_) {
				return false;
			}
			job_ = job;
			jobWorkerRequired_ = noInstances;
			jobWorkerFinished_ = 0;
			jobId_++;
			workersNeedAttention_.signalAll();
			// creating missing workers
			for (int i = 0; i < noInstances; i++) {
				Thread workerThread = workerThreads_[i];
				if (workerThread == null) {
					workerThread = new Thread(new Worker(i),
							threadPoolName_ + "-thread-" + (i + 1));
					workerThreads_[i] = workerThread;
					workerThread.start();
				}
			}
			checkException();
			return true;
		} finally {
			lock_.unlock();
		}
	}

	/**
	 * Waits until all computations are done
	 * 
	 * @throws InterruptedException
	 *             if was interrupted while waiting
	 */
	public void waitDone() throws InterruptedException {
		lock_.lock();
		try {
			for (;;) {
				checkException();
				if (jobWorkerFinished_ == jobWorkerRequired_) {
					return;
				}
				masterNeedsAttention_.await();
			}
		} finally {
			lock_.unlock();
		}
	}

	/**
	 * Wait until all computations are terminated or the timeout is exceeded. No
	 * new jobs are accepted.
	 * 
	 * @param timeout
	 *            the maximum time to wait
	 * @param unit
	 *            the time unit of the time argument
	 * @return {@code false} if the waiting time detectably elapsed before
	 *         return from the method, else {@code true}
	 * @throws InterruptedException
	 *             if the current thread is interrupted
	 */
	public boolean shutdown(long timeout, TimeUnit unit)
			throws InterruptedException {
		lock_.lock();
		long nanos = unit.toNanos(timeout);
		try {
			shutdown_ = true;
			for (;;) {
				checkException();
				if (jobWorkerFinished_ == jobWorkerRequired_) {
					return true;
				}
				if (nanos <= 0L) {
					return false;
				}
				nanos = masterNeedsAttention_.awaitNanos(nanos);
			}
		} finally {
			lock_.unlock();
		}
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

		Worker(int workerId) {
			this.workerId_ = workerId;
		}

		@Override
		public void run() {
			Runnable job;
			int jobId = 0;
			for (;;) {
				lock_.lock();
				try {
					long nanos = timeout_;
					if (workerId_ >= workerThreads_.length) {
						// this worker is not needed anymore
						return;
					}
					if (exception_ != null) {
						// remove this thread from the pool
						workerThreads_[workerId_] = null;
						return;
					}

					if (jobId == jobId_ || workerId_ >= jobWorkerRequired_) {
						// cannot process anything
						if (shutdown_ || (timeOutEnabled_ && nanos <= 0)) {
							// remove this thread from the pool
							workerThreads_[workerId_] = null;
							return;
						}
						// else wait for new jobs
						if (timeOutEnabled_) {
							nanos = workersNeedAttention_.awaitNanos(nanos);
						} else {
							workersNeedAttention_.await();
						}
						continue;
					} else {
						job = job_;
						jobId = jobId_;
					}
				} catch (InterruptedException e) {
					handleUnexpectedException(e);
					continue;
				} finally {
					lock_.unlock();
				}
				// processing the next job
				try {
					job.run();
					lock_.lock();
					try {
						jobWorkerFinished_++;
						if (jobWorkerFinished_ == jobWorkerRequired_) {
							// job is processed
							masterNeedsAttention_.signalAll();
						}
					} finally {
						lock_.unlock();
					}
				} catch (Throwable e) {
					handleUnexpectedException(e);
				}
			}
		}

		private void handleUnexpectedException(Throwable e) {
			lock_.lock();
			try {
				exception_ = new ComputationRuntimeException(
						"Uncaught exception in a worker thread:", e);
				workersNeedAttention_.signalAll();
				masterNeedsAttention_.signalAll();
			} finally {
				lock_.unlock();
			}
		}

	}

}
