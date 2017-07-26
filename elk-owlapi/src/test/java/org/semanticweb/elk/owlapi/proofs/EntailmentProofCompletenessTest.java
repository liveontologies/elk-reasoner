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
import java.net.URISyntaxException;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.ElkTestUtils;
import org.semanticweb.elk.owlapi.ElkProver;
import org.semanticweb.elk.owlapi.EntailmentTestManifestCreator;
import org.semanticweb.elk.owlapi.OwlApiReasoningTestDelegate;
import org.semanticweb.elk.reasoner.BaseReasoningCorrectnessTest;
import org.semanticweb.elk.reasoner.query.QueryTestInput;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestUtils;
import org.semanticweb.owlapi.model.OWLAxiom;

@RunWith(PolySuite.class)
public class EntailmentProofCompletenessTest extends
		BaseReasoningCorrectnessTest<QueryTestInput<OWLAxiom>, Void, TestManifest<QueryTestInput<OWLAxiom>>, OwlApiReasoningTestDelegate<Void>> {

	// @formatter:off
	static final String[] IGNORE_LIST = {
			ElkTestUtils.TEST_INPUT_LOCATION + "/query/entailment/EmptyOntology.owl",// All entailments are tautologies.
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

	public static final double INTERRUPTION_CHANCE = 0.03;

	public EntailmentProofCompletenessTest(
			final TestManifest<QueryTestInput<OWLAxiom>> manifest) {
		super(manifest, new OwlApiReasoningTestDelegate<Void>(manifest,
				INTERRUPTION_CHANCE) {

			@Override
			public Void getActualOutput() throws Exception {
				// No output should be needed.
				throw new UnsupportedOperationException();
			}

			@Override
			public Class<? extends Exception> getInterruptionExceptionClass() {
				// No exception should be needed.
				throw new UnsupportedOperationException();
			}

		});
	}

	@Test
	public void proofCompletenessTest() throws Exception {
		getDelegate().initWithOutput();

		final ElkProver prover = getDelegate().getProver();

		ProofTestUtils.proofCompletenessTest(prover,
				getManifest().getInput().getQuery(), true);

	}

	@Config
	public static Configuration getConfig()
			throws IOException, URISyntaxException {

		return ConfigurationUtils.loadFileBasedTestConfiguration(
				ElkTestUtils.TEST_INPUT_LOCATION,
				BaseReasoningCorrectnessTest.class,
				EntailmentTestManifestCreator.INSTANCE, "owl", "entailed");

	}

}
