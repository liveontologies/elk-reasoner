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
import org.semanticweb.elk.reasoner.taxonomy.model.FreshInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.FreshTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.FreshTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;

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
	 * setting). If false, a {@link ElkFreshEntitiesException} will be thrown
	 * when encountering entities that did not occur in the ontology.
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
	 * Get whether fresh entities are allowed. See
	 * {@link #setAllowFreshEntities(boolean)} for details.
	 * 
	 * @return {@code true} if fresh entities are allowed
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
		reset();
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
	 * Helper method to get a {@link TaxonomyNode} from the taxonomy.
	 * 
	 * @param elkClass
	 *            an {@link ElkClass} for which to find a {@link TaxonomyNode}
	 * @return the {@link TaxonomyNode} for the given {@link ElkClass}
	 * 
	 */
	protected TaxonomyNode<ElkClass> getTaxonomyNode(ElkClass elkClass)
			throws ElkException {
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
	 * Helper method to get an {@link InstanceNode} from the taxonomy.
	 * 
	 * @param elkNamedIndividual
	 * @return the {@link InstanceNode} for the given {@link ElkNamedIndividual}
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	protected InstanceNode<ElkClass, ElkNamedIndividual> getInstanceNode(
			ElkNamedIndividual elkNamedIndividual) throws ElkException {
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
	 * Helper method to get a {@link TypeNode} from the taxonomy.
	 * 
	 * @param elkClass
	 * @return the {@link TypeNode} for the given {@link ElkClass}
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	protected TypeNode<ElkClass, ElkNamedIndividual> getTypeNode(
			ElkClass elkClass) throws ElkException {
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
	 * Get the {@link Node} for the given {@link ElkClass}. Calling of this
	 * method may trigger the computation of the taxonomy, if it has not been
	 * done yet. If the given {@link ElkClass} does not occur in the ontology
	 * and fresh entities are not allowed, a {@link ElkFreshEntitiesException}
	 * will be thrown.
	 * 
	 * @param elkClass
	 *            the {@link ElkClass} for which to return the {@link Node}
	 * @return the class node for the given {@link ElkClass}
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	public Node<ElkClass> getClassNode(ElkClass elkClass) throws ElkException {
		return getTaxonomyNode(elkClass);
	}

	/**
	 * Return the (direct or indirect) subclasses of the given
	 * {@link ElkClassExpression} as specified by the parameter. Currently, only
	 * instances of {@link ElkClass} are supported. If called with other
	 * objects, an {@link ElkUnsupportedReasoningTaskException} will be thrown.
	 * The method returns a set of {@link Node}s, each of which representing an
	 * equivalent class of subclasses. Calling of this method may trigger the
	 * computation of the taxonomy, if it has not been done yet.
	 * 
	 * @param classExpression
	 *            the {@link ElkClassExpression} for which to return the
	 *            subclass {@link Node}s
	 * @param direct
	 *            if {@code true}, only direct subclasses should be returned
	 * @return the set of {@link Node}s for direct or indirect subclasses of the
	 *         given {@link ElkClassExpression} according to the specified
	 *         parameter
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	public Set<? extends Node<ElkClass>> getSubClasses(
			ElkClassExpression classExpression, boolean direct)
			throws ElkException {
		if (classExpression instanceof ElkClass) {
			TaxonomyNode<ElkClass> node = getTaxonomyNode((ElkClass) classExpression);
			return (direct) ? node.getDirectSubNodes() : node.getAllSubNodes();
		} else { // TODO: complex class expressions currently not supported
			throw new ElkUnsupportedReasoningTaskException(
					"ELK does not support computation of subclasses for complex class expressions.");
		}
	}

	/**
	 * Return the (direct or indirect) superclasses of the given
	 * {@link ElkClassExpression} as specified by the parameter. Currently, only
	 * instances of {@link ElkClass} are supported. If called with other
	 * objects, an {@link ElkUnsupportedReasoningTaskException} will be thrown.
	 * The method returns a set of {@link Node}s, each of which representing an
	 * equivalent class of superclasses. Calling of this method may trigger the
	 * computation of the taxonomy, if it has not been done yet.
	 * 
	 * @param classExpression
	 *            the {@link ElkClassExpression} for which to return the
	 *            superclass {@link Node}s
	 * @param direct
	 *            if {@code true}, only direct superclasses are returned
	 * @return the set of {@link Node}s for direct or indirect superclasses of
	 *         the given {@link ElkClassExpression} according to the specified
	 *         parameter
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	public Set<? extends Node<ElkClass>> getSuperClasses(
			ElkClassExpression classExpression, boolean direct)
			throws ElkException {
		if (classExpression instanceof ElkClass) {
			TaxonomyNode<ElkClass> node = getTaxonomyNode((ElkClass) classExpression);
			return (direct) ? node.getDirectSuperNodes() : node
					.getAllSuperNodes();
		} else { // TODO: complex class expressions currently not supported
			throw new ElkUnsupportedReasoningTaskException(
					"ELK does not support computation of superclasses for complex class expressions.");
		}
	}

	/**
	 * Return the (direct or indirect) instances of the given
	 * {@link ElkClassExpression} as specified by the parameter. Currently, only
	 * instances of {@link ElkClass} are supported. If called with other
	 * objects, an {@link ElkUnsupportedReasoningTaskException} will be thrown.
	 * The method returns a set of {@link Node}s, each of which representing an
	 * equivalent class of instances. Calling of this method may trigger the
	 * computation of the realization, if it has not been done yet.
	 * 
	 * @param classExpression
	 *            the {@link ElkClassExpression} for which to return the
	 *            instances {@link Node}s
	 * @param direct
	 *            if {@code true}, only direct instances are returned
	 * @return the set of {@link Node}s for direct or indirect instances of the
	 *         given {@link ElkClassExpression} according to the specified
	 *         parameter
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	public Set<? extends Node<ElkNamedIndividual>> getInstances(
			ElkClassExpression classExpression, boolean direct)
			throws ElkException {
		if (classExpression instanceof ElkClass) {
			TypeNode<ElkClass, ElkNamedIndividual> node = getTypeNode((ElkClass) classExpression);
			return direct ? node.getDirectInstanceNodes() : node
					.getAllInstanceNodes();
		} else { // TODO: complex class expressions currently not supported
			throw new ElkUnsupportedReasoningTaskException(
					"ELK does not support retrieval of instances for unnamed class expressions.");
		}
	}

	/**
	 * Return the (direct or indirect) types of the given
	 * {@link ElkNamedIndividual}. The method returns a set of {@link Node}s,
	 * each of which representing an equivalent class of types. Calling of this
	 * method may trigger the computation of the realization, if it has not been
	 * done yet.
	 * 
	 * @param elkNamedIndividual
	 *            the {@link ElkNamedIndividual} for which to return the types
	 *            {@link Node}s
	 * @param direct
	 *            if {@code true}, only direct types are returned
	 * @return the set of {@link Node}s for the direct or indirect types of the
	 *         given {@link ElkNamedIndividual} according to the specified
	 *         parameter
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	public Set<? extends Node<ElkClass>> getTypes(
			ElkNamedIndividual elkNamedIndividual, boolean direct)
			throws ElkException {
		InstanceNode<ElkClass, ElkNamedIndividual> node = getInstanceNode(elkNamedIndividual);
		return direct ? node.getDirectTypeNodes() : node.getAllTypeNodes();
	}

	/**
	 * Check if the given {@link ElkClassExpression} is satisfiable, that is, if
	 * it can possibly have instances. Currently, only instances of
	 * {@link ElkClass} are supported. If called with other objects, an
	 * {@link ElkUnsupportedReasoningTaskException} will be thrown.
	 * {@link ElkClassExpression}s are not satisfiable if they are equivalent to
	 * {@code owl:Nothing}. A satisfiable {@link ElkClassExpression} is also
	 * called consistent or coherent. Calling of this method may trigger the
	 * computation of the taxonomy, if it has not been done yet.
	 * 
	 * @param classExpression
	 *            the {@link ElkClassExpression} for which to check
	 *            satisfiability
	 * @return {@code true} if the given input is satisfiable
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	public boolean isSatisfiable(ElkClassExpression classExpression)
			throws ElkException {
		if (classExpression instanceof ElkClass) {
			Node<ElkClass> classNode = getClassNode((ElkClass) classExpression);
			return (!classNode.getMembers().contains(
					PredefinedElkClass.OWL_NOTHING));
		} else { // TODO: complex class expressions currently not supported
			throw new ElkUnsupportedReasoningTaskException(
					"ELK does not support satisfiability checking for unnamed class expressions");
		}
	}

}
