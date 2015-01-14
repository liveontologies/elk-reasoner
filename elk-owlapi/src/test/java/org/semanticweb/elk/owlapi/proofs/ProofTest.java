package org.semanticweb.elk.owlapi.proofs;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owlapi.OWLAPITestUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapitools.proofs.ExplainingOWLReasoner;
import org.semanticweb.owlapitools.proofs.OWLInference;

/**
 * 
 * @author	Pavel Klinov
 * 			pavel.klinov@uni-ulm.de
 *
 */
public class ProofTest {

	@Test
	public void reflexiveRoles() throws Exception {
		final OWLDataFactory factory = OWLManager.getOWLDataFactory();
		// loading and classifying via the OWL API
		final OWLOntology ontology = loadOntology(ProofTest.class.getClassLoader().getResourceAsStream("classification_test_input/ReflexiveRole.owl"));
		final ExplainingOWLReasoner reasoner = OWLAPITestUtils.createReasoner(ontology);
		
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		
		OWLClass sub = factory.getOWLClass(IRI.create("http://example.org/K"));
		OWLClass sup = factory.getOWLClass(IRI.create("http://example.org/L"));
		
		ProofTestUtils.provabilityTest(reasoner, factory.getOWLSubClassOfAxiom(sub, sup));
		RecursiveInferenceVisitor.visitInferences(reasoner, factory.getOWLSubClassOfAxiom(sub, sup), new OWLInferenceVisitor() {
			
			@Override
			public void visit(OWLInference inference) {
				System.err.println(inference);
				System.err.print("");
			}
			
		}, true);
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
}
