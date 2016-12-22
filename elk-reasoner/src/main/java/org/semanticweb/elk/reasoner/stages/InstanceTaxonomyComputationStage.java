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
package org.semanticweb.elk.reasoner.stages;

import java.util.Collection;

import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.reasoner.indexing.model.IndexedIndividual;
import org.semanticweb.elk.reasoner.taxonomy.InstanceTaxonomyComputation;

/**
 * Incrementally updates the instance taxonomy by creating nodes for individuals
 * whose contexts have been either created or modified.
 * {@link InstanceTaxonomyState} keeps track of these individuals.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
public class InstanceTaxonomyComputationStage extends AbstractReasonerStage {

	private InstanceTaxonomyComputation computation_ = null;

	/**
	 * @param reasoner
	 * @param preStages
	 */
	public InstanceTaxonomyComputationStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return "Instance Taxonomy Computation";
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute())
			return false;

		final Collection<IndexedIndividual> toAdd = reasoner.instanceTaxonomyState
				.getToAdd();

		this.computation_ = new InstanceTaxonomyComputation(toAdd,
				reasoner.getInterrupter(), reasoner.getProcessExecutor(),
				workerNo, reasoner.getProgressMonitor(),
				reasoner.saturationState,
				reasoner.instanceTaxonomyState.getTaxonomy());

		return true;
	}

	@Override
	public void executeStage() throws ElkInterruptedException {
		computation_.process();
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute()) {
			return false;
		}

		final Collection<IndexedIndividual> toAdd = reasoner.instanceTaxonomyState
				.getToAdd();
		if (!toAdd.isEmpty()) {
			throw new ElkRuntimeException(
					InstanceTaxonomyComputation.class.getSimpleName()
							+ " did not add all individuals to the taxonomy!");
		}
		reasoner.instanceTaxonomyState.taxonomyComplete();
		reasoner.ontologyIndex.initIndividualChanges();
		// reasoner.ruleAndConclusionStats.add(computation_.getRuleAndConclusionStatistics());
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
