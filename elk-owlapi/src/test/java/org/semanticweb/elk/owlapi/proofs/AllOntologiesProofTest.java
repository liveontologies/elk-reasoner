/**
 * 
 */
package org.semanticweb.elk.owlapi.proofs;

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

import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owlapi.OWLAPITestUtils;
import org.semanticweb.elk.reasoner.saturation.tracing.ComprehensiveSubsumptionTracingTests;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingTestManifest;
import org.semanticweb.elk.reasoner.saturation.tracing.TracingTests;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.VoidTestOutput;
import org.semanticweb.elk.testing.io.URLTestIO;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
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
public class AllOntologiesProofTest {

	final static String INPUT_DATA_LOCATION = "classification_test_input";
	private static final Logger LOGGER_ = LoggerFactory.getLogger(AllOntologiesProofTest.class);

	protected final TracingTestManifest manifest;

	public AllOntologiesProofTest(TracingTestManifest testManifest) {
		manifest = testManifest;
	}

	@Before
	public void before() throws IOException, Owl2ParseException {
		assumeTrue(!ignore(manifest.getInput()));
	}

	protected boolean ignore(TestInput input) {
		return false;
	}

	@Test
	public void proofTest() throws Exception {
		final OWLDataFactory factory = OWLManager.getOWLDataFactory();
		// loading and classifying via the OWL API
		final OWLOntology ontology = loadOntology(manifest.getInput().getInputStream());
		final ExplainingOWLReasoner reasoner = OWLAPITestUtils.createReasoner(ontology);
		
		try {
			reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		} catch (InconsistentOntologyException e) {
			return;
		}

		try {
			// now do testing
			// this visitor checks binding of premises to axioms in the source ontology
	        final OWLInferenceVisitor bindingChecker = ProofTestUtils.getAxiomBindingChecker(ontology);
	        
	        ProofTestUtils.visitAllSubsumptionsForProofTests(reasoner, new ProofTestVisitor<Exception>() {
				
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
			});
			
		} catch (Exception e) {
			//swallow..
			LOGGER_.error("", e);
		} finally {
			reasoner.dispose();
		}
	}
	
	private OWLOntology loadOntology(InputStream stream) throws IOException, Owl2ParseException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = null;

		try {
			ontology = manager.loadOntologyFromOntologyDocument(stream);
		} catch (OWLOntologyCreationIOException e) {
			throw new IOException(e);
		} catch (OWLOntologyCreationException e) {
			throw new Owl2ParseException(e);
		}
		
		return ontology;
	}

	protected TracingTests getProvabilityTests(Taxonomy<ElkClass> taxonomy) {
		return new ComprehensiveSubsumptionTracingTests(taxonomy);
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
