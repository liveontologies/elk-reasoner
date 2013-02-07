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
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.taxonomy.InstanceTaxonomyComputation;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceTaxonomy;

/**
 * A {@link ReasonerStage} during which the instance taxonomy of the current
 * ontology is computed
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
	private InstanceTaxonomyComputation computation_ = null;

	public InstanceTaxonomyComputationStage(ReasonerStageManager manager) {
		super(manager);
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
		return Arrays.asList(manager.classTaxonomyComputationStage);
	}

	@SuppressWarnings("unchecked")
	@Override
	boolean preExecute() {
		if (!super.preExecute())
			return false;
		if (reasoner.doneClassTaxonomy) {
			// TODO Think how to get rid of this type cast
			// it's here b/c our class taxonomy computation outputs
			// updateable class taxonomy (as it, in principle, should)
			// while here we have to start with a partial instance taxonomy
			// we just *know* that concurrent taxonomy is an instance of both
			// so we can cast here.
			// This can be avoided either by obliging the class taxonomy
			// computation to always compute some instance taxonomy
			// or (better) by initializing instance taxonomy based
			// on class taxonomy if the latter happens to not be an instance
			// taxonomy
			if (reasoner.classTaxonomyState.taxonomy instanceof UpdateableInstanceTaxonomy) {
				reasoner.instanceTaxonomy = (UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual>) reasoner.classTaxonomyState.taxonomy;
			} else {
				// this should never happen
				throw new IllegalStateException(
						"Class taxonomy does not support instances, can't proceed");
			}

			this.computation_ = new InstanceTaxonomyComputation(
					reasoner.ontologyIndex.getIndexedIndividuals(),
					reasoner.getProcessExecutor(), workerNo, progressMonitor,
					reasoner.saturationState, reasoner.instanceTaxonomy);
		}

		if (LOGGER_.isInfoEnabled())
			LOGGER_.info(getName() + " using " + workerNo + " workers");
		return true;
	}

	@Override
	public void executeStage() throws ElkInterruptedException {
		for (;;) {
			computation_.process();
			if (!spuriousInterrupt())
				break;
		}
	}

	boolean postExecute() {
		if (!super.postExecute())
			return false;
		reasoner.instanceTaxonomy = computation_.getTaxonomy();
		reasoner.classTaxonomyState.taxonomy = reasoner.instanceTaxonomy;
		reasoner.doneInstanceTaxonomy = true;
		this.computation_ = null;
		return true;
	}

	@Override
	public void printInfo() {
		if (computation_ != null)
			computation_.printStatistics();
	}

}
