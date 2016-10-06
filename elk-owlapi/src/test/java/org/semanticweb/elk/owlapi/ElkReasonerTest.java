package org.semanticweb.elk.owlapi;

/*
 * #%L
 * ELK OWL API Binding
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.model.RemoveImport;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import uk.ac.manchester.cs.owl.owlapi.OWLImportsDeclarationImpl;

/**
 * Testing correctness of ELK implementation of {@link OWLReasoner} interface
 * 
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 * 
 */
public class ElkReasonerTest {

	/**
	 * Testing correctness of the reasoner with respect to ontology changes
	 * 
	 */
	@Test
	public void testNoChanges() throws Exception {

		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLDataFactory dataFactory = man.getOWLDataFactory();

		// set up resolution of prefixes
		PrefixManager pm = new DefaultPrefixManager();
		pm.setDefaultPrefix("http://www.example.com/main#");
		pm.setPrefix("A:", "http://www.example.com/A#");
		pm.setPrefix("B:", "http://www.example.com/B#");

		// define query classes
		OWLClass mainX = dataFactory.getOWLClass(":X", pm);
		OWLClass mainY = dataFactory.getOWLClass(":Y", pm);
		OWLClass extA = dataFactory.getOWLClass("A:A", pm);
		OWLClass extB = dataFactory.getOWLClass("B:B", pm);
		OWLClass extC = dataFactory.getOWLClass("B:C", pm);

		// loading the root ontology
		OWLOntology root = loadOntology(man, "root.owl");

		// Create an ELK reasoner.
		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(root);

		try {

			// statistics about the root ontology
			assertEquals(root.getAxiomCount(), 3);
			// all three ontologies should be in the closure
			assertEquals(root.getImportsClosure().size(), 3);
			// all axioms from three ontologies should be in the closure
			assertEquals(getAxioms(root).size(), 6);

			// reasoner queries -- all subclasses are there
			assertTrue(reasoner.getSuperClasses(mainX, true).containsEntity(
					mainY));
			assertTrue(reasoner.getSuperClasses(mainX, true).containsEntity(
					extA));
			assertTrue(reasoner.getSuperClasses(mainY, true).containsEntity(
					extB));
			assertTrue(reasoner.getSuperClasses(extA, true)
					.containsEntity(extB));
			assertTrue(reasoner.getSuperClasses(extB, true)
					.containsEntity(extC));
			
		} finally {
			reasoner.dispose();
		}

	}

	/**
	 * Testing correctness of the reasoner with respect to ontology changes
	 * 
	 * removing an axiom ":X is-a :Y"
	 */
	@Test
	public void testRemovingXY() throws Exception {

		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLDataFactory dataFactory = man.getOWLDataFactory();

		// set up resolution of prefixes
		PrefixManager pm = new DefaultPrefixManager();
		pm.setDefaultPrefix("http://www.example.com/main#");
		pm.setPrefix("A:", "http://www.example.com/A#");
		pm.setPrefix("B:", "http://www.example.com/B#");

		// define query classes
		OWLClass mainX = dataFactory.getOWLClass(":X", pm);
		OWLClass mainY = dataFactory.getOWLClass(":Y", pm);
		OWLClass extA = dataFactory.getOWLClass("A:A", pm);
		OWLClass extB = dataFactory.getOWLClass("B:B", pm);
		OWLClass extC = dataFactory.getOWLClass("B:C", pm);

		// loading the root ontology
		OWLOntology root = loadOntology(man, "root.owl");

		// Create an ELK reasoner.
		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(root);

		try {

			// ************************************
			// ** removing an axiom ":X is-a :Y"
			// ************************************
			OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(mainX, mainY);
			man.removeAxiom(root, axiom);
			reasoner.flush();

			// the root ontology contains one fewer axioms
			assertEquals(root.getAxiomCount(), 2);
			// the number of ontologies in the import closure does not change
			assertEquals(root.getImportsClosure().size(), 3);
			// the total number of axioms reduces
			assertEquals(getAxioms(root).size(), 5);

			// reasoner queries -- first subsumption is gone
			assertFalse(reasoner.getSuperClasses(mainX, true).containsEntity(
					mainY));
			assertTrue(reasoner.getSuperClasses(mainX, true).containsEntity(
					extA));
			assertTrue(reasoner.getSuperClasses(mainY, true).containsEntity(
					extB));
			assertTrue(reasoner.getSuperClasses(extA, true)
					.containsEntity(extB));
			assertTrue(reasoner.getSuperClasses(extB, true)
					.containsEntity(extC));

		} finally {
			reasoner.dispose();
		}

	}

	/**
	 * Testing correctness of the reasoner with respect to ontology changes
	 * <p>
	 * trying to remove "A:A is-a B:B"
	 * <p>
	 * Because the removed axiom belongs to the imported ontology and
	 * not main ontology, the remove does not make any effect. So, we
	 * should end up with the ontology we have started with.
	 * <p>
	 * This test is ignored, because as of OWL API 4.1.3 the removal
	 * of the axiom is broadcasted even though the axiom is not removed.
	 * 
	 * TODO: file a bug report
	 */
	@Test
	@Ignore
	public void testRemovingAB() throws Exception {

		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLDataFactory dataFactory = man.getOWLDataFactory();

		// set up resolution of prefixes
		PrefixManager pm = new DefaultPrefixManager();
		pm.setDefaultPrefix("http://www.example.com/main#");
		pm.setPrefix("A:", "http://www.example.com/A#");
		pm.setPrefix("B:", "http://www.example.com/B#");

		// define query classes
		OWLClass mainX = dataFactory.getOWLClass(":X", pm);
		OWLClass mainY = dataFactory.getOWLClass(":Y", pm);
		OWLClass extA = dataFactory.getOWLClass("A:A", pm);
		OWLClass extB = dataFactory.getOWLClass("B:B", pm);
		OWLClass extC = dataFactory.getOWLClass("B:C", pm);

		// loading the root ontology
		OWLOntology root = loadOntology(man, "root.owl");

		// Create an ELK reasoner.
		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(root);

		try {

			// ************************************
			// ** trying to remove "A:A is-a B:B"
			// ************************************
			OWLSubClassOfAxiom axiom = dataFactory.getOWLSubClassOfAxiom(extA, extB);
			man.removeAxiom(root, axiom);
			reasoner.flush();

			// Because the removed axiom belongs to the imported ontology and
			// not main ontology, the remove does not make any effect. So, we
			// should end up with the ontology we have started with

			assertEquals(root.getAxiomCount(), 3);
			// all three ontologies should be in the closure
			assertEquals(root.getImportsClosure().size(), 3);
			// all axioms from three ontologies should be in the closure
			assertEquals(getAxioms(root).size(), 6);

			// reasoner queries -- all subsumptions are there
			assertTrue(reasoner.getSuperClasses(mainX, true).containsEntity(
					mainY));
			assertTrue(reasoner.getSuperClasses(mainX, true).containsEntity(
					extA));
			assertTrue(reasoner.getSuperClasses(mainY, true).containsEntity(
					extB));
			assertTrue(reasoner.getSuperClasses(extA, true)
					.containsEntity(extB));
			assertTrue(reasoner.getSuperClasses(extB, true)
					.containsEntity(extC));

		} finally {
			reasoner.dispose();
		}

	}

	/**
	 * Testing correctness of the reasoner with respect to ontology changes
	 * <p>
	 * removing the import declaration for </impA>
	 */
	@Test
	public void testRemovingImpA() throws Exception {

		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLDataFactory dataFactory = man.getOWLDataFactory();

		// set up resolution of prefixes
		PrefixManager pm = new DefaultPrefixManager();
		pm.setDefaultPrefix("http://www.example.com/main#");
		pm.setPrefix("A:", "http://www.example.com/A#");
		pm.setPrefix("B:", "http://www.example.com/B#");

		// define query classes
		OWLClass mainX = dataFactory.getOWLClass(":X", pm);
		OWLClass mainY = dataFactory.getOWLClass(":Y", pm);
		OWLClass extA = dataFactory.getOWLClass("A:A", pm);
		OWLClass extB = dataFactory.getOWLClass("B:B", pm);
		OWLClass extC = dataFactory.getOWLClass("B:C", pm);

		// loading the root ontology
		OWLOntology root = loadOntology(man, "root.owl");

		// Create an ELK reasoner.
		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(root);

		try {

			// ************************************
			// ** removing the import declaration for </impA>
			// ************************************

			OWLImportsDeclaration importA = new OWLImportsDeclarationImpl(
					IRI.create("http://www.example.com#impA"));
			OWLOntologyChange change = new RemoveImport(root, importA);
			man.applyChange(change);
			reasoner.flush();

			// Now the root ontology should not import anything
			assertEquals(root.getAxiomCount(), 3);
			assertEquals(root.getImportsClosure().size(), 1);
			assertEquals(getAxioms(root).size(), 3);

			// reasoner queries -- only subsumptions of the root ontology are
			// there
			assertTrue(reasoner.getSuperClasses(mainX, true).containsEntity(
					mainY));
			assertTrue(reasoner.getSuperClasses(mainX, true).containsEntity(
					extA));
			assertTrue(reasoner.getSuperClasses(mainY, true).containsEntity(
					extB));
			assertFalse(reasoner.getSuperClasses(extA, true).containsEntity(
					extB));
			assertFalse(reasoner.getSuperClasses(extB, true).containsEntity(
					extC));

		} finally {
			reasoner.dispose();
		}

	}

	/**
	 * Testing correctness of the reasoner with respect to ontology changes
	 * <p>
	 * removing the import declaration for </impA>,
	 * adding the import declaration for </impB> and removing
	 * ":Y is-a B:B"
	 */
	@Test
	public void testRemovingImpAAddingImpBRemovingYB() throws Exception {

		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLDataFactory dataFactory = man.getOWLDataFactory();

		// set up resolution of prefixes
		PrefixManager pm = new DefaultPrefixManager();
		pm.setDefaultPrefix("http://www.example.com/main#");
		pm.setPrefix("A:", "http://www.example.com/A#");
		pm.setPrefix("B:", "http://www.example.com/B#");

		// define query classes
		OWLClass mainX = dataFactory.getOWLClass(":X", pm);
		OWLClass mainY = dataFactory.getOWLClass(":Y", pm);
		OWLClass extA = dataFactory.getOWLClass("A:A", pm);
		OWLClass extB = dataFactory.getOWLClass("B:B", pm);
		OWLClass extC = dataFactory.getOWLClass("B:C", pm);

		// loading the root ontology
		OWLOntology root = loadOntology(man, "root.owl");

		// Create an ELK reasoner.
		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(root);

		try {

			// ************************************
			// ** adding the import declaration for </impB> and removing
			// ":Y is-a B:B"
			// ************************************

			OWLImportsDeclaration importA = new OWLImportsDeclarationImpl(
					IRI.create("http://www.example.com#impA"));
			OWLOntologyChange change = new RemoveImport(root, importA);
			man.applyChange(change);
			OWLImportsDeclaration importB = new OWLImportsDeclarationImpl(
					IRI.create("http://www.example.com#impB"));
			change = new AddImport(root, importB);
			man.applyChange(change);
			OWLSubClassOfAxiom axiom = dataFactory.getOWLSubClassOfAxiom(mainY, extB);
			man.removeAxiom(root, axiom);
			reasoner.flush();

			// Now ontology should import only ontology </impB>
			assertEquals(root.getAxiomCount(), 2);
			assertEquals(root.getImportsClosure().size(), 2);
			assertEquals(getAxioms(root).size(), 4);

			// reasoner queries -- only subsumptions of the root
			// ontology </impB> and there
			assertTrue(reasoner.getSuperClasses(mainX, true).containsEntity(
					mainY));
			assertTrue(reasoner.getSuperClasses(mainX, true).containsEntity(
					extA));
			assertFalse(reasoner.getSuperClasses(mainY, true).containsEntity(
					extB));
			assertFalse(reasoner.getSuperClasses(extA, true).containsEntity(
					extB));
			assertTrue(reasoner.getSuperClasses(extB, true)
					.containsEntity(extC));

		} finally {
			reasoner.dispose();
		}

	}
	
	
	/**
	 * Testing correctness of the reasoner when changes are made to other, imported or not, ontologies
	 * 
	 */
	@Test
	public void testChangesToOtherOntologies() throws Exception {

		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLDataFactory dataFactory = man.getOWLDataFactory();

		// set up resolution of prefixes
		PrefixManager pm = new DefaultPrefixManager();
		pm.setDefaultPrefix("http://www.example.com/main#");
		pm.setPrefix("A:", "http://www.example.com/A#");
		pm.setPrefix("B:", "http://www.example.com/B#");

		// define query classes
		OWLClass mainY = dataFactory.getOWLClass(":Y", pm);
		OWLClass extA = dataFactory.getOWLClass("A:A", pm);
		OWLClass extB = dataFactory.getOWLClass("B:B", pm);
		OWLClass extC = dataFactory.getOWLClass("B:C", pm);

		// loading the root ontology
		OWLOntology root = loadOntology(man, "root.owl");
		// the imported ontologies must be loaded
		OWLOntology ontoA = man.getOntology(IRI.create("http://www.example.com/A"));
		OWLOntology ontoB = man.getOntology(IRI.create("http://www.example.com/B"));

		// Create an ELK reasoner.
		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(root);

		try {
			
			assertTrue(reasoner.getSuperClasses(extA, false).containsEntity(
					extC));
			assertTrue(reasoner.getSuperClasses(mainY, false).containsEntity(
					extC));
			
			// ************************************
			// ** removing an axiom "A:A is-a B:B" from impA
			// ************************************
			OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(extA, extB);
			man.removeAxiom(ontoA, axiom);
			reasoner.flush();
			
			assertFalse(reasoner.getSuperClasses(extA, false).containsEntity(
					extC));
			// put it back
			man.addAxiom(ontoA, axiom);
			reasoner.flush();
			
			assertTrue(reasoner.getSuperClasses(extA, false).containsEntity(
					extC));

			// ************************************
			// ** removing an axiom "B:B is-a B:C" from impB
			// ************************************
			axiom = dataFactory.getOWLSubClassOfAxiom(extB, extC);
			man.removeAxiom(ontoB, axiom);
			reasoner.flush();
			
			assertFalse(reasoner.getSuperClasses(mainY, false).containsEntity(
					extC));

		}
		finally {
			reasoner.dispose();
		}
	}
	

	/**
	 * Loading ontologies from the test resources
	 * 
	 * @param man
	 * @param name
	 * @return the loaded ontology
	 * @throws URISyntaxException
	 * @throws OWLOntologyCreationException
	 */
	private OWLOntology loadOntology(OWLOntologyManager man, String name)
			throws URISyntaxException, OWLOntologyCreationException {

		final URI ontologyRoot = getClass().getClassLoader()
				.getResource("ontologies").toURI();

		OWLOntologyIRIMapper iriMapper = new ThisIRIMapper(
				ontologyRoot.toString());

		man.setIRIMappers(Collections.singleton(iriMapper));

		final URI mainOntology = getClass().getClassLoader()
				.getResource("ontologies/" + name).toURI();

		return man.loadOntologyFromOntologyDocument(new File(mainOntology));

	}

	/**
	 * @return the list of the axioms from the import closure of the given
	 *         ontology
	 */
	private static List<OWLAxiom> getAxioms(OWLOntology main) {
		List<OWLAxiom> axioms = new ArrayList<OWLAxiom>();
		for (OWLOntology ont : main.getImportsClosure()) {
			axioms.addAll(ont.getAxioms());
		}
		return axioms;
	}

	/**
	 * A simple {@link OWLOntologyIRIMapper} that appends the provided root
	 * directory to the ontologyIRI and creates an owl extension
	 * 
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	static class ThisIRIMapper implements OWLOntologyIRIMapper {

		private static final long serialVersionUID = 7697972905355499952L;
		
		final String root;

		ThisIRIMapper(String root) {
			this.root = root;
		}

		@Override
		public IRI getDocumentIRI(IRI ontologyIRI) {
			// and the prefix to the ontology
			return IRI.create(root + "/" + ontologyIRI.getShortForm() + ".owl");
		}
	}
}
