/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Collection;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputationWithInputs;
import org.semanticweb.elk.reasoner.indexing.model.IndexedIndividual;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceTaxonomy;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;

// TODO: documentation

/**
 * Computing taxonomy relations between named individuals and atomic classes of
 * the ontology.
 * 
 * @author Frantisek Simancik
 * @author Yevgeny Kazakov
 * 
 */
public class InstanceTaxonomyComputation
		extends
		ReasonerComputationWithInputs<IndexedIndividual, InstanceTaxonomyComputationFactory> {

	public InstanceTaxonomyComputation(
			Collection<? extends IndexedIndividual> inputs,
			final InterruptMonitor interrupter,
			ComputationExecutor executor,
			int maxWorkers,
			ProgressMonitor progressMonitor,
			SaturationState<?> saturationState,
			UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> partialTaxonomy) {
		super(inputs, new InstanceTaxonomyComputationFactory(
				interrupter, saturationState, maxWorkers, partialTaxonomy),
				executor, maxWorkers, progressMonitor);
	}

	/**
	 * @return the taxonomy computed by this computation; the method
	 *         {@link #process()} should be called first to compute the taxonomy
	 */
	public UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> getTaxonomy() {
		return processorFactory.getTaxonomy();
	}

	/**
	 * Print statistics about taxonomy computation
	 */
	public void printStatistics() {
		processorFactory.printStatistics();
	}

}