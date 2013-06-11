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


import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.saturation.properties.DataPropertyHierarchyComputation;

/**
 * A {@link ReasonerStage}, which purpose is to compute the data property
 * hierarchy of the given ontology
 *
 * @author "Yevgeny Kazakov"
 *
 */
public class DataPropertyHierarchyComputationStage extends AbstractReasonerStage {

	/**
	 * the computation used for this stage
	 */
	private DataPropertyHierarchyComputation computation_ = null;

	public DataPropertyHierarchyComputationStage(
		AbstractReasonerState reasoner, AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return "Data Property Hierarchy Computation";
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute()) {
			return false;
		}
		computation_ = new DataPropertyHierarchyComputation(
			reasoner.getProcessExecutor(), workerNo,
			reasoner.getProgressMonitor(), reasoner.ontologyIndex);
		return true;
	}

	@Override
	public void executeStage() throws ElkException {
		for (;;) {
			computation_.process();
			if (!spuriousInterrupt()) {
				break;
			}
		}
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute()) {
			return false;
		}
		computation_ = null;
		return true;
	}

	@Override
	public void printInfo() {
		// TODO Auto-generated method stub
	}
}
