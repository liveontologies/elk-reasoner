/**
 * 
 */
package org.semanticweb.elk.util.concurrent.computation;

/*
 * #%L
 * ELK Utilities for Concurrency
 * $Id:$
 * $HeadURL:$
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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * FIXME delete it, the queue is not supposed to be local to the worker
 * 
 * Implements basic job queueing and lets subclasses focus on processing single
 * jobs
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
public abstract class BaseInputProcessor<J> implements InputProcessor<J> {

	/**
	 * The buffer for jobs that need to be processed, i.e., those for which the
	 * method {@link submit(J)} was executed but processing of jobs has not been
	 * started yet.
	 */
	private final Queue<J> jobsToDo_ = new ConcurrentLinkedQueue<J>();

	private final InputProcessorListenerNotifyFinishedJob<J> listener_;

	public BaseInputProcessor() {
		this(null);
	}

	public BaseInputProcessor(
			final InputProcessorListenerNotifyFinishedJob<J> listener) {
		listener_ = listener;
	}

	@Override
	public void submit(J job) {
		jobsToDo_.add(job);
	}

	@Override
	public void process() throws InterruptedException {
		for (;;) {
			if (isInterrupted()) {
				break;
			}

			J nextJob = jobsToDo_.poll();

			if (nextJob == null) {
				break;
			}

			process(nextJob);

			if (listener_ != null) {
				listener_.notifyFinished(nextJob);
			}
		}
	}

	@Override
	public void finish() {
	}

	protected abstract boolean isInterrupted();

	protected abstract void process(J job);
}