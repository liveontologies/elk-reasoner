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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.runner.RunWith;
import org.semanticweb.elk.ElkTestUtils;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.ElkReasoningTestDelegate;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;

import com.google.common.collect.ImmutableMap;

import org.semanticweb.elk.testing.TestManifestWithOutput;
import org.semanticweb.elk.testing.TestUtils;

@RunWith(PolySuite.class)
public class ElkEntailmentQueryTest extends
		BaseQueryTest<Collection<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>> {

	// @formatter:off
	static final String[] IGNORE_LIST = {
			ElkTestUtils.TEST_INPUT_LOCATION + "/query/class/Disjunctions.owl",// Disjuctions not fully supported
			ElkTestUtils.TEST_INPUT_LOCATION + "/query/class/OneOf.owl",// Disjuctions not fully supported
			ElkTestUtils.TEST_INPUT_LOCATION + "/query/class/UnsupportedQueryIndexing.owl",// Unsupported class expression
			ElkTestUtils.TEST_INPUT_LOCATION + "/query/entailment/AssertionRanges.owl",// Ranges not supported with assertions
			ElkTestUtils.TEST_INPUT_LOCATION + "/query/entailment/HasValueRanges.owl",// Ranges not supported with ObjectHasValue
		};
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
			final QueryTestManifest<Collection<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>> manifest) {
		super(manifest,
				new ElkReasoningTestDelegate<EntailmentQueryTestOutput<ElkAxiom>>(
						manifest) {

					@Override
					public EntailmentQueryTestOutput<ElkAxiom> getActualOutput()
							throws Exception {
						final Map<ElkAxiom, EntailmentQueryResult> result = getReasoner()
								.isEntailed(manifest.getInput().getQuery());
						return new EntailmentQueryTestOutput<ElkAxiom>(
								resultToOutput(result));
					}

					@Override
					protected Map<String, String> additionalConfigWithOutput() {
						return ImmutableMap.<String, String> builder()
								.put(ReasonerConfiguration.ENTAILMENT_QUERY_EVICTOR,
										"NQEvictor(0, 0.75)")
								.build();
					}

					@Override
					protected Map<String, String> additionalConfigWithInterrupts() {
						return ImmutableMap.<String, String> builder()
								.put(ReasonerConfiguration.ENTAILMENT_QUERY_EVICTOR,
										"NQEvictor(0, 0.75)")
								.build();
					}

				});
	}

	static Map<ElkAxiom, Boolean> resultToOutput(
			final Map<ElkAxiom, EntailmentQueryResult> result)
			throws ElkQueryException {
		final Map<ElkAxiom, Boolean> output = new HashMap<ElkAxiom, Boolean>();
		for (final Map.Entry<ElkAxiom, EntailmentQueryResult> e : result
				.entrySet()) {
			output.put(e.getKey(), e.getValue().accept(RESULT_VISITOR));
		}
		return output;
	}

	private static final EntailmentQueryResult.Visitor<Boolean, ElkQueryException> RESULT_VISITOR = new EntailmentQueryResult.Visitor<Boolean, ElkQueryException>() {

		@Override
		public Boolean visit(
				final ProperEntailmentQueryResult properEntailmentQueryResult)
				throws ElkQueryException {
			try {
				return properEntailmentQueryResult.isEntailed();
			} finally {
				properEntailmentQueryResult.unlock();
			}
		}

		@Override
		public Boolean visit(
				final UnsupportedIndexingEntailmentQueryResult unsupportedIndexingEntailmentQueryResult) {
			// TODO: this may be an important information for the test
			return false;
		}

		@Override
		public Boolean visit(
				final UnsupportedQueryTypeEntailmentQueryResult unsupportedQueryTypeEntailmentQueryResult) {
			// TODO: this may be an important information for the test
			return false;
		}

	};

	public static final ConfigurationUtils.ManifestCreator<TestManifestWithOutput<QueryTestInput<Collection<ElkAxiom>>, EntailmentQueryTestOutput<ElkAxiom>>> CLASS_QUERY_TEST_MANIFEST_CREATOR = new ConfigurationUtils.ManifestCreator<TestManifestWithOutput<QueryTestInput<Collection<ElkAxiom>>, EntailmentQueryTestOutput<ElkAxiom>>>() {

		@Override
		public Collection<? extends TestManifestWithOutput<QueryTestInput<Collection<ElkAxiom>>, EntailmentQueryTestOutput<ElkAxiom>>> createManifests(
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

				return ElkExpectedTestOutputLoader.load(outputIS)
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
