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
package org.semanticweb.elk.reasoner.consistency;

import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.saturation.properties.ObjectPropertySaturation;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentComputation;
import org.semanticweb.elk.util.concurrent.computation.Interrupter;
import org.semanticweb.elk.util.logging.Statistics;

/**
 * Class for checking ontology consistency.
 * 
 * @author Frantisek Simancik
 * 
 */
public class ConsistencyChecking extends
		ConcurrentComputation<IndexedClassExpression> {

	protected final static Logger LOGGER_ = Logger
			.getLogger(ObjectPropertySaturation.class);

	protected final ProgressMonitor progressMonitor;
	protected final OntologyIndex ontologyIndex;
	protected final Interrupter interrupter;
	protected final ConsistencyCheckingEngine consistencyCheckingEngine;

	protected ConsistencyChecking(Interrupter interrupter,
			ExecutorService executor, int maxWorkers,
			ProgressMonitor progressMonitor, OntologyIndex ontologyIndex,
			ConsistencyCheckingEngine consistencyCheckingEngine) {
		super(consistencyCheckingEngine, interrupter, executor, maxWorkers,
				8 * maxWorkers, 16);
		this.progressMonitor = progressMonitor;
		this.ontologyIndex = ontologyIndex;
		this.interrupter = interrupter;
		this.consistencyCheckingEngine = consistencyCheckingEngine;
	}

	public ConsistencyChecking(Interrupter interrupter,
			ExecutorService executor, int maxWorkers,
			ProgressMonitor progressMonitor, OntologyIndex ontologyIndex) {
		this(interrupter, executor, maxWorkers, progressMonitor, ontologyIndex,
				new ConsistencyCheckingEngine(ontologyIndex, interrupter));
	}

	/**
	 * Prerequisites: object properties must be already saturated.
	 * 
	 */
	public boolean checkConsistent() {

		if (!ontologyIndex.getIndexedOwlNothing().occursPositively())
			return true;

		// number of indexed classes
		final int maxProgress = ontologyIndex.getIndexedIndividualCount();
		// variable used in progress monitors
		int progress = 0;
		start();

		try {
			submit(ontologyIndex.getIndexedOwlThing());
			for (IndexedIndividual ind : ontologyIndex.getIndexedIndividuals()) {
				submit(ind);
				progressMonitor.report(++progress, maxProgress);

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

		return consistencyCheckingEngine.isConsistent();

	}

	/**
	 * Print statistics about consistency checking
	 */
	public void printStatistics() {
		consistencyCheckingEngine.printStatistics();
	}

}