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
package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.ReasonerComputationWithInputs;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.properties.PropertyHierarchyCompositionComputationFactory;
import org.semanticweb.elk.reasoner.stages.PropertyHierarchyCompositionState;
import org.semanticweb.elk.reasoner.tracing.TracingInferenceProducer;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentComputationWithInputs;
import org.semanticweb.elk.util.concurrent.computation.DummyInterruptMonitor;

/**
 * A {@link ReasonerComputationWithInputs} that computes relevant sub-properties
 * and composition maps
 * 
 * @author Yevgeny Kazakov
 * @author Peter Skocovsky
 */
public class TestPropertySaturation
		extends
		ConcurrentComputationWithInputs<IndexedPropertyChain, PropertyHierarchyCompositionComputationFactory> {

	public TestPropertySaturation(ComputationExecutor executor, int maxWorkers) {
		super(new PropertyHierarchyCompositionComputationFactory(
				DummyInterruptMonitor.INSTANCE,
				TracingInferenceProducer.DUMMY,
				PropertyHierarchyCompositionState.Dispatcher.DUMMY), executor,
				maxWorkers);
	}
}
