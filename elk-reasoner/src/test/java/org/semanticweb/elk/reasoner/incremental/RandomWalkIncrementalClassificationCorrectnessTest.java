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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Level;
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
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.reasoner.ClassTaxonomyTestOutput;
import org.semanticweb.elk.reasoner.ElkInconsistentOntologyException;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasoningTestManifest;
import org.semanticweb.elk.reasoner.TaxonomyDiffManifest;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.PostProcessingStageExecutor;
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
import org.semanticweb.elk.util.collections.Operations;

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
	 * the maximum number of rounds used
	 */
	static int MAX_ROUNDS = 5;
	/**
	 * how many changes are generated in every round
	 */
	static int ITERATIONS = 5;

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
		seed = 124;

		Reasoner incrementalReasoner = TestReasonerUtils.createTestReasoner(
				new PostProcessingStageExecutor(), 1);
		incrementalReasoner.setIncrementalMode(true);
		TrackingChangesLoader.setSeed(seed);

		try {

			if (LOGGER_.isInfoEnabled())
				LOGGER_.info("Initial load of test axioms");
			InputStream stream = manifest.getInput().getInputStream();
			OntologyLoader fileLoader = new Owl2StreamLoader(
					new Owl2FunctionalStyleParserFactory(), stream);
			incrementalReasoner
					.registerOntologyLoader(new TrackingOntologyLoader(
							fileLoader, changingAxioms, staticAxioms));
			incrementalReasoner
					.registerOntologyChangesLoader(new EmptyChangesLoader());
			incrementalReasoner.setIncrementalMode(true);
			final String originalTaxonomyHash = TaxonomyPrinter
					.getHashString(getTaxonomy(incrementalReasoner));

			if (LOGGER_.isDebugEnabled())
				LOGGER_.debug("Original taxonomy hash code: "
						+ originalTaxonomyHash);

			int changingAxiomsCount = changingAxioms.size();
			int rounds = getNumberOfRounds(changingAxiomsCount);

			if (LOGGER_.isInfoEnabled())
				LOGGER_.info("Running " + rounds + " rounds with " + ITERATIONS
						+ " random changes");

			int changeSize = getInitialChangeSize(changingAxiomsCount);

			for (int j = 0; j < rounds; j++) {
				if (LOGGER_.isInfoEnabled())
					LOGGER_.info("Generating " + ITERATIONS
							+ " changes of size: " + changeSize);
				changingAxioms.setAllOn();

				taxonomyHashHistory.add(originalTaxonomyHash);
				for (int i = 0; i < ITERATIONS; i++) {
					incrementalReasoner
							.registerOntologyChangesLoader(new TrackingChangesLoader(
									changingAxioms, changesHistory, changeSize));
					String taxonomyHash = TaxonomyPrinter
							.getHashString(getTaxonomy(incrementalReasoner));
					taxonomyHashHistory.add(taxonomyHash);
					if (LOGGER_.isDebugEnabled())
						LOGGER_.debug("Taxonomy hash code for round " + (j + 1)
								+ " iteration " + (i + 1) + ": " + taxonomyHash);

					printCurrentAxioms(changingAxioms, staticAxioms);

					if (LOGGER_.isTraceEnabled()) {
						LOGGER_.trace("======= Current Taxonomy =======");
						printTaxonomy(getTaxonomy(incrementalReasoner));
					}
				}

				if (LOGGER_.isInfoEnabled())
					LOGGER_.info("Checking the final taxonomy");

				String finalTaxonomyHash = taxonomyHashHistory.pollLast();

				Reasoner standardReasoner = TestReasonerUtils
						.createTestReasoner(new SimpleStageExecutor(), 1);
				standardReasoner.setIncrementalMode(false);
				standardReasoner.registerOntologyLoader(new TestAxiomLoader(
						Operations.concat(changingAxioms.getOnElements(),
								staticAxioms)));
				standardReasoner
						.registerOntologyChangesLoader(new EmptyChangesLoader());
				standardReasoner.setIncrementalMode(false);
				String expectedTaxonomyHash = TaxonomyPrinter
						.getHashString(getTaxonomy(standardReasoner));

				try {
					assertEquals("Seed " + seed, expectedTaxonomyHash,
							finalTaxonomyHash);
				} catch (AssertionError e) {
					LOGGER_.info("======= Current Taxonomy =======");
					printTaxonomy(getTaxonomy(incrementalReasoner));
					LOGGER_.info("====== Expected Taxonomy =======");
					printTaxonomy(getTaxonomy(standardReasoner));
					standardReasoner.shutdown();
					throw e;
				}
				standardReasoner.shutdown();

				if (LOGGER_.isInfoEnabled())
					LOGGER_.info("Reverting the changes");
				for (;;) {
					IncrementalChange change = changesHistory.pollLast();
					String expectedHash = taxonomyHashHistory.pollLast();
					if (change == null)
						break;
					incrementalReasoner
							.registerOntologyChangesLoader(new ReversesChangeLoader(
									change));
					String taxonomyHash = TaxonomyPrinter
							.getHashString(getTaxonomy(incrementalReasoner));

					assertEquals("Seed " + seed, expectedHash, taxonomyHash);
				}
				// doubling the change size every round
				changeSize *= 2;
			}

		} finally {
			incrementalReasoner.shutdown();
		}
	}

	private int getInitialChangeSize(int changingAxiomsCount) {
		// the changes size will double with every iteration;
		// we find a good starting size
		int result = changingAxiomsCount >> MAX_ROUNDS;
		if (result == 0)
			result = 1;
		return result;
	}

	private int getNumberOfRounds(int changingAxiomsCount) {
		// we perform a logarithmic number of rounds in the number
		// of changed axioms, unless it is larger than MAX_ROUND
		return Math.min(MAX_ROUNDS,
				2 * (31 - Integer.numberOfLeadingZeros(changingAxiomsCount)));
	}

	private void printCurrentAxioms(OnOffVector<ElkAxiom> changingAxioms,
			Iterable<ElkAxiom> staticAxioms) {
		if (LOGGER_.isTraceEnabled()) {
			for (ElkAxiom axiom : changingAxioms.getOnElements())
				LOGGER_.trace("Current axiom: "
						+ OwlFunctionalStylePrinter.toString(axiom));
			for (ElkAxiom axiom : staticAxioms)
				LOGGER_.trace("Current axiom: "
						+ OwlFunctionalStylePrinter.toString(axiom));
		}
	}

	private void printTaxonomy(Taxonomy<ElkClass> taxonomy) {
		try {
			Writer writer = new OutputStreamWriter(System.out);
			TaxonomyPrinter.dumpClassTaxomomy(taxonomy, writer, false);
			writer.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	// TODO: perhaps add such a method to the reasoner interface?
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
