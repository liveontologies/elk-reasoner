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
package org.semanticweb.elk.reasoner.saturation.properties;

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputation;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.concurrent.computation.Interrupter;

/**
 * Computes the transitive closure of object property inclusions.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 */

public class ObjectPropertyHierarchyComputation
		extends
		ReasonerComputation<IndexedPropertyChain, ObjectPropertyHierarchyComputationEngine> {

	/**
	 * the index of the ontology used for computation
	 */
	protected final OntologyIndex ontologyIndex;

	public ObjectPropertyHierarchyComputation(
			ObjectPropertyHierarchyComputationEngine inputProcessor,
			Interrupter interrupter, int maxWorkers,
			ProgressMonitor progressMonitor, OntologyIndex ontologyIndex) {
		super(ontologyIndex.getIndexedPropertyChains(), ontologyIndex
				.getIndexedObjectPropertyCount(), inputProcessor, interrupter,
				maxWorkers, progressMonitor);
		this.ontologyIndex = ontologyIndex;
	}

	public ObjectPropertyHierarchyComputation(Interrupter interrupter,
			int maxWorkers, ProgressMonitor progressMonitor,
			OntologyIndex ontologyIndex) {
		this(new ObjectPropertyHierarchyComputationEngine(), interrupter, maxWorkers,
				progressMonitor, ontologyIndex);
	}
}
