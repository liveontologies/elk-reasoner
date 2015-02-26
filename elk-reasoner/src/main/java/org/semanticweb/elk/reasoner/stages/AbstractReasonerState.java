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
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.ComposedAxiomLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.DifferentialIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectCache;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateFactory;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;
import org.semanticweb.elk.reasoner.taxonomy.ConcurrentClassTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.ConcurrentInstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.OrphanInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.OrphanNode;
import org.semanticweb.elk.reasoner.taxonomy.OrphanTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.SingletoneInstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.SingletoneTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.SimpleInterrupter;

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
	private static final Logger LOGGER_ = Logger
			.getLogger(AbstractReasonerState.class);

	final SaturationState saturationState;

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
	 * the cache for indexed objects
	 */
	final IndexedObjectCache objectCache_;
	/**
	 * the (differential) index for loading of axioms and changes
	 */
	final DifferentialIndex ontologyIndex;
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

	protected AbstractReasonerState() {
		this.objectCache_ = new IndexedObjectCache();
		this.ontologyIndex = new DifferentialIndex(objectCache_);
		this.saturationState = SaturationStateFactory
				.createSaturationState(ontologyIndex);
		this.ruleAndConclusionStats = new SaturationStatistics();
		this.stageManager = new ReasonerStageManager(this);
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
	 *             if the reasoning process cannot be completed successfully
	 */
	public void forceLoading() throws ElkException {
		loadAxioms();
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
		loadAxioms();

		if (isIncrementalMode() && !saturationState.getContexts().isEmpty()) {
			complete(stageManager.incrementalConsistencyCheckingStage);
		} else {
			setNonIncrementalMode();
			complete(stageManager.consistencyCheckingStage);
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
		complete(stageManager.axiomLoadingStage);
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
				.getIndexedClasses().size());
		for (IndexedClass ic : ontologyIndex.getIndexedClasses())
			result.add(ic.getElkClass());
		return result;
	}

	/**
	 * @return all {@link ElkNamedIndividual}s occurring in the ontology
	 */
	public synchronized Set<ElkNamedIndividual> getAllNamedIndividuals() {
		Set<ElkNamedIndividual> allNamedIndividuals = new ArrayHashSet<ElkNamedIndividual>(
				ontologyIndex.getIndexedClasses().size());
		for (IndexedIndividual ii : ontologyIndex.getIndexedIndividuals())
			allNamedIndividuals.add(ii.getElkNamedIndividual());
		return allNamedIndividuals;
	}

	public synchronized Map<IndexedClassExpression, Context> getContextMap() {
		final Map<IndexedClassExpression, Context> result = new ArrayHashMap<IndexedClassExpression, Context>(
				1024);
		for (IndexedClassExpression ice : ontologyIndex
				.getIndexedClassExpressions()) {
			Context context = ice.getContext();
			if (context == null)
				continue;
			result.put(ice, context);
		}
		return result;
	}

	public synchronized Map<IndexedPropertyChain, SaturatedPropertyChain> getPropertySaturationMap() {
		final Map<IndexedPropertyChain, SaturatedPropertyChain> result = new ArrayHashMap<IndexedPropertyChain, SaturatedPropertyChain>(
				256);
		for (IndexedPropertyChain ipc : ontologyIndex
				.getIndexedPropertyChains()) {
			SaturatedPropertyChain saturation = ipc.getSaturated();
			if (saturation == null)
				continue;
			result.put(ipc, saturation);
		}
		return result;
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

	// ////////////////////////////////////////////////////////////////
	/*
	 * SOME DEBUG METHODS, FIXME: REMOVE
	 */
	// ////////////////////////////////////////////////////////////////
	public synchronized Collection<IndexedClassExpression> getIndexedClassExpressions() {
		return ontologyIndex.getIndexedClassExpressions();
	}
}
