/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
package org.semanticweb.elk.reasoner;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;

import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.parsing.javacc.ParseException;
import org.semanticweb.elk.reasoner.classification.ClassNode;
import org.semanticweb.elk.reasoner.classification.ClassTaxonomy;
import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;

public class ReasonerTest extends TestCase {

	final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

	public ReasonerTest(String testName) {
		super(testName);
	}

	public void testExistentials() throws InterruptedException,
			ExecutionException, ParseException, IOException {

		Reasoner reasoner = new Reasoner();
		reasoner.loadOntologyFromString(""//
				+ "Ontology("//
				+ "EquivalentClasses(:B :C)"//
				+ "SubClassOf(:A ObjectSomeValuesFrom(:R :B))"//
				+ "SubClassOf(ObjectSomeValuesFrom(:S :C) :D)"//
				+ "SubObjectPropertyOf(:R :S)"//
				+ ")"//
		);

		ElkClass a = objectFactory.getClass(":A");
		ElkClass d = objectFactory.getClass(":D");
		ElkObjectProperty r = objectFactory.getObjectProperty(":R");
		ElkObjectProperty s = objectFactory.getObjectProperty(":S");

		reasoner.classify();
		ClassTaxonomy taxonomy = reasoner.getTaxonomy();

		OntologyIndex index = reasoner.ontologyIndex;

		IndexedObjectProperty R = index.getIndexedObjectProperty(r);
		IndexedObjectProperty S = index.getIndexedObjectProperty(s);

		assertTrue("R subrole S", R.getToldSuperObjectProperties().contains(S));
		assertTrue("S superrole R", S.getToldSubObjectProperties().contains(R));
		assertTrue("A contains D", taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(d)));
	}

	public void testConjunctions() throws InterruptedException,
			ExecutionException, ParseException, IOException {

		final Reasoner reasoner = new Reasoner();
		reasoner.loadOntologyFromString("Ontology(" + "SubClassOf(:A :B)"
				+ "SubClassOf(:A :C)" + "SubClassOf(:A :D)"
				+ "SubClassOf(ObjectIntersectionOf(:B :C :D) :E)" + ")");

		ElkClass a = objectFactory.getClass(":A");
		ElkClass b = objectFactory.getClass(":B");
		ElkClass c = objectFactory.getClass(":C");
		ElkClass d = objectFactory.getClass(":D");
		ElkClass e = objectFactory.getClass(":E");

		OntologyIndex index = reasoner.ontologyIndex;

		IndexedClassExpression A = index.getIndexedClassExpression(a);
		IndexedClassExpression B = index.getIndexedClassExpression(b);
		IndexedClassExpression C = index.getIndexedClassExpression(c);
		IndexedClassExpression D = index.getIndexedClassExpression(d);
		IndexedClassExpression E = index.getIndexedClassExpression(e);

		assertTrue("A SubClassOf B",
				A.getToldSuperClassExpressions().contains(B));
		assertTrue("A SubClassOf C",
				A.getToldSuperClassExpressions().contains(C));
		assertTrue("A SubClassOf D",
				A.getToldSuperClassExpressions().contains(D));
		assertFalse("A SubClassOf E", A.getToldSuperClassExpressions()
				.contains(E));

		reasoner.classify();

		ClassTaxonomy taxonomy = reasoner.getTaxonomy();
		ClassNode aNode = taxonomy.getNode(a);

		assertTrue("A contains A", aNode.getMembers().contains(a));
		assertTrue("A contains B",
				aNode.getDirectSuperNodes().contains(taxonomy.getNode(b)));
		assertTrue("A contains C",
				aNode.getDirectSuperNodes().contains(taxonomy.getNode(c)));
		assertTrue("A contains D",
				aNode.getDirectSuperNodes().contains(taxonomy.getNode(d)));
		assertTrue("A contains E",
				aNode.getDirectSuperNodes().contains(taxonomy.getNode(e)));
	}
	
	public void testPropertyChains() throws ParseException, IOException {
		Reasoner reasoner = new Reasoner();
		reasoner.loadOntologyFromString(""//
				+ "Ontology("//
				+ "SubClassOf(:A ObjectSomeValuesFrom(:R1 :B))"//
				+ "SubClassOf(:B ObjectSomeValuesFrom(:R2 :C))"//
				+ "SubClassOf(:C ObjectSomeValuesFrom(:R3 :D))"//
				+ "SubClassOf(:D ObjectSomeValuesFrom(:R4 :E))"//
				+ "SubClassOf(ObjectIntersectionOf(owl:Thing ObjectSomeValuesFrom(:T owl:Thing)) :X)"//
				+ "SubObjectPropertyOf(ObjectPropertyChain(:R3 :R4) :S)"//
				+ "SubObjectPropertyOf(ObjectPropertyChain(:R1 :R2 :S) :T)"//
				+ ")"//
		);
		
		reasoner.classify();

		ClassTaxonomy taxonomy = reasoner.getTaxonomy();
		ClassNode aNode = taxonomy.getNode(objectFactory.getClass(":A"));
		assertTrue("A SubClassOf X", aNode.getDirectSuperNodes().contains(
				taxonomy.getNode(objectFactory.getClass(":X"))));
	}


	public void testAncestors() throws InterruptedException,
			ExecutionException, ParseException, IOException {

		final Reasoner reasoner = new Reasoner();
		reasoner.loadOntologyFromString("Ontology(" + "SubClassOf(:A :B)"
				+ "SubClassOf(:A :C)" + "SubClassOf(:B :D)"
				+ "SubClassOf(:C :D))");

		ElkClass a = objectFactory.getClass(":A");
		ElkClass b = objectFactory.getClass(":B");
		ElkClass c = objectFactory.getClass(":C");
		ElkClass d = objectFactory.getClass(":D");

		OntologyIndex index = reasoner.ontologyIndex;

		IndexedClassExpression A = index.getIndexedClassExpression(a);
		IndexedClassExpression B = index.getIndexedClassExpression(b);
		IndexedClassExpression C = index.getIndexedClassExpression(c);
		IndexedClassExpression D = index.getIndexedClassExpression(d);

		assertTrue("A SubClassOf B",
				A.getToldSuperClassExpressions().contains(B));
		assertTrue("A SubClassOf C",
				A.getToldSuperClassExpressions().contains(C));
		assertTrue("C SubClassOf D",
				C.getToldSuperClassExpressions().contains(D));
		assertTrue("B SubClassOf D",
				B.getToldSuperClassExpressions().contains(D));

		reasoner.classify();

		ClassTaxonomy taxonomy = reasoner.getTaxonomy();
		ClassNode aNode = taxonomy.getNode(a);
		ClassNode bNode = taxonomy.getNode(b);

		assertTrue("A contains A", aNode.getMembers().contains(a));
		assertTrue("A direct subclass of B", aNode.getDirectSuperNodes()
				.contains(taxonomy.getNode(b)));
		assertTrue("A direct subclass of C", aNode.getDirectSuperNodes()
				.contains(taxonomy.getNode(c)));
		assertFalse("A not direct subclass of D", aNode.getDirectSuperNodes()
				.contains(taxonomy.getNode(d)));
		assertTrue("B direct subclass of D", bNode.getDirectSuperNodes()
				.contains(taxonomy.getNode(d)));
		assertTrue("A indirect subclass of B", aNode.getDirectSuperNodes()
				.contains(taxonomy.getNode(b)));
		assertTrue("A indirect subclass of D", aNode.getAllSuperNodes()
				.contains(taxonomy.getNode(d)));
		assertEquals("A has exactly two direct super-classes", 2, aNode
				.getDirectSuperNodes().size());
		assertEquals("A has exactly four super-classes: B, C, D and owl:Thing",
				4, aNode.getAllSuperNodes().size());
	}
}
