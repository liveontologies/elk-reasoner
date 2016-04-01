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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.RandomSeedProvider;
import org.semanticweb.elk.owlapi.OWLAPITestUtils;
import org.semanticweb.elk.reasoner.tracing.TracingTestManifest;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.VoidTestOutput;
import org.semanticweb.elk.testing.io.URLTestIO;
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
import org.semanticweb.owlapitools.proofs.ExplainingOWLReasoner;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
import org.semanticweb.owlapitools.proofs.expressions.OWLExpression;

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

	public RandomProofCompletenessTest(final TracingTestManifest testManifest) {
		super(testManifest);
	}

	@Test
	public void proofCompletenessTest() throws Exception {
		final long seed = RandomSeedProvider.VALUE;
//		final long seed = 1459433444278L; TODO: seed to reproduce the problem with property chain optimization
		final Random random = new Random(seed);
		
		final OWLDataFactory factory = manager_.getOWLDataFactory();
		
		// loading and classifying via the OWL API
		final OWLOntology ontology =
				loadOntology(manifest_.getInput().getInputStream());
		final ExplainingOWLReasoner reasoner =
				OWLAPITestUtils.createReasoner(ontology);
		try {
			reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		} catch (final InconsistentOntologyException e) {
			// we will explain it, too
		}

		try {
			// now do testing
	        
	        ProofTestUtils.visitAllSubsumptionsForProofTests(reasoner, factory,
	        		new ProofTestVisitor<ProofGenerationException>() {
				
				@Override
				public void visit(final OWLClassExpression subsumee,
						final OWLClassExpression subsumer)
								throws ProofGenerationException {
					randomProofCompletenessTest(reasoner,
							factory.getOWLSubClassOfAxiom(subsumee, subsumer),
							ontology, random, seed);
				}

				@Override
				public void inconsistencyTest()
						throws ProofGenerationException {
					randomInconsistencyProofCompletenessTest(reasoner, ontology,
							random, seed);
				}
				
			});
			
		} catch (final Exception e) {
			throw new RuntimeException(e);
		} finally {
			reasoner.dispose();
		}
		
	}
	
	private void randomProofCompletenessTest(
			final ExplainingOWLReasoner reasoner,
			final OWLSubClassOfAxiom conclusion, final OWLOntology ontology,
			final Random random, final long seed)
					throws ProofGenerationException {
		final OWLExpression expr = reasoner.getDerivedExpression(conclusion);
		
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
				reasoner.getSuperClasses(conclusion.getSubClass(), false)
				.containsEntity((OWLClass) conclusion.getSuperClass());
		
		manager_.applyChanges(additions);
		
		assertFalse("Not all proofs were found!\n"
						+ "Seed: " + seed + "\n"
						+ "Conclusion: " + conclusion + "\n"
						+ "Proof Breaker: " + proofBreaker,
				conclusionDerived
		);
	}
	
	private void randomInconsistencyProofCompletenessTest(
			final ExplainingOWLReasoner reasoner, final OWLOntology ontology,
			final Random random, final long seed)
					throws ProofGenerationException {
		final OWLExpression expr =
				reasoner.getDerivedExpressionForInconsistency();
		
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
		
		final boolean conclusionDerived = !reasoner.isConsistent();
		
		manager_.applyChanges(additions);
		
		assertFalse("Not all proofs were found!\n"
						+ "Seed: " + seed + "\n"
						+ "Conclusion: Ontology is inconsistent\n"
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
						new TestManifestCreator<URLTestIO, VoidTestOutput, VoidTestOutput>() {
							@Override
							public TestManifest<URLTestIO, VoidTestOutput, VoidTestOutput> create(
									final URL input, final URL output) throws IOException {
								// don't need an expected output for these tests
								return new TracingTestManifest(input);
							}
						});
	}
}