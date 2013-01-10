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

import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.loading.OntologyLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.exceptions.ElkRuntimeException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.ClassTaxonomyTestOutput;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasoningTestManifest;
import org.semanticweb.elk.reasoner.TaxonomyDiffManifest;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.PostProcessingStageExecutor;
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

	@Test
	public void randomWalk() throws Exception {
		// axioms that can change
		OnOffVector<ElkAxiom> changingAxioms = new OnOffVector<ElkAxiom>(128);
		// other axioms that do not change
		List<ElkAxiom> staticAxioms = new ArrayList<ElkAxiom>();
		Reasoner incrementalReasoner = TestReasonerUtils.createTestReasoner(
				new PostProcessingStageExecutor(), 1);
		long seed = System.currentTimeMillis();

		incrementalReasoner.setIncrementalMode(true);
		

		if (LOGGER_.isInfoEnabled()) {
			LOGGER_.info("Initial load of test axioms");
		}

		try {
			InputStream stream = manifest.getInput().getInputStream();
			OntologyLoader fileLoader = new Owl2StreamLoader(
					new Owl2FunctionalStyleParserFactory(), stream);
			incrementalReasoner
					.registerOntologyLoader(new TrackingOntologyLoader(
							fileLoader, changingAxioms, staticAxioms));
			incrementalReasoner.loadOntology();
			// let the runner run..
			new RandomWalkIncrementalClassificationRunner(MAX_ROUNDS,
					ITERATIONS).run(incrementalReasoner, changingAxioms,
					staticAxioms, seed, new CleanIndexHook());

		} catch (Exception e) {
			throw new ElkRuntimeException("Seed " + seed, e);
		}
		finally {
			incrementalReasoner.shutdown();
		}
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
