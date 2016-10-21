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

import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationNoInput;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationDeletionNotSaturatedFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;

// TODO: obsolete?
/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalContextCleaningStage extends AbstractReasonerStage {

	private ClassExpressionSaturationNoInput cleaning_ = null;

	public IncrementalContextCleaningStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return IncrementalStages.CONTEXT_CLEANING.toString();
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute())
			return false;
		RuleApplicationFactory<?, RuleApplicationInput> cleaningFactory = new RuleApplicationDeletionNotSaturatedFactory(
				reasoner.getInterrupter(), reasoner.saturationState);
		this.cleaning_ = new ClassExpressionSaturationNoInput(
				reasoner.getProcessExecutor(), workerNo, cleaningFactory,
				ContextModificationListener.DUMMY);
		return true;
	}

	@Override
	public void executeStage() throws ElkInterruptedException {
		cleaning_.process();
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute())
			return false;
		reasoner.ruleAndConclusionStats.add(cleaning_
				.getRuleAndConclusionStatistics());
		this.cleaning_ = null;
		return true;
	}

	@Override
	public void printInfo() {
		if (cleaning_ != null)
			cleaning_.printStatistics();
	}

}
