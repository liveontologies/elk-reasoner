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

import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationNoInput;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationDeletionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reverts inferences
 * 
 * @author Pavel Klinov
 * 
 */
public class IncrementalDeletionStage extends AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(IncrementalDeletionStage.class);

	private ClassExpressionSaturationNoInput desaturation_ = null;

	public IncrementalDeletionStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return IncrementalStages.DELETION.toString();
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute())
			return false;
		desaturation_ = new ClassExpressionSaturationNoInput(
				reasoner.getProcessExecutor(), workerNo,
				new RuleApplicationDeletionFactory(reasoner.saturationState),
				ContextModificationListener.DUMMY);
		return true;
	}

	@Override
	public void executeStage() throws ElkInterruptedException {
		desaturation_.process();
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute())
			return false;
		reasoner.ruleAndConclusionStats.add(desaturation_
				.getRuleAndConclusionStatistics());
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("Number of modified contexts "
					+ reasoner.saturationState.getNotSaturatedContexts().size());
		}
		desaturation_ = null;
		return true;
	}

	@Override
	public void printInfo() {
		if (desaturation_ != null)
			desaturation_.printStatistics();
	}

	@Override
	public synchronized void setInterrupt(boolean flag) {
		super.setInterrupt(flag);
		setInterrupt(desaturation_, flag);
	}

}
