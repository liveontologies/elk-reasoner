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

import org.semanticweb.elk.util.concurrent.computation.ConcurrentComputation;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.Interrupter;

public class ReasonerComputation<I, P extends InputProcessor<I>> extends
		ConcurrentComputation<I, P> {

	protected final ProgressMonitor progressMonitor;
	/**
	 * the inputs to be processed
	 */
	protected final Iterator<? extends I> todo;
	/**
	 * number of indexed entities to classify
	 */
	private final int maxProgress;
	/**
	 * variable used in progress monitors
	 */
	private int progress;
	/**
	 * next input to be submitted
	 */
	I nextInput;

	public ReasonerComputation(Collection<? extends I> inputs,
			P inputProcessor, Interrupter interrupter, int maxWorkers,
			ProgressMonitor progressMonitor) {
		super(inputProcessor, interrupter, maxWorkers);
		this.progressMonitor = progressMonitor;
		this.todo = inputs.iterator();
		this.maxProgress = inputs.size();
		this.progress = 0;
		this.nextInput = null;
	}

	public ReasonerComputation(Iterable<? extends I> inputs, int inputsSize,
			P inputProcessor, Interrupter interrupter, int maxWorkers,
			ProgressMonitor progressMonitor) {
		super(inputProcessor, interrupter, maxWorkers);
		this.progressMonitor = progressMonitor;
		this.todo = inputs.iterator();
		this.maxProgress = inputsSize;
		this.progress = 0;
		this.nextInput = null;
	}

	/**
	 * Process the given input concurrently using the provided input processor.
	 * If the process has been interrupted as can be determined by calling
	 * {@link Interrupter#isInterrupted()} method for the supplied interrupter,
	 * then this method can be called again to continue the computation.
	 */
	public void process() {

		if (!todo.hasNext() && nextInput == null)
			return;

		start();

		try {
			// submit the leftover from the previous run
			if (nextInput != null)
				submit(nextInput);
			// submit the next inputs from todo
			while (todo.hasNext() && !interrupter.isInterrupted()) {
				nextInput = todo.next();
				submit(nextInput);
				progressMonitor.report(++progress, maxProgress);
			}
			finish();
		} catch (InterruptedException e) {
			// request all workers to stop as soon as possible
			interrupter.interrupt();
			// wait until all workers are killed
			for (;;) {
				try {
					finish();
					break;
				} catch (InterruptedException e1) {
					// we'll still wait until all workers stop
					continue;
				}
			}
		}

	}
}
