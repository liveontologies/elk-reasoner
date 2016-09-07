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
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.taxonomy.MockObjectPropertyTaxonomyLoader;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifestWithOutput;
import org.semanticweb.elk.testing.UrlTestInput;

/**
 * @author Peter Skocovsky
 */
public abstract class DiffObjectPropertyClassificationCorrectnessTest extends
		BaseObjectPropertyClassificationCorrectnessTest<TaxonomyTestOutput<?>> {

	public DiffObjectPropertyClassificationCorrectnessTest(
			final ReasoningTestManifest<TaxonomyTestOutput<?>, TaxonomyTestOutput<?>> testManifest,
			final ReasoningTestWithInterruptsDelegate<TaxonomyTestOutput<?>> testDelegate) {
		super(testManifest, testDelegate);
	}

	/*
	 * Configuration: loading all inputs and expected outputs
	 */
	@Config
	public static Configuration getConfig()
			throws URISyntaxException, IOException {
		// @formatter:off
		return ConfigurationUtils.loadFileBasedTestConfiguration(
				INPUT_DATA_LOCATION,
				DiffObjectPropertyClassificationCorrectnessTest.class,
				"owl",
				"expected",
				new TestManifestCreator<UrlTestInput, TaxonomyTestOutput<?>, TaxonomyTestOutput<?>>() {
					@Override
					public TestManifestWithOutput<UrlTestInput, TaxonomyTestOutput<?>, TaxonomyTestOutput<?>> create(
							final URL input, final URL output) throws IOException {
						final ElkObject.Factory objectFactory = new ElkObjectEntityRecyclingFactory();

						// input and expected output are OWL ontologies
						InputStream stream = null;

						try {
							stream = output.openStream();
							final Taxonomy<ElkObjectProperty> expectedTaxonomy = MockObjectPropertyTaxonomyLoader.load(
									objectFactory,
									new Owl2FunctionalStyleParserFactory(objectFactory).getParser(stream)
								);

							return new TaxonomyDiffManifest<TaxonomyTestOutput<?>, TaxonomyTestOutput<?>>(
									input,
									new TaxonomyTestOutput<Taxonomy<ElkObjectProperty>>(expectedTaxonomy)
								);

						} catch (final Owl2ParseException e) {
							throw new IOException(e);
						} finally {
							IOUtils.closeQuietly(stream);
						}
					}
				});
		// @formatter:on
	}
}
