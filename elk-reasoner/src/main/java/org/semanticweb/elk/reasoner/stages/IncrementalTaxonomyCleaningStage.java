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
package org.semanticweb.elk.reasoner.stages;

import java.util.Collection;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.model.IndexedIndividual;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyCleaning;
import org.semanticweb.elk.util.collections.Operations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to clean both class and instance taxonomies (removed nodes which
 * super-nodes or types need to be recomputed)
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalTaxonomyCleaningStage extends AbstractReasonerStage {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(IncrementalTaxonomyCleaningStage.class);

	private TaxonomyCleaning cleaning_ = null;

	public IncrementalTaxonomyCleaningStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return IncrementalStages.TAXONOMY_CLEANING.toString();
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute()) {
			return false;
		}

		final Collection<IndexedClass> removedClasses = reasoner.classTaxonomyState
				.getRemoved();
		final Collection<IndexedIndividual> removedIndividuals = reasoner.instanceTaxonomyState
				.getRemoved();
		@SuppressWarnings("unchecked")
		Collection<IndexedClassEntity> inputs = Operations.getCollection(
				Operations.concat(removedClasses, removedIndividuals),
				removedClasses.size() + removedIndividuals.size());

		LOGGER_.trace("{}: removed classes", removedClasses);
		LOGGER_.trace("{}: removed individuals", removedIndividuals);

		cleaning_ = new TaxonomyCleaning(inputs, reasoner.getInterrupter(),
				reasoner.classTaxonomyState, reasoner.instanceTaxonomyState,
				reasoner.getProcessExecutor(), workerNo,
				reasoner.getProgressMonitor());

		return true;
	}

	@Override
	public void executeStage() throws ElkException {

		if (reasoner.classTaxonomyState.getTaxonomy() == null) {
			// nothing to do
			return;
		}
		cleaning_.process();
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute()) {
			return false;
		}
		final Collection<IndexedClass> removedClasses = reasoner.classTaxonomyState
				.getRemoved();
		if (!removedClasses.isEmpty()) {
			throw new ElkRuntimeException(TaxonomyCleaning.class.getSimpleName()
					+ " did not consume all removed classes!");
		}
		final Collection<IndexedIndividual> removedIndividuals = reasoner.instanceTaxonomyState
				.getRemoved();
		if (!removedIndividuals.isEmpty()) {
			throw new ElkRuntimeException(TaxonomyCleaning.class.getSimpleName()
					+ " did not consume all removed individuals!");
		}
		// at this point we're done with unsaturated contexts
		markAllContextsAsSaturated();
		this.cleaning_ = null;

		return true;
	}

	@Override
	public void printInfo() {
		// TODO
	}

}
