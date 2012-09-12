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
import org.semanticweb.elk.reasoner.taxonomy.TransitiveReductionExperiment;

/**
 * A {@link ReasonerStage} for experimenting with different transitive reduction
 * algorithms.
 * 
 * @author Frantisek Simancik
 * 
 */
class TransitiveReductionExperimentStage extends AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(TransitiveReductionExperimentStage.class);

	/**
	 * the computation used for this stage
	 */
	private TransitiveReductionExperiment computation = null;

	public TransitiveReductionExperimentStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return "Transitive Reduction Experiment";
	}

	@Override
	public boolean done() {
		return false;
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Arrays.<ReasonerStage> asList(new ClassTaxonomyComputationStage(
				reasoner));
	}

	@Override
	public void execute() throws ElkInterruptedException {
		if (computation == null)
			initComputation();
		progressMonitor.start(getName());
		try {
			for (;;) {
				computation.process();
				if (!interrupted())
					break;
			}
		} finally {
			progressMonitor.finish();
		}
	}

	@Override
	void initComputation() {
		super.initComputation();
		if (LOGGER_.isInfoEnabled())
			LOGGER_.info(getName() + " using " + 1 + " workers");
		this.computation = new TransitiveReductionExperiment(
				reasoner.ontologyIndex, reasoner.getProcessExecutor(),
				1, progressMonitor);
	}

	@Override
	public void printInfo() {
		if (computation != null)
			computation.printStatistics();
	}

}
