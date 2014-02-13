/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import java.util.Collection;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.taxonomy.InstanceTaxonomyComputation;
import org.semanticweb.elk.util.collections.Operations;

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
 * Incrementally updates the instance taxonomy by creating nodes for individuals
 * whose contexts have been either created or modified. The taxonomy is expected
 * to be previously cleaned.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalInstanceTaxonomyComputationStage extends
		AbstractReasonerStage {

	// private static final Logger LOGGER_ = Logger
	// .getLogger(IncrementalInstanceTaxonomyComputationStage.class);

	private InstanceTaxonomyComputation computation_ = null;

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
	public boolean preExecute() {
		if (!super.preExecute())
			return false;
		/*
		 * individuals which correspond to removed instance nodes in the
		 * taxonomy they must include new individuals
		 */
		final Set<ElkNamedIndividual> modifiedIndividuals = reasoner.instanceTaxonomyState
				.getIndividualsWithModifiedNodes();
		// let's convert to indexed objects and filter out removed individuals
		Operations.Transformation<ElkNamedIndividual, IndexedIndividual> transformation = new Operations.Transformation<ElkNamedIndividual, IndexedIndividual>() {
			@Override
			public IndexedIndividual transform(ElkNamedIndividual element) {
				IndexedIndividual indexedindividual = (IndexedIndividual) element
						.accept(reasoner.objectCache_.getIndexObjectConverter());

				return indexedindividual.occurs() ? indexedindividual : null;
			}
		};
		Collection<IndexedIndividual> modified = Operations.getCollection(
				Operations.map(modifiedIndividuals, transformation),
				// an upper bound
				modifiedIndividuals.size());

		this.computation_ = new InstanceTaxonomyComputation(modified,
				reasoner.getProcessExecutor(), workerNo, progressMonitor,
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

		reasoner.instanceTaxonomyState.getWriter().clearModifiedNodeObjects();
		reasoner.ontologyIndex.initIndividualSignatureChanges();
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
