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
package org.semanticweb.elk.reasoner.query;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.runner.RunWith;
import org.semanticweb.elk.ElkTestUtils;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.ElkReasoningTestDelegate;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifestWithOutput;
import org.semanticweb.elk.testing.TestUtils;

import com.google.common.collect.ImmutableMap;

@RunWith(PolySuite.class)
public class ElkEntailmentQueryTest extends
		BaseQueryTest<Collection<ElkAxiom>, ElkEntailmentQueryTestOutput> {

	// @formatter:off
	static final String[] IGNORE_LIST = {};
	// @formatter:on

	static {
		Arrays.sort(IGNORE_LIST);
	}

	@Override
	protected boolean ignore(final QueryTestInput<Collection<ElkAxiom>> input) {
		return super.ignore(input) || TestUtils.ignore(input,
				ElkTestUtils.TEST_INPUT_LOCATION, IGNORE_LIST);
	}

	public ElkEntailmentQueryTest(
			final QueryTestManifest<Collection<ElkAxiom>, ElkEntailmentQueryTestOutput> manifest) {
		super(manifest,
				new ElkReasoningTestDelegate<ElkEntailmentQueryTestOutput>(
						manifest) {

					@Override
					public ElkEntailmentQueryTestOutput getActualOutput()
							throws Exception {
						return new ElkEntailmentQueryTestOutput(
								getReasoner().checkEntailment(
										manifest.getInput().getQuery()));
					}

					@Override
					protected Map<String, String> additionalConfigWithOutput() {
						return ImmutableMap.<String, String> builder().put(
								ReasonerConfiguration.ENTAILMENT_QUERY_EVICTOR,
								"NQEvictor(0, 0.75)").build();
					}

					@Override
					protected Map<String, String> additionalConfigWithInterrupts() {
						return ImmutableMap.<String, String> builder().put(
								ReasonerConfiguration.ENTAILMENT_QUERY_EVICTOR,
								"NQEvictor(0, 0.75)").build();
					}

				});
	}

	public static final ConfigurationUtils.ManifestCreator<TestManifestWithOutput<QueryTestInput<Collection<ElkAxiom>>, ElkEntailmentQueryTestOutput>> CLASS_QUERY_TEST_MANIFEST_CREATOR = new ConfigurationUtils.ManifestCreator<TestManifestWithOutput<QueryTestInput<Collection<ElkAxiom>>, ElkEntailmentQueryTestOutput>>() {

		@Override
		public Collection<? extends TestManifestWithOutput<QueryTestInput<Collection<ElkAxiom>>, ElkEntailmentQueryTestOutput>> createManifests(
				final String name, final List<URL> urls) throws IOException {

			if (urls == null || urls.size() < 2) {
				// Not enough inputs. Probably forgot something.
				throw new IllegalArgumentException("Need at least 2 URL-s!");
			}
			if (urls.get(0) == null || urls.get(1) == null) {
				// No inputs, no manifests.
				return Collections.emptySet();
			}

			try (InputStream outputIS = urls.get(1).openStream()) {
				return ElkExpectedTestOutputLoader.load(outputIS)
						.getEntailmentManifests(name + " checkEntailment",
								urls.get(0));
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
