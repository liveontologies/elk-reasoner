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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import junit.framework.TestCase;

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
			ExecutionException {
		Future<ElkClass> a = constructor.getFutureElkClass("A");
		Future<ElkClass> b = constructor.getFutureElkClass("B");
		Future<ElkClass> c = constructor.getFutureElkClass("C");
		Future<ElkClass> d = constructor.getFutureElkClass("D");
		Future<ElkObjectProperty> r = constructor
				.getFutureElkObjectProperty("R");
		Future<ElkObjectProperty> s = constructor
				.getFutureElkObjectProperty("S");

		Reasoner reasoner = new Reasoner();
		reasoner.load(constructor.getFutureElkEquivalentClassesAxiom(b, c));
		reasoner.load(constructor.getFutureElkSubClassOfAxiom(a,
				constructor.getFutureElkObjectSomeValuesFrom(r, b)));
		reasoner.load(constructor.getFutureElkSubClassOfAxiom(
				constructor.getFutureElkObjectSomeValuesFrom(s, c), d));
		reasoner.load(constructor.getFutureElkSubObjectPropertyOfAxiom(r, s));
		reasoner.finishLoading();

		OntologyIndex index = reasoner.indexingManager.getOntologyIndex();
		reasoner.classify();

		ClassTaxonomy taxonomy = reasoner.getTaxonomy();

		IndexedObjectProperty R = index.getIndexedObjectProperty(r.get());
		IndexedObjectProperty S = index.getIndexedObjectProperty(r.get());
		assertTrue("R subrole S", R.inferredSuperObjectProperties.contains(S));
		assertTrue("S superrole R", S.inferredSubObjectProperties.contains(R));
		assertTrue("A contains D", taxonomy.getNode(a.get()).getParents()
				.contains(taxonomy.getNode(d.get())));
	}

	public void testConjunctions() throws InterruptedException,
			ExecutionException {
		Future<ElkClass> a = constructor.getFutureElkClass("A");
		Future<ElkClass> b = constructor.getFutureElkClass("B");
		Future<ElkClass> c = constructor.getFutureElkClass("C");
		Future<ElkClass> d = constructor.getFutureElkClass("D");

		final Reasoner reasoner = new Reasoner();
		reasoner.load(constructor.getFutureElkSubClassOfAxiom(a, b));
		reasoner.load(constructor.getFutureElkSubClassOfAxiom(a, c));
		reasoner.load(constructor.getFutureElkSubClassOfAxiom(
				constructor.getFutureElkObjectIntersectionOf(b, c), d));
		reasoner.finishLoading();
		OntologyIndex index = reasoner.indexingManager.getOntologyIndex();
		
		IndexedClassExpression A = index.getIndexedClassExpression(a.get());
		IndexedClassExpression B = index.getIndexedClassExpression(b.get());
		IndexedClassExpression C = index.getIndexedClassExpression(c.get());
		IndexedClassExpression D = index.getIndexedClassExpression(d.get());
		IndexedClassExpression I = index.getIndexedClassExpression(constructor
				.getFutureElkObjectIntersectionOf(b, c).get());

		assertTrue("A SubClassOf B", A.superClassExpressions.contains(B));
		assertTrue("A SubClassOf C", A.superClassExpressions.contains(C));
		assertFalse("A SubClassOf D", A.superClassExpressions.contains(D));
		assertTrue("I SubClassOf D", I.superClassExpressions.contains(D));

		reasoner.classify();

		ClassTaxonomy taxonomy = reasoner.getTaxonomy();
		ClassNode aNode = taxonomy.getNode(a.get());
		
		assertTrue("A contains A", aNode.getMembers().contains(a.get()));
		assertTrue("A contains B", aNode.getParents().contains(taxonomy.getNode(b.get())));
		assertTrue("A contains C", aNode.getParents().contains(taxonomy.getNode(c.get())));		
		assertTrue("A contains D", aNode.getParents().contains(taxonomy.getNode(d.get())));
	}
}