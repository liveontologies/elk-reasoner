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

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.owlapi.OWLAPITestUtils;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingTestManifest;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.VoidTestOutput;
import org.semanticweb.elk.testing.io.URLTestIO;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapitools.proofs.ExplainingOWLReasoner;
import org.semanticweb.owlapitools.proofs.exception.ProofGenerationException;
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

	public AllOntologiesProofTest(TracingTestManifest testManifest) {
		super(testManifest);
	}

	@Test
	public void proofTest() throws Exception {
		final OWLDataFactory factory = manager_.getOWLDataFactory();
		// loading and classifying via the OWL API
		final OWLOntology ontology = loadOntology(manifest_.getInput().getInputStream());
		final ExplainingOWLReasoner reasoner = OWLAPITestUtils.createReasoner(ontology);
		
		try {
			reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		} catch (InconsistentOntologyException e) {
			// we will explain it, too
		}

		try {
			// now do testing
			// this visitor checks binding of premises to axioms in the source ontology
	        final OWLInferenceVisitor bindingChecker = ProofTestUtils.getAxiomBindingChecker(ontology);
	        
	        ProofTestUtils.visitAllSubsumptionsForProofTests(reasoner, factory,
	        		new ProofTestVisitor<Exception>() {
				
				@Override
				public void visit(OWLClassExpression subsumee, OWLClassExpression subsumer) {
					LOGGER_.info("Requesting proofs for {} <= {}", subsumee, subsumer);
					
					try {
						ProofTestUtils.provabilityTest(reasoner, factory.getOWLSubClassOfAxiom(subsumee, subsumer));
						RecursiveInferenceVisitor.visitInferences(reasoner, factory.getOWLSubClassOfAxiom(subsumee, subsumer), bindingChecker, true);
					} catch (ProofGenerationException e) {
						fail(e.getMessage());
					}
				}

				@Override
				public void inconsistencyTest() throws Exception {
					ProofTestUtils.provabilityOfInconsistencyTest(reasoner);
					RecursiveInferenceVisitor.visitInferencesOfInconsistency(reasoner, bindingChecker, true);
				}
			});
			
		} catch (Exception e) {
			LOGGER_.error("Unexpected exception", e);
		} finally {
			reasoner.dispose();
		}
	}

	@Config
	public static Configuration getConfig() throws URISyntaxException,
			IOException {
		return ConfigurationUtils
				.loadFileBasedTestConfiguration(
						INPUT_DATA_LOCATION,
						AllOntologiesProofTest.class,
						"owl",
						new TestManifestCreator<URLTestIO, VoidTestOutput, VoidTestOutput>() {
							@Override
							public TestManifest<URLTestIO, VoidTestOutput, VoidTestOutput> create(
									URL input, URL output) throws IOException {
								// don't need an expected output for these tests
								return new TracingTestManifest(input);
							}
						});
	}
}
