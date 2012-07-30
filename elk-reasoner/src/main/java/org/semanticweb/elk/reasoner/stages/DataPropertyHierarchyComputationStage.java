package org.semanticweb.elk.reasoner.stages;
/*
 * #%L
 * ELK Reasoner
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

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.saturation.properties.DataPropertyHierarchyComputation;

/**
 * A {@link ReasonerStage}, which purpose is to compute the data property
 * hierarchy of the given ontology
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class DataPropertyHierarchyComputationStage extends
		AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(DataPropertyHierarchyComputationStage.class);

	/**
	 * the computation used for this stage
	 */
	private DataPropertyHierarchyComputation computation_;
	/**
	 * the number of workers used in the computation for this stage
	 */
	private final int workerNo;

	public DataPropertyHierarchyComputationStage(AbstractReasonerState reasoner) {
		super(reasoner);
		this.workerNo = reasoner.getNumberOfWorkers();
		this.progressMonitor = reasoner.getProgressMonitor();
	}

	@Override
	public String getName() {
		return "Data Property Hierarchy Computation";
	}

	@Override
	public boolean done() {
		return reasoner.doneDataPropertyHierarchyComputation;
	}

	@Override
	public List<? extends ReasonerStage> getDependencies() {
		return Arrays.asList(new OntologyLoadingStage(reasoner),
				new ChangesLoadingStage(reasoner));
	}

	@Override
	public void execute() throws ElkInterruptedException {
		if (computation_ == null)
			initComputation();
		for (;;) {
			computation_.process();
			if (!interrupted())
				break;
		}
		reasoner.doneDataPropertyHierarchyComputation = true;
	}

	@Override
	void initComputation() {
		super.initComputation();
		this.computation_ = new DataPropertyHierarchyComputation(
				reasoner.getProcessExecutor(), workerNo, progressMonitor,
				reasoner.ontologyIndex);
		if (LOGGER_.isInfoEnabled())
			LOGGER_.info(getName() + " using " + workerNo + " workers");
	}

	@Override
	public void printInfo() {
	}

}
