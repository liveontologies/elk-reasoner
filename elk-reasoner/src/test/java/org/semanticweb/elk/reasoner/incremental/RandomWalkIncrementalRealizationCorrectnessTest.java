/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.incremental;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.InstanceTaxonomyTestOutput;
import org.semanticweb.elk.reasoner.ReasoningTestManifest;
import org.semanticweb.elk.reasoner.TaxonomyDiffManifest;
import org.semanticweb.elk.reasoner.incremental.RandomWalkRunnerIO.ElkAPIBasedIO;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifestWithOutput;
import org.semanticweb.elk.testing.UrlTestInput;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RandomWalkIncrementalRealizationCorrectnessTest extends
		BaseRandomWalkIncrementalCorrectnessTest {

	final static String INPUT_DATA_LOCATION = "realization_test_input";

	public RandomWalkIncrementalRealizationCorrectnessTest(
			ReasoningTestManifest<InstanceTaxonomyTestOutput<?>, InstanceTaxonomyTestOutput<?>> testManifest) {
		super(testManifest);
	}

	@Override
	protected RandomWalkIncrementalClassificationRunner<ElkAxiom> getRandomWalkRunner(
			int rounds, int iterations) {
		return new RandomWalkIncrementalRealizationRunner<ElkAxiom>(rounds,
				iterations, new ElkAPIBasedIO());
	}

	@Override
	protected AxiomLoader getAxiomTrackingLoader(AxiomLoader fileLoader,
			OnOffVector<ElkAxiom> changingAxioms, List<ElkAxiom> staticAxioms) {
		//return new ClassAndIndividualAxiomTrackingOntologyLoader(fileLoader, changingAxioms, staticAxioms);
		return new AllAxiomTrackingOntologyLoader(fileLoader, changingAxioms);
	}

	@Config
	public static Configuration getConfig() throws URISyntaxException,
			IOException {
		return ConfigurationUtils
				.loadFileBasedTestConfiguration(
						INPUT_DATA_LOCATION,
						RandomWalkIncrementalClassificationCorrectnessTest.class,
						"owl",
						"expected",
						new TestManifestCreator<UrlTestInput, InstanceTaxonomyTestOutput<?>, InstanceTaxonomyTestOutput<?>>() {
							@Override
							public TestManifestWithOutput<UrlTestInput, InstanceTaxonomyTestOutput<?>, InstanceTaxonomyTestOutput<?>> create(
									URL input, URL output) throws IOException {
								// don't need an expected output for these tests
								return new TaxonomyDiffManifest<InstanceTaxonomyTestOutput<?>, InstanceTaxonomyTestOutput<?>>(
										input, null);
							}
						});
	}

}
