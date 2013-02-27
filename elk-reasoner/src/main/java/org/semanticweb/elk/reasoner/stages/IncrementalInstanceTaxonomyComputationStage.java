/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkObjectsToIndexedEntitiesSet;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.taxonomy.InstanceTaxonomyComputation;
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
public class IncrementalInstanceTaxonomyComputationStage extends 	AbstractReasonerStage {

	private static final Logger LOGGER_ = Logger
			.getLogger(IncrementalInstanceTaxonomyComputationStage.class);

	protected InstanceTaxonomyComputation computation_ = null;
	
	
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
		if (!super.preExecute())
			return false;

		if (reasoner.instanceTaxonomyState.getTaxonomy() == null) {
			if (LOGGER_.isInfoEnabled()) {
				LOGGER_.info("Using non-incremental taxonomy");
			}
			
			reasoner.initInstanceTaxonomy();
			computation_ = new InstanceTaxonomyComputation(
					reasoner.ontologyIndex.getIndexedIndividuals(),
					reasoner.getProcessExecutor(), workerNo, progressMonitor,
					reasoner.saturationState, reasoner.instanceTaxonomyState.getTaxonomy());
		} else {
			// individuals which correspond to removed instance nodes in the taxonomy
			// they must include new individuals
			final Set<ElkNamedIndividual> modifiedIndividuals = reasoner.instanceTaxonomyState.getModifiedIndividuals();
			// let's convert to indexed objects and filter out removed individuals
			Collection<IndexedIndividual> modified = new ElkObjectsToIndexedEntitiesSet<ElkNamedIndividual, IndexedIndividual>(
					modifiedIndividuals,
					reasoner.objectCache_.getIndexObjectConverter());

			this.computation_ = new InstanceTaxonomyComputation(modified,
					reasoner.getProcessExecutor(), workerNo,
					progressMonitor, reasoner.saturationState,
					reasoner.instanceTaxonomyState.getTaxonomy());
		}
		
		return true;
	}

	@Override
	public void executeStage() throws ElkInterruptedException {
		computation_.process();
	}

	@Override
	boolean postExecute() {
		if (!super.postExecute()) {
			return false;
		}
		
		reasoner.instanceTaxonomyState.getWriter().clearModifiedIndividuals();
		reasoner.ontologyIndex.clearIndividualSignatureChanges();
		//reasoner.ruleAndConclusionStats.add(computation_.getRuleAndConclusionStatistics());
		this.computation_ = null;
		
		return true;

	}

	@Override
	public void printInfo() {
		if (computation_ != null) {
			computation_.printStatistics();
		}
	}
	
}
