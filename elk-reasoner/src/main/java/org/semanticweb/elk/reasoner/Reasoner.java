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
package org.semanticweb.elk.reasoner;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.stages.AbstractReasonerState;
import org.semanticweb.elk.reasoner.stages.ReasonerStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.FreshInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.FreshTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.FreshTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.Node;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.TypeNode;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.logging.Statistics;

/**
 * The class for querying the results of the reasoning tasks for a given
 * ontology. The input ontology is represented internally by the
 * {@link OntologyIndex} object, which is updated by adding or removing
 * {@link ElkAxiom}s (methods addAxiom() and removeAxiom()). When querying the
 * results of the reasoning tasks, the reasoner will ensure that all necessary
 * reasoning stages, such as consistency checking, are performed.
 * 
 * Reasoners are created (and pre-configured) by the {@link ReasonerFactory}.
 */
public class Reasoner extends AbstractReasonerState {

	// logger for this class
	private static final Logger LOGGER_ = Logger.getLogger(Reasoner.class);

	/**
	 * The progress monitor that is used for reporting progress.
	 */
	protected ProgressMonitor progressMonitor;
	/**
	 * The executor for various stages of the reasoner
	 */
	protected final ReasonerStageExecutor stageExecutor;
	/**
	 * the executor used for concurrent tasks
	 */
	protected volatile ComputationExecutor executor;
	/**
	 * Number of workers for concurrent jobs.
	 */
	protected final int workerNo;

	/**
	 * Should fresh entities in reasoner queries be accepted (configuration
	 * setting). If false, a {@link FreshEntityException} will be thrown when
	 * encountering entities that did not occur in the ontology.
	 */
	protected boolean allowFreshEntities;

	/**
	 * Constructor. In most cases, Reasoners should be created by the
	 * {@link ReasonerFactory}.
	 * */
	protected Reasoner(ReasonerStageExecutor stageExecutor,
			ExecutorService executor, int workerNo) {
		this.stageExecutor = stageExecutor;
		this.workerNo = workerNo;
		this.progressMonitor = new DummyProgressMonitor();
		this.allowFreshEntities = true;
		reset();
		if (LOGGER_.isInfoEnabled())
			LOGGER_.info("ELK reasoner was created");
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
	 * Set if fresh entities should be allowed. Fresh entities are entities that
	 * occur as parameters of reasoner queries without occurring in the loaded
	 * ontology. If false, a {@link ElkFreshEntitiesException} will be thrown in
	 * such cases. If true, the reasoner will answer as if the entity had been
	 * declared (but not used in any axiom).
	 * 
	 * @param allow
	 */
	public void setAllowFreshEntities(boolean allow) {
		allowFreshEntities = allow;
	}

	/**
	 * Get whether fresh entities are allowed. See setAllowFreshEntities() for
	 * details.
	 * 
	 * @return
	 */
	public boolean getAllowFreshEntities() {
		return allowFreshEntities;
	}

	@Override
	protected int getNumberOfWorkers() {
		return workerNo;
	}

	@Override
	protected ComputationExecutor getProcessExecutor() {
		if (executor == null)
			executor = new ComputationExecutor(workerNo, "elk-reasoner");
		return executor;
	}

	@Override
	protected ReasonerStageExecutor getStageExecutor() {
		return stageExecutor;
	}

	@Override
	protected ProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	/**
	 * Tries to shut down the reasoner within the specified time
	 * 
	 * @param timeout
	 *            the maximum time to wait
	 * @param unit
	 *            the time unit of the timeout argument
	 * @return {@code true} if the operation was successful
	 * @throws InterruptedException
	 *             if the current thread was interrupted
	 */
	public boolean shutdown(long timeout, TimeUnit unit)
			throws InterruptedException {
		if (executor == null)
			return true;
		executor.shutdown();
		executor.awaitTermination(timeout, unit);
		boolean success = executor.isShutdown();
		executor = null;
		if (success) {
			if (LOGGER_.isInfoEnabled())
				LOGGER_.info("ELK reasoner has shut down");
		} else {
			LOGGER_.error("ELK reasoner failed to shut down!");
		}
		return success;

	}

	/**
	 * Tries to shut down the reasoner within 1 minute
	 * 
	 * @return {@code true} if the operation was successful
	 * @throws InterruptedException
	 *             if the current thread was interrupted
	 */
	public boolean shutdown() throws InterruptedException {
		return shutdown(1, TimeUnit.MINUTES);
	}

	/**
	 * Helper method to get a taxonomy node node from the taxonomy.
	 * 
	 * @param elkClass
	 * @return
	 * @throws ElkFreshEntitiesException
	 *             if the given {@link ElkClass} does not occur in the ontology
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 */
	protected TaxonomyNode<ElkClass> getTaxonomyNode(ElkClass elkClass)
			throws ElkFreshEntitiesException, ElkInconsistentOntologyException,
			ElkException {
		TaxonomyNode<ElkClass> node = getTaxonomy().getNode(elkClass);
		if (node == null) {
			if (allowFreshEntities) {
				node = new FreshTaxonomyNode<ElkClass>(elkClass, getTaxonomy());
			} else {
				throw new ElkFreshEntitiesException(elkClass);
			}
		}
		return node;
	}

	/**
	 * Helper method to get an instance node node from the taxonomy.
	 * 
	 * @param elkNamedIndividual
	 * @return
	 * @throws ElkFreshEntitiesException
	 *             if the given {@link ElkNamedIndividual} does not occur in the
	 *             ontology
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 */
	protected InstanceNode<ElkClass, ElkNamedIndividual> getInstanceNode(
			ElkNamedIndividual elkNamedIndividual)
			throws ElkFreshEntitiesException, ElkInconsistentOntologyException,
			ElkException {
		InstanceNode<ElkClass, ElkNamedIndividual> node = getInstanceTaxonomy()
				.getInstanceNode(elkNamedIndividual);
		if (node == null) {
			if (allowFreshEntities) {
				node = new FreshInstanceNode<ElkClass, ElkNamedIndividual>(
						elkNamedIndividual, getInstanceTaxonomy());
			} else {
				throw new ElkFreshEntitiesException(elkNamedIndividual);
			}
		}
		return node;
	}

	/**
	 * Helper method to get a type node node from the taxonomy.
	 * 
	 * @param elkClass
	 * @return
	 * @throws ElkFreshEntitiesException
	 *             if the given {@link ElkClass} does not occur in the ontology
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 */
	protected TypeNode<ElkClass, ElkNamedIndividual> getTypeNode(
			ElkClass elkClass) throws ElkFreshEntitiesException,
			ElkInconsistentOntologyException, ElkException {
		TypeNode<ElkClass, ElkNamedIndividual> node = getInstanceTaxonomy()
				.getTypeNode(elkClass);
		if (node == null) {
			if (allowFreshEntities) {
				node = new FreshTypeNode<ElkClass, ElkNamedIndividual>(
						elkClass, getInstanceTaxonomy());
			} else {
				throw new ElkFreshEntitiesException(elkClass);
			}
		}
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
	 * @throws ElkFreshEntitiesException
	 *             if the given {@link ElkClass} does not occur in the ontology
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 */
	public Node<ElkClass> getClassNode(ElkClass elkClass)
			throws ElkFreshEntitiesException, ElkInconsistentOntologyException,
			ElkException {
		return getTaxonomyNode(elkClass);
	}

	/**
	 * Return the (direct or indirect) subclasses of the given
	 * {@link ElkClassExpression}. The method returns a set of Node<ElkClass>,
	 * each of which might represent multiple equivalent classes. In theory,
	 * this method does not require the whole taxonomy to be constructed, and
	 * the result does not provide (indirect) access to a taxonomy object.
	 * 
	 * @param classExpression
	 *            currently, only objects of type ElkClass are supported
	 * @param direct
	 *            if {@code true}, only direct subclasses are returned
	 * @throws ElkFreshEntitiesException
	 *             if the given {@link ElkClassExpression} contains entities
	 *             that do not occur in the ontology
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 */
	public Set<? extends Node<ElkClass>> getSubClasses(
			ElkClassExpression classExpression, boolean direct)
			throws ElkFreshEntitiesException, ElkInconsistentOntologyException,
			ElkException {
		if (classExpression instanceof ElkClass) {
			TaxonomyNode<ElkClass> node = getTaxonomyNode((ElkClass) classExpression);
			return (direct) ? node.getDirectSubNodes() : node.getAllSubNodes();
		} else { // TODO: complex class expressions currently not supported
			throw new UnsupportedOperationException(
					"ELK does not support retrieval of superclasses for unnamed class expressions.");
		}
	}

	/**
	 * Return the (direct or indirect) superclasses of the given
	 * {@link ElkClassExpression}. The method returns a set of Node<ElkClass>,
	 * each of which might represent multiple equivalent classes. In theory,
	 * this method does not require the whole taxonomy to be constructed, and
	 * the result does not provide (indirect) access to a taxonomy object.
	 * 
	 * @param classExpression
	 *            currently, only objects of type ElkClass are supported
	 * @param direct
	 *            if {@code true}, only direct superclasses are returned
	 * @throws ElkFreshEntitiesException
	 *             if the given {@link ElkClassExpression} contains entities
	 *             that do not occur in the ontology
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 */
	public Set<? extends Node<ElkClass>> getSuperClasses(
			ElkClassExpression classExpression, boolean direct)
			throws ElkFreshEntitiesException, ElkInconsistentOntologyException,
			ElkException {
		if (classExpression instanceof ElkClass) {
			TaxonomyNode<ElkClass> node = getTaxonomyNode((ElkClass) classExpression);
			return (direct) ? node.getDirectSuperNodes() : node
					.getAllSuperNodes();
		} else { // TODO: complex class expressions currently not supported
			throw new UnsupportedOperationException(
					"ELK does not support retrieval of superclasses for unnamed class expressions.");
		}
	}

	/**
	 * Return the (direct or indirect) instances of the given
	 * {@link ElkClassExpression}. The method returns a set of
	 * Node<ElkNamedIndividual>, each of which might represent multiple
	 * equivalent classes. In theory, this method does not require the whole
	 * taxonomy to be constructed, and the result does not provide (indirect)
	 * access to a taxonomy object.
	 * 
	 * @param classExpression
	 *            currently, only objects of type ElkClass are supported
	 * @param direct
	 *            if {@code true}, only direct subclasses are returned
	 * @throws ElkFreshEntitiesException
	 *             if the given {@link ElkClassExpression} contains entities
	 *             that do not occur in the ontology
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 */
	public Set<? extends Node<ElkNamedIndividual>> getInstances(
			ElkClassExpression classExpression, boolean direct)
			throws ElkFreshEntitiesException, ElkInconsistentOntologyException,
			ElkException {
		if (classExpression instanceof ElkClass) {
			TypeNode<ElkClass, ElkNamedIndividual> node = getTypeNode((ElkClass) classExpression);
			return direct ? node.getDirectInstanceNodes() : node
					.getAllInstanceNodes();
		} else { // TODO: complex class expressions currently not supported
			throw new UnsupportedOperationException(
					"ELK does not support retrieval of instances for unnamed class expressions.");
		}
	}

	/**
	 * Return the (direct or indirect) types of the given
	 * {@link ElkNamedIndividual}. The method returns a set of Node<ElkClass>,
	 * each of which might represent multiple equivalent classes. In theory,
	 * this method does not require the whole taxonomy to be constructed, and
	 * the result does not provide (indirect) access to a taxonomy object.
	 * 
	 * @param elkNamedIndividual
	 * @param direct
	 *            if true, only direct types are returned
	 * @throws ElkFreshEntitiesException
	 * @throws ElkInconsistentOntologyException
	 * @throws ElkException
	 */
	public Set<? extends Node<ElkClass>> getTypes(
			ElkNamedIndividual elkNamedIndividual, boolean direct)
			throws ElkFreshEntitiesException, ElkInconsistentOntologyException,
			ElkException {
		InstanceNode<ElkClass, ElkNamedIndividual> node = getInstanceNode(elkNamedIndividual);
		return direct ? node.getDirectTypeNodes() : node.getAllTypeNodes();
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
	 * @throws ElkFreshEntitiesException
	 * @throws ElkException
	 */
	public boolean isSatisfiable(ElkClassExpression classExpression)
			throws ElkFreshEntitiesException, ElkInconsistentOntologyException,
			ElkException {
		if (classExpression instanceof ElkClass) {
			Node<ElkClass> classNode = getClassNode((ElkClass) classExpression);
			return (!classNode.getMembers().contains(
					PredefinedElkClass.OWL_NOTHING));
		} else { // TODO: complex class expressions currently not supported
			throw new UnsupportedOperationException(
					"ELK does not support satisfiability checking for unnamed class expressions");
		}
	}

	public void writeTaxonomyToFile(File file) throws IOException,
			ElkInconsistentOntologyException, ElkException {
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Writing taxonomy to " + file);
		}
		Statistics.logOperationStart("Writing taxonomy", LOGGER_);
		TaxonomyPrinter.dumpClassTaxomomyToFile(this.getTaxonomy(),
				file.getPath(), true);
		Statistics.logOperationFinish("Writing taxonomy", LOGGER_);
	}

	// TODO: get rid of this
	// used only in tests
	@Override
	public OntologyIndex getOntologyIndex() throws ElkException {
		return super.getOntologyIndex();
	}

}
