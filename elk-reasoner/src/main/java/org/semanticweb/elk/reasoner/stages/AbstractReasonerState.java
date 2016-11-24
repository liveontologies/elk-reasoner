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
package org.semanticweb.elk.reasoner.stages;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.ComposedAxiomLoader;
import org.semanticweb.elk.loading.QueryLoader;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerInterrupter;
import org.semanticweb.elk.reasoner.consistency.ConsistencyCheckingState;
import org.semanticweb.elk.reasoner.indexing.classes.DifferentialIndex;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverterImpl;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverterImpl;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OntologyIndex;
import org.semanticweb.elk.reasoner.query.QueryNode;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateFactory;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.SaturationConclusionBaseFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DerivedClassConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.taxonomy.ConcurrentClassTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.ConcurrentInstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.ElkClassKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.ElkIndividualKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.ElkObjectPropertyKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.OrphanInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.OrphanTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.OrphanTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.SingletoneInstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.SingletoneTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNodeFactory;
import org.semanticweb.elk.reasoner.tracing.TraceState;
import org.semanticweb.elk.reasoner.tracing.TracingInferenceSet;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The execution state of the reasoner containing information about which
 * reasoning stages have been completed and holding the results of these
 * reasoning stages, such as the consistency status of the ontology, class, or
 * instance taxonomy.
 * 
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 */
public abstract class AbstractReasonerState {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractReasonerState.class);

	/**
	 * The factory for creating auxiliary ElkObjects
	 */
	private final ElkObject.Factory elkFactory_;

	final SaturationState<? extends Context> saturationState;

	/**
	 * Accumulated statistics regarding produced conclusions and rule
	 * applications. Stored here because more than one stage can apply inference
	 * rules (e.g. during incremental reasoning).
	 * 
	 * TODO Better to hide this behind a common interface? Then we can uniformly
	 * maintain aggregated stats of different kinds produced by different
	 * computations across multiple stages
	 */
	final SaturationStatistics ruleAndConclusionStats;
	/**
	 * the (differential) index for loading of axioms and changes
	 */
	final DifferentialIndex ontologyIndex;
	/**
	 * Manages information about property hierarchy computation.
	 */
	final PropertyHierarchyCompositionState propertyHierarchyCompositionState_;
	/**
	 * Stores (partial) information about consistency checking computation
	 */
	final ConsistencyCheckingState consistencyCheckingState;
	/**
	 * Taxonomy state that stores (partial) classification
	 */
	final ClassTaxonomyState classTaxonomyState;
	/**
	 * Taxonomy state that stores property hierarchy
	 */
	final ObjectPropertyTaxonomyState objectPropertyTaxonomyState;
	/**
	 * Stores information about queried class expressions.
	 */
	final ClassExpressionQueryState classExpressionQueryState_;
	/**
	 * Defines reasoning stages and dependencies between them
	 */
	final ReasonerStageManager stageManager;
	/**
	 * Taxonomy that stores (partial) classification and (partial) realization
	 * of individuals
	 */
	final InstanceTaxonomyState instanceTaxonomyState;
	/**
	 * The source where axioms and changes in ontology can be loaded
	 */
	private AxiomLoader axiomLoader_ = null;
	/**
	 * Indexes the registered queries.
	 */
	private QueryLoader queryLoader_ = null;
	/**
	 * if {@code true}, reasoning will be done incrementally whenever possible
	 */
	private boolean allowIncrementalMode_ = true;

	/**
	 * if the property hierarchy correspond to the loading axioms
	 */
	boolean propertyHierarchyUpToDate_ = true;

	/**
	 * Keeps relevant information about tracing
	 */
	private final TraceState traceState_;

	/**
	 * creates conclusions for tracing
	 */
	private final SaturationConclusion.Factory factory_ = new SaturationConclusionBaseFactory();

	private final ElkPolarityExpressionConverter expressionConverter_;

	private final ElkSubObjectPropertyExpressionVisitor<ModifiableIndexedPropertyChain> subPropertyConverter_;

	protected AbstractReasonerState(ElkObject.Factory elkFactory) {
		final ClassTaxonomyState.Builder classTaxonomyStateBuilder = new ClassTaxonomyState.Builder();
		final InstanceTaxonomyState.Builder instanceTaxonomyStateBuilder = new InstanceTaxonomyState.Builder();
		this.elkFactory_ = elkFactory;
		this.ontologyIndex = new DifferentialIndex(elkFactory,
				Arrays.asList(classTaxonomyStateBuilder.getOntologyListener(),
						instanceTaxonomyStateBuilder.getOntologyListener()));
		this.propertyHierarchyCompositionState_ = new PropertyHierarchyCompositionState();
		this.saturationState = SaturationStateFactory
				.createSaturationState(ontologyIndex);
		this.consistencyCheckingState = ConsistencyCheckingState
				.create(saturationState, propertyHierarchyCompositionState_);
		this.classTaxonomyState = classTaxonomyStateBuilder
				.build(saturationState, ontologyIndex, elkFactory);
		this.instanceTaxonomyState = instanceTaxonomyStateBuilder
				.build(saturationState, ontologyIndex, elkFactory);
		this.objectPropertyTaxonomyState = new ObjectPropertyTaxonomyState(
				elkFactory);
		this.ruleAndConclusionStats = new SaturationStatistics();
		this.stageManager = new ReasonerStageManager(this);
		this.expressionConverter_ = new ElkPolarityExpressionConverterImpl(
				elkFactory, ontologyIndex);
		this.subPropertyConverter_ = new ElkAxiomConverterImpl(elkFactory,
				ontologyIndex);
		this.traceState_ = new TraceState(saturationState,
				propertyHierarchyCompositionState_, elkFactory, ontologyIndex);
		this.classExpressionQueryState_ = new ClassExpressionQueryState(
				saturationState, elkFactory, ontologyIndex, factory_);
	}

	public ElkObject.Factory getElkFactory() {
		return elkFactory_;
	}

	protected void complete(ReasonerStage stage) throws ElkException {
		getStageExecutor().complete(stage);
	}

	public synchronized void setAllowIncrementalMode(boolean allow) {
		if (allowIncrementalMode_ == allow)
			return;
		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.debug("Incremental mode is "
					+ (allow ? "allowed" : "disallowed"));
		}
		allowIncrementalMode_ = allow;

		if (!allow) {
			setNonIncrementalMode();
		}

	}

	public synchronized boolean isAllowIncrementalMode() {
		return allowIncrementalMode_;
	}

	public synchronized boolean isIncrementalMode() {
		return ontologyIndex.isIncrementalMode();
	}

	void setNonIncrementalMode() {
		if (!isIncrementalMode()) {
			return;
		}
		ontologyIndex.setIncrementalMode(false);
		// delete existing taxonomies as they cannot be updated incrementally
		resetTaxonomy();
		resetInstanceTaxonomy();
		resetObjectPropertyTaxonomy();
	}

	boolean trySetIncrementalMode() {
		if (!allowIncrementalMode_) {
			// switching to incremental mode not allowed
			return false;
		}
		// else
		ontologyIndex.setIncrementalMode(true);
		return true;

	}

	/**
	 * Reset the property saturation stage and all subsequent stages
	 */
	public synchronized void resetPropertySaturation() {
		LOGGER_.trace("Reset property saturation");
		stageManager.propertyInitializationStage.invalidateRecursive();
	}

	public synchronized void resetTaxonomy() {
		LOGGER_.trace("Reset class taxonomy");
		// force non-incremental taxonomy computation
		classTaxonomyState.getWriter().clearTaxonomy();
		stageManager.classTaxonomyComputationStage.invalidateRecursive();
	}

	public synchronized void resetInstanceTaxonomy() {
		LOGGER_.trace("Reset instance taxonomy");
		// force non-incremental taxonomy computation
		instanceTaxonomyState.getWriter().clearTaxonomy();
		stageManager.instanceTaxonomyComputationStage.invalidateRecursive();
	}

	public synchronized void resetObjectPropertyTaxonomy() {
		LOGGER_.trace("Reset object property taxonomy");
		// force non-incremental taxonomy computation
		stageManager.objectPropertyTaxonomyComputationStage
				.invalidateRecursive();
	}

	public synchronized void registerAxiomLoader(
			final AxiomLoader.Factory axiomLoaderFactory) {
		LOGGER_.trace("Registering new axiom loader");

		final AxiomLoader newAxiomLoader = axiomLoaderFactory
				.getAxiomLoader(getInterrupter());

		if (axiomLoader_ == null || axiomLoader_.isLoadingFinished()) {
			axiomLoader_ = newAxiomLoader;
		} else {
			axiomLoader_ = new ComposedAxiomLoader(axiomLoader_,
					newAxiomLoader);
		}
	}

	/**
	 * @return the {@link AxiomLoader} currently registered for loading of
	 *         axioms or {@code null} if no loader is registered
	 */
	public AxiomLoader getAxiomLoader() {
		return this.axiomLoader_;
	}

	/**
	 * @return the {@link QueryLoader} currently registered for loading of
	 *         queries or {@code null} if no loader is registered
	 */
	public QueryLoader getQueryLoader() {
		return queryLoader_;
	}

	/**
	 * @return {@code true} if there is no input pending loading, {@code false}
	 *         otherwise.
	 */
	protected synchronized boolean isLoadingFinished() {
		return (axiomLoader_ == null || axiomLoader_.isLoadingFinished())
				&& (queryLoader_ == null || queryLoader_.isLoadingFinished());
	}

	/**
	 * Flushes index, if needed, and completes loading if there is new input.
	 * 
	 * @throws ElkException
	 */
	public synchronized void ensureLoading() throws ElkException {

		if (!isLoadingFinished()) {
			if (isIncrementalMode()) {
				if (!stageManager.incrementalAdditionStage.isCompleted()) {
					complete(stageManager.incrementalAdditionStage);
				}
			} else {
				if (!stageManager.contextInitializationStage.isCompleted()) {
					complete(stageManager.contextInitializationStage);
				}
			}
			LOGGER_.trace("Reset axiom loading");
			stageManager.inputLoadingStage.invalidateRecursive();
			// Invalidate stages at the beginnings of the dependency chains.
			stageManager.contextInitializationStage.invalidateRecursive();
			stageManager.incrementalCompletionStage.invalidateRecursive();
			complete(stageManager.inputLoadingStage);
		}

	}

	/**
	 * @return the maximal number of workers that can be used for running
	 *         concurrent reasoning tasks
	 */
	protected abstract int getNumberOfWorkers();

	/**
	 * @return the {@link ReasonerStageExecutor} that is used for executing the
	 *         stages of the reasoner.
	 */
	protected abstract ReasonerStageExecutor getStageExecutor();

	/**
	 * @return The object that is notified and propagates the information about
	 *         interruption.
	 */
	protected abstract ReasonerInterrupter getInterrupter();

	/**
	 * @return the {@link ConcurrentExecutor} that is used for execution of
	 *         reasoning processes
	 */
	protected abstract ConcurrentExecutor getProcessExecutor();

	/**
	 * @return the {@link ProgressMonitor} that is used for reporting progress
	 *         on all potentially long-running operations.
	 */
	protected abstract ProgressMonitor getProgressMonitor();

	protected ModifiableOntologyIndex getModifiableOntologyIndex() {
		return this.ontologyIndex;
	}

	/**
	 * interrupts running reasoning stages
	 */
	public void interrupt() {
		LOGGER_.debug("Interrupt requested");
		getInterrupter().interrupt();
	}

	/**
	 * If interrupted, clears the interruption status and throws
	 * ElkInterruptedException.
	 * <p>
	 * This method should be called only from inside of the reasoner. Also it
	 * must be called from the same thread as the one on which the reasoning
	 * methods run. Otherwise it blocks until the reasoning method exits.
	 * 
	 * @throws ElkInterruptedException
	 *             if interrupted
	 */
	public synchronized void checkInterrupt() throws ElkInterruptedException {
		getInterrupter().checkInterrupt();
	}

	/**
	 * Check consistency of the current ontology, if this has not been done yet.
	 * 
	 * @return {@code true} if the ontology is inconsistent, that is,
	 *         unsatisfiable.
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public synchronized boolean isInconsistent() throws ElkException {

		ruleAndConclusionStats.reset();
		ensureLoading();

		if (isIncrementalMode() && !saturationState.getContexts().isEmpty()) {
			LOGGER_.trace("Consistency checking [incremental]");
			complete(stageManager.incrementalConsistencyCheckingStage);
		} else {
			LOGGER_.trace("Consistency checking [non-incremental]");
			setNonIncrementalMode();
			complete(stageManager.consistencyCheckingStage);
		}

		return consistencyCheckingState.isInconsistent();
	}

	/**
	 * Compute the inferred taxonomy of the named classes for the given ontology
	 * if it has not been done yet.
	 * 
	 * @return the class taxonomy implied by the current ontology
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public synchronized Taxonomy<ElkClass> getTaxonomy()
			throws ElkInconsistentOntologyException, ElkException {

		ruleAndConclusionStats.reset();

		if (isInconsistent())
			throw new ElkInconsistentOntologyException();

		if (isIncrementalMode() && classTaxonomyState.getTaxonomy() != null) {
			LOGGER_.trace("Taxonomy computation [incremental]");
			complete(stageManager.incrementalClassTaxonomyComputationStage);
		} else {
			LOGGER_.trace("Taxonomy computation [non-incremental]");
			setNonIncrementalMode();
			complete(stageManager.classTaxonomyComputationStage);
		}

		return classTaxonomyState.getTaxonomy();
	}

	/**
	 * Compute the inferred taxonomy of the named classes for the given ontology
	 * if it has not been done yet.
	 * 
	 * @return the class taxonomy implied by the current ontology
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public synchronized Taxonomy<ElkClass> getTaxonomyQuietly()
			throws ElkException {
		Taxonomy<ElkClass> result;

		try {
			result = getTaxonomy();
		} catch (ElkInconsistentOntologyException e) {
			LOGGER_.debug("Ontology is inconsistent");

			result = new SingletoneTaxonomy<ElkClass, OrphanTaxonomyNode<ElkClass>>(
					ElkClassKeyProvider.INSTANCE, getAllClasses(),
					new TaxonomyNodeFactory<ElkClass, OrphanTaxonomyNode<ElkClass>, Taxonomy<ElkClass>>() {
						@Override
						public OrphanTaxonomyNode<ElkClass> createNode(
								final Iterable<? extends ElkClass> members,
								final int size,
								final Taxonomy<ElkClass> taxonomy) {
							return new OrphanTaxonomyNode<ElkClass>(members,
									size, elkFactory_.getOwlNothing(),
									taxonomy);
						}
					});
		}

		return result;
	}

	/**
	 * Compute the inferred taxonomy of the named classes with instances if this
	 * has not been done yet.
	 * 
	 * @return the instance taxonomy implied by the current ontology
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public synchronized InstanceTaxonomy<ElkClass, ElkNamedIndividual> getInstanceTaxonomy()
			throws ElkException {

		ruleAndConclusionStats.reset();

		if (isInconsistent())
			throw new ElkInconsistentOntologyException();

		if (isIncrementalMode()
				&& instanceTaxonomyState.getTaxonomy() != null) {
			LOGGER_.trace("Instance taxonomy computation [incremental]");
			complete(stageManager.incrementalInstanceTaxonomyComputationStage);
		} else {
			LOGGER_.trace("Instance taxonomy computation [non-incremental]");
			setNonIncrementalMode();
			complete(stageManager.instanceTaxonomyComputationStage);
		}

		return instanceTaxonomyState.getTaxonomy();
	}

	/**
	 * Compute the inferred taxonomy of the named classes with instances if this
	 * has not been done yet.
	 * 
	 * @return the instance taxonomy implied by the current ontology
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public synchronized InstanceTaxonomy<ElkClass, ElkNamedIndividual> getInstanceTaxonomyQuietly()
			throws ElkException {

		InstanceTaxonomy<ElkClass, ElkNamedIndividual> result;

		try {
			result = getInstanceTaxonomy();
		} catch (ElkInconsistentOntologyException e) {
			LOGGER_.debug("Ontology is inconsistent");
			result = new SingletoneInstanceTaxonomy<ElkClass, ElkNamedIndividual, OrphanTypeNode<ElkClass, ElkNamedIndividual>>(
					ElkClassKeyProvider.INSTANCE, getAllClasses(),
					new TaxonomyNodeFactory<ElkClass, OrphanTypeNode<ElkClass, ElkNamedIndividual>, Taxonomy<ElkClass>>() {
						@Override
						public OrphanTypeNode<ElkClass, ElkNamedIndividual> createNode(
								final Iterable<? extends ElkClass> members,
								final int size,
								final Taxonomy<ElkClass> taxonomy) {
							final OrphanTypeNode<ElkClass, ElkNamedIndividual> node = new OrphanTypeNode<ElkClass, ElkNamedIndividual>(
									members, size, elkFactory_.getOwlNothing(),
									taxonomy, 1);
							final Set<ElkNamedIndividual> allNamedIndividuals = getAllNamedIndividuals();
							final Iterator<ElkNamedIndividual> namedIndividualIterator = allNamedIndividuals
									.iterator();
							if (namedIndividualIterator.hasNext()) {
								// there is at least one individual
								node.addInstanceNode(
										new OrphanInstanceNode<ElkClass, ElkNamedIndividual>(
												allNamedIndividuals,
												allNamedIndividuals.size(),
												namedIndividualIterator.next(),
												ElkIndividualKeyProvider.INSTANCE,
												node));
							}
							return node;
						}
					}, ElkIndividualKeyProvider.INSTANCE);
		}

		return result;
	}

	/**
	 * Compute the inferred taxonomy of the object properties for the given
	 * ontology if it has not been done yet.
	 * 
	 * @return the object property taxonomy implied by the current ontology
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public synchronized Taxonomy<ElkObjectProperty> getObjectPropertyTaxonomy()
			throws ElkInconsistentOntologyException, ElkException {

		ruleAndConclusionStats.reset();

		if (isInconsistent()) {
			throw new ElkInconsistentOntologyException();
		}

		LOGGER_.trace("Property hierarchy computation");
		complete(stageManager.objectPropertyTaxonomyComputationStage);

		return objectPropertyTaxonomyState.getTaxonomy();
	}

	/**
	 * Compute the inferred taxonomy of the object properties for the given
	 * ontology if it has not been done yet.
	 * 
	 * @return the object property taxonomy implied by the current ontology
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public synchronized Taxonomy<ElkObjectProperty> getObjectPropertyTaxonomyQuietly()
			throws ElkException {
		Taxonomy<ElkObjectProperty> result;

		try {
			result = getObjectPropertyTaxonomy();
		} catch (ElkInconsistentOntologyException e) {
			LOGGER_.debug("Ontology is inconsistent");

			result = new SingletoneTaxonomy<ElkObjectProperty, OrphanTaxonomyNode<ElkObjectProperty>>(
					ElkObjectPropertyKeyProvider.INSTANCE,
					getAllObjectProperties(),
					new TaxonomyNodeFactory<ElkObjectProperty, OrphanTaxonomyNode<ElkObjectProperty>, Taxonomy<ElkObjectProperty>>() {
						@Override
						public OrphanTaxonomyNode<ElkObjectProperty> createNode(
								final Iterable<? extends ElkObjectProperty> members,
								final int size,
								final Taxonomy<ElkObjectProperty> taxonomy) {
							return new OrphanTaxonomyNode<ElkObjectProperty>(
									members, size,
									elkFactory_.getOwlBottomObjectProperty(),
									taxonomy);
						}
					});
		}

		return result;
	}

	/**
	 * If the query results are not cached yet, indexes the supplied class
	 * expression and, if successful, computes the query so that the results for
	 * this expressions are ready in {@link #classExpressionQueryState_}.
	 * 
	 * @param classExpression
	 * @param computeInstanceTaxonomy
	 *            if {@code false}, only class taxonomy is computed, if
	 *            {@code true}, also instance taxonomy is computed.
	 * @return <code>true</code> if the query was indexed successfully,
	 *         <code>false</code> otherwise, i.e., when the class expression is
	 *         not supported
	 * @throws ElkInconsistentOntologyException
	 * @throws ElkException
	 */
	private boolean computeQuery(final ElkClassExpression classExpression,
			final boolean computeInstanceTaxonomy)
			throws ElkInconsistentOntologyException, ElkException {

		// Load the query
		if (classExpressionQueryState_.registerQuery(classExpression)) {
			if (queryLoader_ == null) {
				queryLoader_ = classExpressionQueryState_
						.getQueryLoader(getInterrupter());
			}
		}
		ensureLoading();

		if (!classExpressionQueryState_.isIndexed(classExpression)) {
			return false;
		}

		// Complete all stages
		if (computeInstanceTaxonomy) {
			getInstanceTaxonomy();
		} else {
			getTaxonomy();
		}
		/*
		 * If query result is cashed, but there were some changes to the
		 * ontology, it may not be up to date. Whether it is is checked during
		 * stages that clean contexts. These are run, if necessary, by the call
		 * above.
		 */
		if (classExpressionQueryState_.isComputed(classExpression)) {
			return true;
		}
		stageManager.classExpressionQueryStage.invalidateRecursive();
		try {
			complete(stageManager.classExpressionQueryStage);
		} catch (final ElkInterruptedException e) {
			if (classExpressionQueryState_.isComputed(classExpression)) {
				/*
				 * If the stage was interrupted, but the query is already
				 * computed, completing the stage will not be attempted during
				 * the next call. We need to call postExecute() manually, so
				 * that the stage wouldn't stay initialized with computation
				 * that already processed all its inputs (or at least the
				 * queried class).
				 */
				stageManager.classExpressionQueryStage.postExecute();
			} else {
				throw e;
			}
		}

		return true;
	}

	/**
	 * Decides whether the supplied (possibly complex) class expression is
	 * satisfiable. The query state is updated accordingly.
	 * 
	 * @param classExpression
	 *            The queried class expression.
	 * @return whether the supplied class expression is satisfiable.
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	protected boolean querySatisfiability(
			final ElkClassExpression classExpression)
			throws ElkInconsistentOntologyException, ElkException {

		if (computeQuery(classExpression, false)) {
			return classExpressionQueryState_.isSatisfiable(classExpression);
		} else {
			// classExpression couldn't be indexed; pretend it is a fresh class
			return true;
		}

	}

	/**
	 * Computes all atomic classes that are equivalent to the supplied (possibly
	 * complex) class expression. The query state is updated accordingly.
	 * 
	 * @param classExpression
	 *            The queried class expression.
	 * @return all atomic classes that are equivalent to the supplied class
	 *         expression.
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	protected Node<ElkClass> queryEquivalentClasses(
			final ElkClassExpression classExpression)
			throws ElkInconsistentOntologyException, ElkException {

		if (computeQuery(classExpression, false)) {

			final Node<ElkClass> result = classExpressionQueryState_
					.getEquivalentClasses(classExpression);
			if (result == null) {
				return getTaxonomy().getBottomNode();
			} else {
				return result;
			}

		} else {
			// classExpression couldn't be indexed; pretend it is a fresh class

			return new QueryNode<ElkClass>(ElkClassKeyProvider.INSTANCE);
		}

	}

	/**
	 * Computes all atomic direct super-classes of the supplied (possibly
	 * complex) class expression. The query state is updated accordingly.
	 * 
	 * @param classExpression
	 *            The queried class expression.
	 * @return all atomic direct super-classes of the supplied class expression.
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	protected Set<? extends Node<ElkClass>> queryDirectSuperClasses(
			final ElkClassExpression classExpression)
			throws ElkInconsistentOntologyException, ElkException {

		if (computeQuery(classExpression, false)) {

			final Set<? extends Node<ElkClass>> result = classExpressionQueryState_
					.getDirectSuperClasses(classExpression);
			if (result == null) {
				return getTaxonomy().getBottomNode().getDirectSuperNodes();
			} else {
				return result;
			}

		} else {
			// classExpression couldn't be indexed; pretend it is a fresh class

			return Collections.singleton(getTaxonomy().getTopNode());
		}

	}

	/**
	 * Computes all atomic direct sub-classes of the supplied (possibly complex)
	 * class expression. The query state is updated accordingly.
	 * 
	 * @param classExpression
	 *            The queried class expression.
	 * @return all atomic direct sub-classes of the supplied class expression.
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	protected Set<? extends Node<ElkClass>> queryDirectSubClasses(
			final ElkClassExpression classExpression)
			throws ElkInconsistentOntologyException, ElkException {

		if (computeQuery(classExpression, false)) {

			final Taxonomy<ElkClass> taxonomy = getTaxonomy();

			final Set<? extends Node<ElkClass>> result = classExpressionQueryState_
					.getDirectSubClasses(classExpression, taxonomy);
			if (result == null) {
				return taxonomy.getBottomNode().getDirectSubNodes();
			} else {
				return result;
			}

		} else {
			// classExpression couldn't be indexed; pretend it is a fresh class

			return Collections.singleton(getTaxonomy().getBottomNode());
		}

	}

	/**
	 * Computes all direct instances of the supplied (possibly complex) class
	 * expression. The query state is updated accordingly.
	 * 
	 * @param classExpression
	 *            The queried class expression.
	 * @return all direct instances of the supplied class expression.
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	protected Set<? extends Node<ElkNamedIndividual>> queryDirectInstances(
			final ElkClassExpression classExpression)
			throws ElkInconsistentOntologyException, ElkException {

		if (computeQuery(classExpression, true)) {

			final InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = getInstanceTaxonomy();

			final Set<? extends Node<ElkNamedIndividual>> result = classExpressionQueryState_
					.getDirectInstances(classExpression, taxonomy);
			if (result == null) {
				return taxonomy.getBottomNode().getDirectInstanceNodes();
			} else {
				return result;
			}

		} else {
			// classExpression couldn't be indexed; pretend it is a fresh class

			return Collections.emptySet();
		}

	}

	/**
	 * @return all {@link ElkClass}es occurring in the ontology
	 */
	public synchronized Set<ElkClass> getAllClasses() {
		Set<ElkClass> result = new ArrayHashSet<ElkClass>(
				ontologyIndex.getClasses().size());
		for (IndexedClass ic : ontologyIndex.getClasses())
			result.add(ic.getElkEntity());
		return result;
	}

	/**
	 * @return all {@link ElkNamedIndividual}s occurring in the ontology
	 */
	public synchronized Set<ElkNamedIndividual> getAllNamedIndividuals() {
		Set<ElkNamedIndividual> allNamedIndividuals = new ArrayHashSet<ElkNamedIndividual>(
				ontologyIndex.getClasses().size());
		for (IndexedIndividual ii : ontologyIndex.getIndividuals())
			allNamedIndividuals.add(ii.getElkEntity());
		return allNamedIndividuals;
	}

	/**
	 * @return all {@link ElkObjectProperty}es occurring in the ontology
	 */
	public synchronized Set<ElkObjectProperty> getAllObjectProperties() {
		final Set<ElkObjectProperty> result = new ArrayHashSet<ElkObjectProperty>(
				ontologyIndex.getObjectProperties().size());
		for (final IndexedObjectProperty prop : ontologyIndex
				.getObjectProperties()) {
			result.add(prop.getElkEntity());
		}
		return result;
	}

	/**
	 * @return {@code true} if the ontology has been checked for consistency.
	 */
	public synchronized boolean doneConsistencyCheck() {
		return stageManager.consistencyCheckingStage.isCompleted();
	}

	/**
	 * @return {@code true} if the class taxonomy has been computed
	 */
	public synchronized boolean doneTaxonomy() {
		return stageManager.classTaxonomyComputationStage.isCompleted()
				|| stageManager.incrementalClassTaxonomyComputationStage
						.isCompleted();
	}

	/**
	 * @return {@code true} if the instance taxonomy has been computed
	 */
	public synchronized boolean doneInstanceTaxonomy() {
		return stageManager.instanceTaxonomyComputationStage.isCompleted();
	}

	/**
	 * @return {@code true} if the object property taxonomy has been computed
	 */
	public synchronized boolean doneObjectPropertyTaxonomy() {
		return stageManager.objectPropertyTaxonomyComputationStage
				.isCompleted();
	}

	/**
	 * Compute the index representation of the given ontology if it has not been
	 * done yet.
	 * 
	 * @return the index representation of the given ontology
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	protected OntologyIndex getOntologyIndex() throws ElkException {
		ensureLoading();
		return ontologyIndex;
	}

	public synchronized void initClassTaxonomy() {
		classTaxonomyState.getWriter().setTaxonomy(new ConcurrentClassTaxonomy(
				elkFactory_, ElkClassKeyProvider.INSTANCE));
	}

	public synchronized void initInstanceTaxonomy() {
		instanceTaxonomyState.getWriter()
				.setTaxonomy(new ConcurrentInstanceTaxonomy(
						classTaxonomyState.getTaxonomy(),
						ElkIndividualKeyProvider.INSTANCE));
	}

	@Deprecated
	public ElkPolarityExpressionConverter getExpressionConverter() {
		return this.expressionConverter_;
	}

	@Deprecated
	public ElkSubObjectPropertyExpressionVisitor<? extends IndexedPropertyChain> getSubPropertyConverter() {
		return this.subPropertyConverter_;
	}

	/*---------------------------------------------------
	 * TRACING METHODS
	 *---------------------------------------------------*/

	public void visitDerivedConclusionsForSubsumption(
			ElkClassExpression subClass, ElkClassExpression superClass,
			DerivedClassConclusionVisitor visitor) throws ElkException {
		isInconsistent();
		// checking owl:Thing consistency
		if (consistencyCheckingState.isOwlThingInconsistent()) {
			if (!visitor.inconsistentOwlThing(
					factory_.getContradiction(ontologyIndex.getOwlThing()))) {
				return;
			}
		}
		// processing inconsistent individuals
		for (IndexedIndividual ind : consistencyCheckingState
				.getInconsistentIndividuals()) {
			if (!visitor.inconsistentIndividual(factory_.getContradiction(ind),
					ind.getElkEntity())) {
				return;
			}
		}
		// checking inconsistency of subClass
		ModifiableIndexedClassExpression root = subClass
				.accept(expressionConverter_);
		if (root == null) {
			return;
		}
		getTaxonomyQuietly(); // ensure classes are saturated
		ClassInconsistency conclusion = checkDerived(
				factory_.getContradiction(root));
		if (conclusion != null && !visitor.inconsistentSubClass(conclusion)) {
			return;
		}
		// checking subsumption
		IndexedClassExpression superExpression = superClass
				.accept(expressionConverter_);
		if (superExpression == null) {
			return;
		}
		SubClassInclusionComposed subsumption = checkDerived(
				factory_.getSubClassInclusionComposed(root, superExpression));
		if (subsumption != null
				&& !visitor.derivedClassInclusion(subsumption)) {
			return;
		}
	}

	private <C extends ClassConclusion> C checkDerived(C conclusion)
			throws ElkException {
		Context context = saturationState.getContext(conclusion.getDestination());
		if (context == null) {
			// not derived
			return null;
		}
		// else
		if (context.containsConclusion(conclusion)) {
			return conclusion;
		}
		// else not derived
		return null;
	}

	public TracingInferenceSet explainConclusions(
			Iterable<? extends ClassConclusion> conclusions)
			throws ElkException {
		for (ClassConclusion conclusion : conclusions) {
			traceState_.toTrace(conclusion);
		}
		stageManager.inferenceTracingStage.invalidateRecursive();
		getTaxonomyQuietly(); // ensure that classes are saturated
		complete(stageManager.inferenceTracingStage);
		return traceState_;
	}

	public TracingInferenceSet explainConclusion(ClassConclusion conclusion)
			throws ElkException {
		LOGGER_.debug("{}: explaining", conclusion);
		return explainConclusions(Collections.singleton(conclusion));
	}

	public TracingInferenceSet getTracingInferences() {
		return traceState_;
	}
	
	@Deprecated
	IndexedClassExpression transform(ElkClassExpression ce) {
		return ce.accept(expressionConverter_);
	}

	@Deprecated
	IndexedObjectProperty transform(ElkObjectProperty ce) {
		return ce.accept(expressionConverter_);
	}

	@Deprecated
	IndexedPropertyChain transform(ElkSubObjectPropertyExpression ce) {
		return ce.accept(subPropertyConverter_);
	}

	TraceState getTraceState() {
		return traceState_;
	}

	// ////////////////////////////////////////////////////////////////
	/*
	 * SOME DEBUG METHODS, FIXME: REMOVE
	 */
	// ////////////////////////////////////////////////////////////////
	@Deprecated
	public synchronized Collection<? extends IndexedClassExpression> getIndexedClassExpressions() {
		return ontologyIndex.getClassExpressions();
	}
}
