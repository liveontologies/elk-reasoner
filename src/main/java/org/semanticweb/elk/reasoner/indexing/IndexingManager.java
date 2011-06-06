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
package org.semanticweb.elk.reasoner.indexing;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.syntax.ElkAxiom;

public class IndexingManager {
	// ontology index used
	private final OntologyIndexComputation ontologyIndex;
	// maximum number of concurrent workers
	private final int maxWorkers;
	// thread executor service used
	private final ExecutorService executor;
	// number of currently running jobs
	private final AtomicInteger workerCount;
	// buffer for future axioms required to be indexed
	private final Queue<Future<? extends ElkAxiom>> futureAxiomBuffer;
	// the number of axioms in the queue
	private final AtomicInteger axiomBufferSize;
	// minimal size of the buffer before running the indexing job
	private final int bufferThreshold;

	public IndexingManager(ExecutorService executor, int nWorkers) {
		this.ontologyIndex = new SerialOntologyIndex();
		this.maxWorkers = nWorkers;
		this.executor = executor;
		this.workerCount = new AtomicInteger(0);
		this.futureAxiomBuffer = new ConcurrentLinkedQueue<Future<? extends ElkAxiom>>();
		this.axiomBufferSize = new AtomicInteger(0);
		this.bufferThreshold = 512;
	}

	// class for concurrent indexing jobs
	private class Worker implements Runnable {
		public void run() {
			for (;;) {
				Future<? extends ElkAxiom> futureAxiom = futureAxiomBuffer
						.poll();
				if (futureAxiom != null) {
					axiomBufferSize.decrementAndGet();
					ontologyIndex.addTarget(futureAxiom);
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
	private Worker worker = new Worker();

	private void addWorker() {
		int n = workerCount.get();
		if (n < maxWorkers)
			if (workerCount.compareAndSet(n, n + 1))
				executor.execute(worker);
	}

	public void submit(Future<? extends ElkAxiom> futureAxiom) {
		futureAxiomBuffer.add(futureAxiom);
		if (axiomBufferSize.incrementAndGet() > bufferThreshold)
			addWorker();
	}

	void waitCompletion() {
		addWorker();
		synchronized (workerCount) {
			while (workerCount.get() > 0)
				try {
					workerCount.wait();
				} catch (InterruptedException e) {
				}
		}
	}

	public void computeRoleHierarchy() {
		waitCompletion();
		ontologyIndex.computeRoleHierarchy();
	}

	public OntologyIndex getOntologyIndex() {
		return ontologyIndex;
	}

}
