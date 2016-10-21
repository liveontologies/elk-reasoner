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

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A common implementation of {@link ReasonerStage}s for a given reasoner.
 * 
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 */
abstract class AbstractReasonerStage implements ReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractReasonerStage.class);

	final AbstractReasonerState reasoner;

	/**
	 * {@code true} if all information required for execution of this
	 * {@link ReasonerStage} has been initialized
	 */
	private boolean isInitialized_ = false;

	/**
	 * {@code true} if the stage does not require execution, i.e., if it is
	 * already executed
	 */
	private boolean isCompleted_ = false;

	/**
	 * the maximal number of concurrent workers used in this computation stage
	 */
	int workerNo;

	/**
	 * Stages that need to be executed before this stage
	 */
	private final Iterable<AbstractReasonerStage> preStages_;

	/**
	 * Stages that need to be executed after this stage
	 */
	private final List<AbstractReasonerStage> postStages_ = new LinkedList<AbstractReasonerStage>();

	/**
	 * Creates a new reasoner stage for a given reasoner.
	 * 
	 * @param reasoner
	 *            the reasoner for which the reasoner stage is created
	 * @param preStages
	 *            the reasoner stages that should be executed directly before
	 *            this stage
	 */
	public AbstractReasonerStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		this.reasoner = reasoner;
		this.preStages_ = Arrays.asList(preStages);
		for (AbstractReasonerStage preStage : preStages)
			preStage.postStages_.add(this);
	}

	@Override
	public boolean isCompleted() {
		return isCompleted_;
	}

	@Override
	public Iterable<? extends ReasonerStage> getPreStages() {
		return preStages_;
	}
	
	/**
	 * Initialize the parameters of the computation for this stage; this is the
	 * first thing to be done before stage is executed
	 * 
	 * @return {@code true} if the operation is successful
	 */
	@Override
	public boolean preExecute() {
		if (isInitialized_)
			return false;
		LOGGER_.trace("{}: initialized", this);
		this.workerNo = reasoner.getNumberOfWorkers();
		return isInitialized_ = true;
	}

	/**
	 * Clear the parameters of the computation for this stage; this is the last
	 * thing to be done when the stage is executed *
	 * 
	 * @return {@code true} if the operation is successful
	 */
	@Override
	public boolean postExecute() {
		if (!isInitialized_)
			return false;
		LOGGER_.trace("{}: done", this);
		this.isCompleted_ = true;
		this.workerNo = 0;
		this.isInitialized_ = false;		
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
		LOGGER_.info("{} using {} workers", this, workerNo);
		LOGGER_.info("=== {} using {} workers  ===", this, workerNo);
		reasoner.getProgressMonitor().start(getName());

		try {
			executeStage();
			checkInterrupt();
		} finally {
			reasoner.getProgressMonitor().finish();
		}
	}
	
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Marks this {@link AbstractReasonerStage} as not completed; this will
	 * require its execution next time unless {@link #setCompleted()} is called
	 * 
	 * @return {@code true} if this stage was not invalidated and {@code false}
	 *         if this stage was already invalidated before the call
	 */
	boolean invalidate() {
		if (!isCompleted_)
			return false;
		LOGGER_.trace("{}: invalidated", this);
		isCompleted_ = false;		
		return true;
	}
	
	/**
	 * Invalidates this stage and all subsequent stages if not already done so
	 */
	public void invalidateRecursive() {
		Queue<AbstractReasonerStage> toInvalidate_ = new LinkedList<AbstractReasonerStage>();
		toInvalidate_.add(this);
		AbstractReasonerStage next;
		while ((next = toInvalidate_.poll()) != null) {
			if (next.invalidate()) {
				for (AbstractReasonerStage postStage : next.postStages_) {
					toInvalidate_.add(postStage);
				}
			}
		}
	}

	/**
	 * Marks this {@link AbstractReasonerStage} as completed; next time the
	 * stage will not be executed unless {@link #invalidate()} is called
	 * 
	 * @return {@code true} if this stage was invalidated and {@code false} if
	 *         this stage was already not invalidated before the call
	 */
	boolean setCompleted() {
		if (isCompleted_)
			return false;
		LOGGER_.trace("{}: marked completed", this);
		isCompleted_ = true;
		return true;
	}

	protected void checkInterrupt() throws ElkInterruptedException {
		reasoner.getInterrupter().checkInterrupt();
	}

	protected void markAllContextsAsSaturated() {
		for (;;) {
			Context context = reasoner.saturationState
					.setNextContextSaturated();
			if (context == null)
				return;
		}
	}

	protected SaturationStatistics getRuleAndConclusionStatistics() {
		return reasoner.ruleAndConclusionStats;
	}

	@Override
	public boolean isInterrupted() {
		return reasoner.getInterrupter().isInterrupted();
	}

}
