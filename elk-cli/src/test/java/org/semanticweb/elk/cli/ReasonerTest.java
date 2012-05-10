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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.reasoner.FreshEntitiesException;
import org.semanticweb.elk.reasoner.InconsistentOntologyException;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.taxonomy.ClassNode;
import org.semanticweb.elk.reasoner.taxonomy.ClassTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyClassNode;

//TODO This test won't be necessary as soon as we can specify the expected class taxonomy
//for our main classification tests, see BaseClassificationCorrectnessTest

public class ReasonerTest {

	final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

	@Test
	public void testExistentials() throws InterruptedException,
			ExecutionException, Owl2ParseException, IOException {

		IOReasoner IOReasoner = new IOReasonerFactory().createReasoner();
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

	@Test
	public void testConjunctions() throws InterruptedException,
			ExecutionException, Owl2ParseException, IOException,
			FreshEntitiesException, InconsistentOntologyException {

		final IOReasoner ioReasoner = new IOReasonerFactory().createReasoner();
		ioReasoner.loadOntologyFromString("Prefix( : = <http://example.org/> )"
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

		OntologyIndex index = ioReasoner.getOntologyIndex();

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

		Set<? extends ClassNode> superClassesOfA = ioReasoner.getSuperClasses(
				a, true);

		assertTrue("A contains B",
				superClassesOfA.contains(ioReasoner.getClassNode(b)));
		assertTrue("A contains C",
				superClassesOfA.contains(ioReasoner.getClassNode(c)));
		assertTrue("A contains D",
				superClassesOfA.contains(ioReasoner.getClassNode(d)));
		assertTrue("A contains E",
				superClassesOfA.contains(ioReasoner.getClassNode(e)));
	}

	@Test
	public void testPropertyChains() throws Owl2ParseException, IOException,
			FreshEntitiesException, InconsistentOntologyException {
		IOReasoner ioReasoner = new IOReasonerFactory().createReasoner();
		ioReasoner
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

		ElkClass a = objectFactory.getClass(new ElkFullIri(
				"http://example.org/A"));
		ElkClass x = objectFactory.getClass(new ElkFullIri(
				"http://example.org/X"));

		Set<? extends ClassNode> superClassesOfA = ioReasoner.getSuperClasses(
				a, true);

		assertTrue(
				"A SubClassOf X",
				superClassesOfA.contains(
						ioReasoner.getClassNode(x)));
	}

	@Test
	public void testBottom() throws InterruptedException, ExecutionException,
			Owl2ParseException, IOException {

		IOReasoner IOReasoner = new IOReasonerFactory().createReasoner();
		IOReasoner.loadOntologyFromString(""//
				+ "Prefix( : = <http://example.org/> )"//
				+ "Prefix( owl: = <http://www.w3.org/2002/07/owl#> )"//
				+ "Ontology("//
				+ "SubClassOf(:A ObjectSomeValuesFrom(:R :B))"//
				+ "SubClassOf(:C ObjectSomeValuesFrom(:S :A))"//
				+ "SubClassOf(:B owl:Nothing)"//
				+ ")"//
		);

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

	@Test
	public void testDisjoint() throws InterruptedException, ExecutionException,
			Owl2ParseException, IOException {

		IOReasoner IOReasoner = new IOReasonerFactory().createReasoner();
		IOReasoner.loadOntologyFromString(""//
				+ "Prefix( : = <http://example.org/> )"//
				+ "Prefix( owl: = <http://www.w3.org/2002/07/owl#> )"//
				+ "Ontology("//
				+ "SubClassOf(:A :C)"//
				+ "SubClassOf(:B :C)"//
				+ "DisjointClasses(:A :B :C)"//
				+ ")"//
		);

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
		assertNotSame("C satisfiable", bottom, taxonomy.getNode(c));
	}

	public void testDisjointSelf() throws InterruptedException,
			ExecutionException, Owl2ParseException, IOException {

		IOReasoner IOReasoner = new IOReasonerFactory().createReasoner();
		IOReasoner.loadOntologyFromString(""//
				+ "Prefix( : = <http://example.org/> )"//
				+ "Prefix( owl: = <http://www.w3.org/2002/07/owl#> )"//
				+ "Ontology("//
				+ "DisjointClasses(:A :B :A)"//
				+ "DisjointClasses(:C :C)"//
				+ ")"//
		);

		ElkClass a = objectFactory.getClass(new ElkFullIri(
				"http://example.org/A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(
				"http://example.org/B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(
				"http://example.org/C"));

		ClassTaxonomy taxonomy = IOReasoner.getTaxonomy();
		ClassNode bottom = taxonomy.getNode(PredefinedElkClass.OWL_NOTHING);

		assertSame("A unsatisfiable", bottom, taxonomy.getNode(a));
		assertNotSame("B satisfiable", bottom, taxonomy.getNode(b));
		assertSame("C unsatisfiable", bottom, taxonomy.getNode(c));
	}

	public void testReflexiveRole() throws InterruptedException,
			ExecutionException, Owl2ParseException, IOException,
			FreshEntitiesException, InconsistentOntologyException {

		IOReasoner ioReasoner = new IOReasonerFactory().createReasoner();
		ioReasoner.loadOntologyFromString(""//
				+ "Prefix( : = <http://example.org/> )"//
				+ "Prefix( owl: = <http://www.w3.org/2002/07/owl#> )"//
				+ "Ontology("//
				+ "ReflexiveObjectProperty(:R)"//
				+ "EquivalentClasses(:B ObjectSomeValuesFrom(:R :A))"//
				+ "EquivalentClasses(:D ObjectSomeValuesFrom(:S :C))"//
				+ "SubObjectPropertyOf(ObjectPropertyChain(:R :R) :S)"//
				+ ")"//
		);

		ElkClass a = objectFactory.getClass(new ElkFullIri(
				"http://example.org/A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(
				"http://example.org/B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(
				"http://example.org/C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(
				"http://example.org/D"));
		
		Set<? extends ClassNode> superClassesOfA = ioReasoner.getSuperClasses(
				a, true);
		Set<? extends ClassNode> superClassesOfC = ioReasoner.getSuperClasses(
				c, true);

		assertTrue("SubClassOf(A B)", superClassesOfA.contains(ioReasoner.getClassNode(b)));
		assertTrue("SubClassOf(C D)", superClassesOfC.contains(ioReasoner.getClassNode(d)));
	}

	@Test
	public void testAncestors() throws InterruptedException,
			ExecutionException, Owl2ParseException, IOException,
			FreshEntitiesException, InconsistentOntologyException {

		final IOReasoner ioReasoner = new IOReasonerFactory().createReasoner();
		ioReasoner.loadOntologyFromString("Prefix( : = <http://example.org/> )"
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

		OntologyIndex index = ioReasoner.getOntologyIndex();

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

		ClassNode bNode = ioReasoner.getClassNode(b);
		ClassNode cNode = ioReasoner.getClassNode(c);
		ClassNode dNode = ioReasoner.getClassNode(d);
		
		Set<? extends ClassNode> directSuperClassesOfA = ioReasoner.getSuperClasses(a,true);
		Set<? extends ClassNode> indirectSuperClassesOfA = ioReasoner.getSuperClasses(a,false);

		assertTrue("A direct subclass of B", directSuperClassesOfA
				.contains(bNode));
		assertTrue("A direct subclass of C", directSuperClassesOfA 
				.contains(cNode));
		assertFalse("A not direct subclass of D", directSuperClassesOfA
				.contains(dNode));
		assertTrue("B direct subclass of D", ioReasoner.getSuperClasses(b,true)
				.contains(dNode));
		assertTrue("A indirect subclass of B", indirectSuperClassesOfA
				.contains(bNode));
		assertTrue("A indirect subclass of D", indirectSuperClassesOfA
				.contains(dNode));
		assertEquals("A has exactly two direct super-classes", 2, directSuperClassesOfA.size());
		assertEquals("A has exactly four super-classes: B, C, D and owl:Thing",
				4, indirectSuperClassesOfA.size());
	}

	@Test
	public void testTop() throws InterruptedException, ExecutionException,
			Owl2ParseException, IOException {

		final IOReasoner ioReasoner = new IOReasonerFactory().createReasoner();
		ioReasoner.loadOntologyFromString(""//
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

		ClassTaxonomy taxonomy = ioReasoner.getTaxonomy();
		TaxonomyClassNode botNode = taxonomy.getNode(bot);
		TaxonomyClassNode aNode = taxonomy.getNode(a);
		TaxonomyClassNode bNode = taxonomy.getNode(b);
		TaxonomyClassNode cNode = taxonomy.getNode(c);
		TaxonomyClassNode topNode = taxonomy.getNode(top);

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

	@Test
	public void testInconsistent() throws InterruptedException,
			ExecutionException, Owl2ParseException, IOException {

		IOReasoner IOReasoner = new IOReasonerFactory().createReasoner();
		IOReasoner.loadOntologyFromString(""
				+ "Prefix( : = <http://example.org/> )"
				+ "Prefix( owl: = <http://www.w3.org/2002/07/owl#> )"
				+ "Ontology(" + "EquivalentClasses(:A :C)"
				+ "SubClassOf(owl:Thing ObjectSomeValuesFrom(:R :B))"
				+ "SubClassOf(ObjectSomeValuesFrom(:S :B) :A)"
				+ "SubObjectPropertyOf(:R :S)"
				+ "SubClassOf(:C ObjectSomeValuesFrom(:T :B))"
				+ "ObjectPropertyDomain(:T owl:Nothing)" + ")");

		ClassTaxonomy taxonomy = IOReasoner.getTaxonomy();

		ClassNode thing = taxonomy.getNode(PredefinedElkClass.OWL_THING);
		ClassNode nothing = taxonomy.getNode(PredefinedElkClass.OWL_NOTHING);

		assertEquals(nothing.getMembers(), thing.getMembers());
		assertSame(nothing.getCanonicalMember(), thing.getCanonicalMember());
	}

	class TestElkClass implements ElkClass {
		protected final ElkIri iri;

		// do not allow construction of other instances of this class
		TestElkClass(ElkIri iri) {
			this.iri = iri;
		}

		@Override
		public ElkIri getIri() {
			return iri;
		}

		@Override
		public <O> O accept(ElkClassExpressionVisitor<O> visitor) {
			return visitor.visit(this);
		}

		@Override
		public <O> O accept(ElkObjectVisitor<O> visitor) {
			return visitor.visit(this);
		}

		@Override
		public <O> O accept(ElkEntityVisitor<O> visitor) {
			return visitor.visit(this);
		}
	}

}
