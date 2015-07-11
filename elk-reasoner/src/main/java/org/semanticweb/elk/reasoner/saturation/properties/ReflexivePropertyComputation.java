package org.semanticweb.elk.reasoner.saturation.properties;

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

import java.util.Collection;

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputationWithInputs;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.tracing.ObjectPropertyInferenceProducer;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;

/**
 * A {@link ReasonerComputationWithInputs} that computes reflexive
 * {@link IndexedPropertyChain}s
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ReflexivePropertyComputation
		extends
		ReasonerComputationWithInputs<IndexedObjectProperty, ReflexivePropertyComputationFactory> {

	public ReflexivePropertyComputation(OntologyIndex ontIndex,
			ObjectPropertyInferenceProducer inferenceProducer,
			ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor) {
		this(ontIndex.getReflexiveObjectProperties().keySet(),
				new ReflexivePropertyComputationFactory(ontIndex,
						inferenceProducer), executor, maxWorkers,
				progressMonitor);
	}

	ReflexivePropertyComputation(
			Collection<? extends IndexedObjectProperty> inputs,
			ReflexivePropertyComputationFactory inputProcessorFactory,
			ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor) {
		super(inputs, inputProcessorFactory, executor, maxWorkers,
				progressMonitor);
	}

}
