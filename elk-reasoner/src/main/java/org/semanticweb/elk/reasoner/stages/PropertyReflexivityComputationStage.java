package org.semanticweb.elk.reasoner.stages;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.saturation.properties.ReflexivePropertyComputation;

public class PropertyReflexivityComputationStage extends AbstractReasonerStage {

	/**
	 * the computation used for this stage
	 */
	private ReflexivePropertyComputation computation_ = null;

	public PropertyReflexivityComputationStage(ReasonerStageManager manager) {
		super(manager);
	}

	@Override
	public String getName() {
		return "Reflexive Property Computation";
	}

	@Override
	public boolean done() {
		return reasoner.donePropertyReflexivityComputation;
	}

	@Override
	public Iterable<ReasonerStage> getDependencies() {
		return Arrays.asList(manager.ontologyLoadingStage,
				manager.changesLoadingStage,
				manager.propertyInitializationStage);
	}

	@Override
	boolean preExecute() {
		if (!super.preExecute())
			return false;
		this.computation_ = new ReflexivePropertyComputation(
				reasoner.ontologyIndex, reasoner.getProcessExecutor(),
				workerNo, reasoner.getProgressMonitor());
		return true;
	}

	@Override
	boolean postExecute() {
		if (!super.postExecute())
			return false;
		this.computation_ = null;
		return true;
	}

	@Override
	public void executeStage() throws ElkException {
		if (computation_ == null)
			preExecute();
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
		reasoner.donePropertyReflexivityComputation = true;
		computation_ = null;
	}

	@Override
	public void printInfo() {
		// TODO Auto-generated method stub

	}

}
