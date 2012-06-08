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
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ComputationExecutor extends ThreadPoolExecutor {

	/**
	 * the number of workers of this executor
	 */
	private final int maxWorkers;

	/**
	 * the thread group used for this executor
	 */
	private final ThreadGroup threadGroup;

	/**
	 * all jobs are done
	 */
	CountDownLatch done;

	public ComputationExecutor(int maxWorkers, final ThreadGroup threadGroup) {
		super(maxWorkers, maxWorkers, 0, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(maxWorkers),
				new ThreadFactory() {
					int threadCount = 0;

					@Override
					public Thread newThread(Runnable r) {
						Thread result = new Thread(threadGroup, r,
								threadGroup.getName() + "-thread-"
										+ threadCount++);
						result.setPriority(Thread.NORM_PRIORITY);
						return result;
					}
				});
		this.maxWorkers = maxWorkers;
		this.threadGroup = threadGroup;
	}

	public void start(Runnable job) {

		this.done = new CountDownLatch(maxWorkers);
		Worker worker = new Worker(job, done);
		for (int i = 0; i < maxWorkers; i++) {
			execute(worker);
		}
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
	 */
	public void waitDone() throws InterruptedException {
		done.await();
	}

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
