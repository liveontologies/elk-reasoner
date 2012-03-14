/*
 * #%L
 * elk-IOReasoner
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
package org.semanticweb.elk.cli;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;

import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.parsing.javacc.ParseException;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.taxonomy.ClassNode;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomy;

public class ReasonerTest extends TestCase {

	final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

	public ReasonerTest(String testName) {
		super(testName);
	}

	public void testExistentials() throws InterruptedException,
			ExecutionException, ParseException, IOException {

		IOReasoner IOReasoner = new IOReasoner();
		IOReasoner.loadOntologyFromString(""//
				+ "Prefix( : = <http://example.org/> )" + "Ontology("//
				+ "EquivalentClasses(:B :C)"//
				+ "SubClassOf(:A ObjectSomeValuesFrom(:R :B))"//
				+ "SubClassOf(ObjectSomeValuesFrom(:S :C) :D)"//
				+ "SubObjectPropertyOf(:R :S)"//
				+ "ObjectPropertyDomain(:S :E)"//
				+ ")"//
		);

		ElkClass a = objectFactory.getClass(new ElkFullIri(
				"http://example.org/A"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(
				"http://example.org/D"));
		ElkClass e = objectFactory.getClass(new ElkFullIri(
				"http://example.org/E"));
		ElkObjectProperty r = objectFactory.getObjectProperty(new ElkFullIri(
				"http://example.org/R"));
		ElkObjectProperty s = objectFactory.getObjectProperty(new ElkFullIri(
				"http://example.org/S"));

		IOReasoner.classify();
		ClassTaxonomy taxonomy = IOReasoner.getTaxonomy();

		OntologyIndex index = IOReasoner.getOntologyIndex();

		IndexedPropertyChain R = index.getIndexed(r);
		IndexedPropertyChain S = index.getIndexed(s);

		assertTrue("R subrole S", R.getToldSuperProperties().contains(S));
		assertTrue("S superrole R", S.getToldSubProperties().contains(R));
		assertTrue("A contains D", taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(d)));
		assertTrue("A contains E", taxonomy.getNode(a).getDirectSuperNodes()
				.contains(taxonomy.getNode(e)));
	}

	public void testConjunctions() throws InterruptedException,
			ExecutionException, ParseException, IOException {

		final IOReasoner IOReasoner = new IOReasoner();
		IOReasoner.loadOntologyFromString("Prefix( : = <http://example.org/> )"
				+ "Ontology(" + "SubClassOf(:A :B)" + "SubClassOf(:A :C)"
				+ "SubClassOf(:A :D)"
				+ "SubClassOf(ObjectIntersectionOf(:B :C :D) :E)" + ")");

		ElkClass a = objectFactory.getClass(new ElkFullIri(
				"http://example.org/A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(
				"http://example.org/B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(
				"http://example.org/C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(
				"http://example.org/D"));
		ElkClass e = objectFactory.getClass(new ElkFullIri(
				"http://example.org/E"));

		OntologyIndex index = IOReasoner.getOntologyIndex();

		IndexedClassExpression A = index.getIndexed(a);
		IndexedClassExpression B = index.getIndexed(b);
		IndexedClassExpression C = index.getIndexed(c);
		IndexedClassExpression D = index.getIndexed(d);
		IndexedClassExpression E = index.getIndexed(e);

		assertTrue("A SubClassOf B",
				A.getToldSuperClassExpressions().contains(B));
		assertTrue("A SubClassOf C",
				A.getToldSuperClassExpressions().contains(C));
		assertTrue("A SubClassOf D",
				A.getToldSuperClassExpressions().contains(D));
		assertFalse("A SubClassOf E", A.getToldSuperClassExpressions()
				.contains(E));

		IOReasoner.classify();

		ClassTaxonomy taxonomy = IOReasoner.getTaxonomy();
		ClassNode aNode = taxonomy.getNode(a);

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
		IOReasoner IOReasoner = new IOReasoner();
		IOReasoner
				.loadOntologyFromString(""//
						+ "Prefix( : = <http://example.org/> )"//
						+ "Prefix( owl: = <http://www.w3.org/2002/07/owl#> )"//
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

		IOReasoner.classify();

		ClassTaxonomy taxonomy = IOReasoner.getTaxonomy();
		ClassNode aNode = taxonomy.getNode(objectFactory
				.getClass(new ElkFullIri("http://example.org/A")));
		assertTrue(
				"A SubClassOf X",
				aNode.getDirectSuperNodes().contains(
						taxonomy.getNode(objectFactory.getClass(new ElkFullIri(
								"http://example.org/X")))));
	}
	
	public void testBottom() throws InterruptedException,
			ExecutionException, ParseException, IOException {
		
		IOReasoner IOReasoner = new IOReasoner();
		IOReasoner
				.loadOntologyFromString(""//
						+ "Prefix( : = <http://example.org/> )"//
						+ "Prefix( owl: = <http://www.w3.org/2002/07/owl#> )"//
						+ "Ontology("//
						+ "SubClassOf(:A ObjectSomeValuesFrom(:R :B))"//
						+ "SubClassOf(:C ObjectSomeValuesFrom(:S :A))"//
						+ "SubClassOf(:B owl:Nothing)"//
						+ ")"//
				);

		IOReasoner.classify();
		
		ElkClass a = objectFactory.getClass(new ElkFullIri(
		"http://example.org/A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(
		"http://example.org/B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(
		"http://example.org/C"));
		
		ClassTaxonomy taxonomy = IOReasoner.getTaxonomy();
		ClassNode bottom = taxonomy.getNode(PredefinedElkClass.OWL_NOTHING);

		assertSame("A unsatisfiable", bottom, taxonomy.getNode(a));
		assertSame("B unsatisfiable", bottom, taxonomy.getNode(b));
		assertSame("C unsatisfiable", bottom, taxonomy.getNode(c));
	}

	public void testAncestors() throws InterruptedException,
			ExecutionException, ParseException, IOException {

		final IOReasoner IOReasoner = new IOReasoner();
		IOReasoner.loadOntologyFromString("Prefix( : = <http://example.org/> )"
				+ "Ontology(" + "SubClassOf(:A :B)" + "SubClassOf(:A :C)"
				+ "SubClassOf(:B :D)" + "SubClassOf(:C :D))");

		ElkClass a = objectFactory.getClass(new ElkFullIri(
				"http://example.org/A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(
				"http://example.org/B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(
				"http://example.org/C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(
				"http://example.org/D"));

		OntologyIndex index = IOReasoner.getOntologyIndex();

		IndexedClassExpression A = index.getIndexed(a);
		IndexedClassExpression B = index.getIndexed(b);
		IndexedClassExpression C = index.getIndexed(c);
		IndexedClassExpression D = index.getIndexed(d);

		assertTrue("A SubClassOf B",
				A.getToldSuperClassExpressions().contains(B));
		assertTrue("A SubClassOf C",
				A.getToldSuperClassExpressions().contains(C));
		assertTrue("C SubClassOf D",
				C.getToldSuperClassExpressions().contains(D));
		assertTrue("B SubClassOf D",
				B.getToldSuperClassExpressions().contains(D));

		IOReasoner.classify();

		ClassTaxonomy taxonomy = IOReasoner.getTaxonomy();
		ClassNode aNode = taxonomy.getNode(a);
		ClassNode bNode = taxonomy.getNode(b);

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

	public void testTop() throws InterruptedException, ExecutionException,
			ParseException, IOException {

		final IOReasoner IOReasoner = new IOReasoner();
		IOReasoner.loadOntologyFromString(""//
				+ "Prefix( owl:= <http://www.w3.org/2002/07/owl#> )"//
				+ "Prefix( : = <http://example.org/> )"//
				+ "Ontology("//
				+ "SubClassOf(:A :B)"//
				+ "SubClassOf(owl:Thing :C)"//
				+ ")");

		ElkClass top = new TestElkClass(new ElkFullIri(
				"http://www.w3.org/2002/07/owl#Thing"));
		ElkClass bot = new TestElkClass(new ElkFullIri(
				"http://www.w3.org/2002/07/owl#Nothing"));
		ElkClass a = new TestElkClass(new ElkFullIri("http://example.org/A"));
		ElkClass b = new TestElkClass(new ElkFullIri("http://example.org/B"));
		ElkClass c = new TestElkClass(new ElkFullIri("http://example.org/C"));

		IOReasoner.classify();

		ClassTaxonomy taxonomy = IOReasoner.getTaxonomy();
		ClassNode botNode = taxonomy.getNode(bot);
		ClassNode aNode = taxonomy.getNode(a);
		ClassNode bNode = taxonomy.getNode(b);
		ClassNode cNode = taxonomy.getNode(c);
		ClassNode topNode = taxonomy.getNode(top);

		assertEquals("C and owl:Ting belong to the same node", cNode, topNode);

		assertEquals("Nodes: [bot], [A], [B], [top,C]", taxonomy.getNodes()
				.size(), 4);

		assertTrue("[owl:Nothing] is a node in the taxonomy", taxonomy
				.getNodes().contains(botNode));
		assertEquals("[owl:Nothing] has 1 elemenent", botNode.getMembers()
				.size(), 1);
		assertTrue("[owl:Nothing] node contains owl:Nothing", botNode
				.getMembers().contains(bot));

		assertTrue("[A] is a node in the taxonomy", taxonomy.getNodes()
				.contains(aNode));
		assertEquals("[A] has 1 elemenent", aNode.getMembers().size(), 1);
		assertTrue("[A] contains A", aNode.getMembers().contains(a));

		assertTrue("[B] is a node in the taxonomy", taxonomy.getNodes()
				.contains(bNode));
		assertEquals("[B] has 1 elemenent", bNode.getMembers().size(), 1);
		assertTrue("[B] contains B", bNode.getMembers().contains(b));

		assertTrue("[C] is a node in the taxonomy", taxonomy.getNodes()
				.contains(cNode));
		assertEquals("[C] has 2 elemenent", cNode.getMembers().size(), 2);
		assertTrue("[C] contains C", cNode.getMembers().contains(c));
		assertTrue("[C] contains owl:Thing", cNode.getMembers().contains(top));

		assertTrue("[owl:Thing] is a node in the taxonomy", taxonomy.getNodes()
				.contains(topNode));
		assertEquals("[owl:Thing] has 2 elemenent",
				topNode.getMembers().size(), 2);
		assertTrue("[owl:Thing] contains C", topNode.getMembers().contains(c));
		assertTrue("[owl:Thing] contains owl:Thing", topNode.getMembers()
				.contains(top));

		assertEquals("[owl:Nothing] -> [A]", botNode.getDirectSuperNodes()
				.size(), 1);
		assertTrue("[owl:Nothing] -> [A]", botNode.getDirectSuperNodes()
				.contains(aNode));
		assertEquals("[owl:Nothing] -> [A]", aNode.getDirectSubNodes().size(),
				1);
		assertTrue("[owl:Nothing] -> [A]",
				aNode.getDirectSubNodes().contains(botNode));

		assertEquals("[A] -> [B]", aNode.getDirectSuperNodes().size(), 1);
		assertTrue("[A] -> [B]", aNode.getDirectSuperNodes().contains(bNode));
		assertEquals("[A] -> [B]", bNode.getDirectSubNodes().size(), 1);
		assertTrue("[A] -> [B]", bNode.getDirectSubNodes().contains(aNode));

		assertEquals("[B] -> [owl:Thing, C]", bNode.getDirectSuperNodes()
				.size(), 1);
		assertTrue("[B] -> [owl:Thing, C]", bNode.getDirectSuperNodes()
				.contains(cNode));
		assertEquals("[B] -> [owl:Thing, C]", cNode.getDirectSubNodes().size(),
				1);
		assertTrue("[B] -> [owl:Thing, C]",
				cNode.getDirectSubNodes().contains(bNode));

	}

	class TestElkClass implements ElkClass {
		protected final ElkIri iri;

		// do not allow construction of other instances of this class
		TestElkClass(ElkIri iri) {
			this.iri = iri;
		}

		public ElkIri getIri() {
			return iri;
		}

		public <O> O accept(ElkClassExpressionVisitor<O> visitor) {
			return visitor.visit(this);
		}

		public <O> O accept(ElkObjectVisitor<O> visitor) {
			return visitor.visit(this);
		}

		public <O> O accept(ElkEntityVisitor<O> visitor) {
			return visitor.visit(this);
		}
	}

}
