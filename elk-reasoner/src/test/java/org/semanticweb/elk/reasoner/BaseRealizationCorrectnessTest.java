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
package org.semanticweb.elk.reasoner;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.runner.RunWith;
import org.semanticweb.elk.ElkTestUtils;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.taxonomy.MockTaxonomyLoader;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifestWithOutput;
import org.semanticweb.elk.testing.UrlTestInput;

/**
 * Runs ABox realization tests for all test input in the test directory
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Peter Skocovsky
 */
@RunWith(PolySuite.class)
public abstract class BaseRealizationCorrectnessTest extends
		ReasoningCorrectnessTestWithInterrupts<UrlTestInput, InstanceTaxonomyTestOutput, ReasoningTestManifest<InstanceTaxonomyTestOutput>, ReasoningTestWithOutputAndInterruptsDelegate<InstanceTaxonomyTestOutput>> {

	public BaseRealizationCorrectnessTest(
			final ReasoningTestManifest<InstanceTaxonomyTestOutput> testManifest,
			final ReasoningTestWithOutputAndInterruptsDelegate<InstanceTaxonomyTestOutput> testDelegate) {
		super(testManifest, testDelegate);
	}

	@Config
	public static Configuration getConfig()
			throws URISyntaxException, IOException {
		return ConfigurationUtils.loadFileBasedTestConfiguration(
				ElkTestUtils.TEST_INPUT_LOCATION,
				BaseRealizationCorrectnessTest.class,
				new ConfigurationUtils.ManifestCreator<TestManifestWithOutput<UrlTestInput, InstanceTaxonomyTestOutput>>() {
					@Override
					public Collection<? extends TestManifestWithOutput<UrlTestInput, InstanceTaxonomyTestOutput>> createManifests(
							final String name, final List<URL> urls)
							throws IOException {

						if (urls == null || urls.size() < 2) {
							// Not enough inputs. Probably forgot something.
							throw new IllegalArgumentException(
									"Need at least 2 URL-s!");
						}
						if (urls.get(0) == null || urls.get(1) == null) {
							// No inputs, no manifests.
							return Collections.emptySet();
						}

						// input and expected output are OWL ontologies
						ElkObject.Factory objectFactory = new ElkObjectEntityRecyclingFactory();
						InputStream stream = null;
						try {
							stream = urls.get(1).openStream();
							InstanceTaxonomy<ElkClass, ElkNamedIndividual> expectedTaxonomy = MockTaxonomyLoader
									.load(objectFactory,
											new Owl2FunctionalStyleParserFactory(
													objectFactory)
															.getParser(stream));
							
							return Collections.singleton(
									new ReasoningTestManifest<InstanceTaxonomyTestOutput>(
											name, urls.get(0),
											new InstanceTaxonomyTestOutput(
													expectedTaxonomy, true)));

						} catch (Owl2ParseException e) {
							throw new IOException(e);
						} finally {
							IOUtils.closeQuietly(stream);
						}

					}
				}, "owl", "instancetaxonomy");
	}

}