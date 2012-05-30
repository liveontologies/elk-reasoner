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

import java.util.Arrays;
import java.util.List;

import org.semanticweb.elk.reasoner.taxonomy.TaxonomyComputation;

/**
 * The reasoner stage, which purpose is to compute the instance taxonomy of the
 * current ontology
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class InstanceTaxonomyComputationStage extends AbstractReasonerStage {

	public InstanceTaxonomyComputationStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return "Instance Taxonomy Computation";
	}

	@Override
	public boolean done() {
		return reasoner.doneInstanceTaxonomy;
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Arrays.asList((ReasonerStage) new ConsistencyCheckingStage(
				reasoner));
	}

	@Override
	public void execute() {
		if (reasoner.doneClassTaxonomy) {
			reasoner.taxonomy = (new TaxonomyComputation(
					reasoner.getStageExecutor(), reasoner.getExecutor(),
					reasoner.getNumberOfWorkers(),
					reasoner.getProgressMonitor(), reasoner.ontologyIndex,
					reasoner.taxonomy)).computeTaxonomy(false, true);
		} else {
			reasoner.taxonomy = (new TaxonomyComputation(
					reasoner.getStageExecutor(), reasoner.getExecutor(),
					reasoner.getNumberOfWorkers(),
					reasoner.getProgressMonitor(), reasoner.ontologyIndex))
					.computeTaxonomy(true, true);
		}
		if (isInterrupted())
			return;
		reasoner.doneClassTaxonomy = true;
		reasoner.doneInstanceTaxonomy = true;
	}

	@Override
	public void printInfo() {
		// TODO Auto-generated method stub

	}

}
