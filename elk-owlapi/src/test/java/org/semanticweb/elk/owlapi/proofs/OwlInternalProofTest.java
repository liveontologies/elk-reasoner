/*-
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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
import java.util.Arrays;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.ElkTestUtils;
import org.semanticweb.elk.owlapi.ElkProver;
import org.semanticweb.elk.owlapi.EntailmentTestManifestCreator;
import org.semanticweb.elk.owlapi.OWLAPITestUtils;
import org.semanticweb.elk.owlapi.TestOWLManager;
import org.semanticweb.elk.reasoner.query.BaseQueryTest;
import org.semanticweb.elk.reasoner.query.QueryTestManifest;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestUtils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

@RunWith(PolySuite.class)
public class OwlInternalProofTest {

	// @formatter:off
	static final String[] IGNORE_LIST = {
			ElkTestUtils.TEST_INPUT_LOCATION + "/query/entailment/AssertionRanges.owl", // Ranges not supported with assertions			
	};
	static final String[] IGNORE_COMPLETENESS_LIST = {
			ElkTestUtils.TEST_INPUT_LOCATION + "/query/entailment/EmptyOntology.owl",// All entailments are tautologies.
	};
	// @formatter:on

	static {
		Arrays.sort(IGNORE_LIST);
		Arrays.sort(IGNORE_COMPLETENESS_LIST);
	}

	private final QueryTestManifest<OWLAxiom, ?> manifest_;

	private ElkProver prover_ = null;
	private OWLAxiom query_ = null;
	private OwlInternalProof adapter_ = null;

	public OwlInternalProofTest(final QueryTestManifest<OWLAxiom, ?> manifest) {
		this.manifest_ = manifest;
	}

	@Before
	public void before() throws Exception {
		Assume.assumeFalse(TestUtils.ignore(manifest_.getInput(),
				ElkTestUtils.TEST_INPUT_LOCATION, IGNORE_LIST));

		final InputStream input = manifest_.getInput().getUrl().openStream();
		OWLOntologyManager manager = TestOWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(input);

		this.prover_ = OWLAPITestUtils.createProver(ontology);

		this.query_ = manifest_.getInput().getQuery();

		// exclude incomplete entailments
		Assume.assumeTrue(!prover_.getDelegate().checkEntailment(query_)
				.getIncompletenessMonitor().isIncompletenessDetected());
		this.adapter_ = new OwlInternalProof(
				prover_.getDelegate().getInternalReasoner(), query_);
	}

	@Test
	public void testProvability() throws Exception {
		ProofTestUtils.provabilityTest(adapter_, adapter_.getGoal());
	}

	@Test
	public void testProofCompleteness() throws Exception {
		Assume.assumeFalse(TestUtils.ignore(manifest_.getInput(),
				ElkTestUtils.TEST_INPUT_LOCATION, IGNORE_COMPLETENESS_LIST));
		ProofTestUtils.proofCompletenessTest(prover_.getDelegate(), query_,
				adapter_.getGoal(), adapter_, new OwlInternalJustifier(), true);
	}

	@Config
	public static Configuration getConfig()
			throws IOException, URISyntaxException {

		return ConfigurationUtils.loadFileBasedTestConfiguration(
				ElkTestUtils.TEST_INPUT_LOCATION, BaseQueryTest.class,
				EntailmentTestManifestCreator.INSTANCE, "owl", "entailed");

	}

	@After
	public void after() throws Exception {
		if (prover_ != null) {
			prover_.dispose();
		}
	}

}
