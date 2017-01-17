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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.runner.RunWith;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.incremental.CliIncrementalReasoningTestDelegate;
import org.semanticweb.elk.reasoner.query.EntailmentQueryResult;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.MultiManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestManifestWithOutput;

@RunWith(PolySuite.class)
public class ElkIncrementalEntailmentQueryTest extends
		BaseIncrementalQueryTest<Collection<ElkAxiom>, ElkAxiom, EntailmentQueryTestOutput<ElkAxiom>> {

	public ElkIncrementalEntailmentQueryTest(
			final TestManifest<QueryTestInput<Collection<ElkAxiom>>> manifest) {
		super(manifest,
				new CliIncrementalReasoningTestDelegate<EntailmentQueryTestOutput<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>>(
						manifest) {

					@Override
					public EntailmentQueryTestOutput<ElkAxiom> getExpectedOutput()
							throws Exception {
						final Map<ElkAxiom, EntailmentQueryResult> result = getStandardReasoner()
								.isEntailed(manifest.getInput().getQuery());
						return new EntailmentQueryTestOutput<ElkAxiom>(
								ElkEntailmentQueryTest.resultToOutput(result));
					}

					@Override
					public EntailmentQueryTestOutput<ElkAxiom> getActualOutput()
							throws Exception {
						final Map<ElkAxiom, EntailmentQueryResult> result = getIncrementalReasoner()
								.isEntailed(manifest.getInput().getQuery());
						return new EntailmentQueryTestOutput<ElkAxiom>(
								ElkEntailmentQueryTest.resultToOutput(result));
					}

				});
	}

	public static final String ENTAILMENT_QUERY_INPUT_DIR = "entailment_query_test_input";

	private static final ConfigurationUtils.ManifestCreator<QueryTestInput<Collection<ElkAxiom>>, EntailmentQueryTestOutput<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>> ENTAILMENT_QUERY_TEST_MANIFEST_CREATOR_ = new ConfigurationUtils.ManifestCreator<QueryTestInput<Collection<ElkAxiom>>, EntailmentQueryTestOutput<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>>() {

		@Override
		public Collection<? extends TestManifestWithOutput<QueryTestInput<Collection<ElkAxiom>>, EntailmentQueryTestOutput<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>>> createManifests(
				final List<URL> urls) throws IOException {

			if (urls.size() < 3) {
				throw new IllegalArgumentException("Need at least 3 URLs!");
			}

			final URL input = urls.get(0);
			InputStream entailedIS = null;
			InputStream notEntailedIS = null;
			try {
				entailedIS = urls.get(1).openStream();
				notEntailedIS = urls.get(2).openStream();

				final List<ElkAxiom> query = new ArrayList<ElkAxiom>();

				query.addAll(TestReasonerUtils.loadAxioms(entailedIS));
				query.addAll(TestReasonerUtils.loadAxioms(notEntailedIS));

				return Collections.singleton(
						new EntailmentQueryTestManifest<ElkAxiom>(input, query,
								null));

			} catch (final Owl2ParseException e) {
				throw new IOException(e);
			} finally {
				IOUtils.closeQuietly(entailedIS);
				IOUtils.closeQuietly(notEntailedIS);
			}

		}

	};

	private static final MultiManifestCreator<QueryTestInput<Collection<ElkAxiom>>, EntailmentQueryTestOutput<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>> CLASS_QUERY_TEST_MANIFEST_CREATOR_ = new MultiManifestCreator<QueryTestInput<Collection<ElkAxiom>>, EntailmentQueryTestOutput<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>>() {

		@Override
		public Collection<? extends TestManifestWithOutput<QueryTestInput<Collection<ElkAxiom>>, EntailmentQueryTestOutput<ElkAxiom>, EntailmentQueryTestOutput<ElkAxiom>>> createManifests(
				final URL input, final URL output) throws IOException {

			InputStream outputIS = null;
			try {
				outputIS = output.openStream();

				return ElkExpectedTestOutputLoader.load(outputIS)
						.getEntailmentManifests(input);

			} finally {
				IOUtils.closeQuietly(outputIS);
			}

		}

	};

	@Config
	public static Configuration getConfig()
			throws IOException, URISyntaxException {

		final Configuration classConfiguration = ConfigurationUtils
				.loadFileBasedTestConfiguration(INPUT_DATA_LOCATION,
						BaseQueryTest.class, "owl", "expected",
						CLASS_QUERY_TEST_MANIFEST_CREATOR_);

		final Configuration entailmentConfiguration = ConfigurationUtils
				.loadFileBasedTestConfiguration(ENTAILMENT_QUERY_INPUT_DIR,
						BaseQueryTest.class,
						ENTAILMENT_QUERY_TEST_MANIFEST_CREATOR_, "owl",
						"entailed", "notentailed");

		return ConfigurationUtils.combine(classConfiguration,
				entailmentConfiguration);

	}

}
