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

import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.Context;
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

		IndexedClassExpression A = reasoner.index.getIndexed(a.get());
		IndexedClassExpression D = reasoner.index.getIndexed(d.get());
		reasoner.index.computeRoleHierarchy();
		reasoner.saturationManager.submit(A);
		reasoner.saturationManager.waitCompletion();
		
		IndexedObjectProperty R = reasoner.index.getIndexed(r.get());
		IndexedObjectProperty S = reasoner.index.getIndexed(r.get());
		assertTrue("R subrole S", R.getSuperObjectProperties().contains(S));
		assertTrue("S superrole R", S.getSubObjectProperties().contains(R));
		assertTrue("A contains D", reasoner.saturation.getContext(A)
				.getDerived().contains(D));
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

		IndexedClassExpression A = reasoner.index.getIndexed(a.get());
		IndexedClassExpression B = reasoner.index.getIndexed(b.get());
		IndexedClassExpression C = reasoner.index.getIndexed(c.get());
		IndexedClassExpression D = reasoner.index.getIndexed(d.get());
		IndexedClassExpression I = reasoner.index.getIndexed(constructor
				.getFutureElkObjectIntersectionOf(b, c).get());

		assertTrue("A SubClassOf B", A.superClassExpressions.contains(B));
		assertTrue("A SubClassOf C", A.superClassExpressions.contains(C));
		assertFalse("A SubClassOf D", A.superClassExpressions.contains(D));
		assertTrue("I SubClassOf D", I.superClassExpressions.contains(D));

		Context context = reasoner.saturation.getContext(A);
		reasoner.saturationManager.submit(A);
		reasoner.saturationManager.waitCompletion();
		assertTrue("A contains A", context.getDerived().contains(A));
		assertTrue("A contains B", context.getDerived().contains(B));
		assertTrue("A contains C", context.getDerived().contains(C));
		assertTrue("A contains I", context.getDerived().contains(I));
		assertTrue("A contains D", context.getDerived().contains(D));
	}
}