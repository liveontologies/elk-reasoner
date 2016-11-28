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
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public class RoleAxiomOWLAPILowLevelIncrementalClassTest extends
		BaseOWLAPILowLevelIncrementalClassTest {

	OWLOntologyManager ontologyManager;
	OWLClass A;
	OWLClass B;
	OWLAxiom axRsubS;

	@Override
	void prepare() {
		final OWLDataFactory factory = new OWLDataFactoryImpl();
		A = factory.getOWLClass(IRI.create("A"));
		B = factory.getOWLClass(IRI.create("B"));
		OWLObjectProperty R = factory.getOWLObjectProperty(IRI.create("R"));
		OWLObjectProperty S = factory.getOWLObjectProperty(IRI.create("S"));
		OWLAxiom axExSsomeAsubB = factory.getOWLSubClassOfAxiom(
				factory.getOWLObjectSomeValuesFrom(S, A), B);
		OWLAxiom axReflR = factory.getOWLReflexiveObjectPropertyAxiom(R);
		axRsubS = factory.getOWLSubObjectPropertyOfAxiom(R, S);
		// from these three axioms it should follow A subclass B
		ontologyManager = TestOWLManager.createOWLOntologyManager();
		try {
			ont = ontologyManager.createOntology(new HashSet<OWLAxiom>(Arrays
					.asList(axExSsomeAsubB, axReflR, axRsubS)));
		} catch (OWLOntologyCreationException e) {
			fail(e.toString());
		}
	}

	@Override
	void add() {
		ontologyManager.addAxiom(ont, axRsubS);
	}

	@Override
	void remove() {
		ontologyManager.removeAxiom(ont, axRsubS);
	}

	@Override
	void testPresent() {
		assertTrue(reasoner.getSuperClasses(A, true).containsEntity(B));
		assertTrue(reasoner.getSubClasses(B, true).containsEntity(A));
	}

	@Override
	void testAbsent() {
		assertFalse(reasoner.getSuperClasses(A, true).containsEntity(B));
		assertFalse(reasoner.getSubClasses(B, true).containsEntity(A));
	}

}
