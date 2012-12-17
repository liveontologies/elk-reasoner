/*
 * #%L
 * ELK Reasoner
 * *
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
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.consistency.ConsistencyChecking;

/**
 * A {@link ReasonerStage} during which consistency of the current ontology is
 * checked
 *
 * @author "Yevgeny Kazakov"
 *
 */
class ConsistencyCheckingStage extends AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(ConsistencyCheckingStage.class);

	/**
	 * the computation used for this stage
	 */
	private ConsistencyChecking computation_ = null;

	public ConsistencyCheckingStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return "Consistency Checking";
	}

	@Override
	public boolean done() {
		return reasoner.doneConsistencyCheck;
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Arrays.asList(
				(ReasonerStage) new OntologyLoadingStage(reasoner),
				(ReasonerStage) new ChangesLoadingStage(reasoner),
				(ReasonerStage) new ContextInitializationStage(reasoner),
				(ReasonerStage) new DataPropertyHierarchyComputationStage(reasoner));
	}

	@Override
	public void execute() throws ElkInterruptedException {
		if (computation_ == null)
			initComputation();
		progressMonitor.start(getName());
		try {
			for (;;) {
				computation_.process();
				if (!interrupted())
					break;
			}
		} finally {
			progressMonitor.finish();
		}
		reasoner.inconsistentOntology = computation_.isInconsistent();
		reasoner.doneConsistencyCheck = true;
	}

	@Override
	void initComputation() {
		super.initComputation();
		this.computation_ = new ConsistencyChecking(
				reasoner.getProcessExecutor(), workerNo,
				reasoner.getProgressMonitor(), reasoner.ontologyIndex);
		if (LOGGER_.isInfoEnabled())
			LOGGER_.info(getName() + " using " + workerNo + " workers");
	}

	@Override
	public void printInfo() {
		if (computation_ != null)
			computation_.printStatistics();
	}

}
