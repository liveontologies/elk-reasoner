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
package org.semanticweb.elk.util;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 *
 * 
 */
public abstract class ConcurrentComputationManager<Input> {
	// thread executor service used
	protected final ExecutorService executor;
	// maximum number of concurrent workers
	protected final int maxWorkers;
	// number of currently running jobs
	protected final AtomicInteger workerCount;
	// buffer for inputs to be processed
	protected final Queue<Input> buffer;
	// the number of inputs in the buffer
	protected final AtomicInteger bufferSize;
	// maximal buffer capacity before a submit is allowed
	protected final int bufferCapacity;
	// minimal size of the buffer before running jobs
	protected final int bufferThreshold;
	
	
	protected abstract void process(Input nextElement);
	
	
	public ConcurrentComputationManager(ExecutorService executor, int workerNo, int bufferCapacity, int bufferThreshold) {
		this.executor = executor;
		this.maxWorkers = workerNo;
		this.workerCount = new AtomicInteger(0);
		this.buffer = new LinkedBlockingQueue<Input>();
		this.bufferSize = new AtomicInteger(0);
		this.bufferCapacity = bufferCapacity;
		this.bufferThreshold = bufferThreshold;
	}
	
	
	public ConcurrentComputationManager(ExecutorService executor, int workerNo, int bufferCapacity) {
		this(executor, workerNo, bufferCapacity, 0);
	}
	
	
	// class for concurrent indexing jobs
	protected class Worker implements Runnable {
		public void run() {
			for (;;) {
				Input nextInput = buffer.poll();
				if (nextInput != null) {
					if (bufferSize.getAndDecrement() == bufferCapacity)
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
		if (bufferCapacity != 0 && bufferSize.get() > 2*bufferCapacity)
			synchronized (bufferSize) {
				while (bufferSize.get() > bufferCapacity)
					try {
						bufferSize.wait();
					}
				catch (InterruptedException e) {
				}
			}
	}
	

	protected void add(Input input) {
		buffer.add(input);
		if (bufferSize.incrementAndGet() > bufferThreshold)
			addWorker();
	}
	
	public void submit(Input input) {
		waitCapacity();
		add(input);
	}
	
	public void waitCompletion() {
		addWorker();
		synchronized (workerCount) {
			while (workerCount.get() > 0)
				try {
					workerCount.wait();
				} catch (InterruptedException e) {
				}
		}
	}
}
