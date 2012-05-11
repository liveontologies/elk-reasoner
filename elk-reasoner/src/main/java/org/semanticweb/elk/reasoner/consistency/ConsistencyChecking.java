package org.semanticweb.elk.reasoner.consistency;

import java.util.concurrent.ExecutorService;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentComputation;

public class ConsistencyChecking extends
		ConcurrentComputation<IndexedClassExpression> {

	final ConsistencyCheckingEngine consistencyCheckingEngine;

	public ConsistencyChecking(ExecutorService executor, int maxWorkers,
			ConsistencyCheckingEngine consistencyCheckingEngine) {
		super(consistencyCheckingEngine, executor, maxWorkers, 8 * maxWorkers,
				16);
		this.consistencyCheckingEngine = consistencyCheckingEngine;
	}

	public ConsistencyChecking(ExecutorService executor, int maxWorkers,
			OntologyIndex ontologyIndex) {
		this(executor, maxWorkers, new ConsistencyCheckingEngine(ontologyIndex));
	}

	public boolean isConsistent() {
		return consistencyCheckingEngine.isConsistent();
	}

	public void printStatistics() {
		consistencyCheckingEngine.printStatistics();
	}
}