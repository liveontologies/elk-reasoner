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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A custom {@link ExecutorService} for starting a several copies of runnable
 * tasks, and waiting for it computation; it allows to interrupt all running
 * tasks without shutting down the {@link ExecutorService}. If terminated, the
 * tasks can be started again.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ComputationExecutor extends ThreadPoolExecutor {

	/**
	 * the thread group used for executor
	 */
	private final ThreadGroup threadGroup;

	/**
	 * the latch indicating that all jobs are done
	 */
	CountDownLatch done;

	/**
	 * <tt>true</tt> if new tasks can be started to be executed; this can happen
	 * only if no tasks are running in this executor
	 */
	volatile boolean canStart = true;

	/**
	 * Create a {@link ComputationExecutor} with a given maximal number of
	 * threads and the given thread group
	 * 
	 * @param threadCount
	 * @param threadGroup
	 */
	public ComputationExecutor(int threadCount, final ThreadGroup threadGroup) {
		super(threadCount, threadCount, 0, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(threadCount),
				new ThreadFactory() {
					int threadCount = 0;

					@Override
					public Thread newThread(Runnable r) {
						Thread result = new Thread(threadGroup, r,
								threadGroup.getName() + "-thread-"
										+ ++threadCount);
						return result;
					}
				});
		this.threadGroup = threadGroup;
	}

	/**
	 * Create a {@link ComputationExecutor} with a given maximal number of
	 * threads and the the given name which is used to identify threads
	 * 
	 * @param threadCount
	 * @param name
	 */
	public ComputationExecutor(int threadCount, String name) {
		this(threadCount, new ThreadGroup(name));
	}

	/**
	 * Starts a several copies of jobs. The jobs cannot be started again until
	 * the method {@link #waitDone()} is called
	 * 
	 * @param job
	 * @param noCopies
	 * @return <tt>true</tt> if the jobs have been started
	 */
	public synchronized boolean start(Runnable job, int noCopies) {
		if (!canStart)
			return false;
		this.done = new CountDownLatch(noCopies);
		Worker worker = new Worker(job, done);
		for (int i = 0; i < noCopies; i++) {
			execute(worker);
		}
		canStart = false;
		return true;
	}

	/**
	 * Interrupting all threads of this executor (used to wake up waiting
	 * threads if something needs to be notified)
	 */
	public void interrupt() {
		threadGroup.interrupt();
	}

	/**
	 * Waits until all computations are done
	 * 
	 * @throws InterruptedException
	 *             if was interrupted while waiting
	 */
	public synchronized void waitDone() throws InterruptedException {
		done.await();
		canStart = true;
	}

	/**
	 * the {@link Runnable} that wraps the job and allows to keep track of the
	 * counter for running jobs
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class Worker implements Runnable {

		protected final Runnable job;
		protected final CountDownLatch done;

		Worker(Runnable job, CountDownLatch done) {
			this.job = job;
			this.done = done;
		}

		@Override
		public void run() {
			job.run();
			done.countDown();
			// clear the interrupt status so that this thread can be reused for
			// other jobs
			Thread.interrupted();
		}

	}

}
