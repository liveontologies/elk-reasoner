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

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.stages.AbstractReasonerState;
import org.semanticweb.elk.reasoner.stages.ReasonerStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.InstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.Node;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.TypeNode;
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
	protected final ComputationExecutor executor;
	/**
	 * Number of workers for concurrent jobs.
	 */
	protected final int workerNo;

	/**
	 * Should fresh entities in reasoner queries be accepted (configuration
	 * setting). If false, a FreshEntityException will be thrown when
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
		this.executor = new ComputationExecutor(workerNo, "elk-computation");
		this.workerNo = workerNo;
		this.progressMonitor = new DummyProgressMonitor();
		this.allowFreshEntities = true;
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
	 * Set if fresh entities should be allowed. Fresh entities are entities that
	 * occur as parameters of reasoner queries without occurring in the loaded
	 * ontology. If false, a {@link FreshEntitiesException} will be thrown in
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
		return this.executor;
	}

	@Override
	protected ReasonerStageExecutor getStageExecutor() {
		return stageExecutor;
	}

	@Override
	protected ProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	public void shutdown() {
		executor.shutdown();
	}

	/**
	 * Helper method to get a taxonomy node node from the taxonomy.
	 * 
	 * @param elkClass
	 * @return
	 * @throws FreshEntitiesException
	 *             if the given {@link ElkClass} does not occur in the ontology
	 * @throws InconsistentOntologyException
	 *             if the ontology is inconsistent
	 */
	protected TaxonomyNode<ElkClass> getTaxonomyNode(ElkClass elkClass)
			throws FreshEntitiesException, InconsistentOntologyException {
		TaxonomyNode<ElkClass> node = getTaxonomy().getNode(elkClass);
		if (node == null) {
			if (allowFreshEntities) {
				node = new FreshClassTypeNode(elkClass);
			} else {
				throw new FreshEntitiesException(elkClass);
			}
		}
		return node;
	}

	/**
	 * Helper method to get an instance node node from the taxonomy.
	 * 
	 * @param elkNamedIndividual
	 * @return
	 * @throws FreshEntitiesException
	 *             if the given {@link ElkNamedIndividual} does not occur in the
	 *             ontology
	 * @throws InconsistentOntologyException
	 *             if the ontology is inconsistent
	 */
	protected InstanceNode<ElkClass, ElkNamedIndividual> getInstanceNode(
			ElkNamedIndividual elkNamedIndividual)
			throws FreshEntitiesException, InconsistentOntologyException {
		InstanceNode<ElkClass, ElkNamedIndividual> node = getInstanceTaxonomy()
				.getInstanceNode(elkNamedIndividual);
		if (node == null) {
			if (allowFreshEntities) {
				node = new FreshClassInstanceNode(elkNamedIndividual);
			} else {
				throw new FreshEntitiesException(elkNamedIndividual);
			}
		}
		return node;
	}

	/**
	 * Helper method to get a type node node from the taxonomy.
	 * 
	 * @param elkClass
	 * @return
	 * @throws FreshEntitiesException
	 *             if the given {@link ElkClass} does not occur in the ontology
	 * @throws InconsistentOntologyException
	 *             if the ontology is inconsistent
	 */
	protected TypeNode<ElkClass, ElkNamedIndividual> getTypeNode(
			ElkClass elkClass) throws FreshEntitiesException,
			InconsistentOntologyException {
		TypeNode<ElkClass, ElkNamedIndividual> node = getInstanceTaxonomy()
				.getTypeNode(elkClass);
		if (node == null) {
			if (allowFreshEntities) {
				node = new FreshClassTypeNode(elkClass);
			} else {
				throw new FreshEntitiesException(elkClass);
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
	 * @throws FreshEntitiesException
	 *             if the given {@link ElkClass} does not occur in the ontology
	 * @throws InconsistentOntologyException
	 *             if the ontology is inconsistent
	 */
	public Node<ElkClass> getClassNode(ElkClass elkClass)
			throws FreshEntitiesException, InconsistentOntologyException {
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
	 *            if <tt>true</tt>, only direct subclasses are returned
	 * @throws FreshEntitiesException
	 *             if the given {@link ElkClassExpression} contains entities
	 *             that do not occur in the ontology
	 * @throws InconsistentOntologyException
	 *             if the ontology is inconsistent
	 */
	public Set<? extends Node<ElkClass>> getSubClasses(
			ElkClassExpression classExpression, boolean direct)
			throws FreshEntitiesException, InconsistentOntologyException {
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
	 *            if <tt>true</tt>, only direct superclasses are returned
	 * @throws FreshEntitiesException
	 *             if the given {@link ElkClassExpression} contains entities
	 *             that do not occur in the ontology
	 * @throws InconsistentOntologyException
	 *             if the ontology is inconsistent
	 */
	public Set<? extends Node<ElkClass>> getSuperClasses(
			ElkClassExpression classExpression, boolean direct)
			throws FreshEntitiesException, InconsistentOntologyException {
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
	 *            if <tt>true</tt>, only direct subclasses are returned
	 * @throws FreshEntitiesException
	 *             if the given {@link ElkClassExpression} contains entities
	 *             that do not occur in the ontology
	 * @throws InconsistentOntologyException
	 *             if the ontology is inconsistent
	 */
	public Set<? extends Node<ElkNamedIndividual>> getInstances(
			ElkClassExpression classExpression, boolean direct)
			throws FreshEntitiesException, InconsistentOntologyException {
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
	 * @throws FreshEntitiesException
	 * @throws InconsistentOntologyException
	 */
	public Set<? extends Node<ElkClass>> getTypes(
			ElkNamedIndividual elkNamedIndividual, boolean direct)
			throws FreshEntitiesException, InconsistentOntologyException {
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
					"ELK does not support satisfiability checking for unnamed class expressions");
		}
	}

	/**
	 * Auxiliary class for representing taxonomy nodes for fresh entities.
	 * 
	 * @author Markus Kroetzsch
	 * 
	 * @param <T>
	 *            the type of members of the node
	 */
	protected abstract class FreshTaxonomyNode<T extends ElkObject> implements
			Node<T> {
		protected final T elkClass;
		protected final TypeNode<ElkClass, ElkNamedIndividual> bottomNode;
		protected final TypeNode<ElkClass, ElkNamedIndividual> topNode;
		protected final InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy;

		public FreshTaxonomyNode(T elkClass)
				throws InconsistentOntologyException {
			// Get bottom and top node *now*; cannot do this later as ontology
			// might change
			this.taxonomy = getInstanceTaxonomy();
			this.bottomNode = taxonomy
					.getTypeNode(PredefinedElkClass.OWL_NOTHING);
			this.topNode = taxonomy.getTypeNode(PredefinedElkClass.OWL_THING);
			this.elkClass = elkClass;
		}

		@Override
		public Set<T> getMembers() {
			return Collections.unmodifiableSet(Collections.singleton(elkClass));
		}

		@Override
		public T getCanonicalMember() {
			return elkClass;
		}

		public InstanceTaxonomy<ElkClass, ElkNamedIndividual> getTaxonomy() {
			return taxonomy;
		}
	}

	/**
	 * Class for representing type nodes for fresh classes.
	 * 
	 * @author Markus Kroetzsch
	 */
	protected class FreshClassTypeNode extends FreshTaxonomyNode<ElkClass>
			implements TypeNode<ElkClass, ElkNamedIndividual> {

		public FreshClassTypeNode(ElkClass elkClass)
				throws InconsistentOntologyException {
			super(elkClass);
		}

		@Override
		public Set<? extends InstanceNode<ElkClass, ElkNamedIndividual>> getDirectInstanceNodes() {
			Set<? extends InstanceNode<ElkClass, ElkNamedIndividual>> result = Collections
					.emptySet();
			return Collections.unmodifiableSet(result);
		}

		@Override
		public Set<? extends InstanceNode<ElkClass, ElkNamedIndividual>> getAllInstanceNodes() {
			return getDirectInstanceNodes();
		}

		@Override
		public Set<TypeNode<ElkClass, ElkNamedIndividual>> getDirectSuperNodes() {
			return Collections.unmodifiableSet(Collections.singleton(topNode));
		}

		@Override
		public Set<TypeNode<ElkClass, ElkNamedIndividual>> getAllSuperNodes() {
			return getDirectSuperNodes();
		}

		@Override
		public Set<TypeNode<ElkClass, ElkNamedIndividual>> getDirectSubNodes() {
			return Collections.unmodifiableSet(Collections
					.singleton(bottomNode));
		}

		@Override
		public Set<TypeNode<ElkClass, ElkNamedIndividual>> getAllSubNodes() {
			return getDirectSubNodes();
		}
	}

	/**
	 * Class for representing instance nodes for fresh named individuals.
	 * 
	 * @author Markus Kroetzsch
	 */
	protected class FreshClassInstanceNode extends
			FreshTaxonomyNode<ElkNamedIndividual> implements
			InstanceNode<ElkClass, ElkNamedIndividual> {

		public FreshClassInstanceNode(ElkNamedIndividual elkClass)
				throws InconsistentOntologyException {
			super(elkClass);
		}

		@Override
		public Set<? extends TypeNode<ElkClass, ElkNamedIndividual>> getDirectTypeNodes() {
			return Collections.unmodifiableSet(Collections.singleton(topNode));
		}

		@Override
		public Set<? extends TypeNode<ElkClass, ElkNamedIndividual>> getAllTypeNodes() {
			return getDirectTypeNodes();
		}
	}

	// used only in tests
	@Override
	protected OntologyIndex getOntologyIndex() {
		return super.getOntologyIndex();
	}

}
