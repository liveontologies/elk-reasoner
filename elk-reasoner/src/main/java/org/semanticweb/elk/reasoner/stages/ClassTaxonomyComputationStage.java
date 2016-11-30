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

import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomyComputation;
import org.semanticweb.elk.util.collections.Operations;

/**
 * Incrementally updates the class taxonomy by creating nodes for named classes
 * whose contexts have been either created or modified.
 * {@link ClassTaxonomyState} keeps track of these classes.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
public class ClassTaxonomyComputationStage extends AbstractReasonerStage {

	protected ClassTaxonomyComputation computation_ = null;

	public ClassTaxonomyComputationStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return "Taxonomy Construction";
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute())
			return false;

		final Collection<IndexedClass> toAdd = reasoner.classTaxonomyState
				.getToAdd();

		this.computation_ = new ClassTaxonomyComputation(
				Operations.split(toAdd, 64), reasoner.getInterrupter(),
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
		if (!super.postExecute()) {
			return false;
		}
		final Collection<IndexedClass> toAdd = reasoner.classTaxonomyState
				.getToAdd();
		if (!toAdd.isEmpty()) {
			throw new ElkRuntimeException(
					ClassTaxonomyComputation.class.getSimpleName()
							+ " did not add all classes to the taxonomy!");
		}
		reasoner.ontologyIndex.initClassChanges();
		reasoner.ruleAndConclusionStats
				.add(computation_.getRuleAndConclusionStatistics());
		this.computation_ = null;
		return true;
	}

	@Override
	public void printInfo() {
		if (computation_ != null)
			computation_.printStatistics();
	}

}
