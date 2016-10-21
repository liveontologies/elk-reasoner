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

	/**
	 * the computation used for this stage
	 */
	protected ClassTaxonomyComputation computation_ = null;

	public ClassTaxonomyComputationStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return "Class Taxonomy Computation";
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute()) {
			return false;
		}

		reasoner.initClassTaxonomy();

		this.computation_ = new ClassTaxonomyComputation(
				Operations.split(reasoner.ontologyIndex.getClasses(), 64),
				reasoner.getInterrupter(),
				reasoner.getProcessExecutor(), workerNo,
				reasoner.getProgressMonitor(), reasoner.saturationState,
				reasoner.classTaxonomyState.getTaxonomy());
		return true;
	}

	@Override
	public void executeStage() throws ElkInterruptedException {
		computation_.process();
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute())
			return false;
		reasoner.classTaxonomyState.getWriter().setTaxonomy(
				computation_.getTaxonomy());
		reasoner.ruleAndConclusionStats.add(computation_
				.getRuleAndConclusionStatistics());
		this.computation_ = null;
		return true;
	}

	@Override
	public void printInfo() {
		if (computation_ != null)
			computation_.printStatistics();
	}

}
