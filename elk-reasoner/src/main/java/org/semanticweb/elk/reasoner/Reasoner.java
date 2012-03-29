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
package org.semanticweb.elk.reasoner;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAxiom;
import org.semanticweb.elk.owl.interfaces.ElkPropertyAxiom;
import org.semanticweb.elk.reasoner.incremental.ContextModificationListener;
import org.semanticweb.elk.reasoner.incremental.IncrementalRuleApplicationEngine;
import org.semanticweb.elk.reasoner.incremental.SaturationChangesInitEngine;
import org.semanticweb.elk.reasoner.incremental.SaturationContextInitEngine;
import org.semanticweb.elk.reasoner.incremental.SaturationProcessEngine;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.OntologyIndexImpl;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.rules.ObjectPropertySaturation;
import org.semanticweb.elk.reasoner.rules.SaturatedClassExpression;
import org.semanticweb.elk.reasoner.taxonomy.ClassNode;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomyEngine;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyCleanerEngine;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentComputation;
import org.semanticweb.elk.util.logging.Statistics;

public class Reasoner {
	/**
	 * the executor for running concurrent jobs
	 */
	protected final ExecutorService executor;
	/**
	 * the limit on the number of concurrent jobs
	 */
	protected final int workerNo;
	/**
	 * indexed representation of the ontology
	 */
	protected final OntologyIndex ontologyIndex;
	/**
	 * the listener used for collecting information about modified contexts
	 * during incremental classification
	 */
	protected final ContextModificationListener contextModificationListener;
	/**
	 * the engine used to de-apply inference rules
	 */
	protected final IncrementalRuleApplicationEngine ruleDeApplicationEngine;
	/**
	 * the engine used to re-apply inference rules
	 */
	protected final IncrementalRuleApplicationEngine ruleReApplicationEngine;
	/**
	 * the engine to clean the modified contexts by de-applying inferences for
	 * them; it should not modify contexts that are saturated
	 */
	protected final IncrementalRuleApplicationEngine ruleCleaningEngine;
	/**
	 * initializing saturation update for deleted index entries
	 */
	protected final SaturationChangesInit saturationChangesDeletionInit;
	/**
	 * performing saturation update for deletions
	 */
	protected final SaturationProcessor saturationChangesDeletion;
	/**
	 * initializing removal of computed saturations for modified contexts
	 */
	protected final SaturationContextInit saturationCleaningInit;
	/**
	 * removal of computed saturations for modified contexts; after removal the
	 * saturation for contexts should be empty
	 */
	protected final SaturationProcessor saturationCleaning;
	/**
	 * initializing saturation update for added index entries
	 */
	protected final SaturationChangesInit saturationChangesAdditionInit;
	/**
	 * initializing saturation update for contexts with empty saturation
	 */
	protected final SaturationContextInit saturationContextInit;
	/**
	 * performing saturation update for additions
	 */
	protected final SaturationProcessor saturationAddition;
	/**
	 * computation of the taxonomy
	 */
	protected final TaxonomyComputation taxonomyComputation;
	/**
	 * cleaning of the taxonomy
	 */
	protected final TaxonomyCleaner taxonomyCleaner;

	/**
	 * if <tt>true</tt> the next classification will be performed incrementally
	 */
	protected boolean incrementalMode;

	// logger for events
	protected final static Logger LOGGER_ = Logger.getLogger(Reasoner.class);

	public Reasoner(ExecutorService executor, int workerNo) {
		this.executor = executor;
		this.workerNo = workerNo;
		this.ontologyIndex = new OntologyIndexImpl();
		this.contextModificationListener = new ContextModificationListener();
		this.ruleDeApplicationEngine = new IncrementalRuleApplicationEngine(
				ontologyIndex, contextModificationListener, true, true);
		this.ruleCleaningEngine = new IncrementalRuleApplicationEngine(
				ontologyIndex, contextModificationListener, true, false);
		this.ruleReApplicationEngine = new IncrementalRuleApplicationEngine(
				ontologyIndex, contextModificationListener, false, true);

		this.saturationChangesDeletionInit = new SaturationChangesInit(
				executor, workerNo, ruleDeApplicationEngine);
		this.saturationChangesDeletion = new SaturationProcessor(executor,
				workerNo, ruleDeApplicationEngine);

		this.saturationCleaningInit = new SaturationContextInit(executor,
				workerNo, ruleCleaningEngine);
		this.saturationCleaning = new SaturationProcessor(executor, workerNo,
				ruleCleaningEngine);

		this.saturationChangesAdditionInit = new SaturationChangesInit(
				executor, workerNo, ruleReApplicationEngine);
		this.saturationContextInit = new SaturationContextInit(executor,
				workerNo, ruleReApplicationEngine);
		this.saturationAddition = new SaturationProcessor(executor, workerNo,
				ruleReApplicationEngine);

		this.taxonomyComputation = new TaxonomyComputation(executor, workerNo,
				ontologyIndex);
		this.taxonomyCleaner = taxonomyComputation.getTaxonomyCleaner();

		incrementalMode = true;
	}

	public Reasoner() {
		this(Executors.newCachedThreadPool(), Runtime.getRuntime()
				.availableProcessors());
	}

	public OntologyIndex getOntologyIndex() {
		return ontologyIndex;
	}

	/**
	 * @return the class taxonomy computed by the reasoner by the last
	 *         classification or <tt>null</tt> if no classification has been
	 *         done yet
	 */
	public ClassTaxonomy getTaxonomy() {
		return taxonomyComputation.getClassTaxonomy();
	}

	/**
	 * switching to non-incremental mode
	 */
	protected void switchOffIncrementalMode() {
		if (incrementalMode) {
			incrementalMode = false;
			// applying incremental changes to the index
			ontologyIndex.getIndexChange().commit();
		}
	}

	/**
	 * Loads an axiom to the reasoner non-incrementally; the ontology will be
	 * classified from scratch after such a modification
	 * 
	 * @param axiom
	 *            the axiom to be loaded
	 */
	public void addAxiom(ElkAxiom axiom) {
		switchOffIncrementalMode();
		ontologyIndex.getDirectAxiomInserter().process(axiom);
	}

	/**
	 * Removes an axiom from the reasoner non-incrementally; the ontology will
	 * be classified from scratch after such a modification. The reasoner will
	 * not check if the given axiom was previously loaded and might produce
	 * incorrect results if it was not the case
	 * 
	 * @param axiom
	 *            the axiom to be removed
	 */
	public void removeAxiom(ElkAxiom axiom) {
		switchOffIncrementalMode();
		ontologyIndex.getDirectAxiomDeleter().process(axiom);
	}

	/**
	 * Loads an axiom to the reasoner incrementally; the next classification
	 * will be performed incrementally unless some axiom has been loaded or
	 * removed non-incrementally since the last classification. Incremental
	 * addition for instances of {@link ElkObjectPropertyAxiom},
	 * {@link ElkDataPropertyAxiom}, and {@link ElkPropertyAxiom} is currently
	 * not supported. When this method is called for such an axiom, addition
	 * will be done non-incrementally.
	 * 
	 * @param axiom
	 */
	public void addAxiomIncrementally(ElkAxiom axiom) {
		if (axiom instanceof ElkObjectPropertyAxiom
				|| axiom instanceof ElkDataPropertyAxiom
				|| axiom instanceof ElkPropertyAxiom<?>)
			switchOffIncrementalMode();
		if (incrementalMode)
			ontologyIndex.getIncrementalAxiomInserter().process(axiom);
		else
			ontologyIndex.getDirectAxiomInserter().process(axiom);
	}

	/**
	 * Removes an axiom from the reasoner incrementally; the next classification
	 * will be performed incrementally unless some axiom has been loaded or
	 * removed non-incrementally since the last classification. Incremental
	 * addition for instances of {@link ElkObjectPropertyAxiom},
	 * {@link ElkDataPropertyAxiom}, and {@link ElkPropertyAxiom} is currently
	 * not supported. When this method is called for such an axiom, removal will
	 * be done non-incrementally.
	 * 
	 * @param axiom
	 */
	public void removeAxiomIncrementally(ElkAxiom axiom) {
		if (axiom instanceof ElkObjectPropertyAxiom
				|| axiom instanceof ElkDataPropertyAxiom
				|| axiom instanceof ElkPropertyAxiom<?>)
			switchOffIncrementalMode();
		if (incrementalMode)
			ontologyIndex.getIncrementalAxiomDeleter().process(axiom);
		else
			ontologyIndex.getDirectAxiomDeleter().process(axiom);
	}

	public void classify(ProgressMonitor progressMonitor)
			throws InterruptedException {
		// number of indexed classes
		final int maxIndexedClassCount = ontologyIndex.getIndexedClassCount();
		// variable used in progress monitors
		int progress;

		// TODO: make object property saturation incremental
		ObjectPropertySaturation objectPropertySaturation = new ObjectPropertySaturation(
				executor, workerNo, ontologyIndex);

		if (LOGGER_.isInfoEnabled())
			LOGGER_.info("Classification using " + workerNo + " workers");

		if (!incrementalMode) {
			Statistics.logOperationStart("Classification", LOGGER_);
			taxonomyComputation.getClassTaxonomy().clear();
			objectPropertySaturation.compute();

			// discarding all previously computed saturations
			for (IndexedClassExpression ice : ontologyIndex
					.getIndexedClassExpressions()) {
				ice.resetSaturated();
			}

			// taxonomy computation
			progressMonitor.start("Classification");
			progress = 0;
			taxonomyComputation.start();
			for (IndexedClass ic : ontologyIndex.getIndexedClasses()) {
				taxonomyComputation.submit(ic);
				progressMonitor.report(++progress, maxIndexedClassCount);
			}
			taxonomyComputation.waitCompletion();
			progressMonitor.finish();
			Statistics.logOperationFinish("Classification", LOGGER_);
			Statistics.logMemoryUsage(LOGGER_);
			taxonomyComputation.printStatistics();

		} else {
			Statistics.logOperationStart("Incremental classification", LOGGER_);

			boolean deletionMode = !ontologyIndex.getIndexChange()
					.getIndexDeletions().isEmpty();

			if (deletionMode) {
				// deletion of inferences after removal
				if (LOGGER_.isDebugEnabled())
					LOGGER_.debug("Removed entries for "
							+ ontologyIndex.getIndexChange()
									.getIndexDeletions().size()
							+ " indexed class expressions");
				Statistics.logOperationStart("Reverting deleted inferences",
						LOGGER_);
				// initialization reversion of inferences
				saturationChangesDeletionInit.start();
				for (IndexedClassExpression ice : ontologyIndex
						.getIndexedClassExpressions()) {
					SaturatedClassExpression saturation = ice.getSaturated();
					if (saturation == null)
						continue;
					saturationChangesDeletionInit.submit(saturation);
				}
				saturationChangesDeletionInit.waitCompletion();
				// reversion of inferences
				saturationChangesDeletion.start();
				for (int i = 0; i < workerNo; i++) {
					saturationChangesDeletion.submit(null);
				}
				saturationChangesDeletion.waitCompletion();
				Statistics.logOperationFinish("Reverting deleted inferences",
						LOGGER_);
				Statistics.logMemoryUsage(LOGGER_);
				ruleDeApplicationEngine.printStatistics();
				if (LOGGER_.isDebugEnabled())
					LOGGER_.debug("overall modified contexts: "
							+ contextModificationListener.getModifiedContexts()
									.size());
				// cleaning of modified contexts
				Statistics.logOperationStart("Cleaning of modified contexts",
						LOGGER_);
				// initialization of cleaning
				saturationCleaningInit.start();
				for (SaturatedClassExpression context : contextModificationListener
						.getModifiedContexts()) {
					saturationCleaningInit.submit(context);
				}
				saturationCleaningInit.waitCompletion();
				// cleaning
				saturationCleaning.start();
				for (int i = 0; i < workerNo; i++) {
					saturationCleaning.submit(null);
				}
				saturationCleaning.waitCompletion();
				Statistics.logOperationFinish("Cleaning of modified contexts",
						LOGGER_);
				Statistics.logMemoryUsage(LOGGER_);
				ruleCleaningEngine.printStatistics();
			}

			boolean contextsCleaned = !contextModificationListener
					.getModifiedContexts().isEmpty();

			boolean additionMode = !ontologyIndex.getIndexChange()
					.getIndexAdditions().isEmpty();

			// re-application of inferences after removal
			if (additionMode || contextsCleaned) {
				Statistics.logOperationStart("Inferences re-application",
						LOGGER_);
			}

			// re-application of inferences after additions
			if (additionMode) {
				// initialization re-application for additions
				saturationChangesAdditionInit.start();
				for (IndexedClassExpression ice : ontologyIndex
						.getIndexedClassExpressions()) {
					SaturatedClassExpression saturation = ice.getSaturated();
					if (saturation == null || !saturation.isSaturated())
						continue;
					saturationChangesAdditionInit.submit(saturation);
				}
				saturationChangesAdditionInit.waitCompletion();
			}
			// applying the changes in the index
			ontologyIndex.getIndexChange().commit();

			if (contextsCleaned) {
				// initialization of re-application for deletions
				saturationContextInit.start();
				Queue<SaturatedClassExpression> unSaturatedContexts = contextModificationListener
						.getModifiedContexts();
				for (SaturatedClassExpression saturation : unSaturatedContexts) {
					saturationContextInit.submit(saturation);
				}
				saturationContextInit.waitCompletion();
			}

			if (additionMode || contextsCleaned) {
				saturationAddition.start();
				for (int i = 0; i < workerNo; i++) {
					saturationAddition.submit(null);
				}
				saturationAddition.waitCompletion();
				Statistics.logOperationFinish("Inferences re-application",
						LOGGER_);
				Statistics.logMemoryUsage(LOGGER_);
				ruleReApplicationEngine.printStatistics();
				if (LOGGER_.isDebugEnabled())
					LOGGER_.debug("overall modified contexts: "
							+ contextModificationListener.getModifiedContexts()
									.size());

				// updating the taxonomy
				Statistics.logOperationStart("Taxonomy update", LOGGER_);
				// cleaning the taxonomy using modified contexts
				taxonomyCleaner.start();
				Queue<SaturatedClassExpression> modifiedContexts = contextModificationListener
						.getModifiedContexts();
				// marking modified contexts as saturated and submitting those
				// whose roots are classes to the taxonomy cleaner
				for (;;) {
					SaturatedClassExpression context = modifiedContexts.poll();
					if (context == null)
						break;
					context.setSaturated();
					IndexedClassExpression root = context.getRoot();
					if (root instanceof IndexedClass) {
						taxonomyCleaner.submit(((IndexedClass) root)
								.getElkClass());
					}
				}
				// cleaning the taxonomy for the removed classes
				for (ElkClass elkClass : ontologyIndex.getIndexChange()
						.getRemovedClasses()) {
					taxonomyCleaner.submit(elkClass);
				}
				taxonomyCleaner.waitCompletion();

				taxonomyComputation.start();
				ClassTaxonomy taxonomy = taxonomyComputation.getClassTaxonomy();
				for (IndexedClass ic : ontologyIndex.getIndexedClasses()) {
					ClassNode node = taxonomy.getNode(ic.getElkClass());
					if (node == null || node.isModified())
						taxonomyComputation.submit(ic);
				}
				taxonomyComputation.waitCompletion();
				// we do not need the changes in the signature anymore
				ontologyIndex.getIndexChange().clearSignatureChange();

				Statistics.logOperationFinish("Taxonomy update", LOGGER_);
				Statistics.logMemoryUsage(LOGGER_);

			}
			Statistics
					.logOperationFinish("Incremental classification", LOGGER_);
			Statistics.logMemoryUsage(LOGGER_);
			taxonomyComputation.printStatistics();
		}

		// trying to classify incrementally next time
		incrementalMode = true;
	}

	public void classify() throws InterruptedException {
		classify(new DummyProgressMonitor());
	}

	public void shutdown() {
		executor.shutdownNow();
	}

	/**
	 * The computation to initialize modification of the saturations using
	 * changes made in the index used for incremental classification
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	class SaturationChangesInit extends
			ConcurrentComputation<SaturatedClassExpression> {

		public SaturationChangesInit(
				ExecutorService executor,
				int maxWorkers,
				SaturationChangesInitEngine<SaturatedClassExpression> saturationChangesInitEngine) {
			super(saturationChangesInitEngine, executor, maxWorkers,
					8 * maxWorkers, 16);
		}

		public SaturationChangesInit(ExecutorService executor, int maxWorkers,
				IncrementalRuleApplicationEngine ruleApplicationEngine) {
			this(executor, maxWorkers,
					new SaturationChangesInitEngine<SaturatedClassExpression>(
							ruleApplicationEngine));
		}
	}

	/**
	 * The computation to initialize computation of saturated class expressions
	 * from the beginning used for incremental classification
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	class SaturationContextInit extends
			ConcurrentComputation<SaturatedClassExpression> {

		public SaturationContextInit(
				ExecutorService executor,
				int maxWorkers,
				SaturationContextInitEngine<SaturatedClassExpression> saturationContextInitEngine) {
			super(saturationContextInitEngine, executor, maxWorkers,
					8 * maxWorkers, 16);
		}

		public SaturationContextInit(ExecutorService executor, int maxWorkers,
				IncrementalRuleApplicationEngine ruleApplicationEngine) {
			this(executor, maxWorkers,
					new SaturationContextInitEngine<SaturatedClassExpression>(
							ruleApplicationEngine));
		}
	}

	/**
	 * The computation to process the saturation used for incremental
	 * classification
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	class SaturationProcessor extends ConcurrentComputation<Void> {

		public SaturationProcessor(ExecutorService executor, int maxWorkers,
				SaturationProcessEngine saturationProcessEngine) {
			super(saturationProcessEngine, executor, maxWorkers, maxWorkers, 1);
		}

		public SaturationProcessor(ExecutorService executor, int maxWorkers,
				IncrementalRuleApplicationEngine ruleApplicationEngine) {
			this(executor, maxWorkers, new SaturationProcessEngine(
					ruleApplicationEngine));
		}
	}

	/**
	 * Concurrent computation of the taxonomy which triggers saturation
	 * computation if necessary
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	class TaxonomyComputation extends ConcurrentComputation<IndexedClass> {

		final ClassTaxonomyEngine classTaxonomyEngine;

		public TaxonomyComputation(ExecutorService executor, int maxWorkers,
				ClassTaxonomyEngine classTaxonomyEngine) {
			super(classTaxonomyEngine, executor, maxWorkers, 8 * maxWorkers, 16);
			this.classTaxonomyEngine = classTaxonomyEngine;
		}

		public TaxonomyComputation(ExecutorService executor, int maxWorkers,
				OntologyIndex ontologyIndex) {
			this(executor, maxWorkers, new ClassTaxonomyEngine(ontologyIndex));
		}

		public ClassTaxonomy getClassTaxonomy() {
			return classTaxonomyEngine.getClassTaxonomy();
		}

		public void printStatistics() {
			classTaxonomyEngine.printStatistics();
		}

		public TaxonomyCleaner getTaxonomyCleaner() {
			return new TaxonomyCleaner(executor, maxWorkers,
					classTaxonomyEngine);
		}
	}

	class TaxonomyCleaner extends ConcurrentComputation<ElkClass> {

		final TaxonomyCleanerEngine classTaxonomyCleaner;

		public TaxonomyCleaner(ExecutorService executor, int maxWorkers,
				TaxonomyCleanerEngine classTaxonomyCleaner) {
			super(classTaxonomyCleaner, executor, maxWorkers, 8 * maxWorkers,
					16);
			this.classTaxonomyCleaner = classTaxonomyCleaner;
		}

		public TaxonomyCleaner(ExecutorService executor, int maxWorkers,
				ClassTaxonomyEngine classTaxonomyEngine) {
			this(executor, maxWorkers, classTaxonomyEngine.getTaxonomyCleaner());
		}
	}

}
