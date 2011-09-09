/*
 * #%L
 * elk-reasoner
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
package org.semanticweb.elk.util.concurrent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 *
 * 
 */
public abstract class AbstractConcurrentComputation<Input> {
	// thread executor service used
	protected final ExecutorService executor;
	// maximum number of concurrent workers
	protected final int maxWorkers;
	// minimal size of the buffer before running jobs
	protected final int bufferThreshold;


	protected final boolean hasBoundedCapacity;
	protected final int underfullLimit;
	protected final int overfullLimit;

	
	// number of currently running jobs	
	protected final AtomicInteger workerCount;
	// buffer for inputs to be processed
	protected final Queue<Input> buffer;
	// the number of inputs in the buffer
	protected final AtomicInteger bufferSize;
	
	
	protected abstract void process(Input nextElement);
	

	public AbstractConcurrentComputation(ExecutorService executor, int maxWorkers, int bufferThreshold, int bufferCapacity) {
		this.executor = executor;
		this.maxWorkers = maxWorkers;
		this.bufferThreshold = bufferThreshold;
		
		hasBoundedCapacity = (bufferCapacity != 0);
		int overfullLowerBound = Math.max(bufferThreshold + 16, 128);
		overfullLimit = Math.max(bufferCapacity, overfullLowerBound);
		underfullLimit = overfullLimit / 2;
		
		workerCount = new AtomicInteger(0);
		buffer = new ConcurrentLinkedQueue<Input> ();
		bufferSize = new AtomicInteger(0);
	}
	

	public void submit(Input input) {
		waitCapacity();
		addJob(input);
	}

	
	public void waitCompletion() {
		synchronized (workerCount) {			
			while (bufferSize.get() > 0 || workerCount.get() > 0) {
				addWorker();
				try {
					workerCount.wait();
				}
				catch (InterruptedException e) {
				}
			}
		}
		// assert bufferSize == 0 && workerCount == 0;
	}
	
	
	// class for concurrent indexing jobs
	protected class Worker implements Runnable {
		public void run() {
			for (;;) {
				Input nextInput = buffer.poll();
				if (nextInput != null) {
					if (bufferSize.getAndDecrement() == underfullLimit)
						synchronized (bufferSize) {
							bufferSize.notify();
						}
					process(nextInput);
					continue;
				}
				break;
			}
			if (workerCount.decrementAndGet() == 0)
				synchronized (workerCount) {
					workerCount.notify();
				}
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
	
	
	protected void waitCapacity() {
		if (hasBoundedCapacity && bufferSize.get() > overfullLimit)
			synchronized (bufferSize) {
				while (bufferSize.get() > underfullLimit)
					try {
						bufferSize.wait();
					}
					catch (InterruptedException e) {
					}
			}
	}

	
	protected void addJob(Input input) {
		buffer.add(input);
		if (bufferSize.incrementAndGet() > bufferThreshold)
			addWorker();
	}

}