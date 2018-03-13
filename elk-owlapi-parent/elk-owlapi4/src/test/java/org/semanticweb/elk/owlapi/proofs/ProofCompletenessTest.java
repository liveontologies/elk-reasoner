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
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.liveontologies.owlapi.proof.OWLProver;
import org.semanticweb.elk.ElkTestUtils;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owlapi.OWLAPITestUtils;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestUtils;
import org.semanticweb.elk.testing.UrlTestInput;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.InferenceType;

/**
 * Finds all repairs from proofs of a conclusion. For every repair, tries to
 * remove the repair from the ontology. If the conclusion is still derived by
 * the reasoner, we missed some proof.
 * 
 * @author Peter Skocovsky
 */
@RunWith(PolySuite.class)
public class ProofCompletenessTest extends BaseProofTest {

	// @formatter:off
	static final String[] IGNORE_LIST = {
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/AssertionDisjoint.owl",
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/ConjunctionsComplex.owl",
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/DifferentSameIndividual.owl",
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/Inconsistent.owl",
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/OneOf.owl",
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/PropertyRangesHierarchy.owl",
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/SameIndividual.owl",
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/forest.owl",
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/TransitivePropertyChain.owl",
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/TransitivityByChain.owl",
		};
	// @formatter:on

	static {
		Arrays.sort(IGNORE_LIST);
	}

	public ProofCompletenessTest(
			final TestManifest<UrlTestInput> testManifest) {
		super(testManifest);
	}

	@Override
	@Before
	public void before() throws IOException, Owl2ParseException {
		assumeTrue(!ignore(manifest_.getInput()));
	}

	@Override
	protected boolean ignore(final UrlTestInput input) {
		return super.ignore(input) || TestUtils.ignore(input,
				ElkTestUtils.TEST_INPUT_LOCATION, IGNORE_LIST);
	}

	@Test
	public void proofCompletenessTest() throws Exception {

		final OWLDataFactory factory = manager_.getOWLDataFactory();

		// loading and classifying via the OWL API
		final OWLOntology ontology = loadOntology(
				manifest_.getInput().getUrl().openStream());
		final OWLProver prover = OWLAPITestUtils.createProver(ontology);
		try {
			prover.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		} catch (final InconsistentOntologyException e) {
			// we will explain it, too
		}

		try {
			// now do testing

			ProofTestUtils.visitAllSubsumptionsForProofTests(prover, factory,
					new ProofTestVisitor() {

						@Override
						public void visit(final OWLClassExpression subsumee,
								final OWLClassExpression subsumer) {
							ProofTestUtils.proofCompletenessTest(prover, factory
									.getOWLSubClassOfAxiom(subsumee, subsumer));
						}

					});

		} finally {
			prover.dispose();
		}

	}

}