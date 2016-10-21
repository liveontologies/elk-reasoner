/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.predefined.PredefinedElkObjectPropertyFactory;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputationWithInputs;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.OntologyIndex;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;

/**
 * A {@link ReasonerComputationWithInputs} that computes object property
 * taxonomy. All sub-properties must be already computed.
 * 
 * @author Peter Skocovsky
 */
public class ObjectPropertyTaxonomyComputation extends
		ReasonerComputationWithInputs<IndexedObjectProperty, ObjectPropertyTaxonomyComputationFactory> {

	public ObjectPropertyTaxonomyComputation(final OntologyIndex ontIndex,
			final InterruptMonitor interrupter,
			final TransitiveReductionOutputVisitor<ElkObjectProperty> outputProcessor,
			final PredefinedElkObjectPropertyFactory predefinedFactory,
			final ComputationExecutor executor, final int maxWorkers,
			final ProgressMonitor progressMonitor) {
		this(ontIndex.getObjectProperties(),
				new ObjectPropertyTaxonomyComputationFactory(interrupter,
						outputProcessor, ontIndex, predefinedFactory),
				executor, maxWorkers, progressMonitor);
	}

	ObjectPropertyTaxonomyComputation(
			final Collection<? extends IndexedObjectProperty> inputs,
			final ObjectPropertyTaxonomyComputationFactory inputProcessorFactory,
			final ComputationExecutor executor, final int maxWorkers,
			final ProgressMonitor progressMonitor) {
		super(inputs, inputProcessorFactory, executor, maxWorkers,
				progressMonitor);
	}

}
