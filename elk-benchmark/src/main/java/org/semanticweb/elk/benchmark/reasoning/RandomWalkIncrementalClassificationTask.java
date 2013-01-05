/**
 * 
 */
package org.semanticweb.elk.benchmark.reasoning;
/*
 * #%L
 * ELK Benchmarking Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.elk.benchmark.BenchmarkUtils;
import org.semanticweb.elk.benchmark.Result;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.loading.EmptyChangesLoader;
import org.semanticweb.elk.loading.OntologyLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.incremental.IncrementalChange;
import org.semanticweb.elk.reasoner.incremental.OnOffVector;
import org.semanticweb.elk.reasoner.incremental.ReversesChangeLoader;
import org.semanticweb.elk.reasoner.incremental.TrackingChangesLoader;
import org.semanticweb.elk.reasoner.incremental.TrackingOntologyLoader;
import org.semanticweb.elk.reasoner.stages.LoggingStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.PredefinedTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * TODO docs
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RandomWalkIncrementalClassificationTask implements Task {

	// logger for this class
	protected static final Logger LOGGER_ = Logger
			.getLogger(RandomWalkIncrementalClassificationTask.class);
	
	private Reasoner reasoner_;
	private final String ontologyFile_;
	private final ReasonerConfiguration reasonerConfig_;
	private OnOffVector<ElkAxiom> changingAxioms_ = new OnOffVector<ElkAxiom>(128);
	// other axioms that do not change
	private List<ElkAxiom> staticAxioms_ = new ArrayList<ElkAxiom>();
	
	/**
	 * how many test rounds is used
	 */
	static int ROUNDS = 5;
	/**
	 * how many changes are generated in every round
	 */
	static int ITERATIONS = 5;
	/**
	 * initial change size; will double with every round
	 */
	static int INITIAL_CHANGES_SIZE = 5;

	public RandomWalkIncrementalClassificationTask(String[] args) {
		ontologyFile_ = args[0];
		reasonerConfig_ = BenchmarkUtils.getReasonerConfiguration(args);
	}
	
	@Override
	public String getName() {
		return "Random walk incremental classification";
	}

	@Override
	public void prepare() throws TaskException {
		try {
			File ontologyFile = BenchmarkUtils.getFile(ontologyFile_);
			
			reasoner_ = TestReasonerUtils.createTestReasoner(
					new LoggingStageExecutor(), reasonerConfig_);
			OntologyLoader fileLoader = new Owl2StreamLoader(
					new Owl2FunctionalStyleParserFactory(), ontologyFile);
			reasoner_.registerOntologyLoader(new TrackingOntologyLoader(
					fileLoader, changingAxioms_, staticAxioms_));
			reasoner_.registerOntologyChangesLoader(new EmptyChangesLoader());
			reasoner_.loadOntology();
		} catch (Exception e) {
			throw new TaskException(e);
		}
	}

	@Override
	public Result run() throws TaskException {
		// for storing change history
		Deque<IncrementalChange> changesHistory = new LinkedList<IncrementalChange>();
		// for storing taxonomy hash history
		Deque<String> taxonomyHashHistory = new LinkedList<String>();

		// use random seed
		long seed = System.currentTimeMillis();

		String taxonomyHash;

		try {

			if (LOGGER_.isInfoEnabled())
				LOGGER_.info("Initial load of test axioms");

			reasoner_.registerOntologyChangesLoader(new EmptyChangesLoader());
			taxonomyHash = TaxonomyPrinter.getHashString(getTaxonomy(reasoner_));
			
			if (LOGGER_.isDebugEnabled())
				LOGGER_.debug("Taxonomy hash code: " + taxonomyHash);

			if (LOGGER_.isInfoEnabled())
				LOGGER_.info("Running " + ROUNDS + " rounds with " + ITERATIONS
						+ " random changes");
			
			reasoner_.setIncrementalMode(true);
			
			int changeSize = INITIAL_CHANGES_SIZE;

			for (int j = 0; j < ROUNDS; j++) {
				if (LOGGER_.isInfoEnabled())
					LOGGER_.info("Generating " + ITERATIONS
							+ " changes of size: " + changeSize);
				for (int i = 0; i < ITERATIONS; i++) {
					taxonomyHashHistory.add(taxonomyHash);
					reasoner_.registerOntologyChangesLoader(new TrackingChangesLoader(
							changingAxioms_, changesHistory, changeSize));
					taxonomyHash = TaxonomyPrinter
							.getHashString(getTaxonomy(reasoner_));
					if (LOGGER_.isDebugEnabled())
						LOGGER_.debug("Taxonomy hash code for iteration " + i
								+ ": " + taxonomyHash);
				}

				if (LOGGER_.isInfoEnabled())
					LOGGER_.info("Reverting the changes");
				for (;;) {
					IncrementalChange change = changesHistory.pollLast();
					String expectedHash = taxonomyHashHistory.pollLast();
					
					if (change == null)
						break;
					reasoner_.registerOntologyChangesLoader(new ReversesChangeLoader(
							change));
					taxonomyHash = TaxonomyPrinter
							.getHashString(getTaxonomy(reasoner_));
					
					assertEquals("Seed " + seed, expectedHash, taxonomyHash);
				}
				// doubling the change size every round
				changeSize *= 2;
			}
		}
		catch(Exception e) {
			throw new TaskException(e);
		} finally {
			try {
				reasoner_.shutdown();
			} catch (InterruptedException e) {}
		}

		return null;
	}

	
	private Taxonomy<ElkClass> getTaxonomy(Reasoner reasoner)
			throws ElkException {
		Taxonomy<ElkClass> result = null;

		try {
			result = reasoner.getTaxonomy();
		} catch (ElkInconsistentOntologyException e) {
			LOGGER_.info("Ontology is inconsistent");

			result = PredefinedTaxonomy.INCONSISTENT_CLASS_TAXONOMY;
		}

		return result;
	}

}
