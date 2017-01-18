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
package org.semanticweb.elk.owlapi.proofs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.runner.RunWith;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owlapi.ElkProver;
import org.semanticweb.elk.owlapi.OwlApiReasoningTestDelegate;
import org.semanticweb.elk.owlapi.TestOWLManager;
import org.semanticweb.elk.reasoner.query.BaseQueryTest;
import org.semanticweb.elk.reasoner.query.QueryTestInput;
import org.semanticweb.elk.reasoner.query.QueryTestManifest;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifestWithOutput;
import org.semanticweb.elk.testing.VoidTestOutput;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.ReasonerInterruptedException;

@RunWith(PolySuite.class)
public class EntailmentProofTest
		extends BaseQueryTest<OWLAxiom, VoidTestOutput> {

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

	public static final double INTERRUPTION_CHANCE = 0.03;

	public EntailmentProofTest(
			final TestManifestWithOutput<QueryTestInput<OWLAxiom>, VoidTestOutput, VoidTestOutput> manifest) {
		super(manifest, new OwlApiReasoningTestDelegate<VoidTestOutput>(
				manifest, INTERRUPTION_CHANCE) {

			@Override
			public VoidTestOutput getActualOutput() throws Exception {

				final ElkProver prover = getProver();

				ProofTestUtils.provabilityTest(prover,
						manifest.getInput().getQuery());

				return null;
			}

			@Override
			public Class<? extends Exception> getInterruptionExceptionClass() {
				return ReasonerInterruptedException.class;
			}

		});
	}

	public static final String ENTAILMENT_QUERY_INPUT_DIR = "entailment_query_test_input";

	private static final ConfigurationUtils.ManifestCreator<QueryTestInput<OWLAxiom>, VoidTestOutput, VoidTestOutput> ENTAILMENT_QUERY_TEST_MANIFEST_CREATOR_ = new ConfigurationUtils.ManifestCreator<QueryTestInput<OWLAxiom>, VoidTestOutput, VoidTestOutput>() {

		@Override
		public Collection<? extends TestManifestWithOutput<QueryTestInput<OWLAxiom>, VoidTestOutput, VoidTestOutput>> createManifests(
				final List<URL> urls) throws IOException {

			if (urls.size() < 2) {
				throw new IllegalArgumentException("Need at least 2 URLs!");
			}

			final OWLOntologyManager manager = TestOWLManager
					.createOWLOntologyManager();

			final URL input = urls.get(0);
			InputStream entailedIS = null;
			try {
				entailedIS = urls.get(1).openStream();

				final Set<OWLLogicalAxiom> query = manager
						.loadOntologyFromOntologyDocument(entailedIS)
						.getLogicalAxioms();

				final Collection<QueryTestManifest<OWLAxiom, VoidTestOutput>> manifests = new ArrayList<QueryTestManifest<OWLAxiom, VoidTestOutput>>(
						query.size());
				for (final OWLAxiom axiom : query) {
					manifests
							.add(new QueryTestManifest<OWLAxiom, VoidTestOutput>(
									input, axiom, null));
				}

				return manifests;

			} catch (final OWLOntologyCreationException e) {
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
