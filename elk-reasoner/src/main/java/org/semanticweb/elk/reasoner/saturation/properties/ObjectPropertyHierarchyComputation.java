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

import java.util.concurrent.ExecutorService;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentComputation;
import org.semanticweb.elk.util.concurrent.computation.Interrupter;

//TODO: Document this class
//TODO: Add progress monitor
/**
 * Computes the transitive closure of object property inclusions.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 */

public class ObjectPropertyHierarchyComputation extends
		ConcurrentComputation<IndexedPropertyChain> {

	// TODO: add progress monitor

	/**
	 * the interrupter used to interrupt and monitor interruption for the
	 * computation
	 */
	protected final Interrupter interrupter;
	/**
	 * the ontology index used to compute the hierarchy
	 */
	protected final OntologyIndex ontologyIndex;

	/**
	 * Creates a new object property hierarhy computation object.
	 * 
	 * @param interrupter
	 *            the interrupter used to interrupt and monitor interruption for
	 *            the computation
	 * @param executor
	 *            the execution service used to run the concurrent workers
	 * @param maxWorkers
	 *            the maximal number of concurrent workers
	 * @param ontologyIndex
	 *            the ontology index used to compute the hierarchy
	 */
	public ObjectPropertyHierarchyComputation(Interrupter interrupter,
			ExecutorService executor, int maxWorkers,
			OntologyIndex ontologyIndex) {
		super(new RoleHierarchyComputationEngine(), interrupter, executor,
				maxWorkers, 2 * maxWorkers, 128);
		this.interrupter = interrupter;
		this.ontologyIndex = ontologyIndex;
	}

	public void compute() {
		start();

		try {
			for (IndexedPropertyChain ipc : ontologyIndex
					.getIndexedPropertyChains()) {
				if (interrupter.isInterrupted())
					return;
				ipc.resetSaturated();
				submit(ipc);
			}

			finish();
			waitWorkersToStop();
		} catch (InterruptedException e) {
			interrupter.interrupt();
			Thread.interrupted();
			// wait until all workers are killed
			for (;;) {
				try {
					waitWorkersToStop();
					break;
				} catch (InterruptedException ex) {
					continue;
				}
			}
		}

	}
}
