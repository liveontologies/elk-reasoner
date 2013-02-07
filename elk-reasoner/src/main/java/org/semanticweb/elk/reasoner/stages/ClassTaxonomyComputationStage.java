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
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomyComputation;
import org.semanticweb.elk.util.collections.Operations;

/**
 * A {@link ReasonerStage} during which the class taxonomy of the current
 * ontology is computed
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class ClassTaxonomyComputationStage extends AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(ClassTaxonomyComputationStage.class);

	/**
	 * the computation used for this stage
	 */
	protected ClassTaxonomyComputation computation_ = null;

	public ClassTaxonomyComputationStage(ReasonerStageManager manager) {
		super(manager);
	}

	@Override
	public String getName() {
		return "Class Taxonomy Computation";
	}

	@Override
	public boolean done() {
		return reasoner.doneClassTaxonomy;
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Arrays.asList(manager.consistencyCheckingStage);
	}

	@Override
	public void execute() throws ElkInterruptedException {
		if (computation_ == null)
			initComputation();
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

		reasoner.classTaxonomyState.taxonomy = computation_.getTaxonomy();
		reasoner.classTaxonomyState.classesForModifiedNodes.clear();
		reasoner.doneClassTaxonomy = true;
		reasoner.ruleAndConclusionStats.add(computation_
				.getRuleAndConclusionStatistics());
		computation_ = null;
	}

	@Override
	void initComputation() {
		super.initComputation();
		if (LOGGER_.isInfoEnabled())
			LOGGER_.info(getName() + " using " + workerNo + " workers");
		this.computation_ = new ClassTaxonomyComputation(Operations.split(
				reasoner.ontologyIndex.getIndexedClasses(), 64),
				reasoner.getProcessExecutor(), workerNo, progressMonitor,
				reasoner.saturationState);
	}

	@Override
	public void printInfo() {
		if (computation_ != null)
			computation_.printStatistics();
	}

}
