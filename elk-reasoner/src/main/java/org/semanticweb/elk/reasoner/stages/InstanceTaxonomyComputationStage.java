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

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyComputation;
import org.semanticweb.elk.util.collections.Operations;

/**
 * The reasoner stage, which purpose is to compute the instance taxonomy of the
 * current ontology
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class InstanceTaxonomyComputationStage extends AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(InstanceTaxonomyComputationStage.class);

	/**
	 * the computation used for this stage
	 */
	private TaxonomyComputation computation = null;

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
		if (computation == null)
			initComputation();
		if (LOGGER_.isInfoEnabled())
			LOGGER_.info(getName() + " using " + workerNo + " workers");
		progressMonitor.start(getName());
		computation.process();
		progressMonitor.finish();
		if (isInterrupted())
			return;
		reasoner.taxonomy = computation.getTaxonomy();
		reasoner.doneClassTaxonomy = true;
		reasoner.doneInstanceTaxonomy = true;
		reasoner.doneReset = false;
	}

	@Override
	void initComputation() {
		super.initComputation();
		OntologyIndex ontologyIndex = reasoner.ontologyIndex;
		if (reasoner.doneClassTaxonomy)
			this.computation = new TaxonomyComputation(
					ontologyIndex.getIndexedIndividuals(),
					ontologyIndex.getIndexedIndividualCount(),
					reasoner.getStageExecutor(), workerNo, progressMonitor,
					reasoner.getOntologyIndex(), reasoner.taxonomy);
		else
			this.computation = new TaxonomyComputation(Operations.concat(
					ontologyIndex.getIndexedClasses(),
					ontologyIndex.getIndexedIndividuals()),
					ontologyIndex.getIndexedClassCount()
							+ ontologyIndex.getIndexedIndividualCount(),
					reasoner.getStageExecutor(), workerNo, progressMonitor,
					reasoner.getOntologyIndex());
	}

	@Override
	public void printInfo() {
		computation.printStatistics();
	}

}
