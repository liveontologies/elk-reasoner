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

/**
 * The reasoner stage, during which the class taxonomy of the current ontology
 * is computed
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
	private ClassTaxonomyComputation computation = null;

	public ClassTaxonomyComputationStage(AbstractReasonerState reasoner) {
		super(reasoner);
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
		return Arrays.asList((ReasonerStage) new ConsistencyCheckingStage(
				reasoner));
	}

	@Override
	public void execute() {
		if (computation == null)
			initComputation();
		progressMonitor.start(getName());
		computation.process();
		progressMonitor.finish();
		if (isInterrupted())
			return;
		reasoner.taxonomy = computation.getTaxonomy();
		reasoner.doneClassTaxonomy = true;
		reasoner.doneReset = false;
	}

	@Override
	void initComputation() {
		super.initComputation();
		if (LOGGER_.isInfoEnabled())
			LOGGER_.info(getName() + " using " + workerNo + " workers");
		this.computation = new ClassTaxonomyComputation(
				reasoner.ontologyIndex.getIndexedClasses(),
				reasoner.ontologyIndex.getIndexedClassCount(),
				reasoner.getProcessExecutor(), workerNo, progressMonitor,
				reasoner.getOntologyIndex());
	}

	@Override
	public void printInfo() {
		if (computation != null)
			computation.printStatistics();
	}

}
