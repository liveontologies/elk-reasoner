/**
 * 
 */
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
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomyComputation;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class IncrementalClassTaxonomyComputationStage extends
		ClassTaxonomyComputationStage {

	private static final Logger LOGGER_ = Logger
			.getLogger(IncrementalClassTaxonomyComputationStage.class);	
	
	public IncrementalClassTaxonomyComputationStage(
			AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return "Incremental Class Taxonomy Computation";
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		//TODO need some incremental taxonomy update/clean phase
		return Arrays.asList((ReasonerStage) new IncrementalConsistencyCheckingStage(reasoner));
	}

	@Override
	void initComputation() {
		super.initComputation();
		if (LOGGER_.isInfoEnabled())
			LOGGER_.info(getName() + " using " + workerNo + " workers");
		this.computation_ = new ClassTaxonomyComputation(
				//Only need to saturate new classes?
				reasoner.ontologyIndex.getIndexedClasses(),
				reasoner.getProcessExecutor(), workerNo, progressMonitor,
				reasoner.ontologyIndex);
	}

	
}
