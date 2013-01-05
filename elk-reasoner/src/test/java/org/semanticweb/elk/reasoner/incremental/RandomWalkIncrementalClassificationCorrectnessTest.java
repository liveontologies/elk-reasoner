/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.loading.EmptyChangesLoader;
import org.semanticweb.elk.loading.OntologyLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.ClassTaxonomyTestOutput;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasoningTestManifest;
import org.semanticweb.elk.reasoner.TaxonomyDiffManifest;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.PredefinedTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.io.URLTestIO;

/**
 * @author "Yevgeny Kazakov"
 * 
 */
@RunWith(PolySuite.class)
public class RandomWalkIncrementalClassificationCorrectnessTest {

	// logger for this class
	protected static final Logger LOGGER_ = Logger
			.getLogger(RandomWalkIncrementalClassificationCorrectnessTest.class);

	final static String INPUT_DATA_LOCATION = "classification_test_input";

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

	protected final ReasoningTestManifest<ClassTaxonomyTestOutput, ClassTaxonomyTestOutput> manifest;

	public RandomWalkIncrementalClassificationCorrectnessTest(
			ReasoningTestManifest<ClassTaxonomyTestOutput, ClassTaxonomyTestOutput> testManifest) {
		manifest = testManifest;
	}

	@Before
	public void before() throws IOException, Owl2ParseException {
		assumeTrue(!ignore(manifest.getInput()));

	}

	@SuppressWarnings("static-method")
	protected boolean ignore(TestInput input) {
		return false;
	}

	/**
	 * The main test method
	 * 
	 * @throws ElkException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Test
	public void randomWalk() throws ElkException, InterruptedException,
			IOException {

		// axioms that can change
		OnOffVector<ElkAxiom> changingAxioms = new OnOffVector<ElkAxiom>(128);
		// other axioms that do not change
		List<ElkAxiom> staticAxioms = new ArrayList<ElkAxiom>();
		// for storing change history
		Deque<IncrementalChange> changesHistory = new LinkedList<IncrementalChange>();
		// for storing taxonomy hash history
		Deque<String> taxonomyHashHistory = new LinkedList<String>();

		// use random seed
		long seed = System.currentTimeMillis();

		String taxonomyHash;

		Reasoner reasoner = TestReasonerUtils.createTestReasoner(
				new SimpleStageExecutor(), 1);

		try {

			// INITIAL LOADING
			if (LOGGER_.isInfoEnabled())
				LOGGER_.info("Initial load of test axioms");
			InputStream stream = manifest.getInput().getInputStream();
			OntologyLoader fileLoader = new Owl2StreamLoader(
					new Owl2FunctionalStyleParserFactory(), stream);
			reasoner.registerOntologyLoader(new TrackingOntologyLoader(
					fileLoader, changingAxioms, staticAxioms));
			reasoner.registerOntologyChangesLoader(new EmptyChangesLoader());
			taxonomyHash = TaxonomyPrinter.getHashString(getTaxonomy(reasoner));
			if (LOGGER_.isDebugEnabled())
				LOGGER_.debug("Taxonomy hash code: " + taxonomyHash);

			if (LOGGER_.isInfoEnabled())
				LOGGER_.info("Running " + ROUNDS + " rounds with " + ITERATIONS
						+ " random changes");
			reasoner.setIncrementalMode(true);
			int changeSize = INITIAL_CHANGES_SIZE;

			for (int j = 0; j < ROUNDS; j++) {
				if (LOGGER_.isInfoEnabled())
					LOGGER_.info("Generating " + ITERATIONS
							+ " changes of size: " + changeSize);
				for (int i = 0; i < ITERATIONS; i++) {
					taxonomyHashHistory.add(taxonomyHash);
					reasoner.registerOntologyChangesLoader(new TrackingChangesLoader(
							changingAxioms, changesHistory, changeSize));
					taxonomyHash = TaxonomyPrinter
							.getHashString(getTaxonomy(reasoner));
				}

				if (LOGGER_.isInfoEnabled())
					LOGGER_.info("Reverting the changes");
				for (;;) {
					IncrementalChange change = changesHistory.pollLast();
					String expectedHash = taxonomyHashHistory.pollLast();
					if (change == null)
						break;
					reasoner.registerOntologyChangesLoader(new ReversesChangeLoader(
							change));
					taxonomyHash = TaxonomyPrinter
							.getHashString(getTaxonomy(reasoner));
					assertEquals("Seed " + seed, expectedHash, taxonomyHash);
				}
				// doubling the change size every round
				changeSize *= 2;
			}

		} finally {

			reasoner.shutdown();
		}
	}

	// TODO: perhaps add such a method to the reasoner interface?
	private Taxonomy<ElkClass> getTaxonomy(Reasoner reasoner) {
		Taxonomy<ElkClass> result = null;

		try {
			result = reasoner.getTaxonomy();
		} catch (ElkException e) {
			LOGGER_.info("Ontology is inconsistent");

			result = PredefinedTaxonomy.INCONSISTENT_CLASS_TAXONOMY;
		}

		return result;
	}

	@Config
	public static Configuration getConfig() throws URISyntaxException,
			IOException {
		return ConfigurationUtils
				.loadFileBasedTestConfiguration(
						INPUT_DATA_LOCATION,
						BaseIncrementalClassificationCorrectnessTest.class,
						"owl",
						"expected",
						new TestManifestCreator<URLTestIO, ClassTaxonomyTestOutput, ClassTaxonomyTestOutput>() {
							@Override
							public TestManifest<URLTestIO, ClassTaxonomyTestOutput, ClassTaxonomyTestOutput> create(
									URL input, URL output) throws IOException {
								// don't need an expected output for these tests
								return new TaxonomyDiffManifest<ClassTaxonomyTestOutput, ClassTaxonomyTestOutput>(
										input, null);
							}
						});
	}

}
