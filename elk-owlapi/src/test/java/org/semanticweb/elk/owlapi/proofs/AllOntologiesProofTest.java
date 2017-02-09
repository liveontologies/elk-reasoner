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

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.liveontologies.owlapi.proof.OWLProver;
import org.liveontologies.puli.InferenceSets;
import org.semanticweb.elk.ElkTestUtils;
import org.semanticweb.elk.owlapi.OWLAPITestUtils;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestUtils;
import org.semanticweb.elk.testing.UrlTestInput;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;
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

	private static final Logger LOGGER_ = LoggerFactory.getLogger(AllOntologiesProofTest.class);

	// @formatter:off
	static final String[] IGNORE_LIST = {
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/AssertionDisjoint.owl",
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/DifferentSameIndividual.owl",
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/Inconsistent.owl",
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/PropertyRangesHierarchy.owl",
			ElkTestUtils.TEST_INPUT_LOCATION + "/classification/ReflexivePropertyRanges.owl",
		};
	// @formatter:on

	static {
		Arrays.sort(IGNORE_LIST);
	}

	public AllOntologiesProofTest(
			final TestManifest<UrlTestInput> testManifest) {
		super(testManifest);
	}

	@Override
	protected boolean ignore(final UrlTestInput input) {
		return super.ignore(input) || TestUtils.ignore(input,
				ElkTestUtils.TEST_INPUT_LOCATION, IGNORE_LIST);
	}

	@Test
	public void proofTest() throws Exception {
		final OWLDataFactory factory = manager_.getOWLDataFactory();
		// loading and classifying via the OWL API
		final OWLOntology ontology = loadOntology(manifest_.getInput()
				.getUrl().openStream());
		final OWLProver prover = OWLAPITestUtils.createProver(ontology);
		final Set<OWLAxiom> axioms = ontology.getAxioms(Imports.INCLUDED);

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
								assertTrue(String.format(
										"Entailment %s not derivable!", axiom),
										InferenceSets.isDerivable(prover.getProof(axiom), axiom, axioms));
							} catch (Exception e) {
								throw new RuntimeException(
										"Exception while running proof test: "
												+ subsumee + " ⊑ " + subsumer,
										e);
							}
						}

					});
		} finally {
			prover.dispose();
		}
	}

}
