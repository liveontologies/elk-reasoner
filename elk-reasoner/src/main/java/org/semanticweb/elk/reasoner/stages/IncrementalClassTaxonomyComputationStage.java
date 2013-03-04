/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import java.util.Collection;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomyComputation;
import org.semanticweb.elk.util.collections.Operations;

/**
 * TODO docs
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
class IncrementalClassTaxonomyComputationStage extends AbstractReasonerStage {

	// private static final Logger LOGGER_ = Logger
	// .getLogger(IncrementalClassTaxonomyComputationStage.class);

	protected ClassTaxonomyComputation computation_ = null;

	public IncrementalClassTaxonomyComputationStage(
			AbstractReasonerState reasoner, AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return IncrementalStages.TAXONOMY_CONSTRUCTION.toString();
	}

	@Override
	boolean preExecute() {
		if (!super.preExecute())
			return false;
		/*
		 * classes which correspond to changed nodes in the taxonomy they must
		 * include new classes let's convert to indexed objects and filter out
		 * removed classes
		 */
		Operations.Transformation<ElkClass, IndexedClass> transformation = new Operations.Transformation<ElkClass, IndexedClass>() {
			@Override
			public IndexedClass transform(ElkClass element) {
				IndexedClass indexedClass = (IndexedClass) element
						.accept(reasoner.objectCache_.getIndexObjectConverter());
				
				return indexedClass.occurs() ? indexedClass : null;
			}
		};
		Collection<IndexedClass> modified = Operations.getCollection(
				Operations.map(reasoner.classTaxonomyState.classesForModifiedNodes, transformation),
				// an upper bound
				reasoner.classTaxonomyState.classesForModifiedNodes.size());

		this.computation_ = new ClassTaxonomyComputation(Operations.split(
				modified, 64), reasoner.getProcessExecutor(), workerNo,
				progressMonitor, reasoner.saturationState,
				reasoner.classTaxonomyState.getTaxonomy());

		return true;
	}

	@Override
	public void executeStage() throws ElkInterruptedException {
		computation_.process();
	}

	@Override
	boolean postExecute() {
		if (!super.postExecute()) {
			return false;
		}
		reasoner.classTaxonomyState.getWriter().clearModifiedNodeObjects();
		reasoner.ontologyIndex.initClassSignatureChanges();
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
