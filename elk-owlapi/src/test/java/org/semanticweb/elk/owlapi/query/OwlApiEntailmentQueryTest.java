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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.runner.RunWith;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owlapi.OwlApiReasoningTestDelegate;
import org.semanticweb.elk.owlapi.TestOWLManager;
import org.semanticweb.elk.reasoner.query.BaseQueryTest;
import org.semanticweb.elk.reasoner.query.EntailmentQueryTestManifest;
import org.semanticweb.elk.reasoner.query.EntailmentQueryTestOutput;
import org.semanticweb.elk.reasoner.query.QueryTestInput;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.MultiManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifestWithOutput;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;

@RunWith(PolySuite.class)
public class OwlApiEntailmentQueryTest extends
		BaseQueryTest<Collection<OWLAxiom>, EntailmentQueryTestOutput<OWLAxiom>> {

	// @formatter:off
	static final String[] IGNORE_LIST = {
			"Disjunctions.owl",// Disjuctions not fully supported
			"OneOf.owl",// Disjuctions not fully supported
			"Inconsistent.owl",// Throwing InconsistentOntologyException
			"InconsistentInstances.owl",// Throwing InconsistentOntologyException
			"UnsupportedQueryIndexing.owl",// Unsupported class expression
		};
	// @formatter:on

	static {
		Arrays.sort(IGNORE_LIST);
	}

	@Override
	protected boolean ignoreInputFile(final String fileName) {
		return Arrays.binarySearch(IGNORE_LIST, fileName) >= 0;
	}

	public OwlApiEntailmentQueryTest(
			final TestManifestWithOutput<QueryTestInput<Collection<OWLAxiom>>, EntailmentQueryTestOutput<OWLAxiom>, EntailmentQueryTestOutput<OWLAxiom>> manifest) {
		super(manifest,
				new OwlApiReasoningTestDelegate<EntailmentQueryTestOutput<OWLAxiom>>(
						manifest) {

					@Override
					public EntailmentQueryTestOutput<OWLAxiom> getActualOutput()
							throws Exception {
						final Map<OWLAxiom, Boolean> output = new HashMap<OWLAxiom, Boolean>();
						for (final OWLAxiom owlAxiom : manifest.getInput()
								.getQuery()) {
							output.put(owlAxiom,
									getReasoner().isEntailed(owlAxiom));
						}
						return new EntailmentQueryTestOutput<OWLAxiom>(output);
					}

					@Override
					public Class<? extends Exception> getInterruptionExceptionClass() {
						return ReasonerInterruptedException.class;
					}

				});
	}

	public static final String ENTAILMENT_QUERY_INPUT_DIR = "entailment_query_test_input";

	private static final ConfigurationUtils.ManifestCreator<QueryTestInput<Collection<OWLAxiom>>, EntailmentQueryTestOutput<OWLAxiom>, EntailmentQueryTestOutput<OWLAxiom>> ENTAILMENT_QUERY_TEST_MANIFEST_CREATOR_ = new ConfigurationUtils.ManifestCreator<QueryTestInput<Collection<OWLAxiom>>, EntailmentQueryTestOutput<OWLAxiom>, EntailmentQueryTestOutput<OWLAxiom>>() {

		@Override
		public Collection<? extends TestManifestWithOutput<QueryTestInput<Collection<OWLAxiom>>, EntailmentQueryTestOutput<OWLAxiom>, EntailmentQueryTestOutput<OWLAxiom>>> createManifests(
				final List<URL> urls) throws IOException {

			if (urls.size() < 3) {
				throw new IllegalArgumentException("Need at least 3 URLs!");
			}

			final OWLOntologyManager manager = TestOWLManager
					.createOWLOntologyManager();

			final URL input = urls.get(0);
			InputStream entailedIS = null;
			InputStream notEntailedIS = null;
			try {
				entailedIS = urls.get(1).openStream();
				notEntailedIS = urls.get(2).openStream();

				final List<OWLAxiom> query = new ArrayList<OWLAxiom>();
				final Map<OWLAxiom, Boolean> output = new HashMap<OWLAxiom, Boolean>();

				final Set<OWLLogicalAxiom> entailed = manager
						.loadOntologyFromOntologyDocument(entailedIS)
						.getLogicalAxioms();
				final Set<OWLLogicalAxiom> notEntailed = manager
						.loadOntologyFromOntologyDocument(notEntailedIS)
						.getLogicalAxioms();
				query.addAll(entailed);
				query.addAll(notEntailed);
				for (final OWLLogicalAxiom elkAxiom : entailed) {
					output.put(elkAxiom, true);
				}
				for (final OWLLogicalAxiom elkAxiom : notEntailed) {
					output.put(elkAxiom, false);
				}

				// OWL API interface can query only one axiom at once.
				final Collection<EntailmentQueryTestManifest<OWLAxiom>> manifests = new ArrayList<EntailmentQueryTestManifest<OWLAxiom>>(
						query.size());
				for (final OWLAxiom axiom : query) {
					manifests.add(new EntailmentQueryTestManifest<OWLAxiom>(
							input, Collections.singleton(axiom),
							new EntailmentQueryTestOutput<OWLAxiom>(Collections
									.singletonMap(axiom, output.get(axiom)))));
				}

				return manifests;

			} catch (final OWLOntologyCreationException e) {
				throw new IOException(e);
			} finally {
				IOUtils.closeQuietly(entailedIS);
				IOUtils.closeQuietly(notEntailedIS);
			}

		}

	};

	private static final MultiManifestCreator<QueryTestInput<Collection<OWLAxiom>>, EntailmentQueryTestOutput<OWLAxiom>, EntailmentQueryTestOutput<OWLAxiom>> CLASS_QUERY_TEST_MANIFEST_CREATOR_ = new MultiManifestCreator<QueryTestInput<Collection<OWLAxiom>>, EntailmentQueryTestOutput<OWLAxiom>, EntailmentQueryTestOutput<OWLAxiom>>() {

		@Override
		public Collection<? extends TestManifestWithOutput<QueryTestInput<Collection<OWLAxiom>>, EntailmentQueryTestOutput<OWLAxiom>, EntailmentQueryTestOutput<OWLAxiom>>> createManifests(
				final URL input, final URL output) throws IOException {

			InputStream outputIS = null;
			try {
				outputIS = output.openStream();

				return OwlExpectedTestOutputLoader.load(outputIS)
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
