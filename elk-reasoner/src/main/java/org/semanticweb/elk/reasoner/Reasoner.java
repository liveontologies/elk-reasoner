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
import org.semanticweb.elk.reasoner.incremental.RuleReApplicationEngine;
import org.semanticweb.elk.reasoner.incremental.RuleUnApplicationEngine;
import org.semanticweb.elk.reasoner.incremental.SaturationAdditionEngine;
import org.semanticweb.elk.reasoner.incremental.SaturationAdditionInitDeletedEngine;
import org.semanticweb.elk.reasoner.incremental.SaturationAdditionInitEngine;
import org.semanticweb.elk.reasoner.incremental.SaturationDeletionEngine;
import org.semanticweb.elk.reasoner.incremental.SaturationDeletionInitEngine;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.OntologyIndexImpl;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.rules.ObjectPropertySaturation;
import org.semanticweb.elk.reasoner.rules.SaturatedClassExpression;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomyEngine;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentComputation;
import org.semanticweb.elk.util.logging.Statistics;

public class Reasoner {
	// executor used to run the jobs
	protected final ExecutorService executor;
	// number of workers for concurrent jobs
	protected final int workerNo;

	protected final OntologyIndex ontologyIndex;

	protected final TaxonomyComputation taxonomyComputation;

	protected final RuleUnApplicationEngine ruleUnApplicationEngine;

	protected final SaturationDeletionInit saturationDeletionInit;

	protected final SaturationDeletion saturationDeletion;

	protected final RuleReApplicationEngine ruleReApplicationEngine;

	protected final SaturationAdditionInit saturationAdditionInit;

	protected final SaturationAdditionInitDeleted saturationAdditionInitDeleted;

	protected final SaturationAddition saturationAddition;

	protected ClassTaxonomy classTaxonomy;

	// logger for events
	protected final static Logger LOGGER_ = Logger.getLogger(Reasoner.class);

	public Reasoner(ExecutorService executor, int workerNo) {
		this.executor = executor;
		this.workerNo = workerNo;
		this.ontologyIndex = new OntologyIndexImpl();
		this.taxonomyComputation = new TaxonomyComputation(executor, workerNo,
				ontologyIndex);
		this.ruleUnApplicationEngine = new RuleUnApplicationEngine(
				ontologyIndex);
		this.saturationDeletionInit = new SaturationDeletionInit(executor,
				workerNo, ruleUnApplicationEngine);
		this.saturationDeletion = new SaturationDeletion(executor, workerNo,
				ruleUnApplicationEngine);
		this.ruleReApplicationEngine = new RuleReApplicationEngine(
				ontologyIndex);
		this.saturationAdditionInit = new SaturationAdditionInit(executor,
				workerNo, ruleReApplicationEngine);
		this.saturationAdditionInitDeleted = new SaturationAdditionInitDeleted(
				executor, workerNo, ruleReApplicationEngine);
		this.saturationAddition = new SaturationAddition(executor, workerNo,
				ruleReApplicationEngine);
		reset();
	}

	public Reasoner() {
		this(Executors.newCachedThreadPool(), Runtime.getRuntime()
				.availableProcessors());
	}

	public void reset() {
		classTaxonomy = null;
	}

	public OntologyIndex getOntologyIndex() {
		return ontologyIndex;
	}

	/**
	 * Returns null if the current state of the index is not classified.
	 */
	public ClassTaxonomy getTaxonomy() {
		return classTaxonomy;
	}

	public void addAxiom(ElkAxiom axiom) {
		ontologyIndex.getDirectAxiomInserter().process(axiom);
		classTaxonomy = null;
	}

	public void removeAxiom(ElkAxiom axiom) {
		ontologyIndex.getDirectAxiomDeleter().process(axiom);
		classTaxonomy = null;
	}

	public void addAxiomIncrementally(ElkAxiom axiom) {
		ontologyIndex.getIncrementalAxiomInserter().process(axiom);
		classTaxonomy = null;
	}

	public void removeAxiomIncrementally(ElkAxiom axiom) {
		ontologyIndex.getIncrementalAxiomDeleter().process(axiom);
		classTaxonomy = null;
	}

	public void classify(ProgressMonitor progressMonitor) {
		// number of indexed classes
		final int maxIndexedClassCount = ontologyIndex.getIndexedClassCount();
		// variable used in progress monitors
		int progress;

		// TODO: make object property saturation incremental
		ObjectPropertySaturation objectPropertySaturation = new ObjectPropertySaturation(
				executor, workerNo, ontologyIndex);

		// reverting of inferences after an incremental removal
		if (!ontologyIndex.getIndexChange().getIndexDeletions().isEmpty()) {
			if (LOGGER_.isInfoEnabled())
				LOGGER_.info("Removed entries for "
						+ ontologyIndex.getIndexChange().getIndexDeletions()
								.size() + " indexed class expressions");
			// initialization reversion of inferences
			Statistics.logOperationStart("Inferences revert init", LOGGER_);
			saturationDeletionInit.start();
			for (IndexedClassExpression ice : ontologyIndex
					.getIndexedClassExpressions()) {
				try {
					SaturatedClassExpression saturation = ice.getSaturated();
					if (saturation == null)
						continue;
					saturationDeletionInit.submit(saturation);
				} catch (InterruptedException e) {
				}
			}
			try {
				saturationDeletionInit.waitCompletion();
			} catch (InterruptedException e) {
			}
			Statistics.logOperationFinish("Inferences revert init", LOGGER_);
			Statistics.logMemoryUsage(LOGGER_);
			// reversion of inferences
			Statistics.logOperationStart("Inferences revert", LOGGER_);
			saturationDeletion.start();
			for (int i = 0; i < workerNo; i++) {
				try {
					saturationDeletion.submit(null);
				} catch (InterruptedException e) {
				}
			}
			try {
				saturationDeletion.waitCompletion();
			} catch (InterruptedException e) {
			}
			Statistics.logOperationFinish("Inferences revert", LOGGER_);
			Statistics.logMemoryUsage(LOGGER_);
			ruleUnApplicationEngine.printStatistics();
			if (LOGGER_.isInfoEnabled())
				LOGGER_.info("affected contexts: "
						+ ruleUnApplicationEngine.getUnSaturatedContexts()
								.size());
		}

		// re-application of inferences after additions
		if (!ontologyIndex.getIndexChange().getIndexAdditions().isEmpty()) {
			if (LOGGER_.isInfoEnabled())
				LOGGER_.info("Added entries for "
						+ ontologyIndex.getIndexChange().getIndexAdditions()
								.size() + " indexed class expressions");
			// initialization re-application for additions
			Statistics.logOperationStart("Inferences re-application init",
					LOGGER_);
			saturationAdditionInit.start();
			for (IndexedClassExpression ice : ontologyIndex
					.getIndexedClassExpressions()) {
				try {
					SaturatedClassExpression saturation = ice.getSaturated();
					if (saturation == null || !saturation.isSaturated())
						continue;
					saturationAdditionInit.submit(saturation);
				} catch (InterruptedException e) {
				}
			}
			try {
				saturationAdditionInit.waitCompletion();
			} catch (InterruptedException e) {
			}
			Statistics.logOperationFinish("Inferences re-application init",
					LOGGER_);
			Statistics.logMemoryUsage(LOGGER_);
		}
		// applying the changes in the index
		ontologyIndex.getIndexChange().commit();
		// initialization of re-application for deletions
		Statistics.logOperationStart("Inferences re-application deleted init",
				LOGGER_);
		saturationAdditionInitDeleted.start();
		Queue<SaturatedClassExpression> unSaturatedContexts = ruleUnApplicationEngine
				.getUnSaturatedContexts();
		for (SaturatedClassExpression saturation : unSaturatedContexts) {
			try {
				saturationAdditionInitDeleted.submit(saturation);
			} catch (InterruptedException e) {
			}
		}
		try {
			saturationAdditionInitDeleted.waitCompletion();
		} catch (InterruptedException e) {
		}
		Statistics.logOperationFinish("Inferences re-application deleted init",
				LOGGER_);
		Statistics.logMemoryUsage(LOGGER_);

		Statistics.logOperationStart("Inferences re-application", LOGGER_);
		saturationAddition.start();
		for (int i = 0; i < workerNo; i++) {
			try {
				saturationAddition.submit(null);
			} catch (InterruptedException e) {
			}
		}
		try {
			saturationAddition.waitCompletion();
		} catch (InterruptedException e) {
		}
		Statistics.logOperationFinish("Inferences re-application", LOGGER_);
		Statistics.logMemoryUsage(LOGGER_);
		ruleReApplicationEngine.printStatistics();
		if (LOGGER_.isInfoEnabled())
			LOGGER_.info("affected contexts: "
					+ ruleReApplicationEngine.getUnSaturatedContexts().size());
		// marking all contexts as saturated
		unSaturatedContexts = ruleUnApplicationEngine.getUnSaturatedContexts();
		for (;;) {
			SaturatedClassExpression context = unSaturatedContexts.poll();
			if (context == null)
				break;
			context.setSaturated();
		}
		unSaturatedContexts = ruleReApplicationEngine.getUnSaturatedContexts();
		for (;;) {
			SaturatedClassExpression context = unSaturatedContexts.poll();
			if (context == null)
				break;
			context.setSaturated();
		}

		// classification
		if (LOGGER_.isInfoEnabled())
			LOGGER_.info("Classification using " + workerNo + " workers");
		Statistics.logOperationStart("Classification", LOGGER_);
		progressMonitor.start("Classification");

		try {
			objectPropertySaturation.compute();
		} catch (InterruptedException e1) {
		}

		TaxonomyComputation taxonomyComputation = new TaxonomyComputation(
				executor, workerNo, ontologyIndex);
		progress = 0;
		taxonomyComputation.start();
		for (IndexedClass ic : ontologyIndex.getIndexedClasses()) {
			try {
				taxonomyComputation.submit(ic);
			} catch (InterruptedException e) {
			}
			progressMonitor.report(++progress, maxIndexedClassCount);
		}
		try {
			taxonomyComputation.waitCompletion();
		} catch (InterruptedException e) {
		}
		// setting all contexts as saturated
		for (IndexedClassExpression ice : ontologyIndex
				.getIndexedClassExpressions()) {
			SaturatedClassExpression saturation = ice.getSaturated();
			if (saturation == null)
				continue;
			saturation.setSaturated();
		}
		classTaxonomy = taxonomyComputation.getClassTaxonomy();
		progressMonitor.finish();
		Statistics.logOperationFinish("Classification", LOGGER_);
		Statistics.logMemoryUsage(LOGGER_);
		taxonomyComputation.printStatistics();
	}

	public void classify() {
		classify(new DummyProgressMonitor());
	}

	public void shutdown() {
		executor.shutdownNow();
	}

	class SaturationDeletionInit extends
			ConcurrentComputation<SaturatedClassExpression> {

		public SaturationDeletionInit(
				ExecutorService executor,
				int maxWorkers,
				SaturationDeletionInitEngine<SaturatedClassExpression> saturationCleanupInitEngine) {
			super(saturationCleanupInitEngine, executor, maxWorkers,
					8 * maxWorkers, 16);
		}

		public SaturationDeletionInit(ExecutorService executor, int maxWorkers,
				RuleUnApplicationEngine ruleUnApplicationEngine) {
			this(executor, maxWorkers,
					new SaturationDeletionInitEngine<SaturatedClassExpression>(
							ruleUnApplicationEngine));
		}

	}

	class SaturationDeletion extends ConcurrentComputation<Void> {

		public SaturationDeletion(ExecutorService executor, int maxWorkers,
				SaturationDeletionEngine saturationDeletionEngine) {
			super(saturationDeletionEngine, executor, maxWorkers, maxWorkers, 1);
		}

		public SaturationDeletion(ExecutorService executor, int maxWorkers,
				RuleUnApplicationEngine ruleUnApplicationEngine) {
			this(executor, maxWorkers, new SaturationDeletionEngine(
					ruleUnApplicationEngine));
		}
	}

	class SaturationAdditionInitDeleted extends
			ConcurrentComputation<SaturatedClassExpression> {

		public SaturationAdditionInitDeleted(
				ExecutorService executor,
				int maxWorkers,
				SaturationAdditionInitDeletedEngine<SaturatedClassExpression> saturationAdditionInitDeletedEngine) {
			super(saturationAdditionInitDeletedEngine, executor, maxWorkers,
					8 * maxWorkers, 16);
		}

		public SaturationAdditionInitDeleted(ExecutorService executor,
				int maxWorkers, RuleReApplicationEngine ruleReApplicationEngine) {
			this(
					executor,
					maxWorkers,
					new SaturationAdditionInitDeletedEngine<SaturatedClassExpression>(
							ruleReApplicationEngine));
		}

	}

	class SaturationAdditionInit extends
			ConcurrentComputation<SaturatedClassExpression> {

		public SaturationAdditionInit(
				ExecutorService executor,
				int maxWorkers,
				SaturationAdditionInitEngine<SaturatedClassExpression> saturationAdditionInitEngine) {
			super(saturationAdditionInitEngine, executor, maxWorkers,
					8 * maxWorkers, 16);
		}

		public SaturationAdditionInit(ExecutorService executor, int maxWorkers,
				RuleReApplicationEngine ruleReApplicationEngine) {
			this(executor, maxWorkers,
					new SaturationAdditionInitEngine<SaturatedClassExpression>(
							ruleReApplicationEngine));
		}

	}

	class SaturationAddition extends ConcurrentComputation<Void> {

		public SaturationAddition(ExecutorService executor, int maxWorkers,
				SaturationAdditionEngine saturationAdditionEngine) {
			super(saturationAdditionEngine, executor, maxWorkers, maxWorkers, 1);
		}

		public SaturationAddition(ExecutorService executor, int maxWorkers,
				RuleReApplicationEngine ruleReApplicationEngine) {
			this(executor, maxWorkers, new SaturationAdditionEngine(
					ruleReApplicationEngine));
		}
	}

	/**
	 * Concurrent computation of the taxonomy
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
	}
}
