/*
 * #%L
 * ELK Command Line Interface
 * 
 * $Id$
 * $HeadURL$
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
package org.semanticweb.elk.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.FreshEntitiesException;
import org.semanticweb.elk.reasoner.InconsistentOntologyException;
import org.semanticweb.elk.reasoner.stages.TestStageExecutor;

//TODO This test won't be necessary as soon as we can compute hash code of InstanceTaxonomies

public class ABoxTest {
	final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

	@Test
	public void testInconsistent() throws Owl2ParseException, IOException {

		IOReasoner reasoner = new IOReasonerFactory()
				.createReasoner(new TestStageExecutor());
		reasoner.loadOntologyFromString("Prefix( : = <http://example.org/> )"
				+ "Prefix( owl: = <http://www.w3.org/2002/07/owl#> )"
				+ "Ontology(" + "SubClassOf(owl:Thing :B)"
				+ "ClassAssertion(:A :ind)" + "DisjointClasses(:A :B)" + ")");

		boolean isConsistent = reasoner.isConsistent();
		assertFalse("inconsisten", isConsistent);
	}

	@Test
	public void testInstances() throws Owl2ParseException, IOException,
			FreshEntitiesException, InconsistentOntologyException {

		IOReasoner reasoner = new IOReasonerFactory()
				.createReasoner(new TestStageExecutor());
		reasoner.loadOntologyFromString("Prefix( : = <http://example.org/> )"
				+ "Ontology("
				+ "ObjectPropertyAssertion(:R :a :b)"
				+ "ObjectPropertyAssertion(:R :b :c)"
				+ "TransitiveObjectProperty(:R)"
				+ "ClassAssertion(:A :a)"
				+ "ClassAssertion(:C :c)"
				+ "SubClassOf(:A :B)"
				+ "SubClassOf(ObjectIntersectionOf(:A ObjectSomeValuesFrom(:R :C)) :X)"
				+ ")");

		boolean isConsistent = reasoner.isConsistent();
		assertTrue("consistent", isConsistent);

		ElkNamedIndividual a = objectFactory.getNamedIndividual(new ElkFullIri(
				"http://example.org/a"));
		ElkNamedIndividual b = objectFactory.getNamedIndividual(new ElkFullIri(
				"http://example.org/b"));
		ElkNamedIndividual c = objectFactory.getNamedIndividual(new ElkFullIri(
				"http://example.org/c"));

		ElkClass A = objectFactory.getClass(new ElkFullIri(
				"http://example.org/A"));
		ElkClass B = objectFactory.getClass(new ElkFullIri(
				"http://example.org/B"));
		ElkClass C = objectFactory.getClass(new ElkFullIri(
				"http://example.org/C"));
		ElkClass X = objectFactory.getClass(new ElkFullIri(
				"http://example.org/X"));

		ElkClass thing = PredefinedElkClass.OWL_THING;

		reasoner.getTaxonomy();
		assertEquals(3, reasoner.getInstances(thing, false).size());
		assertEquals(1, reasoner.getInstances(thing, true).size());
		assertEquals(1, reasoner.getInstances(B, false).size());
		assertEquals(0, reasoner.getInstances(B, true).size());
		assertEquals(1, reasoner.getInstances(A, true).size());
		assertEquals(1, reasoner.getInstances(C, true).size());
		assertEquals(1, reasoner.getInstances(X, true).size());

		assertEquals(2, reasoner.getTypes(a, true).size());
		assertEquals(4, reasoner.getTypes(a, false).size());
		assertEquals(1, reasoner.getTypes(c, true).size());
		assertEquals(2, reasoner.getTypes(c, false).size());
		assertEquals(1, reasoner.getTypes(b, false).size());
	}

}
