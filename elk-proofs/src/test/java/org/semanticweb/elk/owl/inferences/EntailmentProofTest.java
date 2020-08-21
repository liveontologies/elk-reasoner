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
package org.semanticweb.elk.owl.inferences;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.runner.RunWith;
import org.semanticweb.elk.ElkTestUtils;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.reasoner.ElkReasoningTestDelegate;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.query.BaseQueryTest;
import org.semanticweb.elk.reasoner.query.QueryTestInput;
import org.semanticweb.elk.reasoner.query.QueryTestManifest;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifestWithOutput;

@RunWith(PolySuite.class)
public class EntailmentProofTest
		extends BaseQueryTest<ElkAxiom, ElkQueryDerivabilityTestOutput> {

	// @formatter:off
	static final String[] IGNORE_LIST = {
			ElkTestUtils.TEST_INPUT_LOCATION + "/query/entailment/AssertionRanges.owl",// Ranges not supported with assertions
			ElkTestUtils.TEST_INPUT_LOCATION + "/query/entailment/HasValueRanges.owl",// Ranges not supported with ObjectHasValue
		};
	// @formatter:on

	static {
		Arrays.sort(IGNORE_LIST);
	}

	@Override
	protected boolean ignore(final QueryTestInput<ElkAxiom> input) {
		return super.ignore(input) || org.semanticweb.elk.testing.TestUtils
				.ignore(input, ElkTestUtils.TEST_INPUT_LOCATION, IGNORE_LIST);
	}

	public EntailmentProofTest(
			final QueryTestManifest<ElkAxiom, ElkQueryDerivabilityTestOutput> manifest) {
		super(manifest,
				new ElkReasoningTestDelegate<ElkQueryDerivabilityTestOutput>(
						manifest) {

					@Override
					public ElkQueryDerivabilityTestOutput getActualOutput()
							throws Exception {
						return new ElkQueryDerivabilityTestOutput(getReasoner(),
								manifest.getInput().getQuery());
					}

				});
	}

	private static final ConfigurationUtils.ManifestCreator<TestManifestWithOutput<QueryTestInput<ElkAxiom>, ElkQueryDerivabilityTestOutput>> ENTAILMENT_QUERY_TEST_MANIFEST_CREATOR_ = new ConfigurationUtils.ManifestCreator<TestManifestWithOutput<QueryTestInput<ElkAxiom>, ElkQueryDerivabilityTestOutput>>() {

		@Override
		public Collection<? extends TestManifestWithOutput<QueryTestInput<ElkAxiom>, ElkQueryDerivabilityTestOutput>> createManifests(
				final String name, final List<URL> urls) throws IOException {

			if (urls == null || urls.size() < 2) {
				// Not enough inputs. Something was probably forgotten.
				throw new IllegalArgumentException("Need at least 2 URL-s!");
			}
			if (urls.get(0) == null || urls.get(1) == null) {
				// No inputs, no manifests.
				return Collections.emptySet();
			}

			final URL input = urls.get(0);
			InputStream entailedIS = null;
			try {
				entailedIS = urls.get(1).openStream();

				final Set<ElkAxiom> query = TestReasonerUtils
						.loadAxioms(entailedIS);

				final Collection<QueryTestManifest<ElkAxiom, ElkQueryDerivabilityTestOutput>> manifests = new ArrayList<QueryTestManifest<ElkAxiom, ElkQueryDerivabilityTestOutput>>(
						query.size());
				for (final ElkAxiom axiom : query) {
					manifests.add(
							new QueryTestManifest<ElkAxiom, ElkQueryDerivabilityTestOutput>(
									name, input, axiom,
									new ElkQueryDerivabilityTestOutput(axiom,
											true)));
				}

				return manifests;

			} catch (final Owl2ParseException e) {
				throw new IOException(e);
			} finally {
				IOUtils.closeQuietly(entailedIS);
			}

		}

	};

	@Config
	public static Configuration getConfig()
			throws IOException, URISyntaxException {

		return ConfigurationUtils.loadFileBasedTestConfiguration(
				ElkTestUtils.TEST_INPUT_LOCATION, BaseQueryTest.class,
				ENTAILMENT_QUERY_TEST_MANIFEST_CREATOR_, "owl", "entailed");

	}

}
