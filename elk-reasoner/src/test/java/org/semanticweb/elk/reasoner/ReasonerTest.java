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
import java.util.concurrent.Future;

import junit.framework.TestCase;

import org.semanticweb.elk.parser.javacc.ParseException;
import org.semanticweb.elk.reasoner.classification.ClassNode;
import org.semanticweb.elk.reasoner.classification.ClassTaxonomy;
import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.ElkObjectProperty;
import org.semanticweb.elk.syntax.FutureElkObjectFactory;
import org.semanticweb.elk.syntax.FutureElkObjectFactoryImpl;

public class ReasonerTest extends TestCase {

	final FutureElkObjectFactory constructor = new FutureElkObjectFactoryImpl();

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

		ElkClass a = constructor.getFutureElkClass(":A").get();
		ElkClass d = constructor.getFutureElkClass(":D").get();
		ElkObjectProperty r = constructor.getFutureElkObjectProperty(":R")
				.get();
		ElkObjectProperty s = constructor.getFutureElkObjectProperty(":S")
				.get();

		reasoner.classify();
		ClassTaxonomy taxonomy = reasoner.getTaxonomy();

		OntologyIndex index = reasoner.ontologyIndex;

		IndexedObjectProperty R = index.getIndexedObjectPropertyExpression(r);
		IndexedObjectProperty S = index.getIndexedObjectPropertyExpression(s);

		assertTrue("R subrole S", R.getToldSuperObjectProperties().contains(S));
		assertTrue("S superrole R", S.getToldSubObjectProperties().contains(R));
		assertTrue("A contains D",
				taxonomy.getNode(a).getParents().contains(taxonomy.getNode(d)));
	}

	public void testConjunctions() throws InterruptedException,
			ExecutionException, ParseException, IOException {

		final Reasoner reasoner = new Reasoner();
		reasoner.loadOntologyFromString("Ontology(" + "SubClassOf(:A :B)"
				+ "SubClassOf(:A :C)" + "SubClassOf(:A :D)"
				+ "SubClassOf(ObjectIntersectionOf(:B :C :D) :E)" + ")");

		Future<ElkClass> a = constructor.getFutureElkClass(":A");
		Future<ElkClass> b = constructor.getFutureElkClass(":B");
		Future<ElkClass> c = constructor.getFutureElkClass(":C");
		Future<ElkClass> d = constructor.getFutureElkClass(":D");
		Future<ElkClass> e = constructor.getFutureElkClass(":E");

		OntologyIndex index = reasoner.ontologyIndex;

		IndexedClassExpression A = index.getIndexedClassExpression(a.get());
		IndexedClassExpression B = index.getIndexedClassExpression(b.get());
		IndexedClassExpression C = index.getIndexedClassExpression(c.get());
		IndexedClassExpression D = index.getIndexedClassExpression(d.get());
		IndexedClassExpression E = index.getIndexedClassExpression(e.get());

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
		ClassNode aNode = taxonomy.getNode(a.get());

		assertTrue("A contains A", aNode.getMembers().contains(a.get()));
		assertTrue("A contains B",
				aNode.getParents().contains(taxonomy.getNode(b.get())));
		assertTrue("A contains C",
				aNode.getParents().contains(taxonomy.getNode(c.get())));
		assertTrue("A contains D",
				aNode.getParents().contains(taxonomy.getNode(d.get())));
		assertTrue("A contains E",
				aNode.getParents().contains(taxonomy.getNode(e.get())));
	}
}
