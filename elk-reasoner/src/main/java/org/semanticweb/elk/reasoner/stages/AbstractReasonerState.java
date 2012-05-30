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

import java.util.concurrent.ExecutorService;

import org.semanticweb.elk.owl.ElkAxiomProcessor;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.InconsistentOntologyException;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.OntologyIndexImpl;
import org.semanticweb.elk.reasoner.taxonomy.IndividualClassTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.Taxonomy;

/**
 * The execution state of the reasoner containing information about which
 * reasoning stages that have been completed and holding the results of these
 * reasoning stages, such as the consistency status of the ontology, class, or
 * instance taxonomy.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class AbstractReasonerState {
	/**
	 * <tt>true</tt> if the object property saturation has been computed
	 */
	boolean doneObjectPropertySaturation = false;
	/**
	 * <tt>true</tt> if the assignment of contexts to class expressions has been
	 * reset
	 */
	boolean doneContextReset = true;
	/**
	 * <tt>true</tt> if the ontology has been checked for consistency.
	 */
	boolean doneConsistencyCheck = false;
	/**
	 * <tt>true</tt> if the class taxonomy has been computed
	 */
	boolean doneClassTaxonomy = false;
	/**
	 * <tt>true</tt> if the instance taxonomy has been computed
	 */
	boolean doneInstanceTaxonomy = false;
	/**
	 * <tt>true</tt> if all stages have been reseted after changes
	 */
	boolean doneReset = true;
	/**
	 * the index representing the current ongology
	 */
	OntologyIndex ontologyIndex;
	/**
	 * <tt>true</tt> if the current ontology is consistent
	 */
	boolean consistentOntology;
	/**
	 * Taxonomy that stores (partial) reasoning results.
	 */
	IndividualClassTaxonomy taxonomy;

	/**
	 * Invalidates all previously computed reasoning results. By calling this
	 * method it is indicated that the input ontology might have been changed
	 * and so, some reasoning stages need to be executed again.
	 * 
	 * 
	 * This does not mean that the deductions that have been made are deleted.
	 * Rather, this method would be called if the deductions (and the index in
	 * general) have changed, and the Reasoner should not rely on its records of
	 * earlier deductions any longer.
	 */
	protected void resetStages() {
		if (!doneReset) {
			doneObjectPropertySaturation = false;
			doneContextReset = false;
			doneConsistencyCheck = false;
			doneClassTaxonomy = false;
			doneInstanceTaxonomy = false;
			doneReset = true;
		}
	}

	/**
	 * @return the {@link ExecutorService} that is used for running concurrent
	 *         reasoning tasks
	 */
	protected abstract ExecutorService getExecutor();

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
	 * @return the {@link ProgressMonitor} that is used for reporting progress
	 *         on all potentially long-running operations.
	 */
	protected abstract ProgressMonitor getProgressMonitor();

	/**
	 * Reset the ontology all data. After this, the Reasoner holds an empty
	 * ontology.
	 */
	public void reset() {
		ontologyIndex = new OntologyIndexImpl();
		resetStages();
	}

	/**
	 * Add an axiom to the ontology. This method updates the
	 * {@link OntologyIndex} according to the added axiom. The previously
	 * computed reasoner results (e.g., taxonomies) will become invalid when new
	 * axioms are added.
	 * 
	 * TODO Clarify what happens if an axiom is added twice.
	 * 
	 * @param axiom
	 */
	public void addAxiom(ElkAxiom axiom) {
		ontologyIndex.getAxiomInserter().process(axiom);
		resetStages();
	}

	/**
	 * Remove an axiom from the ontology.This method updates the
	 * {@link OntologyIndex} according to the deleted axiom. The previously
	 * computed reasoner results (e.g., taxonomies) will become invalid when new
	 * axioms are added.
	 * 
	 * TODO Clarify what happens when deleting axioms that were not added.
	 * 
	 * @param axiom
	 */
	public void removeAxiom(ElkAxiom axiom) {
		ontologyIndex.getAxiomDeleter().process(axiom);
		resetStages();
	}

	/**
	 * @return an {@link ElkAxiomProcessor} that adds axiom to the given
	 *         ontology
	 */
	protected ElkAxiomProcessor getAxiomInserter() {
		return new ElkAxiomProcessor() {
			@Override
			public void process(ElkAxiom elkAxiom) {
				addAxiom(elkAxiom);
			}
		};
	}

	/**
	 * @return an {@link ElkAxiomProcessor} that removes axiom from the given
	 *         ontology
	 */
	protected ElkAxiomProcessor getAxiomDeleter() {
		return new ElkAxiomProcessor() {
			@Override
			public void process(ElkAxiom elkAxiom) {
				removeAxiom(elkAxiom);
			}
		};
	}

	/**
	 * Check consistency of the current ontology, if this has not been done yet.
	 * 
	 * @return <tt>true</tt> if the ontology is consistent, that is,
	 *         satisfiable.
	 */
	public boolean isConsistent() {
		getStageExecutor().complete(new ConsistencyCheckingStage(this));
		return consistentOntology;
	}

	/**
	 * Compute the inferred taxonomy of the named classes for the given ontology
	 * if it has not been done yet.
	 * 
	 * @return the class taxonomy implied by the current ontology
	 */
	public Taxonomy<ElkClass> getTaxonomy()
			throws InconsistentOntologyException {
		if (!isConsistent())
			throw new InconsistentOntologyException();

		getStageExecutor().complete(new ClassTaxonomyComputationStage(this));
		return taxonomy;
	}

	/**
	 * Compute the inferred taxonomy of the named classes with instances if this
	 * has not been done yet.
	 * 
	 * @return the instance taxonomy implied by the current ontology
	 */
	public InstanceTaxonomy<ElkClass, ElkNamedIndividual> getInstanceTaxonomy()
			throws InconsistentOntologyException {
		if (!isConsistent())
			throw new InconsistentOntologyException();

		getStageExecutor().complete(new InstanceTaxonomyComputationStage(this));
		return taxonomy;
	}

	// used only in tests
	protected OntologyIndex getOntologyIndex() {
		return ontologyIndex;
	}

}
