/*
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owlapi.query;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.runner.RunWith;
import org.semanticweb.elk.ElkTestUtils;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owlapi.EntailmentTestManifestCreator;
import org.semanticweb.elk.owlapi.OwlApiReasoningTestDelegate;
import org.semanticweb.elk.reasoner.query.BaseQueryTest;
import org.semanticweb.elk.reasoner.query.QueryTestInput;
import org.semanticweb.elk.reasoner.query.QueryTestManifest;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestUtils;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;

@RunWith(PolySuite.class)
public class OwlApiEntailmentQueryTest
		extends BaseQueryTest<OWLAxiom, Boolean> {

	// @formatter:off
	static final String[] IGNORE_LIST = {
			ElkTestUtils.TEST_INPUT_LOCATION + "/query/class/Disjunctions.owl",// Disjuctions not fully supported
			ElkTestUtils.TEST_INPUT_LOCATION + "/query/class/OneOf.owl",// Disjuctions not fully supported
			ElkTestUtils.TEST_INPUT_LOCATION + "/query/class/UnsupportedQueryIndexing.owl",// Unsupported class expression
		};
	// @formatter:on

	static {
		Arrays.sort(IGNORE_LIST);
	}

	@Override
	protected boolean ignore(final QueryTestInput<OWLAxiom> input) {
		return super.ignore(input) || TestUtils.ignore(input,
				ElkTestUtils.TEST_INPUT_LOCATION, IGNORE_LIST);
	}

	public OwlApiEntailmentQueryTest(
			final QueryTestManifest<OWLAxiom, Boolean> manifest) {
		super(manifest, new OwlApiReasoningTestDelegate<Boolean>(manifest) {

			@Override
			public Boolean getActualOutput() throws Exception {
				return getReasoner().isEntailed(manifest.getInput().getQuery());
			}

			@Override
			public Class<? extends Exception> getInterruptionExceptionClass() {
				return ReasonerInterruptedException.class;
			}

		});
	}

	public static final ConfigurationUtils.ManifestCreator<QueryTestManifest<OWLAxiom, Boolean>> CLASS_QUERY_TEST_MANIFEST_CREATOR = new ConfigurationUtils.ManifestCreator<QueryTestManifest<OWLAxiom, Boolean>>() {

		@Override
		public Collection<? extends QueryTestManifest<OWLAxiom, Boolean>> createManifests(
				final String name, final List<URL> urls) throws IOException {

			if (urls == null || urls.size() < 2) {
				// Not enough inputs. Probably forgot something.
				throw new IllegalArgumentException("Need at least 2 URL-s!");
			}
			if (urls.get(0) == null || urls.get(1) == null) {
				// No inputs, no manifests.
				return Collections.emptySet();
			}

			InputStream outputIS = null;
			try {
				outputIS = urls.get(1).openStream();

				return OwlExpectedTestOutputLoader.load(outputIS)
						.getEntailmentManifests(name, urls.get(0));

			} finally {
				IOUtils.closeQuietly(outputIS);
			}

		}

	};

	@Config
	public static Configuration getConfig()
			throws IOException, URISyntaxException {

		final Configuration classConfiguration = ConfigurationUtils
				.loadFileBasedTestConfiguration(
						ElkTestUtils.TEST_INPUT_LOCATION, BaseQueryTest.class,
						CLASS_QUERY_TEST_MANIFEST_CREATOR, "owl", "classquery");

		final Configuration entailmentConfiguration = ConfigurationUtils
				.loadFileBasedTestConfiguration(
						ElkTestUtils.TEST_INPUT_LOCATION, BaseQueryTest.class,
						EntailmentTestManifestCreator.INSTANCE, "owl",
						"entailed", "notentailed");

		return ConfigurationUtils.combine(classConfiguration,
				entailmentConfiguration);

	}

}
