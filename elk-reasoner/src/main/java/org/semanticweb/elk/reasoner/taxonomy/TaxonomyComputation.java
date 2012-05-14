/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.saturation.properties.ObjectPropertySaturation;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentComputation;
import org.semanticweb.elk.util.logging.Statistics;

/**
 * Class for computing taxonomies for classification and instance retrieval tasks.
 * 
 * 
 * @author Frantisek Simancik
 * 
 */
public class TaxonomyComputation extends
		ConcurrentComputation<IndexedClassEntity> {

	protected final static Logger LOGGER_ = Logger
			.getLogger(ObjectPropertySaturation.class);

	protected final ProgressMonitor progressMonitor;
	protected final OntologyIndex ontologyIndex;
	protected final TaxonomyComputationEngine taxonomyComputationEngine;

	protected TaxonomyComputation(ExecutorService executor, int maxWorkers,
			ProgressMonitor progressMonitor, OntologyIndex ontologyIndex,
			TaxonomyComputationEngine taxonomyComputationEngine) {
		super(taxonomyComputationEngine, executor, maxWorkers, 8 * maxWorkers, 16);
		this.progressMonitor = progressMonitor;
		this.ontologyIndex = ontologyIndex;
		this.taxonomyComputationEngine = taxonomyComputationEngine;
	}

	public TaxonomyComputation(ExecutorService executor, int maxWorkers,
			ProgressMonitor progressMonitor, OntologyIndex ontologyIndex) {
		this(executor, maxWorkers, progressMonitor, ontologyIndex,
				new TaxonomyComputationEngine(ontologyIndex));
	}

	public TaxonomyComputation(ExecutorService executor, int maxWorkers,
			ProgressMonitor progressMonitor, OntologyIndex ontologyIndex,
			IndividualClassTaxonomy partialTaxonomy) {
		this(executor, maxWorkers, progressMonitor, ontologyIndex,
				new TaxonomyComputationEngine(ontologyIndex, partialTaxonomy));
	}

	/**
	 * Prerequisites: object properties must be already saturated and the
	 * ontology must be consistent.
	 * 
	 */
	public IndividualClassTaxonomy computeTaxonomy(boolean includeClasses,
			boolean includeIndividuals) {

		if (LOGGER_.isInfoEnabled())
			LOGGER_.info("Classification using " + maxWorkers + " workers");
		Statistics.logOperationStart("Classification", LOGGER_);
		progressMonitor.start("Classification");

		// number of indexed entities to classify
		final int maxProgress = (includeClasses ? ontologyIndex
				.getIndexedClassCount() : 0)
				+ (includeIndividuals ? ontologyIndex
						.getIndexedIndividualCount() : 0);
		// variable used in progress monitors
		int progress = 0;
		start();

		try {
			if (includeClasses)
				for (IndexedClass ic : ontologyIndex.getIndexedClasses()) {
					submit(ic);
					progressMonitor.report(++progress, maxProgress);
				}
			if (includeIndividuals)
				for (IndexedIndividual ind : ontologyIndex
						.getIndexedIndividuals()) {
					submit(ind);
					progressMonitor.report(++progress, maxProgress);
				}
			waitCompletion();
		} catch (InterruptedException e) {
			// FIXME Either document why this is ignored or do something
			// better.
		}

		Statistics.logOperationFinish("Classification", LOGGER_);
		Statistics.logMemoryUsage(LOGGER_);
		taxonomyComputationEngine.printStatistics();
		progressMonitor.finish();

		return taxonomyComputationEngine.getClassTaxonomy();
	}

}