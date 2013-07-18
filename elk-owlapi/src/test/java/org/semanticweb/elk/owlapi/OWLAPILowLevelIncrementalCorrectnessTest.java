package org.semanticweb.elk.owlapi;

/*
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public class OWLAPILowLevelIncrementalCorrectnessTest {

	@Test
	public void testClassification() throws OWLOntologyCreationException {

		OWLOntologyManager ontologyManager = OWLManager
				.createOWLOntologyManager();

		OWLDataFactory factory = new OWLDataFactoryImpl();

		OWLClass A = factory.getOWLClass(IRI.create("A"));
		OWLClass B = factory.getOWLClass(IRI.create("B"));
		OWLObjectProperty R = factory.getOWLObjectProperty(IRI.create("R"));
		OWLObjectProperty S = factory.getOWLObjectProperty(IRI.create("S"));

		Set<OWLAxiom> initialAxioms = new ArrayHashSet<OWLAxiom>();

		OWLAxiom axAsubB = factory.getOWLSubClassOfAxiom(A, B);
		OWLAxiom axBsubA = factory.getOWLSubClassOfAxiom(B, A);

		initialAxioms.addAll(Arrays.asList(axAsubB, axBsubA));

		OWLOntology ont = ontologyManager.createOntology(initialAxioms);

		// Create an ELK reasoner.
		OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ont);

		assertTrue(reasoner.getEquivalentClasses(A).contains(B));
		assertFalse(reasoner.getSubClasses(B, true).containsEntity(A));
		assertFalse(reasoner.getSuperClasses(A, true).containsEntity(B));

		ontologyManager.removeAxiom(ont, axBsubA);

		// the changes are still not taken into account because the reasoner
		// is buffering
		assertTrue(reasoner.getEquivalentClasses(A).contains(B));
		assertFalse(reasoner.getSubClasses(B, true).containsEntity(A));
		assertFalse(reasoner.getSuperClasses(A, true).containsEntity(B));

		// this should take into account the changes for reasoning queries
		reasoner.flush();

		assertFalse(reasoner.getEquivalentClasses(A).contains(B));
		assertTrue(reasoner.getSubClasses(B, true).containsEntity(A));
		assertTrue(reasoner.getSuperClasses(A, true).containsEntity(B));

		ontologyManager.addAxiom(ont, axBsubA);
		reasoner.flush();
		ontologyManager.removeAxiom(ont, axBsubA);

		// the reasoner should reflect only changes until the last flush()
		assertTrue(reasoner.getEquivalentClasses(A).contains(B));
		assertFalse(reasoner.getSubClasses(B, true).containsEntity(A));
		assertFalse(reasoner.getSuperClasses(A, true).containsEntity(B));

		reasoner.flush();
		
		assertFalse(reasoner.getEquivalentClasses(A).contains(B));
		assertTrue(reasoner.getSubClasses(B, true).containsEntity(A));
		assertTrue(reasoner.getSuperClasses(A, true).containsEntity(B));
		
		// this axiom should trigger non-incremental classification
		OWLAxiom axRsubS = factory.getOWLSubObjectPropertyOfAxiom(R, S);
		ontologyManager.addAxiom(ont, axRsubS);

		assertFalse(reasoner.getEquivalentClasses(A).contains(B));
		assertTrue(reasoner.getSubClasses(B, true).containsEntity(A));
		assertTrue(reasoner.getSuperClasses(A, true).containsEntity(B));

		// Terminate the worker threads used by the reasoner.
		reasoner.dispose();

	}
}
