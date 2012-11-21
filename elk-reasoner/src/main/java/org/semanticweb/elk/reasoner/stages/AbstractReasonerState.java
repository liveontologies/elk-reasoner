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

import org.apache.log4j.Logger;
import org.semanticweb.elk.loading.ChangesLoader;
import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.OntologyLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectCache;
import org.semanticweb.elk.reasoner.indexing.hierarchy.OntologyIndexImpl;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy;
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

	final SaturationState saturationState;
	
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
	 * {@code true} if all stages have been reseted after changes
	 */
	final OntologyIndex ontologyIndex;
	
	private final IndexedObjectCache objectCache_;
	/**
	 * {@code true} if the current ontology is inconsistent
	 */
	boolean inconsistentOntology = false;
	/**
	 * Taxonomy that stores (partial) classification
	 */
	UpdateableTaxonomy<ElkClass> taxonomy = null;
	
	/**
	 * Taxonomy that stores (partial) classification and (partial) realization
	 * of individuals
	 */
	UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual> instanceTaxonomy = null;

	/**
	 * The source where the input ontology can be loaded
	 */
	private Loader ontologyLoader;
	/**
	 * The source where changes in ontology can be loaded
	 */
	private Loader changesLoader;

	protected AbstractReasonerState() {
		OntologyIndexImpl ontoIndex = new OntologyIndexImpl();
		
		this.ontologyIndex = ontoIndex;
		this.objectCache_ = ontoIndex;
		this.saturationState = new SaturationState(ontoIndex);
	}

	public void setIncrementalMode(boolean set) {
		if (set && incrementalState == null) {
			incrementalState = new IncrementalReasonerState(objectCache_, ontologyIndex);
		}
		else if (!set) {
			incrementalState = null;
		}
	}
	
	/**
	 * Reset the loading stage and all subsequent stages
	 */
	private void resetLoading() {
		if (this.ontologyLoader != null) {
			this.ontologyLoader.dispose();
			this.ontologyLoader = null;
		}
		if (doneLoading) {
			doneLoading = false;
			ontologyIndex.clear();
		}
		resetChangesLoading();
	}

	/**
	 * Reset the changes loading stage and all subsequent stages
	 * 
	 * FIXME get rid of this proliferation of boolean flags
	 */
	private void resetChangesLoading() {
		if (this.changesLoader != null) {
			this.changesLoader.dispose();
			this.changesLoader = null;
		}
		if (doneChangeLoading) {
			doneChangeLoading = false;
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
		resetLoading();
		this.ontologyLoader = ontologyLoader.getLoader(ontologyIndex
				.getAxiomInserter());
	}

	public void registerOntologyChangesLoader(ChangesLoader changesLoader) {
		resetChangesLoading();

		if (incrementalState != null) {
			this.changesLoader = changesLoader.getLoader(
					incrementalState.diffIndex.getAxiomInserter(),
					incrementalState.diffIndex.getAxiomDeleter());
		}
		else {
			this.changesLoader = changesLoader.getLoader(
					ontologyIndex.getAxiomInserter(),
					ontologyIndex.getAxiomDeleter());			
		}
	}

	/**
	 * @return the source where the input ontology can be loaded
	 */
	Loader getOntologyLoader() {
		return ontologyLoader;
	}

	/**
	 * @return the source where changes in ontology can be loaded
	 */
	Loader getChangesLoader() {
		return changesLoader;
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
		resetLoading();
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
		
		ReasonerStage stage = incrementalState == null 
				? new ConsistencyCheckingStage(	this) 
				: new IncrementalConsistencyCheckingStage(this); 
		
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

		/*for (IndexedClassExpression ice : ontologyIndex.getIndexedClasses()) {
			System.out.println(ice + ", context " + ice.getContext());
		}*/
		
		if (isInconsistent())
			throw new ElkInconsistentOntologyException();		
		
		if (incrementalState != null) {
			getStageExecutor()
					.complete(new IncrementalClassTaxonomyComputationStage(this));
		} else {
			getStageExecutor()
					.complete(new ClassTaxonomyComputationStage(this));
		}
		
		/*for (IndexedClassExpression ice : ontologyIndex.getIndexedClasses()) {
			System.out.println(ice + ", context " + ice.getContext());
		}*/
		
		return taxonomy;
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

}
