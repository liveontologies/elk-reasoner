/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner;

import java.util.Collection;
import java.util.Iterator;

import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentExecutor;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentComputationWithInputs;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ConcurrentComputationWithInputs} used for executing of reasoner
 * stages
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the input that can be processed by the computation
 * @param <F>
 *            the type of the factory for the input processors
 */
public class ReasonerComputationWithInputs<I, F extends InputProcessorFactory<I, ?>>
		extends ConcurrentComputationWithInputs<I, F> {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ReasonerComputationWithInputs.class);

	/**
	 * the progress monitor used to report the progress of this computation
	 */
	protected final ProgressMonitor progressMonitor;
	/**
	 * the inputs to be processed
	 */
	protected final Iterator<? extends I> todo;
	/**
	 * the final value of the progress monitor: the total number of inputs
	 */
	private final int maxProgress;
	/**
	 * the current value of the progress monitors: the current input
	 */
	private int progress;
	/**
	 * next input to be submitted
	 */
	I nextInput;

	public ReasonerComputationWithInputs(Collection<? extends I> inputs,
			F inputProcessorFactory, ConcurrentExecutor executor,
			int maxWorkers, ProgressMonitor progressMonitor) {
		super(inputProcessorFactory, executor, maxWorkers);
		this.progressMonitor = progressMonitor;
		this.todo = inputs.iterator();
		this.maxProgress = inputs.size();
		this.progress = 0;
		this.nextInput = null;
	}

	/**
	 * Process the given input concurrently using the provided input processor.
	 * If the process has been interrupted, this method can be called again to
	 * continue the computation.
	 */
	public void process() {

		if (!start()) {
			String message = "Could not start workers required for reasoner computation!";
			LOGGER_.error(message);
			throw new ElkRuntimeException(message);
		}

		try {
			// submit the leftover from the previous run
			if (nextInput != null) {
				if (!processNextInput())
					return;
			}
			// repeatedly submit the next inputs from todo
			while (todo.hasNext()) {
				nextInput = todo.next();
				if (!processNextInput())
					return;
			}
			finish();
		} catch (InterruptedException e) {
			// restore interrupt status
			Thread.currentThread().interrupt();
			throw new ElkRuntimeException(
					"Reasoner computation interrupted externally!");
		}
	}

	private boolean processNextInput() throws InterruptedException {
		if (!submit(nextInput)) {
			waitWorkers();
			return false;
		}
		nextInput = null;
		if (isInterrupted()) {
			waitWorkers();
			return false;
		}
		progressMonitor.report(++progress, maxProgress);
		return true;
	}
}
