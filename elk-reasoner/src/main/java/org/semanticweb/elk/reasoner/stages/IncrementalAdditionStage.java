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
 * pavel.klinov@uni-ulm.de
 */
public class IncrementalAdditionStage extends AbstractReasonerStage {

	private ClassExpressionNoInputSaturation saturation_ = null;
	
	public IncrementalAdditionStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return IncrementalStages.ADDITION.toString();
	}

	@Override
	public boolean done() {
		return reasoner.incrementalState.getStageStatus(IncrementalStages.ADDITION);
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Arrays.asList((ReasonerStage) new IncrementalAdditionInitializationStage(reasoner));
	}

	@Override
	public void execute() throws ElkInterruptedException {
		if (saturation_ == null) {
			initComputation();
		}
		
		//System.out.println("Active contexts: " + reasoner.saturationState.activeContexts_);
		
		progressMonitor.start(getName());
		
		try {
			for (;;) {
				saturation_.process();
				if (!interrupted())
					break;
			}
		} finally {
			progressMonitor.finish();
		}
		
		reasoner.incrementalState.setStageStatus(IncrementalStages.ADDITION, true);
		reasoner.ruleAndConclusionStats.add(saturation_.getRuleAndConclusionStatistics());
		
		markAllContextsAsSaturated();	
		///FIXME
		/*for (IndexedClass ic : reasoner.ontologyIndex.getIndexedClasses()) {
			if (ic.getContext() != null) 
				
			System.out.println(ic + ": " + ic.getContext().getSubsumers());
		}*/
	}
	
	

	@Override
	void initComputation() {
		super.initComputation();
		
		saturation_ = new ClassExpressionNoInputSaturation(
				reasoner.getProcessExecutor(),
				workerNo,
				reasoner.getProgressMonitor(),
				new RuleApplicationFactory(reasoner.saturationState, true),
				ContextModificationListener.DUMMY);
	}

	@Override
	public void printInfo() {
		if (saturation_ != null)
			saturation_.printStatistics();
	}

}