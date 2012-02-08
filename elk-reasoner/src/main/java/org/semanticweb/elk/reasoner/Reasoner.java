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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.OntologyIndexImpl;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.rules.ObjectPropertySaturation;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomyEngine;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentComputation;
import org.semanticweb.elk.util.logging.Statistics;

public class Reasoner {
	// executor used to run the jobs
	protected final ExecutorService executor;
	// number of workers for concurrent jobs
	protected final int workerNo;

	protected OntologyIndex ontologyIndex;

	protected ClassTaxonomy classTaxonomy;

	// logger for events
	protected final static Logger LOGGER_ = Logger.getLogger(Reasoner.class);

	public Reasoner(ExecutorService executor, int workerNo) {
		this.executor = executor;
		this.workerNo = workerNo;
		reset();
	}

	public Reasoner() {
		this(Executors.newCachedThreadPool(), Runtime.getRuntime()
				.availableProcessors());
	}

	public void reset() {
		ontologyIndex = new OntologyIndexImpl();
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
		ontologyIndex.getAxiomInserter().process(axiom);
		classTaxonomy = null;
	}

	public void removeAxiom(ElkAxiom axiom) {
		ontologyIndex.getAxiomDeleter().process(axiom);
		classTaxonomy = null;
	}

	public void classify(ProgressMonitor progressMonitor) {
		// number of indexed classes
		final int maxIndexedClassCount = ontologyIndex.getIndexedClassCount();
		// variable used in progress monitors
		int progress;

		// Saturation stage
		ObjectPropertySaturation objectPropertySaturation = new ObjectPropertySaturation(
				executor, workerNo, ontologyIndex);

		TaxonomyComputation taxonomyComputation = new TaxonomyComputation(
				executor, workerNo, ontologyIndex);

		if (LOGGER_.isInfoEnabled())
			LOGGER_.info("Classification using " + workerNo + " workers");
		Statistics.logOperationStart("Classification", LOGGER_);
		progressMonitor.start("Classification");

		try {
			objectPropertySaturation.compute();
		} catch (InterruptedException e1) {
		}

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
		classTaxonomy = taxonomyComputation.getClassTaxonomy();
		progressMonitor.finish();
		Statistics.logOperationFinish("Classification", LOGGER_);
		Statistics.logMemoryUsage(LOGGER_);
	}

	public void classify() {
		classify(new DummyProgressMonitor());
	}

	public void shutdown() {
		executor.shutdownNow();
	}

	public class TaxonomyComputation extends
			ConcurrentComputation<IndexedClass> {

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
	}
}
