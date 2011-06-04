/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
package org.semanticweb.elk.reasoner.saturation;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;

public class SaturationManager {
	// the saturator used for jobs
	protected final SaturationComputation saturation;
	// maximum number of concurrent workers
	protected final int maxWorkers;
	// thread executor service used
	protected final ExecutorService executor;
	// number of currently running jobs
	protected AtomicInteger workerCount;
	// bounded buffer for concepts added for saturation
	protected final BlockingQueue<IndexedClassExpression> conceptBuffer;

	public SaturationManager(ExecutorService executor, int nWorkers) {
		this.saturation = new ConcurrentSaturation();
		this.maxWorkers = nWorkers;
		this.executor = executor;
		this.workerCount = new AtomicInteger(0);
		this.conceptBuffer = new ArrayBlockingQueue<IndexedClassExpression>(512);
	}

	// class for concurrent saturation jobs
	protected class Worker implements Runnable {
		public void run() {
			for (;;) {
				IndexedClassExpression nextTarget = conceptBuffer.poll();
				if (nextTarget != null) {
					saturation.addTarget(nextTarget);
					saturation.compute();
					continue;
				}
				break;
			}
			if (workerCount.decrementAndGet() == 0)
				synchronized (workerCount) {
					workerCount.notify();
				}
			if (!conceptBuffer.isEmpty())
				addWorker();
		}
	}

	// we are going to use one worker instance to start jobs
	protected Worker worker = new Worker();

	protected void addWorker() {
		int n = workerCount.get();
		if (n < maxWorkers)
			if (workerCount.compareAndSet(n, n + 1))
				executor.execute(worker);
	}

	public void submit(IndexedClassExpression target) {
		try {
			conceptBuffer.put(target);
		} catch (InterruptedException e) {
		}
		addWorker();
	}

	void waitCompletion() {
		synchronized (workerCount) {
			while (workerCount.get() > 0)
				try {
					workerCount.wait();
				} catch (InterruptedException e) {
				}
		}
	}

	public Saturation getSaturation() {
		waitCompletion();
		return saturation;
	}
}