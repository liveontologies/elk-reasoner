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

import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.consistency.ConsistencyChecking;

/**
 * A {@link ReasonerStage} during which consistency of the current ontology is
 * checked
 * 
 * @author Pavel Klinov
 * 
 */
class IncrementalConsistencyCheckingStage extends ConsistencyCheckingStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(IncrementalConsistencyCheckingStage.class);

	public IncrementalConsistencyCheckingStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return "Incremental Consistency Checking";
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		//add incremental stages: 
		return null;//Arrays.asList();
		/*return Arrays.asList(
				(ReasonerStage) new OntologyLoadingStage(reasoner),
				(ReasonerStage) new ChangesLoadingStage(reasoner),
				(ReasonerStage) new ContextInitializationStage(reasoner));*/		
	}


	@Override
	void initComputation() {
		super.initComputation();
		//TODO need one stage that will be executed for any reasoning task:
		//it will re-saturate all affected contexts but will NOT saturate new classes (that should be done only for classification)
		//i.e. it shouldn't create new contexts
		this.computation_ = new ConsistencyChecking(
				reasoner.getProcessExecutor(), workerNo,
				reasoner.getProgressMonitor(), reasoner.ontologyIndex);
		if (LOGGER_.isInfoEnabled())
			LOGGER_.info(getName() + " using " + workerNo + " workers");
	}

}
