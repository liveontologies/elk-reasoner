/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

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
