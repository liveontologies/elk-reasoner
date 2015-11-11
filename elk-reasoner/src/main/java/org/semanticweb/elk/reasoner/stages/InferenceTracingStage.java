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

import java.util.Collection;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.tracing.RecursiveTracingComputation;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceState;
import org.semanticweb.elk.reasoner.saturation.tracing.factories.ProofUnwindingJob;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.Operations.Transformation;

/**
 * Executes {@link RecursiveTracingComputation} to trace inferences queued in
 * {@link TraceState}
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
public class InferenceTracingStage extends AbstractReasonerStage {

	private RecursiveTracingComputation computation_ = null;

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
		if (computation_ != null)
			computation_.printStatistics();
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute()) {
			return false;
		}

		Collection<ProofUnwindingJob<ClassConclusion>> inputs = Operations
				.map(reasoner.traceState.getToTrace(),
						new Transformation<ClassConclusion, ProofUnwindingJob<ClassConclusion>>() {
							@Override
							public ProofUnwindingJob<ClassConclusion> transform(
									ClassConclusion conclusion) {
								return new ProofUnwindingJob<ClassConclusion>(
										conclusion);
							}
						});
		computation_ = new RecursiveTracingComputation(inputs,
				reasoner.getProcessExecutor(), reasoner.getNumberOfWorkers(),
				reasoner.getProgressMonitor(), reasoner.saturationState,
				reasoner.traceState);
		return true;
	}

	@Override
	void executeStage() throws ElkException {
		computation_.process();
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute()) {
			return false;
		}
		reasoner.traceState.clearToTrace();
		this.computation_ = null;
		return true;
	}

}
