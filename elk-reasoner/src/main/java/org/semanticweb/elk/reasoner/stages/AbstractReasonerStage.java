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
package org.semanticweb.elk.reasoner.stages;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.exceptions.ElkRuntimeException;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.util.concurrent.computation.Interrupter;
import org.semanticweb.elk.util.concurrent.computation.SimpleInterrupter;

/**
 * A common implementation of {@link ReasonerStage}s for a given reasoner.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
abstract class AbstractReasonerStage extends SimpleInterrupter implements
		ReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(AbstractReasonerStage.class);

	final AbstractReasonerState reasoner;

	/**
	 * {@code true} if all information required for execution of this
	 * {@link ReasonerStage} has been initialized
	 */
	boolean initialized = false;

	/**
	 * {@code true} if the stage does not require execution, i.e., if it is
	 * already executed
	 */
	boolean isCompleted = false;

	/**
	 * the maximal number of concurrent workers used in this computation stage
	 */
	int workerNo;

	/**
	 * the progress monitor used to report progress of this stage
	 */
	ProgressMonitor progressMonitor;

	/**
	 * Stages that need to be executed before this stage
	 */
	final Iterable<AbstractReasonerStage> preStages;

	/**
	 * Stages that need to be executed after this stage
	 */
	final List<AbstractReasonerStage> postStages = new LinkedList<AbstractReasonerStage>();

	/**
	 * Creates a new reasoner stage for a given reasoner.
	 * 
	 * @param reasoner
	 *            the reasoner for which the reasoner stage is created
	 */
	public AbstractReasonerStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		this.reasoner = reasoner;
		this.preStages = Arrays.asList(preStages);
		for (AbstractReasonerStage preStage : preStages)
			preStage.postStages.add(this);
	}

	@Override
	public boolean isCompleted() {
		return isCompleted;
	}

	@Override
	public Iterable<? extends ReasonerStage> getPreStages() {
		return preStages;
	}

	/**
	 * Initialize the parameters of the computation for this stage; this is the
	 * first thing to be done before stage is executed
	 * 
	 * @return {@code true} if the operation is successful
	 */
	@Override
	public boolean preExecute() {
		if (initialized)
			return false;
		LOGGER_.trace(getName() + ": initialized");
		this.workerNo = reasoner.getNumberOfWorkers();
		this.progressMonitor = reasoner.getProgressMonitor();
		return initialized = true;
	}

	/**
	 * Clear the parameters of the computation for this stage; this is the last
	 * thing to be done when the stage is executed *
	 * 
	 * @return {@code true} if the operation is successful
	 */
	@Override
	public boolean postExecute() {
		if (!initialized)
			return false;
		LOGGER_.trace(getName() + ": done");
		this.workerNo = 0;
		this.progressMonitor = null;
		this.initialized = false;
		return true;
	}

	/**
	 * Execute the stage with initialized parameters
	 * 
	 * @throws ElkException
	 */
	abstract void executeStage() throws ElkException;

	@Override
	public void execute() throws ElkException {
		if (LOGGER_.isInfoEnabled())
			LOGGER_.info(getName() + " using " + workerNo + " workers");
		progressMonitor.start(getName());

		try {
			executeStage();
			checkInterrupt();
			this.isCompleted = true;
		} finally {
			progressMonitor.finish();
		}
	}

	/**
	 * Marks this {@link AbstractReasonerStage} and all dependent stages as not
	 * completed; this will require their execution next time
	 * 
	 * @return {@code true} if this stage is invalidated or {@code false} if
	 *         this stage was already invalidated before the call
	 */
	boolean invalidate() {
		if (!isCompleted)
			return false;
		Queue<AbstractReasonerStage> toInvalidate = new LinkedList<AbstractReasonerStage>();
		toInvalidate.add(this);
		for (;;) {
			AbstractReasonerStage stage = toInvalidate.poll();
			if (stage == null)
				return true;
			if (stage.isCompleted) {
				if (LOGGER_.isTraceEnabled())
					LOGGER_.trace(stage.getName() + ": invalidated");
				stage.isCompleted = false;
				for (AbstractReasonerStage postStage : stage.postStages) {
					toInvalidate.add(postStage);
				}
			}
		}
	}

	/**
	 * Marks this {@link AbstractReasonerStage} and its dependencies as
	 * completed; next time the stage will not be executed unless some of the
	 * dependencies are invalidated
	 * 
	 * @return
	 */
	boolean setCompleted() {
		if (isCompleted)
			return false;
		Queue<AbstractReasonerStage> toMark = new LinkedList<AbstractReasonerStage>();
		toMark.add(this);
		for (;;) {
			AbstractReasonerStage stage = toMark.poll();
			if (stage == null)
				return true;
			if (!stage.isCompleted) {
				if (LOGGER_.isTraceEnabled())
					LOGGER_.trace(stage.getName() + ": marked completed");
				stage.isCompleted = true;
				for (AbstractReasonerStage preStage : stage.preStages) {
					toMark.add(preStage);
				}
			}
		}
	}

	void checkInterrupt() throws ElkInterruptedException {
		if (isInterrupted()) {
			throw new ElkInterruptedException(getName() + " interrupted");
		}
	}

	protected void setInterrupt(Interrupter interrupter, boolean flag) {
		if (interrupter == null) {
			if (!flag)
				throw new ElkRuntimeException(getName()
						+ ": cannot clear interrupt!");
			return;
		}
		// else
		interrupter.setInterrupt(flag);
	}

	protected void markAllContextsAsSaturated() {
		for (IndexedClassExpression ice : reasoner.saturationState
				.getNotSaturatedContexts()) {
			if (ice.getContext() != null) {
				ice.getContext().setSaturated(true);
			}
		}
	}

	protected SaturationStatistics getRuleAndConclusionStatistics() {
		return reasoner.ruleAndConclusionStats;
	}
}
