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
import java.util.Map;

import org.apache.log4j.Logger;
import org.semanticweb.elk.loading.AxiomChangeListener;
import org.semanticweb.elk.loading.ChangesLoader;
import org.semanticweb.elk.loading.ElkAxiomChange;
import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.OntologyLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.DifferentialIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectCache;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;
import org.semanticweb.elk.reasoner.taxonomy.ConcurrentClassTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.ConcurrentInstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.PredefinedTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;

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
	private static final Logger LOGGER_ = Logger
			.getLogger(AbstractReasonerState.class);

	final ReasonerConfiguration config_;

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
	 * {@code true} if the reasoner is interrupted
	 */
	private volatile boolean isInterrupted_ = false;

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
	 * The source where the input ontology can be loaded
	 */
	private final Loader ontologyLoader_;
	/**
	 * The source where changes in ontology can be loaded
	 */
	private ChangesLoader changesLoader_;

	protected AbstractReasonerState(OntologyLoader ontologyLoader,
			ReasonerConfiguration config) {
		this.objectCache_ = new IndexedObjectCache();
		this.ontologyIndex = new DifferentialIndex(objectCache_);
		this.saturationState = new SaturationState(ontologyIndex);
		this.ruleAndConclusionStats = new SaturationStatistics();
		this.config_ = config;
		this.stageManager = new ReasonerStageManager(this);
		this.ontologyLoader_ = ontologyLoader.getLoader(ontologyIndex
				.getAxiomInserter());
	}

	public void setIncrementalMode(boolean mode) {
		ontologyIndex.setIncrementalMode(mode);
	}

	/**
	 * Reset the changes loading stage and all subsequent stages
	 */
	private void resetChangesLoading() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("Reset changes loading");
		stageManager.changesLoadingStage.invalidate();
	}

	public void registerOntologyChangesLoader(ChangesLoader changesLoader) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("Registering ontology changes loader");
		resetChangesLoading();

		changesLoader_ = changesLoader;

		if (changesLoader_ != null) {
			changesLoader_.registerChangeListener(new AxiomChangeListener() {

				@Override
				public void notify(ElkAxiomChange change) {
					resetChangesLoading();
				}
			});
		}
	}

	/**
	 * @return the source where the input ontology can be loaded
	 */
	Loader getOntologyLoader() {
		return ontologyLoader_;
	}

	/**
	 * @return the source where changes in ontology can be loaded
	 */
	Loader getChangesLoader() {
		// return changesLoader;
		if (changesLoader_ == null) {
			return null;
		}
		return changesLoader_.getLoader(ontologyIndex.getAxiomInserter(),
				ontologyIndex.getAxiomDeleter());
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
	public void interrupt() {
		if (LOGGER_.isInfoEnabled())
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
	public boolean isInterrupted() {
		return isInterrupted_;
	}

	/**
	 * clears the interrupt status of the reasoner
	 */
	public void clearInterrupt() {
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
	public boolean isInconsistent() throws ElkException {

		ReasonerStage stage = ontologyIndex.isIncrementalMode() ? stageManager.incrementalConsistencyCheckingStage
				: stageManager.consistencyCheckingStage;

		getStageExecutor().complete(stage);

		return inconsistentOntology;
	}

	/**
	 * Forces the reasoner to load ontology
	 * 
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public void loadOntology() throws ElkException {
		setIncrementalMode(false);
		getStageExecutor().complete(stageManager.ontologyLoadingStage);
	}

	/**
	 * Forces the reasoner to reload ontology changes
	 * 
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public void loadChanges() throws ElkException {
		getStageExecutor().complete(stageManager.changesLoadingStage);
	}

	/**
	 * Compute the inferred taxonomy of the named classes for the given ontology
	 * if it has not been done yet.
	 * 
	 * @return the class taxonomy implied by the current ontology
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public Taxonomy<ElkClass> getTaxonomy() throws ElkException {
		if (isInconsistent())
			throw new ElkInconsistentOntologyException();

		if (ontologyIndex.isIncrementalMode()) {
			getStageExecutor().complete(
					stageManager.incrementalClassTaxonomyComputationStage);
		} else {
			getStageExecutor().complete(
					stageManager.classTaxonomyComputationStage);
		}

		return classTaxonomyState.getTaxonomy();
	}

	public Taxonomy<ElkClass> getTaxonomyQuietly() {
		Taxonomy<ElkClass> result = null;

		try {
			result = getTaxonomy();
		} catch (ElkException e) {
			LOGGER_.info("Ontology is inconsistent");

			result = PredefinedTaxonomy.INCONSISTENT_CLASS_TAXONOMY;
		}

		return result;
	}

	/**
	 * Compute the inferred taxonomy of the named classes with instances if this
	 * has not been done yet.
	 * 
	 * @return the instance taxonomy implied by the current ontology
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public InstanceTaxonomy<ElkClass, ElkNamedIndividual> getInstanceTaxonomy()
			throws ElkException {
		if (isInconsistent())
			throw new ElkInconsistentOntologyException();

		if (ontologyIndex.isIncrementalMode()) {
			getStageExecutor().complete(
					stageManager.incrementalInstanceTaxonomyComputationStage);
		} else {
			getStageExecutor().complete(
					stageManager.instanceTaxonomyComputationStage);
		}

		return instanceTaxonomyState.getTaxonomy();
	}

	public InstanceTaxonomy<ElkClass, ElkNamedIndividual> getInstanceTaxonomyQuietly() {
		InstanceTaxonomy<ElkClass, ElkNamedIndividual> result = null;

		try {
			result = getInstanceTaxonomy();
		} catch (ElkException e) {
			LOGGER_.info("Ontology is inconsistent");

			result = PredefinedTaxonomy.INCONSISTENT_INDIVIDUAL_TAXONOMY;
		}

		return result;
	}

	public Map<IndexedClassExpression, Context> getContextMap() {
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

	public Map<IndexedPropertyChain, SaturatedPropertyChain> getPropertySaturationMap() {
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
	public boolean doneConsistencyCheck() {
		return stageManager.consistencyCheckingStage.isCompleted;
	}

	/**
	 * @return {@code true} if the class taxonomy has been computed
	 */
	public boolean doneTaxonomy() {
		return stageManager.classTaxonomyComputationStage.isCompleted
				|| stageManager.incrementalClassTaxonomyComputationStage.isCompleted;
	}

	/**
	 * @return {@code true} if the instance taxonomy has been computed
	 */
	public boolean doneInstanceTaxonomy() {
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
		getStageExecutor().complete(stageManager.ontologyLoadingStage);
		return ontologyIndex;
	}

	protected boolean useIncrementalTaxonomy() {
		return config_
				.getParameterAsBoolean(ReasonerConfiguration.INCREMENTAL_TAXONOMY);
	}

	public void initClassTaxonomy() {
		classTaxonomyState.initTaxonomy(new ConcurrentClassTaxonomy());
	}	
	
	public void initInstanceTaxonomy() {
		instanceTaxonomyState.initTaxonomy(new ConcurrentInstanceTaxonomy(classTaxonomyState.getTaxonomy()));
	}

	
	// ////////////////////////////////////////////////////////////////
	/*
	 * SOME DEBUG METHODS, FIXME: REMOVE
	 */
	// ////////////////////////////////////////////////////////////////
	public Collection<IndexedClassExpression> getIndexedClassExpressions() {
		return ontologyIndex.getIndexedClassExpressions();
	}
}
