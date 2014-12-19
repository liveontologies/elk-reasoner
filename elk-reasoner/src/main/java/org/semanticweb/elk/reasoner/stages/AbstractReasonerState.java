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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.ComposedAxiomLoader;
import org.semanticweb.elk.loading.ElkLoadingException;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.incremental.NonIncrementalChangeListener;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverterImpl;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ChangeIndexingProcessor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.DifferentialIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.NonIncrementalChangeCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateFactory;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ContradictionImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.DummyConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.OnDemandTracingReader;
import org.semanticweb.elk.reasoner.saturation.tracing.RecursiveTraceUnwinder;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceState;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.taxonomy.ConcurrentClassTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.ConcurrentInstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.OrphanInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.OrphanNode;
import org.semanticweb.elk.reasoner.taxonomy.OrphanTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.SingletoneInstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.SingletoneTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The execution state of the reasoner containing information about which
 * reasoning stages have been completed and holding the results of these
 * reasoning stages, such as the consistency status of the ontology, class, or
 * instance taxonomy.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class AbstractReasonerState {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractReasonerState.class);

	// TODO create reasoner configuration options instead of these flags?
	/**
	 * If true, all inferences will be stored in the trace store.
	 */
	final boolean FULL_TRACING = false;
	/**
	 * If true, all rules, redundant and non-redundant, will be applied during
	 * the class expression saturation stage. Otherwise, only non-redundant
	 * rules will be applied.
	 */
	final boolean REDUNDANT_RULES = false;

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
	 * {@code true} if the reasoner is interrupted
	 */
	private volatile boolean isInterrupted_ = false;
	/**
	 * the (differential) index for loading of axioms and changes
	 */
	final DifferentialIndex ontologyIndex;
	/**
	 * the object using which axioms are inserted and deleted into the index
	 */
	private final ElkAxiomConverter axiomInserter_, axiomDeleter_;
	/**
	 * {@code true} if the current ontology is inconsistent
	 */
	boolean inconsistentOntology = false;
	/**
	 * Taxonomy state that stores (partial) classification
	 */
	final ClassTaxonomyState classTaxonomyState = new ClassTaxonomyState();
	/**
	 * Defines reasoning stages and dependencies between them
	 */
	final ReasonerStageManager stageManager;
	/**
	 * Taxonomy that stores (partial) classification and (partial) realization
	 * of individuals
	 */
	final InstanceTaxonomyState instanceTaxonomyState = new InstanceTaxonomyState();
	/**
	 * The source where axioms and changes in ontology can be loaded
	 */
	private AxiomLoader axiomLoader_;
	/**
	 * if {@code true}, reasoning will be done incrementally whenever possible
	 */
	private boolean allowIncrementalMode_ = true;

	private boolean allowIncrementalTaxonomy_ = true;
	/**
	 * if the property hierarchy correspond to the loading axioms
	 */
	boolean propertyHierarchyUpToDate_ = true;
	/**
	 * The listener used to detect if the axiom has an impact on the role
	 * hierarchy
	 */
	private NonIncrementalChangeListener<ElkAxiom> nonIncrementalChangeListener_ = new NonIncrementalChangeListener<ElkAxiom>() {

		@Override
		public void notify(ElkAxiom axiom) {
			if (!propertyHierarchyUpToDate_)
				return;

			if (LOGGER_.isDebugEnabled()) {
				LOGGER_.debug("Disallowing incremental mode due to "
						+ OwlFunctionalStylePrinter.toString(axiom));
			}

			propertyHierarchyUpToDate_ = false;
		}
	};

	/**
	 * Keeps relevant information about tracing
	 */
	TraceState traceState;

	protected AbstractReasonerState() {
		this.ontologyIndex = new DifferentialIndex();
		this.axiomInserter_ = new ElkAxiomConverterImpl(ontologyIndex, 1);
		this.axiomDeleter_ = new ElkAxiomConverterImpl(ontologyIndex, -1);
		this.saturationState = SaturationStateFactory
				.createSaturationState(ontologyIndex);
		this.ruleAndConclusionStats = new SaturationStatistics();
		this.stageManager = new ReasonerStageManager(this);
	}

	protected AbstractReasonerState(AxiomLoader axiomLoader) {
		this();
		registerAxiomLoader(axiomLoader);
	}

	public synchronized void setAllowIncrementalMode(boolean allow) {
		if (allowIncrementalMode_ == allow)
			return;
		allowIncrementalMode_ = allow;

		if (!allow) {
			setNonIncrementalMode();
		}
		setAllowIncrementalTaxonomy(allow);

		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Incremental mode is "
					+ (allow ? "allowed" : "disallowed"));
		}
	}

	public synchronized boolean isAllowIncrementalMode() {
		return allowIncrementalMode_;
	}

	public synchronized boolean isIncrementalMode() {
		return ontologyIndex.isIncrementalMode();
	}

	void setNonIncrementalMode() {
		ontologyIndex.setIncrementalMode(false);
		setAllowIncrementalTaxonomy(false);
	}

	boolean trySetIncrementalMode() {
		if (!allowIncrementalMode_)
			// switching to incremental mode not allowed
			return false;

		ontologyIndex.setIncrementalMode(true);
		return true;

	}

	/**
	 * Reset the axiom loading stage and all subsequent stages
	 */
	public synchronized void resetAxiomLoading() {
		LOGGER_.trace("Reset axiom loading");
		stageManager.axiomLoadingStage.invalidate();
		stageManager.incrementalCompletionStage.invalidate();
	}

	public synchronized void registerAxiomLoader(AxiomLoader newAxiomLoader) {
		LOGGER_.trace("Registering new axiom loader");

		resetAxiomLoading();

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
		// wrapping both the inserter and the deleter to receive notifications
		// if some axiom change can't be incorporated incrementally

		ElkAxiomProcessor axiomInserter = new ChangeIndexingProcessor(
				new NonIncrementalChangeCheckingVisitor(axiomInserter_,
						nonIncrementalChangeListener_),
				ChangeIndexingProcessor.ADDITION);
		ElkAxiomProcessor axiomDeleter = new ChangeIndexingProcessor(
				new NonIncrementalChangeCheckingVisitor(axiomDeleter_,
						nonIncrementalChangeListener_),
				ChangeIndexingProcessor.REMOVAL);

		axiomLoader_.load(axiomInserter, axiomDeleter);

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
	 * @return the {@link ComputationExecutor} that is used for execution of
	 *         reasoning processes
	 */
	protected abstract ComputationExecutor getProcessExecutor();

	/**
	 * @return the {@link ProgressMonitor} that is used for reporting progress
	 *         on all potentially long-running operations.
	 */
	protected abstract ProgressMonitor getProgressMonitor();

	/**
	 * interrupts running reasoning stages
	 */
	public synchronized void interrupt() {
		LOGGER_.info("Interrupt requested");

		isInterrupted_ = true;
		ReasonerStageExecutor stageExecutor = getStageExecutor();
		if (stageExecutor != null)
			stageExecutor.interrupt();
	}

	/**
	 * @return {@code true} if the reasoner has been interrupted and the
	 *         interrupt status of the reasoner has not been cleared yet
	 */
	public synchronized boolean isInterrupted() {
		return isInterrupted_;
	}

	/**
	 * clears the interrupt status of the reasoner
	 */
	public synchronized void clearInterrupt() {
		isInterrupted_ = false;
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
		loadAxioms();

		if (isIncrementalMode() && !saturationState.getContexts().isEmpty()) {
			getStageExecutor().complete(
					stageManager.incrementalConsistencyCheckingStage);
		} else {
			setNonIncrementalMode();
			getStageExecutor().complete(stageManager.consistencyCheckingStage);
			stageManager.incrementalConsistencyCheckingStage.setCompleted();
		}

		return inconsistentOntology;
	}

	/**
	 * Forces the reasoner to load the axioms (deletions and additions)
	 * 
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	void loadAxioms() throws ElkException {
		if (classTaxonomyState.getTaxonomy() != null)
			trySetIncrementalMode();
		getStageExecutor().complete(stageManager.axiomLoadingStage);
		if (!propertyHierarchyUpToDate_) {
			/*
			 * switching to non-incremental mode due to changes in property
			 * axioms
			 */
			stageManager.propertyInitializationStage.invalidate();
			setNonIncrementalMode();
		}
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
			getStageExecutor().complete(
					stageManager.incrementalClassTaxonomyComputationStage);
		} else {
			setNonIncrementalMode();
			getStageExecutor().complete(
					stageManager.classTaxonomyComputationStage);
			stageManager.incrementalClassTaxonomyComputationStage
					.setCompleted();
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
			LOGGER_.info("Ontology is inconsistent");

			OrphanNode<ElkClass> node = new OrphanNode<ElkClass>(
					getAllClasses(), PredefinedElkClass.OWL_NOTHING);
			result = new SingletoneTaxonomy<ElkClass, OrphanNode<ElkClass>>(
					node);
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

		if (isIncrementalMode() && instanceTaxonomyState.getTaxonomy() != null) {
			getStageExecutor().complete(
					stageManager.incrementalInstanceTaxonomyComputationStage);
		} else {
			setNonIncrementalMode();
			getStageExecutor().complete(
					stageManager.instanceTaxonomyComputationStage);
			stageManager.incrementalInstanceTaxonomyComputationStage
					.setCompleted();
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
			LOGGER_.info("Ontology is inconsistent");
			OrphanTypeNode<ElkClass, ElkNamedIndividual> node = new OrphanTypeNode<ElkClass, ElkNamedIndividual>(
					getAllClasses(), PredefinedElkClass.OWL_NOTHING, 1);
			Set<ElkNamedIndividual> allNamedIndividuals = getAllNamedIndividuals();
			Iterator<ElkNamedIndividual> namedIndividualIterator = allNamedIndividuals
					.iterator();
			if (namedIndividualIterator.hasNext()) {
				// there is at least one individual
				node.addInstanceNode(new OrphanInstanceNode<ElkClass, ElkNamedIndividual>(
						allNamedIndividuals, namedIndividualIterator.next(),
						node));
			}
			result = new SingletoneInstanceTaxonomy<ElkClass, ElkNamedIndividual, OrphanTypeNode<ElkClass, ElkNamedIndividual>>(
					node);
		}

		return result;
	}

	/**
	 * @return all {@link ElkClass}es occurring in the ontology
	 */
	public synchronized Set<ElkClass> getAllClasses() {
		Set<ElkClass> result = new ArrayHashSet<ElkClass>(ontologyIndex
				.getClasses().size());
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
	 * @return {@code true} if the ontology has been checked for consistency.
	 */
	public synchronized boolean doneConsistencyCheck() {
		return stageManager.consistencyCheckingStage.isCompleted;
	}

	/**
	 * @return {@code true} if the class taxonomy has been computed
	 */
	public synchronized boolean doneTaxonomy() {
		return stageManager.classTaxonomyComputationStage.isCompleted
				|| stageManager.incrementalClassTaxonomyComputationStage.isCompleted;
	}

	/**
	 * @return {@code true} if the instance taxonomy has been computed
	 */
	public synchronized boolean doneInstanceTaxonomy() {
		return stageManager.instanceTaxonomyComputationStage.isCompleted;
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
		loadAxioms();
		return ontologyIndex;
	}

	protected boolean setAllowIncrementalTaxonomy(boolean flag) {
		if (!flag) {
			allowIncrementalTaxonomy_ = false;
		} else if (allowIncrementalMode_) {
			allowIncrementalTaxonomy_ = true;
		} else {
			return false;
		}

		return true;
	}

	protected boolean useIncrementalTaxonomy() {
		return allowIncrementalTaxonomy_;
	}

	public synchronized void initClassTaxonomy() {
		classTaxonomyState.getWriter().setTaxonomy(
				new ConcurrentClassTaxonomy());
	}

	public synchronized void initInstanceTaxonomy() {
		instanceTaxonomyState.initTaxonomy(new ConcurrentInstanceTaxonomy(
				classTaxonomyState.getTaxonomy()));
	}

	/*---------------------------------------------------
	 * TRACING METHODS
	 * TODO; clients should not have to work with contexts and conclusions. we
	 * need to represent the trace graphs in a way that is completely detached
	 * from the index or saturation data structures (like the taxonomies).
	 * 
	 * TODO#2: check if FULL_TRACING is on 
	 *---------------------------------------------------*/

	public TraceStore.Reader explainSubsumption(ElkClassExpression sub,
			ElkClassExpression sup) throws ElkException {
		if (traceState == null) {
			resetTraceState();
		}

		IndexedClassExpression subsumee = sub.accept(ontologyIndex
				.getExpressionConverter());
		IndexedClassExpression subsumer = sup.accept(ontologyIndex
				.getExpressionConverter());

		TraceStore.Reader onDemandTracer = new OnDemandTracingReader(
				traceState.getSaturationState(), traceState.getTraceStore()
						.getReader(), traceState.getContextTracingFactory());
		// TraceStore.Reader inferenceReader = new
		// FirstNInferencesReader(onDemandTracer, 1);
		TraceStore.Reader inferenceReader = onDemandTracer;
		RecursiveTraceUnwinder unwinder = new RecursiveTraceUnwinder(
				inferenceReader);
		Context subsumeeContext = saturationState.getContext(subsumee);

		if (subsumeeContext != null) {
			Conclusion conclusion = null;

			if (subsumeeContext.containsConclusion(ContradictionImpl
					.getInstance())) {
				// the subsumee is unsatisfiable so we explain the
				// unsatisfiability
				conclusion = ContradictionImpl.getInstance();
			} else {
				conclusion = new DecomposedSubsumerImpl<IndexedClassExpression>(
						subsumer);
			}

			unwinder.accept(subsumee, conclusion,
					new DummyConclusionVisitor<IndexedClassExpression>());
		} else {
			throw new IllegalArgumentException("Unknown class: " + sub);
		}

		return traceState.getTraceStore().getReader();
	}

	IndexedClassExpression transform(ElkClassExpression ce) {
		return ce.accept(ontologyIndex.getExpressionConverter());
	}

	public void resetTraceState() {
		createTraceState(saturationState);
	}

	private void createTraceState(SaturationState<?> mainState) {
		traceState = new TraceState(mainState, getNumberOfWorkers());
	}

	TraceState getTraceState() {
		if (traceState == null) {
			resetTraceState();
		}

		return traceState;
	}

	// ////////////////////////////////////////////////////////////////
	/*
	 * SOME DEBUG METHODS, FIXME: REMOVE
	 */
	// ////////////////////////////////////////////////////////////////
	public synchronized Collection<? extends IndexedClassExpression> getIndexedClassExpressions() {
		return ontologyIndex.getClassExpressions();
	}
}
