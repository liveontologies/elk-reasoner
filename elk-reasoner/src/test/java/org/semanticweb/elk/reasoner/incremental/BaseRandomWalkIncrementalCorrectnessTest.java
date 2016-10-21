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
package org.semanticweb.elk.reasoner.incremental;

import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.RandomSeedProvider;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.loading.TestAxiomLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.TaxonomyTestOutput;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasoningTestManifest;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.PostProcessingStageExecutor;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.util.concurrent.computation.DummyInterruptMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for unit tests based on the random walk runners
 * 
 * @author Pavel Klinov
 * 
 */
@RunWith(PolySuite.class)
public abstract class BaseRandomWalkIncrementalCorrectnessTest {

	// logger for this class
	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(BaseRandomWalkIncrementalCorrectnessTest.class);

	/**
	 * the maximum number of rounds used
	 */
	static int MAX_ROUNDS = 5;
	/**
	 * how many changes are generated in every round
	 */
	static int ITERATIONS = 5;

	protected final ReasoningTestManifest<? extends TaxonomyTestOutput<?>, ? extends TaxonomyTestOutput<?>> manifest;

	public BaseRandomWalkIncrementalCorrectnessTest(
			ReasoningTestManifest<? extends TaxonomyTestOutput<?>, ? extends TaxonomyTestOutput<?>> testManifest) {
		manifest = testManifest;
	}

	@Before
	public void before() throws IOException, Owl2ParseException {
		assumeTrue(!ignore(manifest.getInput()));

	}

	/**
	 * @param input
	 *            dummy parameter
	 */
	protected static boolean ignore(TestInput input) {
		return false;
	}

	@Test
	public void randomWalk() throws Exception {
		// axioms that can change
		OnOffVector<ElkAxiom> changingAxioms = new OnOffVector<ElkAxiom>(128);
		// other axioms that do not change
		List<ElkAxiom> staticAxioms = new ArrayList<ElkAxiom>();
		Reasoner incrementalReasoner;
		long seed = RandomSeedProvider.VALUE;

		LOGGER_.info("Initial load of test axioms");

		InputStream stream = manifest.getInput().getUrl().openStream();
		AxiomLoader fileLoader = new Owl2StreamLoader.Factory(
				new Owl2FunctionalStyleParserFactory(),
				stream).getAxiomLoader(DummyInterruptMonitor.INSTANCE);
		TestAxiomLoader trackingLoader = getAxiomTrackingLoader(fileLoader,
				changingAxioms, staticAxioms);
		incrementalReasoner = TestReasonerUtils.createTestReasoner(
				trackingLoader, new PostProcessingStageExecutor());
		incrementalReasoner.setAllowIncrementalMode(true);

		try {
			// incrementalReasoner.loadAxioms();
			// let the runner run..
			getRandomWalkRunner(MAX_ROUNDS, ITERATIONS).run(
					incrementalReasoner, changingAxioms, staticAxioms, seed);

		} catch (Exception e) {
			throw new ElkRuntimeException("Seed " + seed, e);
		} finally {
			stream.close();
			incrementalReasoner.shutdown();
		}
	}

	protected abstract TestAxiomLoader getAxiomTrackingLoader(
			AxiomLoader fileLoader, OnOffVector<ElkAxiom> changingAxioms,
			List<ElkAxiom> staticAxioms);

	protected abstract RandomWalkIncrementalClassificationRunner<ElkAxiom> getRandomWalkRunner(
			int rounds, int iterations);

}
