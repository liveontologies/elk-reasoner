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
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.OntologyIndexImpl;
import org.semanticweb.elk.reasoner.saturation.properties.ObjectPropertySaturation;
import org.semanticweb.elk.reasoner.taxonomy.ClassNode;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomyEngine;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentComputation;
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
	 * Current class taxonomy or null if not computed yet.
	 */
	protected ClassTaxonomy classTaxonomy;
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
		reset();
	}

	/**
	 * Reset the ontology all data. After this, the Reasoner holds an empty
	 * ontology.
	 */
	public void reset() {
		ontologyIndex = new OntologyIndexImpl();
		classTaxonomy = null;
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
		classTaxonomy = null;
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
		classTaxonomy = null;
	}

	/**
	 * Return the inferred taxonomy of the named classes. If this has not been
	 * computed yet, then it will be computed at this point.
	 * 
	 * @return class taxonomy (not null)
	 */
	public ClassTaxonomy getTaxonomy() {
		if (classTaxonomy == null) {
			classify();
		}
		return classTaxonomy;
	}

	/**
	 * Get the class node for one named class.
	 * 
	 * TODO This is meant to provide equivalent classes. Since this could be
	 * computed without full classification, it might be better to return less
	 * than a ClassNode here (ClassNodes give indirect access to the whole
	 * taxonomy, which must thus be precomputed to get them).
	 * 
	 * @param elkClass
	 * @return
	 * @throws FreshEntitiesException
	 *             thrown if the given class is not known in the ontology yet
	 */
	public ClassNode getClassNode(ElkClass elkClass)
			throws FreshEntitiesException, InconsistentOntologyException {
		ClassNode node = getTaxonomy().getNode(elkClass);
		if (node == null)
			throw new FreshEntitiesException(elkClass);
		return node;
	}

	/**
	 * Return the (direct or indirect) subclasses of the given
	 * {@link ElkClassExpression}. The method returns a set of ClassNodes from
	 * the ontology's taxonomy, each of which might represent multiple
	 * equivalent classes.
	 * 
	 * @param classExpression
	 *            currently, only objects of type ElkClass are supported
	 * @param direct
	 *            if true, only direct subclasses are returned
	 * @return a set of ClassNodes
	 * @throws FreshEntitiesException
	 * @throws InconsistentOntologyException
	 */
	public Set<ClassNode> getSubClasses(ElkClassExpression classExpression,
			boolean direct) throws FreshEntitiesException,
			InconsistentOntologyException {
		if (classExpression instanceof ElkClass) {
			ClassNode ceClassNode = getClassNode((ElkClass) classExpression);
			return (direct) ? ceClassNode.getDirectSubNodes() : ceClassNode
					.getAllSubNodes();
		} else { // TODO: complex class expressions currently not supported
			throw new UnsupportedOperationException(
					"ELK does not support the retrieval of subclasses for unnamed class expressions.");
		}
	}

	/**
	 * Return the (direct or indirect) superclasses of the given
	 * {@link ElkClassExpression}. The method returns a set of ClassNodes from
	 * the ontology's taxonomy, each of which might represent multiple
	 * equivalent classes.
	 * 
	 * @param classExpression
	 *            currently, only objects of type ElkClass are supported
	 * @param direct
	 *            if true, only direct subclasses are returned
	 * @return a set of ClassNodes
	 * @throws FreshEntitiesException
	 * @throws InconsistentOntologyException
	 */
	public Set<ClassNode> getSuperClasses(ElkClassExpression classExpression,
			boolean direct) throws FreshEntitiesException,
			InconsistentOntologyException {
		if (classExpression instanceof ElkClass) {
			ClassNode ceClassNode = getClassNode((ElkClass) classExpression);
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
	 * TODO: This will be done differently in the future
	 * 
	 * @return
	 */
	public boolean isConsistent() {
		try {
			return getClassNode(PredefinedElkClass.OWL_NOTHING) != getClassNode(PredefinedElkClass.OWL_THING);
		} catch (FreshEntitiesException e) {
			throw new RuntimeException(e);
		} catch (InconsistentOntologyException e) { // TODO: this will go away
													// when the above check is
													// more direct
			return false;
		}
	}

	/**
	 * Check if the given class expression is consistent, that is, if it can
	 * possibly have instances. Classes that are not consistent if they are
	 * equivalent to owl:Nothing. A consistent class is also called satisfiable
	 * or coherent.
	 * 
	 * @param classExpression
	 *            currently, only objects of type ElkClass are supported
	 * @return
	 * @throws FreshEntitiesException
	 */
	public boolean isConsistent(ElkClassExpression classExpression)
			throws FreshEntitiesException, InconsistentOntologyException {
		if (classExpression instanceof ElkClass) {
			ClassNode classNode = getClassNode((ElkClass) classExpression);
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

	public void classify(ProgressMonitor progressMonitor) {
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

	public void classify() {
		classify(new DummyProgressMonitor());
	}

	public class TaxonomyComputation extends
			ConcurrentComputation<IndexedClass> {

		final ClassTaxonomyEngine classTaxonomyEngine;

		public TaxonomyComputation(ExecutorService executor, int maxWorkers,
				ClassTaxonomyEngine classTaxonomyEngine) {
			super(classTaxonomyEngine, executor, maxWorkers, 8 * maxWorkers, 16);
			this.classTaxonomyEngine = classTaxonomyEngine;
		}

		public TaxonomyComputation(ExecutorService executor, int maxWorkers,
				OntologyIndex ontologyIndex) {
			this(executor, maxWorkers, new ClassTaxonomyEngine(ontologyIndex));
		}

		public ClassTaxonomy getClassTaxonomy() {
			return classTaxonomyEngine.getClassTaxonomy();
		}

		public void printStatistics() {
			classTaxonomyEngine.printStatistics();
		}
	}
}
