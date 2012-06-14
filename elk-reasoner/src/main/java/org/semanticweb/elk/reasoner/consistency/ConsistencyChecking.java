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

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputation;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;

/**
 * Class for checking ontology consistency.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class ConsistencyChecking
		extends
		ReasonerComputation<IndexedClassExpression, ConsistencyCheckingFactory.Engine, ConsistencyCheckingFactory> {

	/**
	 * the index of the ontology used for computation
	 */
	protected final OntologyIndex ontologyIndex;

	public ConsistencyChecking(
			ConsistencyCheckingFactory inputProcessorFactory,
			ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor, OntologyIndex ontologyIndex) {
		/*
		 * first consistency is checked for <tt>owl:Thing</tt>, then for the
		 * individuals in the ontology
		 */
		super(Operations.concat(
				Operations.singleton(ontologyIndex.getIndexedOwlThing()),
				ontologyIndex.getIndexedIndividuals()), ontologyIndex
				.getIndexedIndividualCount() + 1, inputProcessorFactory,
				executor, maxWorkers, progressMonitor);
		this.ontologyIndex = ontologyIndex;
	}

	public ConsistencyChecking(ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor, OntologyIndex ontologyIndex) {
		this(new ConsistencyCheckingFactory(ontologyIndex, maxWorkers),
				executor, maxWorkers, progressMonitor, ontologyIndex);
	}

	@Override
	public void process() {
		if (!ontologyIndex.getIndexedOwlNothing().occursPositively())
			return;
		super.process();
	}

	/**
	 * @return <tt>true</tt> if the ontology is consistent; should be called
	 *         after the consistency checking is performed using the method
	 *         {@link #process()}
	 */
	public boolean isConsistent() {
		return inputProcessorFactory.isConsistent();
	}

	/**
	 * Print statistics about consistency checking
	 */
	public void printStatistics() {
		inputProcessorFactory.printStatistics();
	}

}