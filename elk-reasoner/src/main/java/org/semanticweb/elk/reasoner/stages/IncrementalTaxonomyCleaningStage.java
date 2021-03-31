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
 * Used to clean both class and instance taxonomy. Removes nodes with classes or
 * individuals that became not saturated. {@link ClassTaxonomyState} and
 * {@link InstanceTaxonomyState} keep track of these classes and individuals.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
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

		final Collection<IndexedClass> classesToRemove = reasoner.classTaxonomyState
				.getToRemove();
		final Collection<IndexedIndividual> individualsToRemove = reasoner.instanceTaxonomyState
				.getToRemove();
		Collection<IndexedClassEntity> inputs = Operations.getCollection(
				Operations.concat(classesToRemove, individualsToRemove),
				classesToRemove.size() + individualsToRemove.size());

		LOGGER_.trace("{}: classes to remove", classesToRemove);
		LOGGER_.trace("{}: individuals to remove", individualsToRemove);

		cleaning_ = new TaxonomyCleaning(inputs, reasoner.getInterrupter(),
				reasoner.classTaxonomyState.getTaxonomy(),
				reasoner.instanceTaxonomyState.getTaxonomy(),
				reasoner.getProcessExecutor(), workerNo,
				reasoner.getProgressMonitor());

		return true;
	}

	@Override
	public void executeStage() throws ElkException {
		cleaning_.process();
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute()) {
			return false;
		}
		final Collection<IndexedClass> classesToRemove = reasoner.classTaxonomyState
				.getToRemove();
		if (!classesToRemove.isEmpty()) {
			throw new ElkRuntimeException(TaxonomyCleaning.class.getSimpleName()
					+ " did not remove some classes from the taxonomy!");
		}
		final Collection<IndexedIndividual> individualsToRemove = reasoner.instanceTaxonomyState
				.getToRemove();
		if (!individualsToRemove.isEmpty()) {
			throw new ElkRuntimeException(TaxonomyCleaning.class.getSimpleName()
					+ " did not remove some individuals from the taxonomy!");
		}
		this.cleaning_ = null;

		return true;
	}

	@Override
	public void printInfo() {
		// TODO
	}

}
