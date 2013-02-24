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

/**
 * TODO docs
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IncrementalInstanceTaxonomyComputationStage extends
		InstanceTaxonomyComputationStage {

	/**
	 * @param reasoner
	 * @param preStages
	 */
	public IncrementalInstanceTaxonomyComputationStage(
			AbstractReasonerState reasoner, AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}	
	
	@Override
	public String getName() {
		return "Incremental Instance Taxonomy Computation";
	}

	@Override
	boolean preExecute() {
		// TODO Auto-generated method stub
		return super.preExecute();
	}

	@Override
	public void executeStage() throws ElkInterruptedException {
		// TODO Auto-generated method stub
		super.executeStage();
	}

	@Override
	boolean postExecute() {
		// TODO Auto-generated method stub
		return super.postExecute();
	}



	
	
}
