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

import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionNoInputSaturation;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.rules.ContextCleaningFactory;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalContextCleaningStage extends AbstractReasonerStage {

	// logger for this class
	// private static final Logger LOGGER_ = Logger
	// .getLogger(IncrementalContextCleaningStage.class);

	private ClassExpressionNoInputSaturation cleaning_ = null;

	public IncrementalContextCleaningStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return IncrementalStages.CONTEXT_CLEANING.toString();
	}

	@Override
	boolean preExecute() {
		if (!super.preExecute())
			return false;
		RuleApplicationFactory cleaningFactory = new ContextCleaningFactory(
				reasoner.saturationState);
		this.cleaning_ = new ClassExpressionNoInputSaturation(
				reasoner.getProcessExecutor(), workerNo,
				reasoner.getProgressMonitor(), cleaningFactory,
				ContextModificationListener.DUMMY);
		return true;
	}

	@Override
	public void executeStage() throws ElkInterruptedException {
		for (;;) {
			cleaning_.process();
			if (!spuriousInterrupt())
				break;
		}
	}

	@Override
	boolean postExecute() {
		if (!super.postExecute())
			return false;
		reasoner.incrementalState.setStageStatus(
				IncrementalStages.CONTEXT_CLEANING, true);
		reasoner.ruleAndConclusionStats.add(cleaning_
				.getRuleAndConclusionStatistics());
		cleaning_ = null;
		return true;
	}

	@Override
	public void printInfo() {
		if (cleaning_ != null)
			cleaning_.printStatistics();
	}

}
