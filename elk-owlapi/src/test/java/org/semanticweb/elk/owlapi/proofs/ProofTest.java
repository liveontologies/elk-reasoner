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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.liveontologies.owlapi.proof.OWLProof;
import org.liveontologies.owlapi.proof.OWLProofNode;
import org.liveontologies.owlapi.proof.OWLProofStep;
import org.liveontologies.owlapi.proof.OWLProver;
import org.liveontologies.owlapi.proof.ProofChangeListener;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owlapi.OWLAPITestUtils;
import org.semanticweb.elk.owlapi.TestOWLManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
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
	public void inconsistentOwlThing() throws Exception {
		OWLOntologyManager owlManager = OWLManager
				.createConcurrentOWLOntologyManager();
		// creating an ontology
		final OWLOntology ontology = owlManager.createOntology();
		OWLClass a = factory.getOWLClass(IRI.create("http://example.org/A"));
		OWLClass b = factory.getOWLClass(IRI.create("http://example.org/B"));
		// top subclass bottom => inconsistent
		owlManager.addAxiom(ontology, factory.getOWLSubClassOfAxiom(
				factory.getOWLThing(), factory.getOWLNothing()));

		final OWLProver prover = OWLAPITestUtils.createProver(ontology);

		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(a, b));
		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(
						factory.getOWLObjectIntersectionOf(a, b),
						factory.getOWLNothing()));
	}

	@Test
	public void inconsistentIndividual() throws Exception {
		OWLOntologyManager owlManager = OWLManager
				.createConcurrentOWLOntologyManager();
		// creating an ontology
		final OWLOntology ontology = owlManager.createOntology();
		OWLNamedIndividual ind = factory
				.getOWLNamedIndividual(IRI.create("http://example.org/i"));
		OWLClass a = factory.getOWLClass(IRI.create("http://example.org/A"));
		OWLClass b = factory.getOWLClass(IRI.create("http://example.org/B"));
		// ind instance of bottom => inconsistent
		owlManager.addAxiom(ontology, factory
				.getOWLClassAssertionAxiom(factory.getOWLNothing(), ind));

		final OWLProver prover = OWLAPITestUtils.createProver(ontology);

		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(a, b));
		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(
						factory.getOWLObjectIntersectionOf(a, b),
						factory.getOWLNothing()));
	}

	@Test
	public void inconsistentClass() throws Exception {
		OWLOntologyManager owlManager = OWLManager
				.createConcurrentOWLOntologyManager();
		// creating an ontology
		final OWLOntology ontology = owlManager.createOntology();
		OWLClass a = factory.getOWLClass(IRI.create("http://example.org/A"));
		OWLClass b = factory.getOWLClass(IRI.create("http://example.org/B"));
		// A subclass of bottom => A is inconsistent
		owlManager.addAxiom(ontology,
				factory.getOWLSubClassOfAxiom(a, factory.getOWLNothing()));

		final OWLProver prover = OWLAPITestUtils.createProver(ontology);

		ProofTestUtils.provabilityTest(prover,
				factory.getOWLSubClassOfAxiom(a, b));
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

	@Test
	public void proofListener() throws Exception {
		OWLOntologyManager owlManager = OWLManager
				.createConcurrentOWLOntologyManager();
		OWLClass a = factory.getOWLClass(IRI.create("http://example.org/A"));
		OWLClass b = factory.getOWLClass(IRI.create("http://example.org/B"));
		OWLClass c = factory.getOWLClass(IRI.create("http://example.org/C"));
		OWLObjectProperty r = factory
				.getOWLObjectProperty(IRI.create("http://example.org/R"));
		OWLObjectProperty s = factory
				.getOWLObjectProperty(IRI.create("http://example.org/S"));
		OWLAxiom ax1 = factory.getOWLSubClassOfAxiom(a, c);
		OWLAxiom ax2 = factory.getOWLSubClassOfAxiom(c, b);
		OWLAxiom ax3 = factory.getOWLSubClassOfAxiom(a,
				factory.getOWLObjectSomeValuesFrom(r, c));
		OWLAxiom ax4 = factory.getOWLSubClassOfAxiom(
				factory.getOWLObjectSomeValuesFrom(s, c), b);
		OWLAxiom ax5 = factory.getOWLSubObjectPropertyOfAxiom(r, s);

		boolean changed = false; // means entailment has changed

		for (boolean bufferringMode : Arrays.asList(true, false)) {
			// creating an ontology
			final OWLOntology ontology = owlManager.createOntology();

			final OWLProver prover = OWLAPITestUtils.createProver(
					OWLAPITestUtils.createReasoner(ontology, bufferringMode));
			OWLProof proofAB = prover
					.getProof(factory.getOWLSubClassOfAxiom(a, b));
			ProofChangeTracker tracker = new ProofChangeTracker();
			proofAB.addListener(tracker);

			assertFalse(
					ProofTestUtils.isDerivable(proofAB.getRoot(), ontology));

			// add ax1 and ax2 => ENTAIL
			owlManager.applyChanges(Arrays.asList(new AddAxiom(ontology, ax1),
					new AddAxiom(ontology, ax2)));

			// derivable only in non-buffering mode
			changed = tracker.changed();
			assertEquals(!bufferringMode, changed);
			assertEquals(!bufferringMode,
					ProofTestUtils.isDerivable(proofAB.getRoot(), ontology));
			// but always derivable after flush
			prover.flush();
			changed |= tracker.changed();
			assertTrue(changed);
			assertTrue(ProofTestUtils.isDerivable(proofAB.getRoot(), ontology));

			// remove ax1, add ax3, ax4 => NOT ENTAIL
			owlManager.applyChanges(Arrays.asList(
					new RemoveAxiom(ontology, ax1), new AddAxiom(ontology, ax3),
					new AddAxiom(ontology, ax4)));

			// not derivable even in the  mode since ontology changed
			changed = tracker.changed();
			assertEquals(!bufferringMode, changed);
			assertFalse(
					ProofTestUtils.isDerivable(proofAB.getRoot(), ontology));
			// still not derivable after flush
			prover.flush();
			changed |= tracker.changed();
			assertTrue(changed);
			assertFalse(
					ProofTestUtils.isDerivable(proofAB.getRoot(), ontology));

			// add ax5 => ENTAIL
			owlManager.applyChanges(Arrays.asList(new AddAxiom(ontology, ax5)));

			// derivable only in non-buffering mode
			changed = tracker.changed();
			assertEquals(!bufferringMode, changed);
			assertEquals(!bufferringMode,
					ProofTestUtils.isDerivable(proofAB.getRoot(), ontology));
			// but always derivable after flush
			prover.flush();
			changed |= tracker.changed();
			assertTrue(changed);
			assertTrue(ProofTestUtils.isDerivable(proofAB.getRoot(), ontology));

		}

	}

	void printInferences(OWLProver prover, OWLClassExpression sub,
			OWLClassExpression sup) {
		ProofExplorer.visitInferences(prover
				.getProof(factory.getOWLSubClassOfAxiom(sub, sup)).getRoot(),
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
		OWLOntologyManager manager = TestOWLManager.createOWLOntologyManager();
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

	private static class ProofChangeTracker implements ProofChangeListener {

		private boolean changed_ = false;

		@Override
		public void proofChanged() {
			this.changed_ = true;
		}

		boolean changed() {
			boolean result = changed_;
			changed_ = false;
			return result;
		}

	}
}
