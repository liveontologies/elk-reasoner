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
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.reasoner.ElkReasoningTestDelegate;
import org.semanticweb.elk.reasoner.Reasoner;
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
public class EntailmentProofTest extends BaseQueryTest<ElkAxiom, Void> {

	// @formatter:off
	static final String[] IGNORE_LIST = {
		};
	// @formatter:on

	static {
		Arrays.sort(IGNORE_LIST);
	}

	@Override
	protected boolean ignoreInputFile(final String fileName) {
		return Arrays.binarySearch(IGNORE_LIST, fileName) >= 0;
	}

	public static final double INTERRUPTION_CHANCE = 0.003;

	public EntailmentProofTest(
			final QueryTestManifest<ElkAxiom, Void> manifest) {
		super(manifest, new ElkReasoningTestDelegate<Void>(manifest,
				INTERRUPTION_CHANCE) {

			@Override
			public Void getActualOutput() throws Exception {

				final Reasoner reasoner = getReasoner();

				TestUtils.provabilityTest(reasoner, null,
						reasoner.getElkFactory(),
						manifest.getInput().getQuery());

				return null;
			}

		});
	}

	public static final String ENTAILMENT_QUERY_INPUT_DIR = "entailment_query_test_input";

	private static final ConfigurationUtils.ManifestCreator<TestManifestWithOutput<QueryTestInput<ElkAxiom>, Void>> ENTAILMENT_QUERY_TEST_MANIFEST_CREATOR_ = new ConfigurationUtils.ManifestCreator<TestManifestWithOutput<QueryTestInput<ElkAxiom>, Void>>() {

		@Override
		public Collection<? extends TestManifestWithOutput<QueryTestInput<ElkAxiom>, Void>> createManifests(
				final List<URL> urls) throws IOException {

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

				final Collection<QueryTestManifest<ElkAxiom, Void>> manifests = new ArrayList<QueryTestManifest<ElkAxiom, Void>>(
						query.size());
				for (final ElkAxiom axiom : query) {
					manifests.add(new QueryTestManifest<ElkAxiom, Void>(input,
							axiom, null));
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
				ENTAILMENT_QUERY_INPUT_DIR, BaseQueryTest.class,
				ENTAILMENT_QUERY_TEST_MANIFEST_CREATOR_, "owl", "entailed");

	}

}
