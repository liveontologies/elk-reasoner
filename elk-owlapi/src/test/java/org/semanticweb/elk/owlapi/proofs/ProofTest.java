package org.semanticweb.elk.owlapi.proofs;
/*
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.liveontologies.owlapi.proof.OWLProofNode;
import org.liveontologies.owlapi.proof.OWLProofStep;
import org.liveontologies.owlapi.proof.OWLProver;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owlapi.OWLAPITestUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;

/**
 * 
 * @author Pavel Klinov pavel.klinov@uni-ulm.de
 *
 */
public class ProofTest {

	OWLDataFactory factory;

	@Before
	public void initialize() {
		factory = OWLManager.getOWLDataFactory();
	}

	@Test
	public void reflexiveRoles() throws Exception {
		// loading and classifying via the OWL API
		final OWLOntology ontology = loadOntology(
				ProofTest.class.getClassLoader().getResourceAsStream(
						"classification_test_input/ReflexiveRole.owl"));
		final OWLProver prover = OWLAPITestUtils.createProver(ontology);

		prover.precomputeInferences(InferenceType.CLASS_HIERARCHY);

		OWLClass sub = factory.getOWLClass(IRI.create("http://example.org/K"));
		OWLClass sup = factory.getOWLClass(IRI.create("http://example.org/L"));

		// printInferences(reasoner, sub, sup);

		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(sub, sup));
	}

	@Test
	public void reflexiveRoles2() throws Exception {
		final OWLOntology ontology = loadOntology(
				ProofTest.class.getClassLoader().getResourceAsStream(
						"classification_test_input/ReflexiveRole.owl"));
		final OWLProver prover = OWLAPITestUtils.createProver(ontology);

		prover.precomputeInferences(InferenceType.CLASS_HIERARCHY);

		OWLClass sub = factory.getOWLClass(IRI.create("http://example.org/C1"));
		OWLClass sup = factory.getOWLClass(IRI.create("http://example.org/F"));

		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(sub, sup));
	}

	@Test
	public void compositionReflexivity() throws Exception {
		// loading and classifying via the OWL API
		final OWLOntology ontology = loadOntology(
				ProofTest.class.getClassLoader().getResourceAsStream(
						"classification_test_input/CompositionReflexivity.owl"));
		final OWLProver prover = OWLAPITestUtils.createProver(ontology);

		prover.precomputeInferences(InferenceType.CLASS_HIERARCHY);

		OWLClass sub = factory.getOWLClass(IRI.create("http://example.org/A"));
		OWLClass sup = factory.getOWLClass(IRI.create("http://example.org/B"));

		// printInferences(reasoner, sub, sup);

		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(sub, sup));
	}

	@Test
	public void emptyConjunction() throws Exception {
		OWLOntologyManager owlManager = OWLManager
				.createConcurrentOWLOntologyManager();
		// creating an ontology
		final OWLOntology ontology = owlManager.createOntology();
		OWLClass a = factory.getOWLClass(IRI.create("http://example.org/A"));
		OWLClass b = factory.getOWLClass(IRI.create("http://example.org/B"));
		OWLClass c = factory.getOWLClass(IRI.create("http://example.org/C"));
		OWLClass d = factory.getOWLClass(IRI.create("http://example.org/D"));
		// ObjectInteresectionOf() = owl:Thing
		owlManager.addAxiom(ontology, factory.getOWLSubClassOfAxiom(a,
				factory.getOWLObjectIntersectionOf()));
		owlManager.addAxiom(ontology,
				factory.getOWLSubClassOfAxiom(b, factory.getOWLThing()));
		owlManager.addAxiom(ontology, factory.getOWLSubClassOfAxiom(
				factory.getOWLObjectIntersectionOf(), c));
		owlManager.addAxiom(ontology,
				factory.getOWLSubClassOfAxiom(factory.getOWLThing(), d));

		final OWLProver prover = OWLAPITestUtils.createProver(ontology);

		prover.precomputeInferences(InferenceType.CLASS_HIERARCHY);

		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(a, c));
		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(a, d));
		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(b, c));
		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(b, d));
	}

	@Test
	public void emptyDisjunction() throws Exception {
		OWLOntologyManager owlManager = OWLManager
				.createConcurrentOWLOntologyManager();
		// creating an ontology
		final OWLOntology ontology = owlManager.createOntology();
		OWLClass a = factory.getOWLClass(IRI.create("http://example.org/A"));
		OWLClass b = factory.getOWLClass(IRI.create("http://example.org/B"));
		OWLClass c = factory.getOWLClass(IRI.create("http://example.org/C"));
		// ObjectUnionOf() = owl:Nothing
		owlManager.addAxiom(ontology, factory.getOWLSubClassOfAxiom(a,
				factory.getOWLObjectUnionOf()));
		owlManager.addAxiom(ontology,
				factory.getOWLSubClassOfAxiom(b, factory.getOWLNothing()));
		owlManager.addAxiom(ontology, factory
				.getOWLSubClassOfAxiom(factory.getOWLObjectUnionOf(), c));

		final OWLProver prover = OWLAPITestUtils.createProver(ontology);

		prover.precomputeInferences(InferenceType.CLASS_HIERARCHY);

		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(a, b));
		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(b, a));
		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(a, c));
		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(b, c));
	}

	@Test
	public void emptyEnumeration() throws Exception {
		OWLOntologyManager owlManager = OWLManager
				.createConcurrentOWLOntologyManager();
		// creating an ontology
		final OWLOntology ontology = owlManager.createOntology();
		OWLClass a = factory.getOWLClass(IRI.create("http://example.org/A"));
		OWLClass b = factory.getOWLClass(IRI.create("http://example.org/B"));
		OWLClass c = factory.getOWLClass(IRI.create("http://example.org/C"));
		// ObjectOneOf() = owl:Nothing
		owlManager.addAxiom(ontology,
				factory.getOWLSubClassOfAxiom(a, factory.getOWLObjectOneOf()));
		owlManager.addAxiom(ontology,
				factory.getOWLSubClassOfAxiom(b, factory.getOWLNothing()));
		owlManager.addAxiom(ontology,
				factory.getOWLSubClassOfAxiom(factory.getOWLObjectOneOf(), c));

		final OWLProver prover = OWLAPITestUtils.createProver(ontology);

		prover.precomputeInferences(InferenceType.CLASS_HIERARCHY);

		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(a, b));
		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(b, a));
		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(a, c));
		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(b, c));
	}

	@Test
	public void emptyDisjointUnion() throws Exception {
		OWLOntologyManager owlManager = OWLManager
				.createConcurrentOWLOntologyManager();
		// creating an ontology
		final OWLOntology ontology = owlManager.createOntology();
		OWLClass a = factory.getOWLClass(IRI.create("http://example.org/A"));
		OWLClass b = factory.getOWLClass(IRI.create("http://example.org/B"));
		// DisjointUnion(A ) = EquivalentClasses(A owl:Nothing)
		owlManager.addAxiom(ontology, factory.getOWLDisjointUnionAxiom(a,
				Collections.<OWLClassExpression> emptySet()));
		owlManager.addAxiom(ontology, factory.getOWLSubClassOfAxiom(b, b));

		final OWLProver prover = OWLAPITestUtils.createProver(ontology);

		prover.precomputeInferences(InferenceType.CLASS_HIERARCHY);

		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(a, b));
	}

	@Test
	public void proofsUnderOntologyUpdate() throws Exception {
		// loading and classifying via the OWL API
		OWLOntology ontology = loadOntology(
				ProofTest.class.getClassLoader().getResourceAsStream(
						"ontologies/PropertyCompositionsWithHierarchy.owl"));
		final OWLProver prover = OWLAPITestUtils.createProver(ontology);

		prover.precomputeInferences(InferenceType.CLASS_HIERARCHY);

		OWLClass sub = factory.getOWLClass(IRI.create("http://example.org/A"));
		OWLClass sup = factory.getOWLClass(IRI.create("http://example.org/G"));

		// printInferences(reasoner, sub, sup);
		// OWLExpression root =
		// reasoner.getDerivedExpression(factory.getOWLSubClassOfAxiom(sub,
		// sup));
		// System.err.println(OWLProofUtils.printProofTree(root));
		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(sub, sup));

		// now convert C <= R3 some D to C < S3 some D
		OWLClass c = factory.getOWLClass(IRI.create("http://example.org/C"));
		OWLClass d = factory.getOWLClass(IRI.create("http://example.org/D"));
		OWLObjectProperty r3 = factory
				.getOWLObjectProperty(IRI.create("http://example.org/R3"));
		OWLObjectProperty s3 = factory
				.getOWLObjectProperty(IRI.create("http://example.org/S3"));
		OWLAxiom oldAx = factory.getOWLSubClassOfAxiom(c,
				factory.getOWLObjectSomeValuesFrom(r3, d));
		OWLAxiom newAx = factory.getOWLSubClassOfAxiom(c,
				factory.getOWLObjectSomeValuesFrom(s3, d));

		OWLOntologyManager manager = ontology.getOWLOntologyManager();

		manager.applyChanges(Arrays.asList(new RemoveAxiom(ontology, oldAx),
				new AddAxiom(ontology, newAx)));

		prover.precomputeInferences(InferenceType.CLASS_HIERARCHY);

		// printInferences(reasoner, sub, sup);
		// root =
		// reasoner.getDerivedExpression(factory.getOWLSubClassOfAxiom(sub,
		// sup));
		// System.err.println(OWLProofUtils.printProofTree(root));
		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(sub, sup));
	}

	void printInferences(OWLProver prover, OWLClassExpression sub,
			OWLClassExpression sup) {
		ProofExplorer.visitInferences(
				prover.getProof(factory.getOWLSubClassOfAxiom(sub, sup)),
				new ProofExplorer.Controller() {

					@Override
					public boolean nodeVisited(OWLProofNode node) {
						return false;
					}

					@Override
					public boolean inferenceVisited(OWLProofStep inference) {
						System.err.println(inference);
						System.err.print("");
						return false;
					}
				});
	}

	private OWLOntology loadOntology(InputStream stream)
			throws IOException, Owl2ParseException {
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
