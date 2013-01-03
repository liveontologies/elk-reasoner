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

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

/**
 * A common implementation of {@link ReasonerStage}s for a given reasoner.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
abstract class AbstractReasonerStage implements ReasonerStage {

	final AbstractReasonerState reasoner;

	/**
	 * the maximal number of concurrent workers used in this computation stage
	 */
	int workerNo;

	/**
	 * the progress monitor used to report progress of this stage
	 */
	ProgressMonitor progressMonitor;

	/**
	 * Creates a new reasoner stage for a given reasoner.
	 * 
	 * @param reasoner
	 *            the reasoner for which the reasoner stage is created
	 */
	public AbstractReasonerStage(AbstractReasonerState reasoner) {
		this.reasoner = reasoner;
	}

	@Override
	public boolean isInterrupted() {
		return reasoner.isInterrupted();
	}

	@Override
	public void clearInterrupt() {
		reasoner.clearInterrupt();
	}

	/**
	 * Check if the reasoner was interrupted and clears the interrupt status
	 * 
	 * @return {@code true} if the current thread was interrupted but the
	 *         reasoner was not interrupted (e.g., spurious interrupt)
	 * @throws ElkInterruptedException
	 *             if the reasoner was interrupted
	 */
	public boolean interrupted() throws ElkInterruptedException {
		boolean result = false;
		if (Thread.interrupted())
			result = true;
		if (isInterrupted()) {
			clearInterrupt();
			throw new ElkInterruptedException(getName() + " interrupted");
		}
		return result;
	}

	/**
	 * Initialize the parameters of the computation for this stage. This is
	 * usually done the first time the stage is executed.
	 */
	void initComputation() {
		this.workerNo = reasoner.getNumberOfWorkers();
		this.progressMonitor = reasoner.getProgressMonitor();
	}

	protected void markAllContextsAsSaturated() {
		for (IndexedClassExpression ice : reasoner.saturationState
				.getNotSaturatedContexts()) {
			ice.getContext().setSaturated(true);
		}		
	}
}
