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

import org.semanticweb.elk.reasoner.indexing.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.SerialOntologyIndex;
import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.ElkObjectProperty;
import org.semanticweb.elk.syntax.FutureElkObjectFactory;
import org.semanticweb.elk.syntax.FutureElkObjectFactoryImpl;
import org.semanticweb.elk.syntax.parsing.ConcurrentFutureElkAxiomLoader;

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

		OntologyIndex ontologyIndex = new SerialOntologyIndex();

		final ExecutorService executor = Executors.newCachedThreadPool();
		final ConcurrentFutureElkAxiomLoader indexComputation = new ConcurrentFutureElkAxiomLoader(
				executor, 8, ontologyIndex.getAxiomInserter());
		indexComputation.submit(constructor.getFutureElkEquivalentClassesAxiom(
				b, c));
		indexComputation.submit(constructor.getFutureElkSubClassOfAxiom(a,
				constructor.getFutureElkObjectSomeValuesFrom(r, b)));
		indexComputation.submit(constructor.getFutureElkSubClassOfAxiom(
				constructor.getFutureElkObjectSomeValuesFrom(s, c), d));
		indexComputation.submit(constructor
				.getFutureElkSubObjectPropertyOfAxiom(r, s));
		indexComputation.waitCompletion();

		IndexedClassExpression A = ontologyIndex.getIndexed(a
				.get());
		IndexedClassExpression D = ontologyIndex.getIndexed(d
				.get());

		final ObjectPropertySaturation objectPropertySaturation = new ObjectPropertySaturation(
				executor, 16, ontologyIndex);

		for (IndexedObjectProperty iop : ontologyIndex
				.getIndexedObjectProperties())
			objectPropertySaturation.submit(iop);
		objectPropertySaturation.waitCompletion();

		final ClassExpressionSaturation classExpressionSaturation = new ClassExpressionSaturation(
				executor, 16, ontologyIndex);

		classExpressionSaturation.submit(A);
		classExpressionSaturation.waitCompletion();

		assertTrue("A contains D", A.getSaturated().getSuperClassExpressions()
				.contains(D));

		executor.shutdown();
	}

	public void testConjunctions() throws InterruptedException,
			ExecutionException {
		Future<ElkClass> a = constructor.getFutureElkClass("A");
		Future<ElkClass> b = constructor.getFutureElkClass("B");
		Future<ElkClass> c = constructor.getFutureElkClass("C");
		Future<ElkClass> d = constructor.getFutureElkClass("D");

		final OntologyIndex ontologyIndex = new SerialOntologyIndex();
		final ExecutorService executor = Executors.newCachedThreadPool();
		final ConcurrentFutureElkAxiomLoader indexingManager = new ConcurrentFutureElkAxiomLoader(
				executor, 8, ontologyIndex.getAxiomInserter());

		indexingManager.submit(constructor.getFutureElkSubClassOfAxiom(a, b));
		indexingManager.submit(constructor.getFutureElkSubClassOfAxiom(a, c));
		indexingManager.submit(constructor.getFutureElkSubClassOfAxiom(
				constructor.getFutureElkObjectIntersectionOf(b, c), d));
		indexingManager.waitCompletion();

		IndexedClassExpression A = ontologyIndex.getIndexed(a
				.get());
		IndexedClassExpression B = ontologyIndex.getIndexed(b
				.get());
		IndexedClassExpression C = ontologyIndex.getIndexed(c
				.get());
		IndexedClassExpression D = ontologyIndex.getIndexed(d
				.get());
		IndexedClassExpression I = ontologyIndex
				.getIndexed(constructor
						.getFutureElkObjectIntersectionOf(b, c).get());

		assertTrue("A SubClassOf B", A.getToldSuperClassExpressions().contains(
				B));
		assertTrue("A SubClassOf C", A.getToldSuperClassExpressions().contains(
				C));
		assertFalse("A SubClassOf D", A.getToldSuperClassExpressions()
				.contains(D));
		assertTrue("I SubClassOf D", I.getToldSuperClassExpressions().contains(
				D));

		final ClassExpressionSaturation classExpressionSaturation = new ClassExpressionSaturation(
				executor, 16, ontologyIndex);

		classExpressionSaturation.submit(A);
		classExpressionSaturation.waitCompletion();
		SaturatedClassExpression context = A.getSaturated();

		assertTrue("A contains A", context.getSuperClassExpressions().contains(
				A));
		assertTrue("A contains B", context.getSuperClassExpressions().contains(
				B));
		assertTrue("A contains C", context.getSuperClassExpressions().contains(
				C));
		assertTrue("A contains I", context.getSuperClassExpressions().contains(
				I));
		assertTrue("A contains D", context.getSuperClassExpressions().contains(
				D));
	}

}
