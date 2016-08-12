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

import static org.junit.Assert.assertFalse;
import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.liveontologies.owlapi.proof.OWLProver;
import org.liveontologies.owlapi.proof.util.ProofNode;
import org.semanticweb.elk.RandomSeedProvider;
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
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.InferenceType;

/**
 * For some conclusions tries to randomly break all collected proofs by removing
 * required axioms; if the conclusion is still derived by the reasoner,
 * we missed some proof.
 * 
 * @author Peter Skocovsky
 * 
 */
@RunWith(PolySuite.class)
public class RandomProofCompletenessTest extends BaseProofTest {

	final static String INPUT_DATA_LOCATION = "classification_test_input";

	static final String[] IGNORE_LIST = { "AssertionDisjoint.owl",
			"ConjunctionsComplex.owl", "DifferentSameIndividual.owl",
			"Inconsistent.owl", "OneOf.owl", "PropertyRangesHierarchy.owl",
			"SameIndividual.owl", "forest.owl", "TransitivePropertyChain.owl",
			"TransitivityByChain.owl" };

	static {
		Arrays.sort(IGNORE_LIST);
	}
	
	public RandomProofCompletenessTest(final TracingTestManifest testManifest) {
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
	public void proofCompletenessTest() throws Exception {
		final long seed = RandomSeedProvider.VALUE;
//		final long seed = 1459433444278L; // problem with property chain optimization
//		final long seed = 1459864883969L; // problem with incremental mode 
		final Random random = new Random(seed);
		
		final OWLDataFactory factory = manager_.getOWLDataFactory();
		
		// loading and classifying via the OWL API
		final OWLOntology ontology =
				loadOntology(manifest_.getInput().getUrl().openStream());
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
					randomProofCompletenessTest(prover,
							factory.getOWLSubClassOfAxiom(subsumee, subsumer),
							ontology, random, seed);
				}
				
			});
			
		} catch (final Exception e) {
			throw new RuntimeException(e);
		} finally {
			prover.dispose();
		}
		
	}
	
	private void randomProofCompletenessTest(
			final OWLProver prover,
			final OWLSubClassOfAxiom conclusion, final OWLOntology ontology,
			final Random random, final long seed) {
		final ProofNode<OWLAxiom> expr = prover.getProof(conclusion);
		
		final Set<OWLAxiom> proofBreaker =
				ProofTestUtils.collectProofBreaker(expr, ontology, random);
		final List<OWLOntologyChange> deletions =
				new ArrayList<OWLOntologyChange>();
		final List<OWLOntologyChange> additions =
				new ArrayList<OWLOntologyChange>();
		for (final OWLAxiom axiom : proofBreaker) {
			deletions.add(new RemoveAxiom(ontology, axiom));
			additions.add(new AddAxiom(ontology, axiom));
		}
		
		manager_.applyChanges(deletions);
		
		final boolean conclusionDerived =
				prover.getSuperClasses(conclusion.getSubClass(), false)
				.containsEntity((OWLClass) conclusion.getSuperClass());
		
		manager_.applyChanges(additions);
		
		assertFalse("Not all proofs were found!\n"
						+ "Seed: " + seed + "\n"
						+ "Conclusion: " + conclusion + "\n"
						+ "Proof Breaker: " + proofBreaker,
				conclusionDerived
		);
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
									final URL input, final URL output) throws IOException {
								// don't need an expected output for these tests
								return new TracingTestManifest(input);
							}
						});
	}
}