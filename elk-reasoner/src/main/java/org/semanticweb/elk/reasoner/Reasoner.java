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
import java.util.concurrent.TimeUnit;

import org.semanticweb.elk.loading.AbstractAxiomLoader;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.ElkLoadingException;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.indexing.hierarchy.OntologyIndex;
import org.semanticweb.elk.reasoner.stages.AbstractReasonerState;
import org.semanticweb.elk.reasoner.stages.ReasonerStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.AnonymousNode;
import org.semanticweb.elk.reasoner.taxonomy.ElkClassKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.FreshInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.FreshTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.FreshTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(Reasoner.class);

	/**
	 * The progress monitor that is used for reporting progress.
	 */
	protected ProgressMonitor progressMonitor;
	/**
	 * The executor for various stages of the reasoner
	 */
	private final ReasonerStageExecutor stageExecutor_;
	/**
	 * the executor used for concurrent tasks
	 */
	private final ComputationExecutor executor_;
	/**
	 * Number of workers for concurrent jobs.
	 */
	private int workerNo_;

	/**
	 * A factory for creating new {@link ElkObject}s; mainly use for answering
	 * complex queries
	 */
	private ElkObjectFactory elkFactory = new ElkObjectFactoryImpl();

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
	protected Reasoner(AxiomLoader axiomLoader,
			ReasonerStageExecutor stageExecutor, ReasonerConfiguration config) {
		super(axiomLoader);

		this.stageExecutor_ = stageExecutor;
		this.progressMonitor = new DummyProgressMonitor();
		this.allowFreshEntities = true;
		setConfigurationOptions(config);
		this.executor_ = new ComputationExecutor(workerNo_, "elk-reasoner", 1,
				TimeUnit.SECONDS);

		LOGGER_.info("ELK reasoner was created");
	}

	/**
	 * Set the progress monitor that will be used for reporting progress on all
	 * potentially long-running operations.
	 * 
	 * @param progressMonitor
	 */
	public synchronized void setProgressMonitor(ProgressMonitor progressMonitor) {
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
	public synchronized void setAllowFreshEntities(boolean allow) {
		allowFreshEntities = allow;
	}

	/**
	 * Get whether fresh entities are allowed. See
	 * {@link #setAllowFreshEntities(boolean)} for details.
	 * 
	 * @return {@code true} if fresh entities are allowed
	 */
	public synchronized boolean getAllowFreshEntities() {
		return allowFreshEntities;
	}

	@Override
	protected int getNumberOfWorkers() {
		return workerNo_;
	}

	/**
	 * Sets the number of working threads. Shouldn't be used during reasoning.
	 * 
	 * @param workerNo
	 */
	public synchronized void setNumberOfWorkers(int workerNo) {
		workerNo_ = workerNo;
	}

	/**
	 * This supposed to be the central place where the reasoner gets its
	 * configuration options
	 * 
	 * @param config
	 */
	public synchronized void setConfigurationOptions(
			ReasonerConfiguration config) {
		this.workerNo_ = config
				.getParameterAsInt(ReasonerConfiguration.NUM_OF_WORKING_THREADS);

		setAllowIncrementalMode(config
				.getParameterAsBoolean(ReasonerConfiguration.INCREMENTAL_MODE_ALLOWED));
		setAllowIncrementalTaxonomy(config
				.getParameterAsBoolean(ReasonerConfiguration.INCREMENTAL_TAXONOMY));

		if (executor_ != null) {// could be null during initialization
			executor_.setPoolSize(workerNo_);
		}
	}

	@Override
	protected ComputationExecutor getProcessExecutor() {
		return executor_;
	}

	@Override
	protected ReasonerStageExecutor getStageExecutor() {
		return stageExecutor_;
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
	public synchronized boolean shutdown(long timeout, TimeUnit unit)
			throws InterruptedException {
		boolean success = executor_.shutdown(timeout, unit);
		if (success) {
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
	public synchronized boolean shutdown() throws InterruptedException {
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
		if (node != null)
			return node;
		// else
		if (allowFreshEntities)
			return new FreshTaxonomyNode<ElkClass>(elkClass, getTaxonomy());
		// else
		throw new ElkFreshEntitiesException(elkClass);
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
		if (node != null)
			return node;
		// else
		if (allowFreshEntities)
			return new FreshInstanceNode<ElkClass, ElkNamedIndividual>(
					elkNamedIndividual, getInstanceTaxonomy());
		// else
		throw new ElkFreshEntitiesException(elkNamedIndividual);
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
				.getNode(elkClass);
		if (node != null)
			return node;
		// else
		if (allowFreshEntities)
			return new FreshTypeNode<ElkClass, ElkNamedIndividual>(elkClass,
					getInstanceTaxonomy());
		// else
		throw new ElkFreshEntitiesException(elkClass);
	}

	/**
	 * Return the {@link TaxonomyNode} for the given {@link ElkClassExpression}.
	 * Calling of this method may trigger the computation of the taxonomy, if it
	 * has not been done yet. If the input is an {@link ElkClass} that does not
	 * occur in the ontology and fresh entities are not allowed, a
	 * {@link ElkFreshEntitiesException} will be thrown.
	 * 
	 * @param classExpression
	 *            the {@link ElkClassExpression} for which to return the
	 *            {@link Node}
	 * @return the {@link TaxonomyNode} for the given {@link ElkClassExpression}
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	public synchronized TaxonomyNode<ElkClass> getClassNode(
			ElkClassExpression classExpression) throws ElkException {
		if (classExpression instanceof ElkClass) {
			return getTaxonomyNode((ElkClass) classExpression);
		}
		// else
		ElkClass queryClass = elkFactory.getClass(new ElkFullIri(
				OwlFunctionalStylePrinter.toString(classExpression)));
		ElkAxiom materializedQuery = elkFactory.getEquivalentClassesAxiom(
				queryClass, classExpression);
		return getQueryTaxonomyNode(queryClass, materializedQuery);
	}

	/**
	 * Return the {@code Node} containing equivalent classes of the given
	 * {@link ElkClassExpression}. Calling of this method may trigger the
	 * computation of the taxonomy, if it has not been done yet.
	 * 
	 * @param classExpression
	 *            the {@link ElkClassExpression} for which to return the
	 *            {@link Node}
	 * @return the set of {@link Node} whose members are {@link ElkClass}es
	 *         equivalent to the given {@link ElkClassExpression}
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	public synchronized Node<ElkClass> getEquivalentClasses(
			ElkClassExpression classExpression) throws ElkException {
		if (classExpression instanceof ElkClass) {
			return getTaxonomyNode((ElkClass) classExpression);
		}
		// else
		ElkClass queryClass = elkFactory.getClass(new ElkFullIri(
				OwlFunctionalStylePrinter.toString(classExpression)));
		ElkAxiom materializedQuery = elkFactory.getEquivalentClassesAxiom(
				queryClass, classExpression);
		Node<ElkClass> queryNode = getQueryTaxonomyNode(queryClass,
				materializedQuery);

		return new AnonymousNode<ElkClass>(queryClass, queryNode,
				queryNode.size(), ElkClassKeyProvider.INSTANCE);
	}

	/**
	 * Return the (direct or indirect) subclasses of the given
	 * {@link ElkClassExpression} as specified by the parameter. The method
	 * returns a set of {@link Node}s, each of which representing an equivalent
	 * class of subclasses. Calling of this method may trigger the computation
	 * of the taxonomy, if it has not been done yet.
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
	public synchronized Set<? extends Node<ElkClass>> getSubClasses(
			ElkClassExpression classExpression, boolean direct)
			throws ElkException {

		TaxonomyNode<ElkClass> queryNode = getClassNode(classExpression);

		return (direct) ? queryNode.getDirectSubNodes() : queryNode
				.getAllSubNodes();
	}

	/**
	 * Return the (direct or indirect) superclasses of the given
	 * {@link ElkClassExpression} as specified by the parameter. The method
	 * returns a set of {@link Node}s, each of which representing an equivalent
	 * class of superclasses. Calling of this method may trigger the computation
	 * of the taxonomy, if it has not been done yet.
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
	public synchronized Set<? extends Node<ElkClass>> getSuperClasses(
			ElkClassExpression classExpression, boolean direct)
			throws ElkException {

		TaxonomyNode<ElkClass> queryNode = getClassNode(classExpression);

		return (direct) ? queryNode.getDirectSuperNodes() : queryNode
				.getAllSuperNodes();
	}

	/**
	 * Return the (direct or indirect) instances of the given
	 * {@link ElkClassExpression} as specified by the parameter. The method
	 * returns a set of {@link Node}s, each of which representing an equivalent
	 * class of instances. Calling of this method may trigger the computation of
	 * the realization, if it has not been done yet.
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
	public synchronized Set<? extends Node<ElkNamedIndividual>> getInstances(
			ElkClassExpression classExpression, boolean direct)
			throws ElkException {

		TypeNode<ElkClass, ElkNamedIndividual> queryNode;
		if (classExpression instanceof ElkClass) {
			queryNode = getTypeNode((ElkClass) classExpression);
		} else {
			ElkClass queryClass = elkFactory.getClass(new ElkFullIri(
					OwlFunctionalStylePrinter.toString(classExpression)));
			ElkAxiom materializedQuery = elkFactory.getSubClassOfAxiom(
					classExpression, queryClass);
			queryNode = getQueryTypeNode(queryClass, materializedQuery);
		}

		return direct ? queryNode.getDirectInstanceNodes() : queryNode
				.getAllInstanceNodes();

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
	public synchronized Set<? extends Node<ElkClass>> getTypes(
			ElkNamedIndividual elkNamedIndividual, boolean direct)
			throws ElkException {
		InstanceNode<ElkClass, ElkNamedIndividual> node = getInstanceNode(elkNamedIndividual);
		return direct ? node.getDirectTypeNodes() : node.getAllTypeNodes();
	}

	/**
	 * Check if the given {@link ElkClassExpression} is satisfiable, that is, if
	 * it can possibly have instances. {@link ElkClassExpression}s are not
	 * satisfiable if they are equivalent to {@code owl:Nothing}. A satisfiable
	 * {@link ElkClassExpression} is also called consistent or coherent. Calling
	 * of this method may trigger the computation of the taxonomy, if it has not
	 * been done yet.
	 * 
	 * @param classExpression
	 *            the {@link ElkClassExpression} for which to check
	 *            satisfiability
	 * @return {@code true} if the given input is satisfiable
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	public synchronized boolean isSatisfiable(ElkClassExpression classExpression)
			throws ElkException {

		Node<ElkClass> queryNode;
		if (classExpression instanceof ElkClass) {
			queryNode = getClassNode(classExpression);
		} else {
			ElkClass queryClass = elkFactory.getClass(new ElkFullIri(
					OwlFunctionalStylePrinter.toString(classExpression)));
			ElkAxiom materializedQuery = elkFactory.getSubClassOfAxiom(
					queryClass, classExpression);
			queryNode = getQueryNode(queryClass, materializedQuery);
		}

		return !queryNode.contains(PredefinedElkClass.OWL_NOTHING);
	}

	/**
	 * @param materializedQuery
	 *            An {@link ElkAxiom} introduced for the query
	 * @param addition
	 *            {@code true} if the axiom should be added, {@code false} if it
	 *            should be removed
	 * @return an {@code AxiomLoader} that performs addition, or respectively,
	 *         removal of the given axiom
	 */
	static AxiomLoader getQueryLoader(final ElkAxiom materializedQuery,
			final boolean addition) {

		return new AbstractAxiomLoader() {

			boolean finished = false;

			@Override
			public void load(ElkAxiomProcessor axiomInserter,
					ElkAxiomProcessor axiomDeleter) throws ElkLoadingException {
				(addition ? axiomInserter : axiomDeleter)
						.visit(materializedQuery);
				if (LOGGER_.isTraceEnabled())
					LOGGER_.trace((addition ? "adding materialized quiery axiom: "
							: "removing materialized quiery axiom: ")
							+ OwlFunctionalStylePrinter
									.toString(materializedQuery));
				finished = true;
			}

			@Override
			public boolean isLoadingFinished() {
				return finished;
			}
		};

	}

	/**
	 * Computes a {@link Node} for the given {@link ElkClass} in the taxonomy
	 * resulted from adding the given {@link ElkAxiom}
	 * 
	 * @param queryClass
	 *            the {@link ElkClass} for which to return the node
	 * @param materializedQuery
	 *            the {@link ElkAxiom} that should be added to the ontology
	 *            before computing the {@link Node}
	 * @return the {@link Node} for the given {@link ElkClass} in the taxonomy
	 *         resulted from adding the given {@link ElkAxiom}
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	Node<ElkClass> getQueryNode(ElkClass queryClass,
			final ElkAxiom materializedQuery) throws ElkException {

		boolean oldIsAllowIncrementalMode = isAllowIncrementalMode();
		setAllowIncrementalMode(true);
		registerAxiomLoader(getQueryLoader(materializedQuery, true));
		try {
			return getClassNode(queryClass);
		} finally {
			registerAxiomLoader(getQueryLoader(materializedQuery, false));
			setAllowIncrementalMode(oldIsAllowIncrementalMode);
		}
	}

	/**
	 * Compute a {@link TypeNode} for the given {@link ElkClass} in the taxonomy
	 * resulted from adding the given {@link ElkAxiom}
	 * 
	 * @param queryClass
	 *            the {@link ElkClass} for which to return the node
	 * @param materializedQuery
	 *            the {@link ElkAxiom} that should be added to the ontology
	 *            before computing the {@link TypeNode}
	 * @return the {@link TypeNode} for the given {@link ElkClass} in the
	 *         taxonomy resulted from adding the given {@link ElkAxiom}
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	TypeNode<ElkClass, ElkNamedIndividual> getQueryTypeNode(
			ElkClass queryClass, final ElkAxiom materializedQuery)
			throws ElkException {

		boolean oldIsAllowIncrementalMode = isAllowIncrementalMode();
		setAllowIncrementalMode(true);
		registerAxiomLoader(getQueryLoader(materializedQuery, true));
		try {
			return getTypeNode(queryClass);
		} finally {
			registerAxiomLoader(getQueryLoader(materializedQuery, false));
			setAllowIncrementalMode(oldIsAllowIncrementalMode);
		}
	}

	/**
	 * Compute a {@link TaxonomyNode} for the given {@link ElkClass} in the
	 * taxonomy resulted from adding the given {@link ElkAxiom}
	 * 
	 * @param queryClass
	 *            the {@link ElkClass} for which to return the node
	 * @param materializedQuery
	 *            the {@link ElkAxiom} that should be added to the ontology
	 *            before computing the {@link TaxonomyNode}
	 * @return the {@link TaxonomyNode} for the given {@link ElkClass} in the
	 *         taxonomy resulted from adding the given {@link ElkAxiom}
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	TaxonomyNode<ElkClass> getQueryTaxonomyNode(ElkClass queryClass,
			final ElkAxiom materializedQuery) throws ElkException {

		boolean oldIsAllowIncrementalMode = isAllowIncrementalMode();
		setAllowIncrementalMode(true);
		registerAxiomLoader(getQueryLoader(materializedQuery, true));
		try {
			return getTaxonomyNode(queryClass);
		} finally {
			registerAxiomLoader(getQueryLoader(materializedQuery, false));
			setAllowIncrementalMode(oldIsAllowIncrementalMode);
		}
	}

}
