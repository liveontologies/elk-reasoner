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

import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentComputation;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

/**
 * A {@link ConcurrentComputation} used for executing of reasoner stages
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <I>
 *            the input that can be processed by the computation
 * @param <P>
 *            the processor of the input
 * @param <F>
 *            the type of the factory for the input processors
 */
public class ReasonerComputation<I, P extends InputProcessor<I>, F extends InputProcessorFactory<I, P>>
		extends ConcurrentComputation<I, P, F> {

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

	public ReasonerComputation(Collection<? extends I> inputs,
			F inputProcessorFactory, ComputationExecutor executor,
			int maxWorkers, ProgressMonitor progressMonitor) {
		super(inputProcessorFactory, executor, maxWorkers);
		this.progressMonitor = progressMonitor;
		this.todo = inputs.iterator();
		this.maxProgress = inputs.size();
		this.progress = 0;
		this.nextInput = null;
	}

	public ReasonerComputation(Iterable<? extends I> inputs, int inputsSize,
			F inputProcessorFactory, ComputationExecutor executor,
			int maxWorkers, ProgressMonitor progressMonitor) {
		super(inputProcessorFactory, executor, maxWorkers);
		this.progressMonitor = progressMonitor;
		this.todo = inputs.iterator();
		this.maxProgress = inputsSize;
		this.progress = 0;
		this.nextInput = null;
	}

	/**
	 * Process the given input concurrently using the provided input processor.
	 * If the process has been interrupted, this method can be called again to
	 * continue the computation.
	 */
	public void process() {

		start();

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
			// interrupt all workers
			for (;;) {
				try {
					interrupt();
					break;
				} catch (InterruptedException e1) {
					// we'll still wait until all workers stop
					continue;
				}
			}
			// restore interrupt status
			Thread.currentThread().interrupt();
		}
	}

	private boolean processNextInput() throws InterruptedException {
		submit(nextInput);
		if (Thread.currentThread().isInterrupted()) {
			interrupt();
			return false;
		}
		progressMonitor.report(++progress, maxProgress);
		return true;
	}
}
