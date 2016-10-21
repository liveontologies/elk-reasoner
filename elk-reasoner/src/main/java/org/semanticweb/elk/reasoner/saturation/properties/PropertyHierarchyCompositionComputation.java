/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.properties;

import java.util.Collection;

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputationWithInputs;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.stages.PropertyHierarchyCompositionState;
import org.semanticweb.elk.reasoner.tracing.TracingInferenceProducer;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;

/**
 * A {@link ReasonerComputationWithInputs} that computes relevant sub-properties
 * and composition maps
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class PropertyHierarchyCompositionComputation
		extends
		ReasonerComputationWithInputs<IndexedPropertyChain, PropertyHierarchyCompositionComputationFactory> {

	public PropertyHierarchyCompositionComputation(OntologyIndex ontIndex,
			final InterruptMonitor interrupter,
			TracingInferenceProducer<? super ObjectPropertyInference> inferenceProducer,
			final PropertyHierarchyCompositionState.Dispatcher dispatcher,
			ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor) {
		this(ontIndex.getPropertyChains(),
				new PropertyHierarchyCompositionComputationFactory(interrupter,
						inferenceProducer, dispatcher),
				executor, maxWorkers, progressMonitor);
	}

	PropertyHierarchyCompositionComputation(
			Collection<? extends IndexedPropertyChain> inputs,
			PropertyHierarchyCompositionComputationFactory inputProcessorFactory,
			ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor) {
		super(inputs, inputProcessorFactory, executor, maxWorkers,
				progressMonitor);
	}

}
