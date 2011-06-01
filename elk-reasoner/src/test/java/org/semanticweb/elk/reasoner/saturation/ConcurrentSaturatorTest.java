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
package org.semanticweb.elk.reasoner.saturation;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import junit.framework.TestCase;

import org.semanticweb.elk.reasoner.indexing.ConcurrentIndex;
import org.semanticweb.elk.reasoner.indexing.Index;
import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.IndexingManager;
import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.ElkObjectProperty;
import org.semanticweb.elk.syntax.FutureElkObjectFactory;
import org.semanticweb.elk.syntax.FutureElkObjectFactoryImpl;

public class ConcurrentSaturatorTest extends TestCase {

	final FutureElkObjectFactory constructor = new FutureElkObjectFactoryImpl();

	public ConcurrentSaturatorTest(String testName) {
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

		final ExecutorService executor = Executors.newCachedThreadPool();
		final Index index = new ConcurrentIndex();
		final IndexingManager indexingManager = new IndexingManager(index,
				executor, 8);
		indexingManager.submit(constructor.getFutureElkEquivalentClassesAxiom(
				b, c));
		indexingManager.submit(constructor.getFutureElkSubClassOfAxiom(a,
				constructor.getFutureElkObjectSomeValuesFrom(r, b)));
		indexingManager.submit(constructor.getFutureElkSubClassOfAxiom(
				constructor.getFutureElkObjectSomeValuesFrom(s, c), d));
		indexingManager.submit(constructor
				.getFutureElkSubObjectPropertyOfAxiom(r, s));
		indexingManager.waitCompletion();
		index.computeRoleHierarchy();

		IndexedClassExpression A = index.getIndexed(a.get());
		IndexedClassExpression D = index.getIndexed(d.get());

		final Saturation saturation = new ConcurrentSaturation(128);
		final SaturationManager saturationManager = new SaturationManager(
				saturation, executor, 16);

		saturationManager.submit(A);
		saturationManager.waitCompletion();
		Context context = saturation.getContext(A);

		assertTrue("A contains D", context.getDerived().contains(D));

		executor.shutdown();
	}

	public void testConjunctions() throws InterruptedException,
			ExecutionException {
		Future<ElkClass> a = constructor.getFutureElkClass("A");
		Future<ElkClass> b = constructor.getFutureElkClass("B");
		Future<ElkClass> c = constructor.getFutureElkClass("C");
		Future<ElkClass> d = constructor.getFutureElkClass("D");

		final ExecutorService executor = Executors.newCachedThreadPool();
		final Index index = new ConcurrentIndex();
		final IndexingManager indexingManager = new IndexingManager(index,
				executor, 8);

		indexingManager.submit(constructor.getFutureElkSubClassOfAxiom(a, b));
		indexingManager.submit(constructor.getFutureElkSubClassOfAxiom(a, c));
		indexingManager.submit(constructor.getFutureElkSubClassOfAxiom(
				constructor.getFutureElkObjectIntersectionOf(b, c), d));
		indexingManager.waitCompletion();
		index.computeRoleHierarchy();

		IndexedClassExpression A = index.getIndexed(a.get());
		IndexedClassExpression B = index.getIndexed(b.get());
		IndexedClassExpression C = index.getIndexed(c.get());
		IndexedClassExpression D = index.getIndexed(d.get());
		IndexedClassExpression I = index.getIndexed(constructor
				.getFutureElkObjectIntersectionOf(b, c).get());

		assertTrue("A SubClassOf B", A.superClassExpressions.contains(B));
		assertTrue("A SubClassOf C", A.superClassExpressions.contains(C));
		assertFalse("A SubClassOf D", A.superClassExpressions.contains(D));
		assertTrue("I SubClassOf D", I.superClassExpressions.contains(D));

		final Saturation saturation = new ConcurrentSaturation(128);
		final SaturationManager saturationManager = new SaturationManager(
				saturation, executor, 16);

		saturationManager.submit(A);
		saturationManager.waitCompletion();
		Context context = saturation.getContext(A);

		assertTrue("A contains A", context.getDerived().contains(A));
		assertTrue("A contains B", context.getDerived().contains(B));
		assertTrue("A contains C", context.getDerived().contains(C));
		assertTrue("A contains I", context.getDerived().contains(I));
		assertTrue("A contains D", context.getDerived().contains(D));
	}

}