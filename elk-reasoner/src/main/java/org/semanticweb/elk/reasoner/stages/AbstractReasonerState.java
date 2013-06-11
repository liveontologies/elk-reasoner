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
import org.semanticweb.elk.loading.AxiomChangeListener;
import org.semanticweb.elk.loading.ChangesLoader;
import org.semanticweb.elk.loading.ElkAxiomChange;
import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.OntologyLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.datatypes.handlers.ElkDatatypeHandler;
import org.semanticweb.elk.reasoner.incremental.NonIncrementalChangeListener;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ChangeIndexingProcessor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.DifferentialIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkAxiomIndexingVisitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectCache;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.MainAxiomIndexerVisitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.NonIncrementalChangeCheckingVisitor;
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
	 * the object using which axioms are inserted into the index
	 */
	private final ElkAxiomIndexingVisitor axiomInserter_;
	/**
	 * the object using which axioms are deleted from the index
	 */
	private final ElkAxiomIndexingVisitor axiomDeleter_;
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
	 * Datatype manager
	 */
	private final ElkDatatypeHandler datatypeHandler;
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
	/**
	 * if {@code true}, reasoning will be done incrementally whenever possible
	 */
	private boolean allowIncrementalMode_ = true;

	private boolean allowIncrementalTaxonomy_ = true;
	/**
	 * Setting this to true during the change loading stage indicates that some
	 * change, most likely some role axioms, should switch the reasoning mode to
	 * non-incremental
	 */
	private boolean nonIncrementalChange_ = false;
	/**
	 * The listener used to switch off the incremental mode
	 */
	private NonIncrementalChangeListener<ElkAxiom> nonIncrementalChangeListener_ = new NonIncrementalChangeListener<ElkAxiom>() {

		@Override
		public void notify(ElkAxiom axiom) {
			if (LOGGER_.isDebugEnabled()) {
				LOGGER_.debug("Disallowing incremental mode due to "
						+ OwlFunctionalStylePrinter.toString(axiom));
			}

			nonIncrementalChange_ = true;
		}
	};

	protected AbstractReasonerState(OntologyLoader ontologyLoader) {
		this.objectCache_ = new IndexedObjectCache();
		this.ontologyIndex = new DifferentialIndex(objectCache_);
		this.datatypeHandler = new ElkDatatypeHandler();
		this.axiomInserter_ = new MainAxiomIndexerVisitor(ontologyIndex, datatypeHandler, true);
		this.axiomDeleter_ = new MainAxiomIndexerVisitor(ontologyIndex, datatypeHandler, false);
		this.saturationState = SaturationStateFactory.createSaturationState(ontologyIndex);
		this.ruleAndConclusionStats = new SaturationStatistics();
		this.stageManager = new ReasonerStageManager(this);
		this.ontologyLoader_ = ontologyLoader
				.getLoader(new ChangeIndexingProcessor(axiomInserter_));
	}

	public void setAllowIncrementalMode(boolean allow) {
		this.allowIncrementalMode_ = allow;

		if (!allow) {
			trySetIncrementalMode(false);
			allowIncrementalTaxonomy_ = false;
		}

		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Incremental mode is "
					+ (allow ? "allowed" : "disallowed"));
		}
	}

	public boolean isIncrementalMode() {
		return ontologyIndex.isIncrementalMode();
	}

	void trySetIncrementalMode(boolean mode) {
		if (!allowIncrementalMode_ && mode)
			// switching to incremental mode not allowed
			return;

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

		if (changesLoader_ == null) {
			return null;
		}

		// wrapping both the inserter and the deleter to receive notifications
		// if some axiom change can't be incorporated incrementally
		ElkAxiomProcessor inserterWrapper = new ChangeIndexingProcessor(
				new NonIncrementalChangeCheckingVisitor(axiomInserter_,
						nonIncrementalChangeListener_));
		ElkAxiomProcessor deleterWrapper = new ChangeIndexingProcessor(
				new NonIncrementalChangeCheckingVisitor(axiomDeleter_,
						nonIncrementalChangeListener_));

		return changesLoader_.getLoader(inserterWrapper, deleterWrapper);
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
		
		ruleAndConclusionStats.reset();
		loadChanges();

		if (isIncrementalMode() && !saturationState.getContexts().isEmpty()) {
			getStageExecutor().complete(
					stageManager.incrementalConsistencyCheckingStage);
		} else {
			getStageExecutor().complete(stageManager.consistencyCheckingStage);
			stageManager.incrementalConsistencyCheckingStage.setCompleted();
		}

		return inconsistentOntology;
	}

	/**
	 * Forces the reasoner to load ontology
	 * 
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public void loadOntology() throws ElkException {
		trySetIncrementalMode(false);
		getStageExecutor().complete(stageManager.ontologyLoadingStage);
	}

	/**
	 * Forces the reasoner to reload ontology changes
	 * 
	 * @throws ElkException
	 *             if the reasoning process cannot be completed successfully
	 */
	public void loadChanges() throws ElkException {
		trySetIncrementalMode(true);
		
		nonIncrementalChange_ = false;

		getStageExecutor().complete(stageManager.changesLoadingStage);

		if (nonIncrementalChange_) {
			/*
			 * the mode was switched to non-incremental during change loading.
			 * As such, we should recompute everything, e.g., the role
			 * saturation
			 */
			stageManager.propertyInitializationStage.invalidate();
			setAllowIncrementalMode(false);
		}
		
		nonIncrementalChange_ = false;
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
	public Taxonomy<ElkClass> getTaxonomy()
			throws ElkInconsistentOntologyException, ElkException {

		ruleAndConclusionStats.reset();
		
		if (isInconsistent())
			throw new ElkInconsistentOntologyException();

		if (isIncrementalMode() && classTaxonomyState.getTaxonomy() != null) {
			getStageExecutor().complete(
					stageManager.incrementalClassTaxonomyComputationStage);
		} else {
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
	public Taxonomy<ElkClass> getTaxonomyQuietly() throws ElkException {
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
	public InstanceTaxonomy<ElkClass, ElkNamedIndividual> getInstanceTaxonomy()
			throws ElkException {

		ruleAndConclusionStats.reset();
		
		if (isInconsistent())
			throw new ElkInconsistentOntologyException();

		if (isIncrementalMode() && instanceTaxonomyState.getTaxonomy() != null) {
			getStageExecutor().complete(
					stageManager.incrementalInstanceTaxonomyComputationStage);
		} else {
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
	public InstanceTaxonomy<ElkClass, ElkNamedIndividual> getInstanceTaxonomyQuietly()
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
	public Set<ElkClass> getAllClasses() {
		Set<ElkClass> result = new ArrayHashSet<ElkClass>(ontologyIndex
				.getIndexedClasses().size());
		for (IndexedClass ic : ontologyIndex.getIndexedClasses())
			result.add(ic.getElkClass());
		return result;
	}

	/**
	 * @return all {@link ElkNamedIndividual}s occurring in the ontology
	 */
	public Set<ElkNamedIndividual> getAllNamedIndividuals() {
		Set<ElkNamedIndividual> allNamedIndividuals = new ArrayHashSet<ElkNamedIndividual>(
				ontologyIndex.getIndexedClasses().size());
		for (IndexedIndividual ii : ontologyIndex.getIndexedIndividuals())
			allNamedIndividuals.add(ii.getElkNamedIndividual());
		return allNamedIndividuals;
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

	public void initClassTaxonomy() {
		classTaxonomyState.getWriter().setTaxonomy(
				new ConcurrentClassTaxonomy());
	}

	public void initInstanceTaxonomy() {
		instanceTaxonomyState.initTaxonomy(new ConcurrentInstanceTaxonomy(
				classTaxonomyState.getTaxonomy()));
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
