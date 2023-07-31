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
import java.util.Map;
import java.util.Set;

import org.liveontologies.puli.statistics.NestedStats;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.ClassQueryLoader;
import org.semanticweb.elk.loading.ComposedAxiomLoader;
import org.semanticweb.elk.loading.EntailmentQueryLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
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
import org.semanticweb.elk.reasoner.completeness.IncompleteResult;
import org.semanticweb.elk.reasoner.completeness.Incompleteness;
import org.semanticweb.elk.reasoner.completeness.IncompletenessManager;
import org.semanticweb.elk.reasoner.completeness.IncompletenessMonitor;
import org.semanticweb.elk.reasoner.completeness.OccurrencesInOntology;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
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
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.model.OntologyIndex;
import org.semanticweb.elk.reasoner.query.QueryNode;
import org.semanticweb.elk.reasoner.query.VerifiableQueryResult;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateFactory;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.SaturationConclusionBaseFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
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
import org.semanticweb.elk.reasoner.tracing.Conclusion;
import org.semanticweb.elk.reasoner.tracing.TraceState;
import org.semanticweb.elk.reasoner.tracing.TracingInference;
import org.semanticweb.elk.reasoner.tracing.TracingProof;
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
public abstract class AbstractReasonerState implements TracingProof {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractReasonerState.class);

	/**
	 * The factory for creating auxiliary ElkObjects
	 */
	private final ElkObject.Factory elkFactory_;

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
	 * The source where axioms and changes in ontology can be loaded
	 */
	private AxiomLoader axiomLoader_ = null;
	/**
	 * Manages information about property hierarchy computation.
	 */
	final PropertyHierarchyCompositionState propertyHierarchyCompositionState_;
	/**
	 * Maintains the state of saturation.
	 */
	final SaturationState<? extends Context> saturationState;
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
	final ClassExpressionQueryState classExpressionQueryState;
	/**
	 * Stores information about entailment queries.
	 */
	final EntailmentQueryState entailmentQueryState;
	/**
	 * Taxonomy that stores (partial) classification and (partial) realization
	 * of individuals
	 */
	final InstanceTaxonomyState instanceTaxonomyState;
	/**
	 * Keeps relevant information about tracing
	 */
	private final TraceState traceState_;
	/**
	 * Defines reasoning stages and dependencies between them
	 */
	final ReasonerStageManager stageManager;

	/**
	 * Represents {@code Feature} appearing in ontologies
	 */
	private final OccurrencesInOntology occurrencesInOntology_ = new OccurrencesInOntology();

	/**
	 * Keeps track of {@link IncompletenessMonitor}s for different reasoning
	 * tasks
	 */
	private final IncompletenessManager incompletenessManager_ = new IncompletenessManager(
			occurrencesInOntology_);

	/**
	 * if {@code true}, reasoning will be done incrementally whenever possible
	 */
	private boolean allowIncrementalMode_ = true;

	/**
	 * creates conclusions for tracing
	 */
	private final SaturationConclusion.Factory factory_ = new SaturationConclusionBaseFactory();

	private final ElkPolarityExpressionConverter expressionConverter_;

	private final ElkSubObjectPropertyExpressionVisitor<ModifiableIndexedPropertyChain> subPropertyConverter_;

	protected AbstractReasonerState(ElkObject.Factory elkFactory,
			final ReasonerConfiguration config) {
		this.elkFactory_ = elkFactory;
		this.ontologyIndex = new DifferentialIndex(elkFactory);
		this.propertyHierarchyCompositionState_ = new PropertyHierarchyCompositionState();
		this.saturationState = SaturationStateFactory
				.createSaturationState(ontologyIndex);
		this.consistencyCheckingState = ConsistencyCheckingState
				.create(saturationState, propertyHierarchyCompositionState_);
		this.instanceTaxonomyState = new InstanceTaxonomyState(saturationState,
				ontologyIndex, elkFactory);
		this.classTaxonomyState = new ClassTaxonomyState(saturationState,
				ontologyIndex, elkFactory, Arrays.asList(
						instanceTaxonomyState.getClassTaxonomyStateListener()));
		this.objectPropertyTaxonomyState = new ObjectPropertyTaxonomyState(
				elkFactory);
		this.ruleAndConclusionStats = new SaturationStatistics();
		this.stageManager = new ReasonerStageManager(this);
		this.expressionConverter_ = new ElkPolarityExpressionConverterImpl(
				elkFactory, ontologyIndex);
		this.subPropertyConverter_ = new ElkAxiomConverterImpl(elkFactory,
				ontologyIndex);
		this.traceState_ = new TraceState(config, saturationState,
				propertyHierarchyCompositionState_, elkFactory, ontologyIndex);
		this.classExpressionQueryState = new ClassExpressionQueryState(config,
				saturationState, elkFactory, ontologyIndex, factory_,
				incompletenessManager_);
		this.entailmentQueryState = new EntailmentQueryState(config,
				saturationState, consistencyCheckingState, factory_,
				incompletenessManager_);
	}

	public ElkObject.Factory getElkFactory() {
		return elkFactory_;
	}

	protected void complete(ReasonerStage stage) throws ElkException {
		getStageExecutor().complete(stage);
	}

	/**
	 * Completes the provided stage despite interruptions, if it was not
	 * completed yet.
	 * 
	 * @param stage
	 *            The stage that should be completed.
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	protected void completeUninterruptibly(final ReasonerStage stage)
			throws ElkException {
		while (true) {
			try {
				complete(stage);
				break;
			} catch (final ElkInterruptedException e) {
				continue;
			}
		}
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
	AxiomLoader getAxiomLoader() {
		return this.axiomLoader_;
	}

	/**
	 * @return the {@link ClassQueryLoader} currently registered for loading of
	 *         class queries or {@code null} if no loader is registered
	 */
	ClassQueryLoader getClassQueryLoader() {
		return classExpressionQueryState.getQueryLoader(getInterrupter());
	}

	/**
	 * @return the {@link EntailmentQueryLoader} currently registered for
	 *         loading of entailment queries or {@code null} if no loader is
	 *         registered
	 */
	EntailmentQueryLoader getEntailmentQueryLoader() {
		return entailmentQueryState.getQueryLoader(getInterrupter());
	}

	/**
	 * @return {@code true} if there is no input pending loading, {@code false}
	 *         otherwise.
	 */
	protected synchronized boolean isLoadingFinished() {
		final ClassQueryLoader classQueryLoader = getClassQueryLoader();
		final EntailmentQueryLoader entailmentQueryLoader = getEntailmentQueryLoader();
		return (axiomLoader_ == null || axiomLoader_.isLoadingFinished())
				&& (classQueryLoader == null
						|| classQueryLoader.isLoadingFinished())
				&& (entailmentQueryLoader == null
						|| entailmentQueryLoader.isLoadingFinished());
	}

	/**
	 * Flushes index, if needed, and completes loading if there is new input.
	 * Incremental mode should be changed only during completing loading.
	 * 
	 * @throws ElkException
	 */
	public synchronized void ensureLoading() throws ElkException {

		if (isLoadingFinished()) {
			return;
		}

		// ensure that all pending incremental stages are completed
		complete(stageManager.incrementalAdditionStage);

		if (!isIncrementalMode()) {
			// reset saturation
			complete(stageManager.contextInitializationStage);
		}
		LOGGER_.trace("Reset axiom loading");
		stageManager.inputLoadingStage.invalidateRecursive();
		// Invalidate stages at the beginnings of the dependency chains.
		stageManager.contextInitializationStage.invalidateRecursive();
		stageManager.incrementalCompletionStage.invalidateRecursive();

		complete(stageManager.inputLoadingStage);

	}

	/**
	 * Ensures that saturation is restored and taxonomies are cleaned. Also
	 * invalidates stages that depend on the saturation if it changed.
	 * 
	 * @throws ElkException
	 */
	private void restoreSaturation() throws ElkException {

		ensureLoading();

		final boolean changed;

		if (isIncrementalMode()) {
			changed = !stageManager.incrementalTaxonomyCleaningStage
					.isCompleted();
			complete(stageManager.incrementalTaxonomyCleaningStage);
		} else {
			changed = !stageManager.contextInitializationStage.isCompleted();
			complete(stageManager.contextInitializationStage);
		}

		if (!changed) {
			return;
		}
		stageManager.consistencyCheckingStage.invalidateRecursive();

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
	 * Completes consistency checking stage and the stages it depends on, if
	 * this has not been done yet.
	 * 
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	private void restoreConsistencyCheck() throws ElkException {
		ruleAndConclusionStats.reset();
		restoreSaturation();
		complete(stageManager.consistencyCheckingStage);
	}

	/**
	 * Check consistency of the current ontology, if this has not been done yet.
	 * 
	 * @return {@code true} if the ontology is inconsistent, that is,
	 *         unsatisfiable.
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public synchronized IncompleteResult<Boolean> isInconsistent()
			throws ElkException {

		restoreConsistencyCheck();

		return new IncompleteResult<Boolean>(
				consistencyCheckingState.isInconsistent(),
				incompletenessManager_.getOntologySatisfiabilityMonitor());
	}

	/**
	 * Complete the taxonomy computation stage and the stages it depends on, if
	 * it has not been done yet.
	 * 
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	private void restoreTaxonomy()
			throws ElkInconsistentOntologyException, ElkException {

		// also restores saturation and cleans the taxonomy if necessary
		restoreConsistencyCheck();
		if (consistencyCheckingState.isInconsistent()) {
			throw new ElkInconsistentOntologyException();
		}

		complete(stageManager.classTaxonomyComputationStage);
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
	public synchronized IncompleteResult<? extends Taxonomy<ElkClass>> getTaxonomy()
			throws ElkInconsistentOntologyException, ElkException {
		restoreTaxonomy();
		return new IncompleteResult<>(classTaxonomyState.getTaxonomy(),
				incompletenessManager_.getClassTaxonomyMonitor());
	}

	/**
	 * Compute the inferred taxonomy of the named classes for the given ontology
	 * if it has not been done yet.
	 * 
	 * @return the class taxonomy implied by the current ontology
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public synchronized IncompleteResult<? extends Taxonomy<ElkClass>> getTaxonomyQuietly()
			throws ElkException {
		try {
			return getTaxonomy();
		} catch (ElkInconsistentOntologyException e) {
			LOGGER_.debug("Ontology is inconsistent");
			return new IncompleteResult<>(
					new SingletoneTaxonomy<ElkClass, OrphanTaxonomyNode<ElkClass>>(
							ElkClassKeyProvider.INSTANCE, getAllClasses(),
							new TaxonomyNodeFactory<ElkClass, OrphanTaxonomyNode<ElkClass>, Taxonomy<ElkClass>>() {
								@Override
								public OrphanTaxonomyNode<ElkClass> createNode(
										final Iterable<? extends ElkClass> members,
										final int size,
										final Taxonomy<ElkClass> taxonomy) {
									return new OrphanTaxonomyNode<ElkClass>(
											members, size,
											elkFactory_.getOwlNothing(),
											taxonomy);
								}
							}),
					Incompleteness.getNoIncompletenessMonitor());
		}
	}

	/**
	 * Compute the inferred taxonomy of the named classes for the given ontology
	 * despite interruptions if it has not been computed yet.
	 * 
	 * @return the class taxonomy implied by the current ontology
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public synchronized IncompleteResult<? extends Taxonomy<ElkClass>> getTaxonomyQuietlyUninterruptibly()
			throws ElkException {
		while (true) {
			try {
				return getTaxonomyQuietly();
			} catch (final ElkInterruptedException e) {
				continue;
			}
		}
	}

	/**
	 * Completes instance taxonomy computation stage and the stages that it
	 * depends on, if this has not been done yet.
	 * 
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	private void restoreInstanceTaxonomy()
			throws ElkInconsistentOntologyException, ElkException {

		ruleAndConclusionStats.reset();

		// also restores saturation and cleans the taxonomy if necessary
		restoreConsistencyCheck();
		if (consistencyCheckingState.isInconsistent()) {
			throw new ElkInconsistentOntologyException();
		}

		complete(stageManager.instanceTaxonomyComputationStage);
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
	public synchronized IncompleteResult<? extends InstanceTaxonomy<ElkClass, ElkNamedIndividual>> getInstanceTaxonomy()
			throws ElkInconsistentOntologyException, ElkException {

		restoreInstanceTaxonomy();

		return new IncompleteResult<>(instanceTaxonomyState.getTaxonomy(),
				incompletenessManager_.getInstanceTaxonomyMonitor());
	}

	/**
	 * Compute the inferred taxonomy of the named classes with instances if this
	 * has not been done yet.
	 * 
	 * @return the instance taxonomy implied by the current ontology
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public synchronized IncompleteResult<? extends InstanceTaxonomy<ElkClass, ElkNamedIndividual>> getInstanceTaxonomyQuietly()
			throws ElkException {

		try {
			return getInstanceTaxonomy();
		} catch (ElkInconsistentOntologyException e) {
			LOGGER_.debug("Ontology is inconsistent");
			return new IncompleteResult<>(
					new SingletoneInstanceTaxonomy<ElkClass, ElkNamedIndividual, OrphanTypeNode<ElkClass, ElkNamedIndividual>>(
							ElkClassKeyProvider.INSTANCE, getAllClasses(),
							new TaxonomyNodeFactory<ElkClass, OrphanTypeNode<ElkClass, ElkNamedIndividual>, Taxonomy<ElkClass>>() {
								@Override
								public OrphanTypeNode<ElkClass, ElkNamedIndividual> createNode(
										final Iterable<? extends ElkClass> members,
										final int size,
										final Taxonomy<ElkClass> taxonomy) {
									final OrphanTypeNode<ElkClass, ElkNamedIndividual> node = new OrphanTypeNode<ElkClass, ElkNamedIndividual>(
											members, size,
											elkFactory_.getOwlNothing(),
											taxonomy, 1);
									final Set<ElkNamedIndividual> allNamedIndividuals = getAllNamedIndividuals();
									if (allNamedIndividuals.isEmpty()) {
										return node;
									}
									// else add an instance node containing all
									// individuals
									node.addInstanceNode(
											new OrphanInstanceNode<ElkClass, ElkNamedIndividual>(
													allNamedIndividuals,
													ElkIndividualKeyProvider.INSTANCE,
													node));
									return node;
								}
							}, ElkIndividualKeyProvider.INSTANCE),
					Incompleteness.getNoIncompletenessMonitor());
		}
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
	public synchronized IncompleteResult<? extends Taxonomy<ElkObjectProperty>> getObjectPropertyTaxonomy()
			throws ElkInconsistentOntologyException, ElkException {

		ruleAndConclusionStats.reset();

		restoreConsistencyCheck();
		if (consistencyCheckingState.isInconsistent()) {
			throw new ElkInconsistentOntologyException();
		}

		LOGGER_.trace("Property hierarchy computation");
		complete(stageManager.objectPropertyTaxonomyComputationStage);

		return new IncompleteResult<>(objectPropertyTaxonomyState.getTaxonomy(),
				incompletenessManager_.getObjectPropertyTaxonomyMonitor());
	}

	/**
	 * Compute the inferred taxonomy of the object properties for the given
	 * ontology if it has not been done yet.
	 * 
	 * @return the object property taxonomy implied by the current ontology
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public synchronized IncompleteResult<? extends Taxonomy<ElkObjectProperty>> getObjectPropertyTaxonomyQuietly()
			throws ElkException {

		try {
			return getObjectPropertyTaxonomy();
		} catch (ElkInconsistentOntologyException e) {
			LOGGER_.debug("Ontology is inconsistent");
			return new IncompleteResult<>(
					new SingletoneTaxonomy<ElkObjectProperty, OrphanTaxonomyNode<ElkObjectProperty>>(
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
											elkFactory_
													.getOwlBottomObjectProperty(),
											taxonomy);
								}
							}),
					Incompleteness.getNoIncompletenessMonitor());
		}
	}

	/**
	 * If the query results are not cached yet, indexes the supplied class
	 * expression and, if successful, computes the query so that the results for
	 * this expressions are ready in {@link #classExpressionQueryState}.
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
		classExpressionQueryState.registerQuery(classExpression);
		ensureLoading();

		// Complete all stages
		if (computeInstanceTaxonomy) {
			restoreInstanceTaxonomy();
		} else {
			restoreTaxonomy();
		}

		if (!classExpressionQueryState.isIndexed(classExpression)) {
			return false;
		}

		/*
		 * If query result is cashed, but there were some changes to the
		 * ontology, it may not be up to date. Whether it is is checked during
		 * stages that clean contexts. These are run, if necessary, by the call
		 * above.
		 */
		if (classExpressionQueryState.isComputed(classExpression)) {
			return true;
		}
		stageManager.classExpressionQueryStage.invalidateRecursive();
		try {
			complete(stageManager.classExpressionQueryStage);
		} catch (final ElkInterruptedException e) {
			if (classExpressionQueryState.isComputed(classExpression)) {
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
	protected IncompleteResult<? extends Boolean> querySatisfiability(
			final ElkClassExpression classExpression)
			throws ElkInconsistentOntologyException, ElkException {

		final boolean satisfiable;

		if (computeQuery(classExpression, false)) {
			satisfiable = classExpressionQueryState
					.isSatisfiable(classExpression);
		} else {
			// classExpression couldn't be indexed; pretend it is a fresh class
			satisfiable = true;
		}

		return new IncompleteResult<>(satisfiable, classExpressionQueryState
				.getIncompletenessMonitor(classExpression));
	}

	/**
	 * Computes all atomic classes that are equivalent to the supplied (possibly
	 * complex) class expression. The query state is updated accordingly.
	 * 
	 * @param query
	 *            The queried class expression.
	 * @return all atomic classes that are equivalent to the supplied class
	 *         expression.
	 * @throws ElkInconsistentOntologyException
	 *             if the ontology is inconsistent
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	protected IncompleteResult<? extends Node<ElkClass>> queryEquivalentClasses(
			final ElkClassExpression query)
			throws ElkInconsistentOntologyException, ElkException {

		final Node<ElkClass> equivalentClasses;

		if (computeQuery(query, false)) {

			final Node<ElkClass> r = classExpressionQueryState
					.getEquivalentClasses(query);
			if (r == null) {
				equivalentClasses = classTaxonomyState.getTaxonomy()
						.getBottomNode();
			} else {
				equivalentClasses = r;
			}

		} else {
			// classExpression couldn't be indexed; pretend it is a fresh class
			equivalentClasses = new QueryNode<ElkClass>(
					ElkClassKeyProvider.INSTANCE);
		}

		return new IncompleteResult<>(equivalentClasses,
				classExpressionQueryState.getIncompletenessMonitor(query));
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
	protected IncompleteResult<? extends Set<? extends Node<ElkClass>>> queryDirectSuperClasses(
			final ElkClassExpression classExpression)
			throws ElkInconsistentOntologyException, ElkException {

		final Set<? extends Node<ElkClass>> superClasses;

		if (computeQuery(classExpression, false)) {

			final Set<? extends Node<ElkClass>> r = classExpressionQueryState
					.getDirectSuperClasses(classExpression);
			if (r == null) {
				superClasses = classTaxonomyState.getTaxonomy().getBottomNode()
						.getDirectSuperNodes();
			} else {
				superClasses = r;
			}

		} else {
			// classExpression couldn't be indexed; pretend it is a fresh class

			superClasses = Collections
					.singleton(classTaxonomyState.getTaxonomy().getTopNode());
		}

		return new IncompleteResult<>(superClasses, classExpressionQueryState
				.getIncompletenessMonitor(classExpression));
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
	protected IncompleteResult<? extends Set<? extends Node<ElkClass>>> queryDirectSubClasses(
			final ElkClassExpression classExpression)
			throws ElkInconsistentOntologyException, ElkException {

		final Set<? extends Node<ElkClass>> result;

		if (computeQuery(classExpression, false)) {

			final Taxonomy<ElkClass> taxonomy = classTaxonomyState
					.getTaxonomy();

			final Set<? extends Node<ElkClass>> r = classExpressionQueryState
					.getDirectSubClasses(classExpression, taxonomy);

			if (r == null) {
				result = taxonomy.getBottomNode().getDirectSubNodes();
			} else {
				result = r;
			}

		} else {
			// classExpression couldn't be indexed; pretend it is a fresh class

			result = Collections.singleton(
					classTaxonomyState.getTaxonomy().getBottomNode());
		}

		return new IncompleteResult<>(result, classExpressionQueryState
				.getIncompletenessMonitor(classExpression));
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
	protected IncompleteResult<? extends Set<? extends Node<ElkNamedIndividual>>> queryDirectInstances(
			final ElkClassExpression classExpression)
			throws ElkInconsistentOntologyException, ElkException {

		final Set<? extends Node<ElkNamedIndividual>> instances;

		if (computeQuery(classExpression, true)) {

			final InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy = instanceTaxonomyState
					.getTaxonomy();

			final Set<? extends Node<ElkNamedIndividual>> r = classExpressionQueryState
					.getDirectInstances(classExpression, taxonomy);

			if (r == null) {
				instances = taxonomy.getBottomNode().getDirectInstanceNodes();
			} else {
				instances = r;
			}

		} else {
			// classExpression couldn't be indexed; pretend it is a fresh class

			instances = Collections.emptySet();
		}

		return new IncompleteResult<>(instances, classExpressionQueryState
				.getIncompletenessMonitor(classExpression));
	}

	
	/**
	 * Complete the entailment checking stage and the stages it depends on, if
	 * it has not been done yet.
	 * 
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	private void restoreEntailmentCheck() throws ElkException {
		restoreConsistencyCheck();
		complete(stageManager.entailmentQueryStage);
	}
	
	/**
	 * Decides whether the supplied {@code axioms} are entailed by the currently
	 * loaded ontology.
	 * 
	 * @param axioms
	 *            Entailment of what axioms is queried.
	 * @return A map from each queried axiom to the
	 *         {@link VerifiableQueryResult} for that axiom.
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public synchronized Map<ElkAxiom, VerifiableQueryResult> checkEntailment(
			final Iterable<? extends ElkAxiom> axioms) throws ElkException {

		entailmentQueryState.registerQueries(axioms);		
		restoreEntailmentCheck();
		return entailmentQueryState.isEntailed(axioms);
	}

	/**
	 * Decides whether the supplied {@code axiom} is entailed by the currently
	 * loaded ontology.
	 * 
	 * @param axiom
	 *            The queries axiom.
	 * @return the {@link VerifiableQueryResult} for the queried axiom.
	 * @throws ElkException
	 */
	public synchronized VerifiableQueryResult checkEntailment(
			final ElkAxiom axiom) throws ElkException {
		return checkEntailment(Collections.singleton(axiom)).get(axiom);
	}

	public boolean isEntailed(final ElkAxiom axiom) throws ElkException {
		return Incompleteness.getValue(checkEntailment(axiom));
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
		return stageManager.classTaxonomyComputationStage.isCompleted();
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

	@Deprecated
	public ElkPolarityExpressionConverter getExpressionConverter() {
		return this.expressionConverter_;
	}

	@Deprecated
	public ElkSubObjectPropertyExpressionVisitor<? extends IndexedPropertyChain> getSubPropertyConverter() {
		return this.subPropertyConverter_;
	}

	// TODO: limit the output to only what is necessary	
	OccurrencesInOntology getOccurrencesInOntology() {
		return this.occurrencesInOntology_;
	}

	/*---------------------------------------------------
	 * TRACING METHODS
	 *---------------------------------------------------*/

	public TracingProof getProof() {
		return this;
	}

	@Override
	public Collection<? extends TracingInference> getInferences(
			final Object conclusion) {
		if (!(conclusion instanceof Conclusion)) {
			return Collections.emptySet();
		}
		// else
		try {
			// Ensure that classes are saturated.
			getTaxonomyQuietlyUninterruptibly();
			if (!traceState_.requestInferences((Conclusion) conclusion)) {
				stageManager.inferenceTracingStage.invalidateRecursive();
				completeUninterruptibly(stageManager.inferenceTracingStage);
			}
		} catch (final ElkException e) {
			throw new ElkRuntimeException(e);
		}
		return traceState_.getInferences(conclusion);
	}

	@NestedStats(name = "traceState")
	public Object getStatsNestedInTraceSate() {
		return traceState_.getStats();
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

}
