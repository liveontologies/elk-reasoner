package org.semanticweb.elk.alc.reasoner;

import org.semanticweb.elk.alc.indexing.hierarchy.ChangeIndexingProcessor;
import org.semanticweb.elk.alc.indexing.hierarchy.ElkAxiomIndexingVisitor;
import org.semanticweb.elk.alc.indexing.hierarchy.MainAxiomIndexerVisitor;
import org.semanticweb.elk.alc.indexing.hierarchy.OntologyIndex;
import org.semanticweb.elk.alc.loading.AxiomLoader;
import org.semanticweb.elk.alc.loading.ComposedAxiomLoader;
import org.semanticweb.elk.alc.loading.ElkLoadingException;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.util.logging.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reasoner {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(Reasoner.class);

	/**
	 * the (differential) index for loading of axioms and changes
	 */
	private final OntologyIndex ontologyIndex_;
	/**
	 * the object using which axioms are inserted into the index
	 */
	private final ElkAxiomIndexingVisitor axiomInserterVisitor_;
	/**
	 * the object using which axioms are deleted from the index
	 */
	private final ElkAxiomIndexingVisitor axiomDeleterVisitor_;
	/**
	 * The source where axioms and changes in ontology can be loaded
	 */
	private AxiomLoader axiomLoader_;

	public Reasoner() {
		ontologyIndex_ = new OntologyIndex();
		this.axiomInserterVisitor_ = new MainAxiomIndexerVisitor(
				ontologyIndex_, true);
		this.axiomDeleterVisitor_ = new MainAxiomIndexerVisitor(ontologyIndex_,
				false);

	}

	public Reasoner(AxiomLoader axiomLoader) {
		this();
		registerAxiomLoader(axiomLoader);
	}

	public synchronized void registerAxiomLoader(AxiomLoader newAxiomLoader) {
		LOGGER_.trace("Registering new axiom loader");

		if (axiomLoader_ == null || axiomLoader_.isLoadingFinished())
			axiomLoader_ = newAxiomLoader;
		else
			axiomLoader_ = new ComposedAxiomLoader(axiomLoader_, newAxiomLoader);
	}

	/**
	 * Forces loading of all axioms from the registered {@link AxiomLoader}s.
	 * Typically, loading lazily when reasoning tasks are requested.
	 * 
	 * @throws ElkLoadingException
	 *             if axioms cannot be loaded
	 */
	public void forceLoading() throws ElkLoadingException {
		if (axiomLoader_ == null || axiomLoader_.isLoadingFinished()) {
			return;
		}

		ElkAxiomProcessor axiomInserter = new ChangeIndexingProcessor(
				axiomInserterVisitor_);
		ElkAxiomProcessor axiomDeleter = new ChangeIndexingProcessor(
				axiomDeleterVisitor_);

		Statistics.logOperationStart("loading", LOGGER_);
		try {
			axiomLoader_.load(axiomInserter, axiomDeleter);
		} finally {
			Statistics.logOperationFinish("loading", LOGGER_);
			Statistics.logMemoryUsage(LOGGER_);
		}

	}
}
