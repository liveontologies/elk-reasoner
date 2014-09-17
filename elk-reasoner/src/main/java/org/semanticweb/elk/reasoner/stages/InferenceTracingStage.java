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
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.saturation.tracing.RecursiveTracingComputation;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceState;

/**
 * Executes {@link RecursiveTracingComputation} to trace inferences queued in {@link TraceState} 
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class InferenceTracingStage extends AbstractReasonerStage {

	private RecursiveTracingComputation tracing_;
	
	public InferenceTracingStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return "Inference tracing";
	}

	@Override
	public void printInfo() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean preExecute() {
		if (!super.preExecute()) {
			return false;
		}
		
		tracing_ = new RecursiveTracingComputation(reasoner.traceState,
				reasoner.saturationState, reasoner.getProcessExecutor(),
				reasoner.getNumberOfWorkers(), reasoner.getProgressMonitor());
		
		return true;
	}


	@Override
	void executeStage() throws ElkException {
		for (;;) {
			tracing_.process();
			
			if (!spuriousInterrupt()) {
				break;
			}
		}
	}

	@Override
	public boolean postExecute() {
		// merge the stats
		reasoner.ruleAndConclusionStats.add(tracing_.getStatistics());
		
		return super.postExecute();
	}
	
	

}
