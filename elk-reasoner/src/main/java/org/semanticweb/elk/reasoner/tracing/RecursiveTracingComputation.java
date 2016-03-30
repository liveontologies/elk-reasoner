/**
 * 
 */
package org.semanticweb.elk.reasoner.tracing;

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

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputationWithInputs;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.tracing.factories.ProofUnwindingFactory;
import org.semanticweb.elk.reasoner.tracing.factories.ProofUnwindingJob;
import org.semanticweb.elk.reasoner.tracing.factories.ProofUnwindingListener;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;

/**
 * A reasoner computation for recursive tracing. Uses
 * {@link ProofUnwindingFactory} to perform the computation.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RecursiveTracingComputation
		extends
			ReasonerComputationWithInputs<ProofUnwindingJob<ClassConclusion>, ProofUnwindingFactory<ClassConclusion, ProofUnwindingJob<ClassConclusion>>> {

	public RecursiveTracingComputation(
			Collection<? extends ProofUnwindingJob<ClassConclusion>> inputs,
			ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor, SaturationState<?> saturationState,
			InferenceProducer<? super ClassInference> inferenceProducer) {
		super(inputs,
				new ProofUnwindingFactory<ClassConclusion, ProofUnwindingJob<ClassConclusion>>(
						saturationState, inferenceProducer, maxWorkers,
						ProofUnwindingListener.Helper.<ClassConclusion, ProofUnwindingJob<ClassConclusion>>dummyListener()),
				executor, maxWorkers, progressMonitor);
	}

	/**
	 * Print statistics about taxonomy computation
	 */
	public void printStatistics() {
		processorFactory.printStatistics();
	}

	public SaturationStatistics getRuleAndConclusionStatistics() {
		return processorFactory.getRuleAndConclusionStatistics();
	}

}
