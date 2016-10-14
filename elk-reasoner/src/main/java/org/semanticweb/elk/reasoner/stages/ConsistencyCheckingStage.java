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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassEntity;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturation;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationAdditionFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;

/**
 * A {@link ReasonerStage} during which consistency of the current ontology is
 * checked
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class ConsistencyCheckingStage extends AbstractReasonerStage {

	/**
	 * the computation used for this stage
	 */
	protected ClassExpressionSaturation<IndexedClassEntity> computation = null;

	public ConsistencyCheckingStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return "Consistency Checking";
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute())
			return false;
		this.computation = new ClassExpressionSaturation<IndexedClassEntity>(
				reasoner.consistencyCheckingState.getTestEntitites(),
				reasoner.getProcessExecutor(), workerNo,
				reasoner.getProgressMonitor(),
				new RuleApplicationAdditionFactory<RuleApplicationInput>(
						reasoner.saturationState));
		return true;
	}

	@Override
	public void executeStage() throws ElkInterruptedException {
		computation.process();
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute())
			return false;
		reasoner.ruleAndConclusionStats
				.add(computation.getRuleAndConclusionStatistics());
		this.computation = null;
		// FIXME Obviously needed a better clean-up after inconsistency
		if (reasoner.consistencyCheckingState.isInconsistent()) {
			reasoner.classTaxonomyState.getWriter().clearTaxonomy();
			reasoner.instanceTaxonomyState.getWriter().clearTaxonomy();
		}
		return true;
	}

	@Override
	public void printInfo() {
		if (computation != null)
			computation.printStatistics();
	}

	@Override
	public synchronized void setInterrupt(boolean flag) {
		super.setInterrupt(flag);
		setInterrupt(computation, flag);
	}
}
