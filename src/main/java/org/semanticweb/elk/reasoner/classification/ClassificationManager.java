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
/**
 * @author Yevgeny Kazakov, May 16, 2011
 */
package org.semanticweb.elk.reasoner.classification;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.syntax.ElkClass;

/**
 * @author Yevgeny Kazakov
 * 
 */
public class ClassificationManager {
	// the saturator used for jobs
	protected final ClassTaxonomyComputation classTaxonomy;
	// maximum number of concurrent workers
	protected final int maxWorkers;
	// thread executor service used
	protected final ExecutorService executor;
	// number of currently running jobs
	protected AtomicInteger workerCount;
	// bounded buffer for classes added for classification
	protected final BlockingQueue<ElkClass> classBuffer;

	public ClassificationManager(ExecutorService executor, int nWorkers,
			OntologyIndex ontologyIndex) {
		this.classTaxonomy = new ConcurrentClassTaxonomy(ontologyIndex);
		this.maxWorkers = nWorkers;
		this.executor = executor;
		this.workerCount = new AtomicInteger(0);
		this.classBuffer = new ArrayBlockingQueue<ElkClass>(256);
	}

	// class for concurrent classification jobs
	protected class Worker implements Runnable {
		public void run() {
			for (;;) {
				classTaxonomy.compute();
				ElkClass nextClass = classBuffer.poll();
				if (nextClass != null) {
					classTaxonomy.addTarget(nextClass);
					continue;
				}
				break;
			}
			if (workerCount.decrementAndGet() == 0)
				synchronized (workerCount) {
					workerCount.notify();
				}
			if (!classBuffer.isEmpty())
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

	public void submit(ElkClass target) {
		try {
			classBuffer.put(target);
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

	public ClassTaxonomy getClassTaxonomy() {
		waitCompletion();
		return classTaxonomy;
	}

}
