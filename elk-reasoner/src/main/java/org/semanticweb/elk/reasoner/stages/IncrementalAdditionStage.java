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

import java.util.Arrays;
import java.util.List;

import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionNoInputSaturation;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;

/**
 * TODO docs
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalAdditionStage extends AbstractReasonerStage {

	private ClassExpressionNoInputSaturation saturation_ = null;

	public IncrementalAdditionStage(ReasonerStageManager manager) {
		super(manager);
	}

	@Override
	public String getName() {
		return IncrementalStages.ADDITION.toString();
	}

	@Override
	public boolean done() {
		return reasoner.incrementalState
				.getStageStatus(IncrementalStages.ADDITION);
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Arrays.asList(manager.incrementalAdditionInitializationStage);
	}

	@Override
	boolean preExecute() {
		if (!super.preExecute())
			return false;
		// System.out.println("Active contexts: " +
		// reasoner.saturationState.activeContexts_);
		saturation_ = new ClassExpressionNoInputSaturation(
				reasoner.getProcessExecutor(), workerNo,
				reasoner.getProgressMonitor(), new RuleApplicationFactory(
						reasoner.saturationState, true),
				ContextModificationListener.DUMMY);
		return true;
	}

	@Override
	public void executeStage() throws ElkInterruptedException {
		for (;;) {
			saturation_.process();
			if (!spuriousInterrupt())
				break;
		}
	}

	@Override
	boolean postExecute() {
		if (!super.postExecute())
			return false;
		reasoner.incrementalState.setStageStatus(IncrementalStages.ADDITION,
				true);
		reasoner.ruleAndConclusionStats.add(saturation_
				.getRuleAndConclusionStatistics());

		markAllContextsAsSaturated();
		// /FIXME
		/*
		 * for (IndexedClass ic : reasoner.ontologyIndex.getIndexedClasses()) {
		 * if (ic.getContext() != null)
		 * 
		 * System.out.println(ic + ": " + ic.getContext().getSubsumers()); }
		 */
		saturation_ = null;
		return true;
	}

	@Override
	public void printInfo() {
		if (saturation_ != null)
			saturation_.printStatistics();
	}

}