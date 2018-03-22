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

import java.util.Collection;

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturation;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationAdditionFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;
import org.semanticweb.elk.util.collections.Operations;

/**
 * Stage that saturates contexts necessary to answer the pending entailment
 * queries.
 * 
 * @author Peter Skocovsky
 */
class EntailmentQueryStage extends AbstractReasonerStage {

	/**
	 * the computation used for this stage
	 */
	protected ClassExpressionSaturation<IndexedContextRoot> computation_ = null;

	public EntailmentQueryStage(final AbstractReasonerState reasoner,
			final AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return "Entailment Query Computation";
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute()) {
			return false;
		}

		final Collection<IndexedContextRoot> queries = reasoner.entailmentQueryState
				.getNotSaturatedPositivelyIndexedRoots();
		final Collection<? extends IndexedClassEntity> consistencyTestEntities = reasoner.consistencyCheckingState
				.getTestEntitites();

		@SuppressWarnings("unchecked")
		final Collection<IndexedContextRoot> inputs = Operations.getCollection(
				Operations.concat(queries, consistencyTestEntities),
				queries.size() + consistencyTestEntities.size());

		this.computation_ = new ClassExpressionSaturation<IndexedContextRoot>(
				inputs, reasoner.getProcessExecutor(), workerNo,
				reasoner.getProgressMonitor(),
				new RuleApplicationAdditionFactory<RuleApplicationInput>(
						reasoner.getInterrupter(), reasoner.saturationState));

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
		reasoner.ruleAndConclusionStats
				.add(computation_.getRuleAndConclusionStatistics());
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
