/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner;

import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.consistency.ConsistencyChecking;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.OntologyIndexImpl;
import org.semanticweb.elk.reasoner.saturation.properties.ObjectPropertySaturation;
import org.semanticweb.elk.reasoner.taxonomy.Node;
import org.semanticweb.elk.reasoner.taxonomy.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyComputation;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyNode;
import org.semanticweb.elk.util.logging.Statistics;

/**
 * The Reasoner manages an ontology (using an OntologyIndex) and provides
 * methods for solving reasoning tasks over that ontology. Ontologies are loaded
 * and updated by adding or removing {@link ElkAxiom}s (methods addAxiom() and
 * removeAxiom()). The Reasoner will ensure that all methods that solve
 * reasoning tasks return an answer that is correct for the current ontology,
 * that is, the reasoner updates its answers when changes occur.
 * 
 * Reasoners are created (and pre-configured) by the {@link ReasonerFactory}.
 */
public class Reasoner {
	/**
	 * Executor used to run the jobs.
	 */
	protected final ExecutorService executor;
	/**
	 * Number of workers for concurrent jobs.
	 */
	protected final int workerNo;
	/**
	 * OntologyIndex for the ontology on which this reasoner operates.
	 */
	protected OntologyIndex ontologyIndex;

	/**
	 * Consistency flag for the current ontology;
	 */
	protected boolean isConsistent;
	/**
	 * Indicates if isConsistent needs to be recomputed due to ontology change
	 */
	protected boolean recomputeIsConsistent = true;
	/**
	 * Class taxonomy of the current ontology, after it was computed.
	 */
	protected Taxonomy<ElkClass> classTaxonomy;
	/**
	 * Indicates if classTaxonomy needs to be recomputed due to ontology change
	 */
	protected boolean recomputeClassTaxonomy = true;

	/**
	 * The progress monitor that is used for reporting progress.
	 */
	protected ProgressMonitor progressMonitor;

	/**
	 * Logger for events.
	 */
	protected final static Logger LOGGER_ = Logger.getLogger(Reasoner.class);

	/**
	 * Constructor. In most cases, Reasoners should be created by the
	 * {@link ReasonerFactory}.
	 * 
	 * @param executor
	 * @param workerNo
	 */
	protected Reasoner(ExecutorService executor, int workerNo) {
		this.executor = executor;
		this.workerNo = workerNo;
		this.progressMonitor = new DummyProgressMonitor();
		reset();
	}

	/**
	 * Set the progress monitor that will be used for reporting progress on all
	 * potentially long-running operations.
	 * 
	 * @param progressMonitor
	 */
	public void setProgressMonitor(ProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	/**
	 * Get the progress monitor that is used for reporting progress on all
	 * potentially long-running operations.
	 */
	public ProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	/**
	 * Reset the ontology all data. After this, the Reasoner holds an empty
	 * ontology.
	 */
	public void reset() {
		ontologyIndex = new OntologyIndexImpl();
		invalidate();
	}

	/**
	 * Invalidate all previously computed reasoning results. This does not mean
	 * that the deductions that have been made are deleted. Rather, this method
	 * would be called if the deductions (and the index in general) have
	 * changed, and the Reasoner should not rely on its records of earlier
	 * deductions any longer.
	 */
	protected void invalidate() {
		recomputeIsConsistent = true;
		recomputeClassTaxonomy = true;
	}

	/**
	 * Get the OntologyIndex. When changing this index (adding ro deleting
	 * axioms), the Reasoner will not be notified, and may thus fail to return
	 * up-to-date answers for reasoning tasks.
	 * 
	 * FIXME Should this really be public? If yes, then the index should notify
	 * the reasoner of changes.
	 * 
	 * @return the internal ontology index
	 */
	public OntologyIndex getOntologyIndex() {
		return ontologyIndex;
	}

	/**
	 * Add an axom to the ontology. This method makes sure that internal
	 * reasoning results are updated as required, so that all reasoning methods
	 * return correct answers in the future. Previously returned results (e.g.,
	 * taxonomies) will not be updated.
	 * 
	 * TODO Clarify what happens if an axiom is added twice.
	 * 
	 * @param axiom
	 */
	public void addAxiom(ElkAxiom axiom) {
		ontologyIndex.getAxiomInserter().process(axiom);
		invalidate();
	}

	/**
	 * Remove an axom from the ontology. This method makes sure that internal
	 * reasoning results are updated as required, so that all reasoning methods
	 * return correct answers in the future. Previously returned results (e.g.,
	 * taxonomies) will not be updated.
	 * 
	 * TODO Clarify what happens when deleting axioms that were not added.
	 * 
	 * @param axiom
	 */
	public void removeAxiom(ElkAxiom axiom) {
		ontologyIndex.getAxiomDeleter().process(axiom);
		invalidate();
	}

	/**
	 * Return the inferred taxonomy of the named classes. If this has not been
	 * computed yet, then it will be computed at this point.
	 * 
	 * @return class taxonomy (not null)
	 */
	public Taxonomy<ElkClass> getTaxonomy() {
		if (recomputeClassTaxonomy) {
			classify();
			recomputeClassTaxonomy = false;
		}
		return classTaxonomy;
	}

	/**
	 * Helper method to get a suitable node from the taxonomy.
	 * 
	 * @param elkClass
	 * @return
	 * @throws FreshEntitiesException
	 * @throws InconsistentOntologyException
	 */
	protected TaxonomyNode<ElkClass> getTaxonomyClassNode(ElkClass elkClass)
			throws FreshEntitiesException, InconsistentOntologyException {
		TaxonomyNode<ElkClass> node = getTaxonomy().getNode(elkClass);
		if (node == null)
			throw new FreshEntitiesException(elkClass);
		return node;
	}

	/**
	 * Get the class node for one named class. This provides information about
	 * all equivalent classes. In theory, this does not demand that a full
	 * taxonomy is built, and the result does not provide access to such an
	 * object (or to sub- or superclasses).
	 * 
	 * @param elkClass
	 * @return
	 * @throws FreshEntitiesException
	 *             thrown if the given class is not known in the ontology yet
	 */
	public Node<ElkClass> getClassNode(ElkClass elkClass)
			throws FreshEntitiesException, InconsistentOntologyException {
		return getTaxonomyClassNode(elkClass);
	}

	/**
	 * Return the (direct or indirect) subclasses of the given
	 * {@link ElkClassExpression}. The method returns a set of ClassNodes from,
	 * each of which might represent multiple equivalent classes. In theory,
	 * this method does not require the whole taxonomy to be constructed, and
	 * the result does not provide (indirect) access to a taxonomy object.
	 * 
	 * @param classExpression
	 *            currently, only objects of type ElkClass are supported
	 * @param direct
	 *            if true, only direct subclasses are returned
	 * @return a set of ClassNodes
	 * @throws FreshEntitiesException
	 * @throws InconsistentOntologyException
	 */
	public Set<? extends Node<ElkClass>> getSubClasses(
			ElkClassExpression classExpression, boolean direct)
			throws FreshEntitiesException, InconsistentOntologyException {
		if (classExpression instanceof ElkClass) {
			TaxonomyNode<ElkClass> ceClassNode = getTaxonomyClassNode((ElkClass) classExpression);
			return (direct) ? ceClassNode.getDirectSubNodes() : ceClassNode
					.getAllSubNodes();
		} else { // TODO: complex class expressions currently not supported
			throw new UnsupportedOperationException(
					"ELK does not support the retrieval of subclasses for unnamed class expressions.");
		}
	}

	/**
	 * Return the (direct or indirect) superclasses of the given
	 * {@link ElkClassExpression}. The method returns a set of ClassNodes, each
	 * of which might represent multiple equivalent classes. In theory, this
	 * method does not require the whole taxonomy to be constructed, and the
	 * result does not provide (indirect) access to a taxonomy object.
	 * 
	 * @param classExpression
	 *            currently, only objects of type ElkClass are supported
	 * @param direct
	 *            if true, only direct subclasses are returned
	 * @return a set of ClassNodes
	 * @throws FreshEntitiesException
	 * @throws InconsistentOntologyException
	 */
	public Set<? extends Node<ElkClass>> getSuperClasses(
			ElkClassExpression classExpression, boolean direct)
			throws FreshEntitiesException, InconsistentOntologyException {
		if (classExpression instanceof ElkClass) {
			TaxonomyNode<ElkClass> ceClassNode = getTaxonomyClassNode((ElkClass) classExpression);
			return (direct) ? ceClassNode.getDirectSuperNodes() : ceClassNode
					.getAllSuperNodes();
		} else { // TODO: complex class expressions currently not supported
			throw new UnsupportedOperationException(
					"ELK does not support the retrieval of subclasses for unnamed class expressions.");
		}
	}

	/**
	 * Check if the ontology is consistent, that is, satisfiable.
	 * 
	 */
	public boolean isConsistent() {
		if (recomputeIsConsistent) {
			checkConsistent();
			recomputeIsConsistent = false;
		}
		return isConsistent;
	}

	/**
	 * Check if the given class expression is satisfiable, that is, if it can
	 * possibly have instances. Classes that are not satisfiable if they are
	 * equivalent to owl:Nothing. A satisfiable class is also called consistent
	 * or coherent.
	 * 
	 * @param classExpression
	 *            currently, only objects of type ElkClass are supported
	 * @return
	 * @throws FreshEntitiesException
	 */
	public boolean isSatisfiable(ElkClassExpression classExpression)
			throws FreshEntitiesException, InconsistentOntologyException {
		if (classExpression instanceof ElkClass) {
			Node<ElkClass> classNode = getClassNode((ElkClass) classExpression);
			return (!classNode.getMembers().contains(
					PredefinedElkClass.OWL_NOTHING));
		} else {
			throw new UnsupportedOperationException(
					"ELK does not support consistency checking for unnamed class expressions");
		}
	}

	public void shutdown() {
		executor.shutdownNow();
	}

	// used for testing
	int getNumberOfWorkers() {
		return workerNo;
	}

	protected void classify() {
		// number of indexed classes
		final int maxIndexedClassCount = ontologyIndex.getIndexedClassCount();
		// variable used in progress monitors
		int progress;

		// Saturation stage
		ObjectPropertySaturation objectPropertySaturation = new ObjectPropertySaturation(
				executor, workerNo, ontologyIndex);

		TaxonomyComputation taxonomyComputation = new TaxonomyComputation(
				executor, workerNo, ontologyIndex);

		if (LOGGER_.isInfoEnabled())
			LOGGER_.info("Classification using " + workerNo + " workers");
		Statistics.logOperationStart("Classification", LOGGER_);
		progressMonitor.start("Classification");

		try {
			objectPropertySaturation.compute();
		} catch (InterruptedException e1) {
			// FIXME Either document why this is ignored or do something better.
		}

		progress = 0;
		taxonomyComputation.start();
		for (IndexedClass ic : ontologyIndex.getIndexedClasses()) {
			try {
				taxonomyComputation.submit(ic);
			} catch (InterruptedException e) {
				// FIXME Either document why this is ignored or do something
				// better.
			}
			progressMonitor.report(++progress, maxIndexedClassCount);
		}
		try {
			taxonomyComputation.waitCompletion();
		} catch (InterruptedException e) {
			// FIXME Either document why this is ignored or do something better.
		}
		classTaxonomy = taxonomyComputation.getClassTaxonomy();
		progressMonitor.finish();
		Statistics.logOperationFinish("Classification", LOGGER_);
		Statistics.logMemoryUsage(LOGGER_);
		taxonomyComputation.printStatistics();
	}

	public void checkConsistent() {

		if (!ontologyIndex.getIndexedOwlNothing().occursPositively()) {
			isConsistent = true;
			return;
		}

		// number of indexed classes
		final int maxIndexedIndividualCount = ontologyIndex
				.getIndexedIndividualCount();
		// variable used in progress monitors
		int progress;

		// Saturation stage
		ObjectPropertySaturation objectPropertySaturation = new ObjectPropertySaturation(
				executor, workerNo, ontologyIndex);

		ConsistencyChecking consistencyChecking = new ConsistencyChecking(
				executor, workerNo, ontologyIndex);

		if (LOGGER_.isInfoEnabled())
			LOGGER_.info("Consistency checking  using " + workerNo + " workers");
		Statistics.logOperationStart("Consistency checking", LOGGER_);
		progressMonitor.start("Consistency checking");

		try {
			objectPropertySaturation.compute();
		} catch (InterruptedException e1) {
			// FIXME Either document why this is ignored or do something better.
		}

		progress = 0;
		consistencyChecking.start();
		try {
			consistencyChecking.submit(ontologyIndex.getIndexedOwlThing());
		} catch (InterruptedException e) {
			// FIXME Either document why this is ignored or do something
			// better.
		}

		for (IndexedIndividual ind : ontologyIndex.getIndexedIndividuals()) {
			try {
				consistencyChecking.submit(ind);
			} catch (InterruptedException e) {
				// FIXME Either document why this is ignored or do something
				// better.
			}
			progressMonitor.report(++progress, maxIndexedIndividualCount);
		}
		try {
			consistencyChecking.waitCompletion();
		} catch (InterruptedException e) {
			// FIXME Either document why this is ignored or do something better.
		}
		isConsistent = consistencyChecking.isConsistent();
		progressMonitor.finish();
		Statistics.logOperationFinish("ConsistencyChecking", LOGGER_);
		Statistics.logMemoryUsage(LOGGER_);
		consistencyChecking.printStatistics();
	}

}
