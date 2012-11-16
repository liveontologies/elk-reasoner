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

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.incremental.IncrementalStages;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturation;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IncrementalReSaturationStage extends AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger.getLogger(IncrementalReSaturationStage.class);

	private ClassExpressionSaturation<IndexedClassExpression> saturation_ = null;
	public IncrementalReSaturationStage(AbstractReasonerState reasoner) {
		super(reasoner);
	}

	@Override
	public String getName() {
		return IncrementalStages.SATURATION.toString();
	}

	@Override
	public boolean done() {
		return reasoner.incrementalState.getStageStatus(IncrementalStages.SATURATION);
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		// these two stages both modify the shared saturation state
		return Arrays.asList(
					// this initializes fully cleaned contexts (should execute cleaning first)
					(ReasonerStage) new IncrementalContextInitializationStage(reasoner, new IncrementalContextCleaningStage(reasoner)),
					// this initializes changes for additions
					(ReasonerStage) new IncrementalChangesInitializationStage(reasoner, false));
	}

	@Override
	public void execute() throws ElkInterruptedException {
		if (saturation_ == null) {
			initComputation();
		}
		
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
		
		reasoner.incrementalState.setStageStatus(IncrementalStages.SATURATION, true);
		// at this point we're done with unsaturated contexts
		reasoner.saturationState.getWriter().clearNotSaturatedContexts();
	}
	
	

	@Override
	void initComputation() {
		super.initComputation();
		// time to commit the differential index
		reasoner.incrementalState.diffIndex.commit();
		
		RuleApplicationFactory appFactory = new RuleApplicationFactory(reasoner.saturationState, true);
		
		LOGGER_.trace(reasoner.saturationState.getModifiedContexts());
		
		saturation_ = new ClassExpressionSaturation<IndexedClassExpression>(
				reasoner.getProcessExecutor(),
				workerNo,
				reasoner.getProgressMonitor(),
				appFactory);
	}

	@Override
	public void printInfo() {
		if (saturation_ != null)
			saturation_.printStatistics();
	}
}