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
import org.semanticweb.elk.util.collections.Counter;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentComputationWithInputs;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentExecutor;
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
	private final ProgressMonitor progressMonitor_;
	/**
	 * the inputs to be processed
	 */
	private final Iterator<? extends I> todo_;
	/**
	 * the final value of the progress monitor: the total number of inputs
	 */
	private final int maxProgress_;
	/**
	 * the current value of the progress monitors: the current input
	 */
	private int progress_;
	/**
	 * next input to be submitted
	 */
	private I nextInput_;

	/**
	 * keeps track of the number of elements to be processed in the current
	 * batch
	 */
	private final Counter batchCounter_;
	
	/**
	 * the listener to be notified about processing of batches
	 */
	private final BatchListener batchListener_;

	private ReasonerComputationWithInputs(Iterator<? extends I> todo,
			int maxProgress, F inputProcessorFactory,
			ConcurrentExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor, BatchListener batchListener,
			Counter batchCounter) {
		super(inputProcessorFactory, executor, maxWorkers);
		this.progressMonitor_ = progressMonitor;
		this.todo_ = todo;
		this.maxProgress_ = maxProgress;
		this.progress_ = 0;
		this.nextInput_ = null;
		this.batchListener_ = batchListener;
		this.batchCounter_ = batchCounter;
	}

	private ReasonerComputationWithInputs(Iterator<? extends I> todo,
			int maxProgress, F inputProcessorFactory,
			ConcurrentExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor, int batchSize,
			BatchListener batchListener, Counter batchCounter) {
		this(batchSize < maxProgress ? Operations.synchronize(batchCounter, todo)
				: todo, maxProgress, inputProcessorFactory, executor,
				maxWorkers, progressMonitor, batchListener, batchCounter);
	}

	private ReasonerComputationWithInputs(Iterator<? extends I> todo,
			int maxProgress, F inputProcessorFactory,
			ConcurrentExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor, int batchSize,
			BatchListener batchHandler) {
		this(todo, maxProgress, inputProcessorFactory, executor, maxWorkers,
				progressMonitor, batchSize, batchHandler,
				new Counter(batchSize));
	}

	/**
	 * Creates a new reasoner computations that processes the inputs in parallel
	 * 
	 * @param inputs
	 *            the input values to be processed
	 * @param inputProcessorFactory
	 *            determines how exactly the values are processed by individual
	 *            workers
	 * @param executor
	 *            an executer to start worker threads
	 * @param maxWorkers
	 *            the maximal number of workers to use for the computation
	 * @param progressMonitor
	 *            an object using which the number of processed inputs is
	 *            reported
	 * @param batchSize
	 *            the maximal number of inputs to be processed in one batch;
	 *            after each batch all workers are stopped
	 * @param batchListener
	 *            a listener that is notified about processing of batches
	 */
	public ReasonerComputationWithInputs(Collection<? extends I> inputs,
			F inputProcessorFactory, ConcurrentExecutor executor,
			int maxWorkers, ProgressMonitor progressMonitor, int batchSize,
			BatchListener batchListener) {
		this(inputs.iterator(), inputs.size(), inputProcessorFactory, executor,
				maxWorkers, progressMonitor, batchSize, batchListener);
	}

	/**
	 * Creates a new reasoner computations that processes the inputs in parallel
	 * 
	 * @param inputs
	 *            the input values to be processed
	 * @param inputProcessorFactory
	 *            determines how exactly the values are processed by individual
	 *            workers
	 * @param executor
	 *            an executer to start worker threads
	 * @param maxWorkers
	 *            the maximal number of workers to use for the computation
	 * @param progressMonitor
	 *            an object using which the number of processed inputs is
	 *            reported
	 */
	public ReasonerComputationWithInputs(Collection<? extends I> inputs,
			F inputProcessorFactory, ConcurrentExecutor executor,
			int maxWorkers, ProgressMonitor progressMonitor) {
		this(inputs, inputProcessorFactory, executor, maxWorkers,
				progressMonitor, inputs.size() + 1, new DummyBatchListener());
	}

	/**
	 * Process the given input concurrently using the provided input processor.
	 * If the process has been interrupted, this method can be called again to
	 * continue the computation.
	 */
	public void process() {
		do {
			processBatch();
			if (isInterrupted()) {
				return; // batch processing interrupted
			}
			batchListener_.batchProcessed();
		} while (batchCounter_.reset());
	}
	
	private void processBatch() {
		
		if (!start()) {
			String message = "Could not start workers required for reasoner computation!";
			LOGGER_.error(message);
			throw new ElkRuntimeException(message);
		}

		try {
			// submit the leftover from the previous run
			if (nextInput_ != null) {
				if (!processNextInput())
					return;
			}
			// repeatedly submit the next inputs from todo
			while (todo_.hasNext()) {
				nextInput_ = todo_.next();
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
		if (!submit(nextInput_)) {
			waitWorkers();
			return false;
		}
		nextInput_ = null;
		if (isInterrupted()) {
			waitWorkers();
			return false;
		}
		progressMonitor_.report(++progress_, maxProgress_);
		return true;
	}
}
