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
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverterImpl;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverterImpl;
import org.semanticweb.elk.reasoner.indexing.hierarchy.DifferentialIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableIndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.modifiable.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateFactory;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ContradictionImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.ClassInferenceSet;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceState;
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
import org.semanticweb.elk.util.concurrent.computation.SimpleInterrupter;
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
public abstract class AbstractReasonerState extends SimpleInterrupter {

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
	/**
	 * If true, the reasoner will bind asserted axioms to the inference rules
	 * which use them as side conditions. As a result, it'll be possible to
	 * access the axioms when exploring traced inferences. It will cause a
	 * certain memory overhead because otherwise we don't store asserted axioms.
	 */
	final boolean BIND_AXIOMS = true;

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
	 * {@code true} if the current ontology is inconsistent
	 */
	IndexedClassEntity inconsistentEntity = null;
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
	 * Keeps relevant information about tracing
	 */
	TraceState traceState;

	private final ElkPolarityExpressionConverter expressionConverter_;

	private final ElkSubObjectPropertyExpressionVisitor<ModifiableIndexedPropertyChain> subPropertyConverter_;

	protected AbstractReasonerState() {
		this.ontologyIndex = new DifferentialIndex();
		this.saturationState = SaturationStateFactory
				.createSaturationState(ontologyIndex);
		this.ruleAndConclusionStats = new SaturationStatistics();
		this.stageManager = new ReasonerStageManager(this);
		this.expressionConverter_ = new ElkPolarityExpressionConverterImpl(
				ontologyIndex);
		this.subPropertyConverter_ = new ElkAxiomConverterImpl(ontologyIndex);
	}

	protected AbstractReasonerState(AxiomLoader axiomLoader) {
		this();
		registerAxiomLoader(axiomLoader);
	}

	protected void complete(ReasonerStage stage) throws ElkException {
		try {
			getStageExecutor().complete(stage);
		} catch (ElkInterruptedException e) {
			// clear the interrupt flag
			setInterrupt(false);
			throw e;
		}
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

	/**
	 * Reset the property saturation stage and all subsequent stages
	 */
	public synchronized void resetPropertySaturation() {
		LOGGER_.trace("Reset property saturation");
		stageManager.propertyInitializationStage.invalidate();
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
	 * @return the {@link AxiomLoader} currently registered for loading of
	 *         axioms or {@code null} if no loader is registered
	 */
	public AxiomLoader getAxiomLoader() {
		return this.axiomLoader_;
	}

	/**
	 * Forces loading of all axioms from the registered {@link AxiomLoader}s.
	 * Typically, loading lazily when reasoning tasks are requested.
	 * 
	 * @throws ElkException
	 */
	public synchronized void forceLoading() throws ElkException {
		if (classTaxonomyState.getTaxonomy() != null)
			trySetIncrementalMode();
		complete(stageManager.axiomLoadingStage);
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

	protected ModifiableOntologyIndex getModifiableOntologyIndex() {
		return this.ontologyIndex;
	}

	/**
	 * interrupts running reasoning stages
	 */
	public void interrupt() {
		LOGGER_.info("Interrupt requested");
		setInterrupt(true);
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
		forceLoading();

		if (isIncrementalMode() && !saturationState.getContexts().isEmpty()) {
			complete(stageManager.incrementalConsistencyCheckingStage);
		} else {
			setNonIncrementalMode();
			complete(stageManager.consistencyCheckingStage);
			stageManager.incrementalConsistencyCheckingStage.setCompleted();
		}

		return inconsistentEntity != null;
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
			complete(stageManager.incrementalClassTaxonomyComputationStage);
		} else {
			setNonIncrementalMode();
			complete(stageManager.classTaxonomyComputationStage);
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
			complete(stageManager.incrementalInstanceTaxonomyComputationStage);
		} else {
			setNonIncrementalMode();
			complete(stageManager.instanceTaxonomyComputationStage);
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

	@Override
	public void setInterrupt(boolean flag) {
		super.setInterrupt(flag);
		ReasonerStageExecutor stageExecutor = getStageExecutor();
		if (stageExecutor != null)
			stageExecutor.setInterrupt(flag);
		// this flag will be cleared after ElkInterruptedException is thrown
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
		forceLoading();
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

	private void toTrace(Conclusion conclusion) {
		if (traceState.getInferencesForOrigin(conclusion.getOriginRoot()) != null)
			// already traced
			return;
		// else
		stageManager.inferenceTracingStage.invalidate();
		traceState.addToTrace(conclusion);
	}

	public ClassInferenceSet explainSubsumption(ElkClassExpression sub,
			ElkClassExpression sup) throws ElkException {
		if (traceState == null) {
			resetTraceState();
		}

		IndexedClassExpression subsumee = sub.accept(expressionConverter_);
		IndexedClassExpression subsumer = sup.accept(expressionConverter_);

		if (subsumee != null & subsumer != null) {
			toTrace(convertTraceTarget(subsumee, subsumer));
		}

		getStageExecutor().complete(stageManager.inferenceTracingStage);

		return traceState;
	}

	/**
	 * @return either owl:Thing, if it was derived to be a subclass of
	 *         owl:Nothing or the individual which was inferred to be an
	 *         instance of owl:Nothing.
	 * @throws ElkException
	 */
	public ElkEntity explainInconsistency() throws ElkException {
		if (!isInconsistent()) {
			throw new IllegalStateException("The ontology is consistent");
		}
		toTrace(new ContradictionImpl(inconsistentEntity));
		getStageExecutor().complete(stageManager.inferenceTracingStage);
		return inconsistentEntity.getElkEntity();
	}

	boolean isSatisfiable(IndexedClassExpression subsumee) {
		Context subsumeeContext = saturationState.getContext(subsumee);

		if (subsumeeContext != null) {
			return !subsumeeContext.containsConclusion(new ContradictionImpl(
					subsumee));
		}
		// shouldn't happen if we suppport only named classes or abbreviate
		// class expressions and saturate them prior to tracing
		return true;
	}

	private Conclusion convertTraceTarget(IndexedClassExpression subsumee,
			IndexedClassExpression subsumer) {
		if (!isSatisfiable(subsumee)) {
			// the subsumee is unsatisfiable so we explain the unsatisfiability
			return new ContradictionImpl(subsumee);
		}
		// else
		return new DecomposedSubsumerImpl<IndexedClassExpression>(subsumee,
				subsumer);
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

	public void resetTraceState() {
		createTraceState();
	}

	private void createTraceState() {
		traceState = new TraceState();
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
