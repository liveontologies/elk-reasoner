/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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
package org.semanticweb.elk.owlapi.proofs;

import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.liveontologies.owlapi.proof.OWLProver;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owlapi.OWLAPITestUtils;
import org.semanticweb.elk.reasoner.tracing.TracingTestManifest;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.TestManifestWithOutput;
import org.semanticweb.elk.testing.UrlTestInput;
import org.semanticweb.elk.testing.VoidTestOutput;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tracing tests over the OWL API interfaces
 * 
 * @author Pavel Klinov
 * 
 */
@RunWith(PolySuite.class)
public class AllOntologiesProofTest extends BaseProofTest {

	final static String INPUT_DATA_LOCATION = "classification_test_input";
	private static final Logger LOGGER_ = LoggerFactory.getLogger(AllOntologiesProofTest.class);

	static final String[] IGNORE_LIST = { "AssertionDisjoint.owl",
			"DifferentSameIndividual.owl", "Inconsistent.owl",
			"PropertyRangesHierarchy.owl", "ReflexivePropertyRanges.owl" };

	static {
		Arrays.sort(IGNORE_LIST);
	}

	public AllOntologiesProofTest(TracingTestManifest testManifest) {
		super(testManifest);
	}

	@Override
	@Before
	public void before() throws IOException, Owl2ParseException {
		assumeTrue(!ignore(manifest_.getInput()));
	}

	@Override
	protected boolean ignore(TestInput input) {
		return Arrays.binarySearch(IGNORE_LIST, input.getName()) >= 0;
	}

	@Test
	public void proofTest() throws Exception {
		final OWLDataFactory factory = manager_.getOWLDataFactory();
		// loading and classifying via the OWL API
		final OWLOntology ontology = loadOntology(manifest_.getInput()
				.getUrl().openStream());
		final OWLProver prover = OWLAPITestUtils.createProver(ontology);

		try {
			prover.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		} catch (InconsistentOntologyException e) {
			// we will explain it, too
		}

		try {
			ProofTestUtils.visitAllSubsumptionsForProofTests(prover,
					factory, new ProofTestVisitor() {

						@Override
						public void visit(OWLClassExpression subsumee,
								OWLClassExpression subsumer) {
							LOGGER_.debug("Proof test: {} ⊑ {}", subsumee, subsumer);

							try {
								OWLSubClassOfAxiom axiom = factory
										.getOWLSubClassOfAxiom(subsumee,
												subsumer);
								ProofTestUtils.provabilityTest(prover, axiom);
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}

					});
		} finally {
			prover.dispose();
		}
	}

	@Config
	public static Configuration getConfig() throws URISyntaxException,
			IOException {
		return ConfigurationUtils
				.loadFileBasedTestConfiguration(
						INPUT_DATA_LOCATION,
						TracingTestManifest.class,
						"owl",
						new TestManifestCreator<UrlTestInput, VoidTestOutput, VoidTestOutput>() {
							@Override
							public TestManifestWithOutput<UrlTestInput, VoidTestOutput, VoidTestOutput> create(
									URL input, URL output) throws IOException {
								// don't need an expected output for these tests
								return new TracingTestManifest(input);
							}
						});
	}
}
