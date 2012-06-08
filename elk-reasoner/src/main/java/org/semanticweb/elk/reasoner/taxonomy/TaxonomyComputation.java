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

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputation;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassEntity;

// TODO: documentation

/**
 * Computing taxonomy relations between atomic classes of the ontology.
 * 
 * @author Frantisek Simancik
 * @author Yevgeny Kazakov
 * 
 */
public class TaxonomyComputation extends
		ReasonerComputation<IndexedClassEntity, TaxonomyComputationEngine> {

	public TaxonomyComputation(Iterable<? extends IndexedClassEntity> inputs,
			int inputsSize, int maxWorkers, ProgressMonitor progressMonitor,
			OntologyIndex ontologyIndex, IndividualClassTaxonomy partialTaxonomy) {
		super(inputs, inputsSize, new TaxonomyComputationEngine(ontologyIndex,
				partialTaxonomy), maxWorkers, progressMonitor);
	}

	public TaxonomyComputation(Iterable<? extends IndexedClassEntity> inputs,
			int inputsSize, int maxWorkers, ProgressMonitor progressMonitor,
			OntologyIndex ontologyIndex) {
		this(inputs, inputsSize, maxWorkers, progressMonitor, ontologyIndex,
				new ConcurrentTaxonomy());
	}

	/**
	 * @return the taxonomy computed by this computation; the method
	 *         {@link #process()} should be called first to compute the taxonomy
	 */
	public IndividualClassTaxonomy getTaxonomy() {
		return inputProcessor.getTaxonomy();
	}

	/**
	 * Print statistics about taxonomy computation
	 */
	public void printStatistics() {
		inputProcessor.printStatistics();
	}

}