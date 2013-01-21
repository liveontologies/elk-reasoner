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
import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;
import org.semanticweb.elk.loading.AxiomChangeListener;
import org.semanticweb.elk.loading.ChangesLoader;
import org.semanticweb.elk.loading.ElkAxiomChange;
import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.OntologyLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkPropertyAxiom;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectCache;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.OntologyIndexImpl;
import org.semanticweb.elk.reasoner.saturation.RuleAndConclusionStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;
import org.semanticweb.elk.reasoner.taxonomy.PredefinedTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceTaxonomy;
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
	final RuleAndConclusionStatistics ruleAndConclusionStats;

	/**
	 * 
	 */
	IncrementalReasonerState incrementalState = null;
	/**
	 * {@code true} if the ontology is loaded
	 */
	boolean doneLoading = false;
	/**
	 * {@code true} if the ontology changes are loaded
	 */
	boolean doneChangeLoading = false;
	/**
	 * {@code true} if the assignment of saturations to properties has been
	 * reset
	 */
	boolean donePropertySaturationReset = true;
	/**
	 * {@code true} if entailed reflexive properties have been computed
	 */
	boolean donePropertyReflexivityComputation = false;
	/**
	 * {@code true} if property hierarchy and compositions have been computed
	 */
	boolean donePropertyHierarchyCompositionComputation = false;
	/**
	 * {@code true} if the assignment of contexts to class expressions has been
	 * reset
	 */
	boolean doneContextReset = true;
	/**
	 * {@code true} if the ontology has been checked for consistency.
	 */
	boolean doneConsistencyCheck = false;
	/**
	 * {@code true} if saturation for every class of the ontology is computed
	 */
	boolean doneClassSaturation = false;
	/**
	 * {@code true} if the class taxonomy has been computed
	 */
	boolean doneClassTaxonomy = false;
	/**
	 * {@code true} if the instance taxonomy has been computed
	 */
	boolean doneInstanceTaxonomy = false;
	/**
	 * {@code true} if the reasoner is interrupted
	 */
	private volatile boolean isInterrupted_ = false;

	/**
	 * the cache for indexed objects
	 */
	final IndexedObjectCache objectCache_;

	/**
	 * the current ontology index
	 */
	OntologyIndex ontologyIndex;

	/**
	 * {@code true} if the current ontology is inconsistent
	 */
	boolean inconsistentOntology = false;
	
	/**
	 * Taxonomy that stores (partial) classification
	 */
	//UpdateableTaxonomy<ElkClass> taxonomy = null;
	
	final TaxonomyState classTaxonomyState = new TaxonomyState();

	/**
	 * Taxonomy that stores (partial) classification and (partial) realization
	 * of individuals
	 */
	UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> instanceTaxonomy = null;

	/**
	 * The source where the input ontology can be loaded
	 */
	private Loader ontologyLoader_;
	/**
	 * The source where changes in ontology can be loaded
	 */
	private ChangesLoader changesLoader_;

	protected AbstractReasonerState(final ReasonerConfiguration config) {
		this.objectCache_ = new IndexedObjectCache();
		this.ontologyIndex = new OntologyIndexImpl(objectCache_);
		this.saturationState = new SaturationState(ontologyIndex);
		this.ruleAndConclusionStats = new RuleAndConclusionStatistics();
		this.config_ = config;
		this.incrementalState = new IncrementalReasonerState(ontologyIndex);
	}

	public void setIncrementalMode(boolean set) {
		if (set && incrementalState == null) {
			incrementalState = new IncrementalReasonerState(ontologyIndex);
		} else if (!set) {
			incrementalState = null;
		}
	}

	/**
	 * Reset the loading stage and all subsequent stages
	 */
	private void resetLoading() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("Reset loading");
		if (this.ontologyLoader_ != null) {
			this.ontologyLoader_.dispose();
			this.ontologyLoader_ = null;
		}

		if (doneLoading) {
			doneLoading = false;
			objectCache_.clear();
			ontologyIndex = new OntologyIndexImpl(objectCache_);
		}

		resetChangesLoading();
		resetPropertySaturation();
		registerOntologyChangesLoader(null);
	}

	/**
	 * Reset the changes loading stage and all subsequent stages
	 * 
	 * FIXME get rid of this proliferation of boolean flags
	 */
	private void resetChangesLoading() {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("Reset changes loading");
		if (doneChangeLoading) {
			doneChangeLoading = false;
			resetSaturation();
			// TODO: currently it is assumed that changes do not have property
			// axioms
			// resetPropertySaturation();
		}
	}

	public void resetPropertySaturation() {
		if (donePropertySaturationReset) {
			donePropertySaturationReset = false;
			donePropertyReflexivityComputation = false;
			donePropertyHierarchyCompositionComputation = false;
			resetSaturation();
		}
	}

	public void resetSaturation() {
		if (doneContextReset) {
			doneContextReset = false;
			doneConsistencyCheck = false;
			doneClassSaturation = false;
			doneClassTaxonomy = false;
			doneInstanceTaxonomy = false;

			if (incrementalState != null) {
				incrementalState.resetAllStagesStatus();
			}
		}
	}

	public void registerOntologyLoader(OntologyLoader ontologyLoader) {
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace("Registering ontology loader");
		resetLoading();
		this.ontologyLoader_ = ontologyLoader.getLoader(ontologyIndex
				.getAxiomInserter());
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

					if (!incrementalChange(change)) {
						if (LOGGER_.isInfoEnabled()) {
							LOGGER_.info("The axiom change " + change + " cannot be accommodated incrementally");
						}
						
						incrementalState = null;
						
						//throw new RuntimeException(change.toString());
					}

					resetChangesLoading();
				}
			});
		}
	}

	private boolean incrementalChange(ElkAxiomChange change) {
		ElkAxiom axiom = change.getAxiom();
		/*
		 * FIXME Here we determine if the change can be accommodated
		 * incrementally. In principle, we should have a hook inside the indexer
		 * to accomplish this
		 */
		return !(axiom instanceof ElkPropertyAxiom<?>);
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

		if (incrementalState != null) {
			return changesLoader_.getLoader(
					incrementalState.diffIndex.getAxiomInserter(),
					incrementalState.diffIndex.getAxiomDeleter());
		} else {
			return changesLoader_.getLoader(ontologyIndex.getAxiomInserter(),
					ontologyIndex.getAxiomDeleter());
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
	 * Reset the ontology all data. After this, the Reasoner holds an empty
	 * ontology.
	 */
	public void reset() {
		if (LOGGER_.isInfoEnabled())
			LOGGER_.info("Reset data");
		resetLoading();
		ruleAndConclusionStats.reset();
	}

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

		ReasonerStage stage = incrementalState == null ? new ConsistencyCheckingStage(
				this) : new IncrementalConsistencyCheckingStage(this);

		getStageExecutor().complete(stage);

		return inconsistentOntology;
	}

	/**
	 * @return {@code true} if the ontology has been checked for consistency.
	 */
	public boolean doneConsistencyCheck() {
		return doneConsistencyCheck;
	}

	/**
	 * Forces the reasoner to load ontology
	 * 
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public void loadOntology() throws ElkException {
		setIncrementalMode(false);
		getStageExecutor().complete(new OntologyLoadingStage(this));
	}

	/**
	 * Forces the reasoner to reload ontology changes
	 * 
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public void loadChanges() throws ElkException {
		getStageExecutor().complete(new ChangesLoadingStage(this));
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

		if (incrementalState != null) {
			getStageExecutor().complete(
					new IncrementalClassTaxonomyComputationStage(this));
		} else {
			getStageExecutor()
					.complete(new ClassTaxonomyComputationStage(this));
			this.incrementalState = new IncrementalReasonerState(ontologyIndex);
		}

		return classTaxonomyState.taxonomy;//taxonomy;
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
	 * @return {@code true} if the class taxonomy has been computed
	 */
	public boolean doneTaxonomy() {
		return doneClassTaxonomy;
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

		getStageExecutor().complete(new InstanceTaxonomyComputationStage(this));

		return instanceTaxonomy;
	}

	/**
	 * @return {@code true} if the instance taxonomy has been computed
	 */
	public boolean doneInstanceTaxonomy() {
		return doneInstanceTaxonomy;
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
		getStageExecutor().complete(new OntologyLoadingStage(this));
		return ontologyIndex;
	}

	protected boolean useIncrementalTaxonomy() {
		return config_
				.getParameterAsBoolean(ReasonerConfiguration.INCREMENTAL_TAXONOMY);
	}

	// ////////////////////////////////////////////////////////////////
	/*
	 * SOME DEBUG METHODS, FIXME: REMOVE
	 */
	// ////////////////////////////////////////////////////////////////
	public Collection<IndexedClassExpression> getIndexedClassExpressions() {
		return ontologyIndex == null ? Collections
				.<IndexedClassExpression> emptyList() : ontologyIndex
				.getIndexedClassExpressions();
	}
}
