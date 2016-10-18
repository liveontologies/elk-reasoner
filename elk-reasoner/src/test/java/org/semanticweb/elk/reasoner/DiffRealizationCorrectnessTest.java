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
public abstract class DiffRealizationCorrectnessTest extends
		BaseRealizationCorrectnessTest<InstanceTaxonomyTestOutput<?>> {

	public DiffRealizationCorrectnessTest(
			final ReasoningTestManifest<InstanceTaxonomyTestOutput<?>, InstanceTaxonomyTestOutput<?>> testManifest,
			final ReasoningTestWithOutputDelegate<InstanceTaxonomyTestOutput<?>> testDelegate) {
		super(testManifest, testDelegate);
	}

	/*
	 * Configuration: loading all inputs and expected outputs
	 */
	@Config
	public static Configuration getConfig() throws URISyntaxException,
			IOException {
		return ConfigurationUtils
				.loadFileBasedTestConfiguration(
						INPUT_DATA_LOCATION,
						DiffRealizationCorrectnessTest.class,
						"owl",
						"expected",
						new TestManifestCreator<UrlTestInput, InstanceTaxonomyTestOutput<?>, InstanceTaxonomyTestOutput<?>>() {
							@Override
							public TestManifestWithOutput<UrlTestInput, InstanceTaxonomyTestOutput<?>, InstanceTaxonomyTestOutput<?>> create(
									URL input, URL output) throws IOException {
								// input and expected output are OWL ontologies
								InputStream stream = null;

								try {
									ElkObject.Factory objectFactory = new ElkObjectEntityRecyclingFactory();
									InstanceTaxonomy<ElkClass, ElkNamedIndividual> expectedTaxonomy = MockTaxonomyLoader
											.load(objectFactory,
													new Owl2FunctionalStyleParserFactory(
															objectFactory).getParser(stream = output
															.openStream()));

									return new TaxonomyDiffManifest<InstanceTaxonomyTestOutput<?>, InstanceTaxonomyTestOutput<?>>(
											input,
											new InstanceTaxonomyTestOutput<InstanceTaxonomy<ElkClass, ElkNamedIndividual>>(
													expectedTaxonomy));

								} catch (Owl2ParseException e) {
									throw new IOException(e);
								} finally {
									IOUtils.closeQuietly(stream);
								}
							}
						});
	}
}