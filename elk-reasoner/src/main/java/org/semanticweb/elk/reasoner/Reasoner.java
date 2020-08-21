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

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.reasoner.completeness.IncompleteResult;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.indexing.model.OntologyIndex;
import org.semanticweb.elk.reasoner.stages.AbstractReasonerState;
import org.semanticweb.elk.reasoner.stages.ReasonerStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.FreshInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.FreshTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.FreshTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyNodeUtils;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TypeNode;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentExecutor;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentExecutors;
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
	 * the executor used for concurrent tasks
	 */
	private final static ConcurrentExecutor EXECUTOR_ = ConcurrentExecutors
			.create("elk-reasoner");

	/**
	 * The progress monitor that is used for reporting progress.
	 */
	protected ProgressMonitor progressMonitor;
	/**
	 * The executor for various stages of the reasoner
	 */
	private final ReasonerStageExecutor stageExecutor_;
	/**
	 * The object that is notified and propagates the information about
	 * interruption.
	 */
	private final ReasonerInterrupter interrupter_;
	/**
	 * Number of workers for concurrent jobs.
	 */
	private int workerNo_;

	/**
	 * Should fresh entities in reasoner queries be accepted (configuration
	 * setting). If false, a {@link ElkFreshEntitiesException} will be thrown
	 * when encountering entities that did not occur in the ontology.
	 */
	protected boolean allowFreshEntities;

	/**
	 * Constructor. In most cases, Reasoners should be created by the
	 * {@link ReasonerFactory}.
	 */
	protected Reasoner(ElkObject.Factory elkFactory,
			final ReasonerInterrupter interrupter,
			ReasonerStageExecutor stageExecutor, ReasonerConfiguration config) {
		super(elkFactory, config);

		this.stageExecutor_ = stageExecutor;
		this.interrupter_ = interrupter;
		this.progressMonitor = new DummyProgressMonitor();
		this.allowFreshEntities = true;
		setConfigurationOptions(config);
		LOGGER_.info("ELK reasoner was created");
	}

	/**
	 * Set the progress monitor that will be used for reporting progress on all
	 * potentially long-running operations.
	 * 
	 * @param progressMonitor
	 */
	public synchronized void setProgressMonitor(
			ProgressMonitor progressMonitor) {
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
	protected synchronized int getNumberOfWorkers() {
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
		this.workerNo_ = config.getParameterAsInt(
				ReasonerConfiguration.NUM_OF_WORKING_THREADS);

		setAllowIncrementalMode(config.getParameterAsBoolean(
				ReasonerConfiguration.INCREMENTAL_MODE_ALLOWED));

	}

	@Override
	protected ConcurrentExecutor getProcessExecutor() {
		return EXECUTOR_;
	}

	@Override
	protected ReasonerStageExecutor getStageExecutor() {
		return stageExecutor_;
	}

	@Override
	protected ReasonerInterrupter getInterrupter() {
		return interrupter_;
	}

	@Override
	protected synchronized ProgressMonitor getProgressMonitor() {
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
		boolean success = true;
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
	protected IncompleteResult<? extends TaxonomyNode<ElkClass>> getTaxonomyNode(
			ElkClass elkClass) throws ElkException {
		return getTaxonomy().map(taxonomy -> {
			TaxonomyNode<ElkClass> node = taxonomy.getNode(elkClass);
			if (node != null) {
				return node;
			}
			// else
			if (allowFreshEntities) {
				return new FreshTaxonomyNode<ElkClass>(elkClass, taxonomy);
			}
			// else
			throw new ElkFreshEntitiesException(elkClass);
		});
	}

	/**
	 * Helper method to get an {@link InstanceNode} from the taxonomy.
	 * 
	 * @param elkNamedIndividual
	 * @return the {@link InstanceNode} for the given {@link ElkNamedIndividual}
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	protected IncompleteResult<? extends InstanceNode<ElkClass, ElkNamedIndividual>> getInstanceNode(
			ElkNamedIndividual elkNamedIndividual) throws ElkException {
		return getInstanceTaxonomy().map(tax -> {
			InstanceNode<ElkClass, ElkNamedIndividual> node = tax
					.getInstanceNode(elkNamedIndividual);
			if (node != null)
				return node;
			// else
			if (allowFreshEntities)
				return new FreshInstanceNode<ElkClass, ElkNamedIndividual>(
						elkNamedIndividual, tax);
			// else
			throw new ElkFreshEntitiesException(elkNamedIndividual);
		});
	}

	/**
	 * Helper method to get a {@link TypeNode} from the taxonomy.
	 * 
	 * @param elkClass
	 * @return the {@link TypeNode} for the given {@link ElkClass}
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	protected IncompleteResult<? extends TypeNode<ElkClass, ElkNamedIndividual>> getTypeNode(
			ElkClass elkClass) throws ElkException {
		return getInstanceTaxonomy().map(tax -> {
			final TypeNode<ElkClass, ElkNamedIndividual> node = tax
					.getNode(elkClass);
			if (node != null)
				return node;
			// else
			if (allowFreshEntities)
				return new FreshTypeNode<ElkClass, ElkNamedIndividual>(elkClass,
						tax);
			// else
			throw new ElkFreshEntitiesException(elkClass);
		});
	}

	/**
	 * Helper method to get a {@link TaxonomyNode} from the property taxonomy.
	 * 
	 * @param elkProperty
	 *            an {@link ElkObjectProperty} for which to find a
	 *            {@link TaxonomyNode}
	 * @return the {@link TaxonomyNode} for the given {@link ElkObjectProperty}
	 * 
	 */
	protected IncompleteResult<? extends TaxonomyNode<ElkObjectProperty>> getObjectPropertyTaxonomyNode(
			final ElkObjectProperty elkProperty) throws ElkException {
		return getObjectPropertyTaxonomy().map(tax -> {
			final TaxonomyNode<ElkObjectProperty> node = tax
					.getNode(elkProperty);
			if (node != null) {
				return node;
			}
			// else
			if (allowFreshEntities) {
				return new FreshTaxonomyNode<ElkObjectProperty>(elkProperty,
						tax);
			}
			// else
			throw new ElkFreshEntitiesException(elkProperty);
		});
	}

	/**
	 * Return the {@link TaxonomyNode} for the given {@link ElkObjectProperty}.
	 * Calling of this method may trigger the computation of the taxonomy, if it
	 * has not been done yet. If the input is an {@link ElkObjectProperty} that
	 * does not occur in the ontology and fresh entities are not allowed, a
	 * {@link ElkFreshEntitiesException} will be thrown.
	 * 
	 * @param property
	 *            the {@link ElkObjectProperty} for which to return the
	 *            {@link Node}
	 * @return the {@link TaxonomyNode} for the given {@link ElkObjectProperty}
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	public synchronized IncompleteResult<? extends TaxonomyNode<ElkObjectProperty>> getObjectPropertyNode(
			final ElkObjectProperty property) throws ElkException {
		return getObjectPropertyTaxonomyNode(property);
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
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	public synchronized IncompleteResult<? extends Node<ElkClass>> getEquivalentClasses(
			ElkClassExpression classExpression)
			throws ElkInconsistentOntologyException, ElkException {
		if (classExpression instanceof ElkClass) {
			return getTaxonomyNode((ElkClass) classExpression);
		}
		// else
		return queryEquivalentClasses(classExpression);
	}

	public synchronized IncompleteResult<? extends Node<ElkClass>> getEquivalentClassesQuitely(
			ElkClassExpression classExpression) throws ElkException {
		try {
			return getEquivalentClasses(classExpression);
		} catch (ElkInconsistentOntologyException e) {
			// all classes are equivalent
			return getTaxonomyQuietly().map(tax -> tax.getBottomNode());
		}
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
	public synchronized IncompleteResult<? extends Node<ElkClass>> getEquivalentClassesQuietly(
			ElkClassExpression classExpression) throws ElkException {
		try {
			return getEquivalentClasses(classExpression);
		} catch (final ElkInconsistentOntologyException e) {
			return getTaxonomyQuietly().map(Taxonomy::getBottomNode);
		}
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
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	public synchronized IncompleteResult<? extends Set<? extends Node<ElkClass>>> getSubClasses(
			ElkClassExpression classExpression, boolean direct)
			throws ElkInconsistentOntologyException, ElkException {
		if (classExpression instanceof ElkClass) {
			return getTaxonomyNode((ElkClass) classExpression)
					.map(node -> direct ? node.getDirectSubNodes()
							: node.getAllSubNodes());
		}
		// else
		IncompleteResult<? extends Set<? extends Node<ElkClass>>> incompleteNodes = queryDirectSubClasses(
				classExpression);
		if (direct) {
			return incompleteNodes;
		}
		// else all nodes

		final IncompleteResult<? extends Taxonomy<ElkClass>> incompleteTaxonomy = getTaxonomy();
		boolean isComplete = incompleteNodes.isComplete()
				&& incompleteTaxonomy.isComplete();

		Set<? extends Node<ElkClass>> nodes = incompleteNodes.getValue();
		return new IncompleteResult<>(
				TaxonomyNodeUtils.getAllReachable(Operations.map(nodes,
						new Operations.Transformation<Node<ElkClass>, TaxonomyNode<ElkClass>>() {

							@Override
							public TaxonomyNode<ElkClass> transform(
									final Node<ElkClass> node) {
								return incompleteTaxonomy.getValue()
										.getNode(node.getCanonicalMember());
							}

						}),
						new Operations.Functor<TaxonomyNode<ElkClass>, Set<? extends TaxonomyNode<ElkClass>>>() {

							@Override
							public Set<? extends TaxonomyNode<ElkClass>> apply(
									final TaxonomyNode<ElkClass> node) {
								return node.getDirectSubNodes();
							}

						}),
				isComplete);
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
	public synchronized IncompleteResult<? extends Set<? extends Node<ElkClass>>> getSubClassesQuietly(
			final ElkClassExpression classExpression, final boolean direct)
			throws ElkException {
		try {
			return getSubClasses(classExpression, direct);
		} catch (final ElkInconsistentOntologyException e) {
			// All classes are equivalent to each other, so also to owl:Nothing.
			return getTaxonomyQuietly().map(tax -> {
				TaxonomyNode<ElkClass> node = tax.getBottomNode();
				return direct ? node.getDirectSubNodes()
						: node.getAllSubNodes();
			});
		}
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
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	public synchronized IncompleteResult<? extends Set<? extends Node<ElkClass>>> getSuperClasses(
			ElkClassExpression classExpression, boolean direct)
			throws ElkInconsistentOntologyException, ElkException {
		if (classExpression instanceof ElkClass) {
			return getTaxonomyNode((ElkClass) classExpression)
					.map(n -> direct ? n.getDirectSuperNodes()
							: n.getAllSuperNodes());
		}
		// else
		IncompleteResult<? extends Set<? extends Node<ElkClass>>> incompleteSuperNodes = queryDirectSuperClasses(
				classExpression);
		if (direct) {
			return incompleteSuperNodes;
		}
		// else all nodes

		IncompleteResult<? extends Taxonomy<ElkClass>> incompleteTaxonomy = getTaxonomy();
		boolean isComplete = incompleteSuperNodes.isComplete()
				&& incompleteTaxonomy.isComplete();
		Set<? extends Node<ElkClass>> superNodes = incompleteSuperNodes
				.getValue();

		return new IncompleteResult<>(
				TaxonomyNodeUtils.getAllReachable(Operations.map(superNodes,
						new Operations.Transformation<Node<ElkClass>, TaxonomyNode<ElkClass>>() {

							@Override
							public TaxonomyNode<ElkClass> transform(
									final Node<ElkClass> node) {
								return incompleteTaxonomy.getValue()
										.getNode(node.getCanonicalMember());
							}

						}),
						new Operations.Functor<TaxonomyNode<ElkClass>, Set<? extends TaxonomyNode<ElkClass>>>() {

							@Override
							public Set<? extends TaxonomyNode<ElkClass>> apply(
									final TaxonomyNode<ElkClass> node) {
								return node.getDirectSuperNodes();
							}

						}),
				isComplete);

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
	public synchronized IncompleteResult<? extends Set<? extends Node<ElkClass>>> getSuperClassesQuietly(
			ElkClassExpression classExpression, boolean direct)
			throws ElkException {
		try {
			return getSuperClasses(classExpression, direct);
		} catch (final ElkInconsistentOntologyException e) {
			// All classes are equivalent to each other, so also to owl:Nothing.
			return getTaxonomyQuietly().map(
					tax -> direct ? tax.getBottomNode().getDirectSuperNodes()
							: tax.getBottomNode().getAllSuperNodes());
		}
	}

	/**
	 * Return the (direct or indirect) sub-properties of the given
	 * {@link ElkObjectProperty} as specified by the parameter. The method
	 * returns a set of {@link Node}s, each of which representing an equivalence
	 * class of sub-properties. Calling of this method may trigger the
	 * computation of the taxonomy, if it has not been done yet.
	 * 
	 * @param property
	 *            the {@link ElkObjectProperty} for which to return the
	 *            sub-property {@link Node}s
	 * @param direct
	 *            if {@code true}, only direct sub-properties should be returned
	 * @return the set of {@link Node}s for direct or indirect sub-properties of
	 *         the given {@link ElkObjectProperty} according to the specified
	 *         parameter
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	public synchronized IncompleteResult<? extends Set<? extends Node<ElkObjectProperty>>> getSubObjectProperties(
			final ElkObjectProperty property, final boolean direct)
			throws ElkException {
		return getObjectPropertyNode(property)
				.map(queryNode -> (direct) ? queryNode.getDirectSubNodes()
						: queryNode.getAllSubNodes());
	}

	/**
	 * Return the (direct or indirect) super-properties of the given
	 * {@link ElkObjectProperty} as specified by the parameter. The method
	 * returns a set of {@link Node}s, each of which representing an equivalence
	 * class of super-properties. Calling of this method may trigger the
	 * computation of the taxonomy, if it has not been done yet.
	 * 
	 * @param property
	 *            the {@link ElkObjectProperty} for which to return the
	 *            super-property {@link Node}s
	 * @param direct
	 *            if {@code true}, only direct super-properties are returned
	 * @return the set of {@link Node}s for direct or indirect super-properties
	 *         of the given {@link ElkObjectProperty} according to the specified
	 *         parameter
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	public synchronized IncompleteResult<? extends Set<? extends Node<ElkObjectProperty>>> getSuperObjectProperties(
			final ElkObjectProperty property, final boolean direct)
			throws ElkException {
		return getObjectPropertyNode(property)
				.map(queryNode -> direct ? queryNode.getDirectSuperNodes()
						: queryNode.getAllSuperNodes());
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
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	public synchronized IncompleteResult<? extends Set<? extends Node<ElkNamedIndividual>>> getInstances(
			ElkClassExpression classExpression, boolean direct)
			throws ElkInconsistentOntologyException, ElkException {

		if (classExpression instanceof ElkClass) {
			return getTypeNode((ElkClass) classExpression).map(
					queryNode -> direct ? queryNode.getDirectInstanceNodes()
							: queryNode.getAllInstanceNodes());
		}

		IncompleteResult<? extends Set<? extends Node<ElkNamedIndividual>>> incompleteInstances = queryDirectInstances(
				classExpression);

		if (direct) {
			return incompleteInstances;
		}
		// else all instances
		Set<? extends Node<ElkNamedIndividual>> instances = incompleteInstances
				.getValue();
		IncompleteResult<? extends InstanceTaxonomy<ElkClass, ElkNamedIndividual>> incompleteTaxonomy = getInstanceTaxonomy();
		InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = incompleteTaxonomy
				.getValue();

		IncompleteResult<? extends Set<? extends Node<ElkClass>>> incompleteSubNodes = queryDirectSubClasses(
				classExpression);
		Set<? extends Node<ElkClass>> subNodes = incompleteSubNodes.getValue();

		boolean isComplete = incompleteInstances.isComplete()
				&& incompleteTaxonomy.isComplete()
				&& incompleteSubNodes.isComplete();

		return new IncompleteResult<>(TaxonomyNodeUtils
				.collectFromAllReachable(Operations.map(subNodes,
						new Operations.Transformation<Node<ElkClass>, TypeNode<ElkClass, ElkNamedIndividual>>() {

							@Override
							public TypeNode<ElkClass, ElkNamedIndividual> transform(
									final Node<ElkClass> node) {
								return taxonomy
										.getNode(node.getCanonicalMember());
							}

						}), Operations.map(instances,
								new Operations.Transformation<Node<ElkNamedIndividual>, InstanceNode<ElkClass, ElkNamedIndividual>>() {

									@Override
									public InstanceNode<ElkClass, ElkNamedIndividual> transform(
											final Node<ElkNamedIndividual> node) {
										return taxonomy.getInstanceNode(
												node.getCanonicalMember());
									}

								}),
						new Operations.Functor<TypeNode<ElkClass, ElkNamedIndividual>, Set<? extends TypeNode<ElkClass, ElkNamedIndividual>>>() {

							@Override
							public Set<? extends TypeNode<ElkClass, ElkNamedIndividual>> apply(
									final TypeNode<ElkClass, ElkNamedIndividual> node) {
								return node.getDirectSubNodes();
							}

						},
						new Operations.Functor<TypeNode<ElkClass, ElkNamedIndividual>, Set<? extends InstanceNode<ElkClass, ElkNamedIndividual>>>() {

							@Override
							public Set<? extends InstanceNode<ElkClass, ElkNamedIndividual>> apply(
									final TypeNode<ElkClass, ElkNamedIndividual> node) {
								return node.getDirectInstanceNodes();
							}

						}),
				isComplete);
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
	public synchronized IncompleteResult<? extends Set<? extends Node<ElkNamedIndividual>>> getInstancesQuietly(
			ElkClassExpression classExpression, boolean direct)
			throws ElkException {
		try {
			return getInstances(classExpression, direct);
		} catch (final ElkInconsistentOntologyException e) {
			// All classes are equivalent to each other, so also to owl:Nothing.
			return getInstanceTaxonomyQuietly()
					.map(tax -> tax.getInstanceNodes());
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
	public synchronized IncompleteResult<? extends Set<? extends Node<ElkClass>>> getTypes(
			ElkNamedIndividual elkNamedIndividual, boolean direct)
			throws ElkException {
		return getInstanceNode(elkNamedIndividual)
				.map(node -> direct ? node.getDirectTypeNodes()
						: node.getAllTypeNodes());
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
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 *             if the result cannot be computed
	 */
	public synchronized IncompleteResult<? extends Boolean> isSatisfiable(
			ElkClassExpression classExpression)
			throws ElkInconsistentOntologyException, ElkException {

		if (classExpression instanceof ElkClass) {
			return getTaxonomyNode((ElkClass) classExpression)
					.map(queryNode -> !queryNode
							.contains(getElkFactory().getOwlNothing()));
		}
		// else
		return querySatisfiability(classExpression);
	}

	public synchronized IncompleteResult<? extends Boolean> isSatisfiableQuitely(
			ElkClassExpression classExpression)
			throws ElkInconsistentOntologyException, ElkException {
		try {
			return isSatisfiable(classExpression);
		} catch (ElkInconsistentOntologyException e) {
			return new IncompleteResult<Boolean>(false, true);
		}
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
	public synchronized IncompleteResult<? extends Boolean> isSatisfiableQuietly(
			final ElkClassExpression classExpression) throws ElkException {
		try {
			return isSatisfiable(classExpression);
		} catch (final ElkInconsistentOntologyException e) {
			// Any class is unsatisfiable.
			return new IncompleteResult<>(false, true);
		}
	}

}
