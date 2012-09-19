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

import junit.framework.TestCase;

import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.OntologyIndexImpl;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;

public class ConcurrentSaturatorTest extends TestCase {

	final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

	public ConcurrentSaturatorTest(String testName) {
		super(testName);
	}

	public void testExistentials() throws InterruptedException,
			ExecutionException {
		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));
		ElkObjectProperty r = objectFactory.getObjectProperty(new ElkFullIri(
				"R"));
		ElkObjectProperty s = objectFactory.getObjectProperty(new ElkFullIri(
				"S"));

		OntologyIndex ontologyIndex = new OntologyIndexImpl();
		ComputationExecutor executor = new ComputationExecutor(16, "test");

		final ElkAxiomProcessor inserter = ontologyIndex.getAxiomInserter();
		inserter.visit(objectFactory.getEquivalentClassesAxiom(b, c));
		inserter.visit(objectFactory.getSubClassOfAxiom(a,
				objectFactory.getObjectSomeValuesFrom(r, b)));
		inserter.visit(objectFactory.getSubClassOfAxiom(
				objectFactory.getObjectSomeValuesFrom(s, c), d));
		inserter.visit(objectFactory.getSubObjectPropertyOfAxiom(r, s));

		IndexedClassExpression A = ontologyIndex.getIndexed(a);
		IndexedClassExpression D = ontologyIndex.getIndexed(d);

		final TestClassExpressionSaturation<SaturationJob<IndexedClassExpression>> classExpressionSaturation = new TestClassExpressionSaturation<SaturationJob<IndexedClassExpression>>(
				executor, 16, ontologyIndex);

		classExpressionSaturation.start();
		classExpressionSaturation
				.submit(new SaturationJob<IndexedClassExpression>(A));

		classExpressionSaturation.finish();

		assertTrue("A contains D", A.getContext().getSuperClassExpressions()
				.contains(D));

	}

	public void testConjunctions() throws InterruptedException,
			ExecutionException {
		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));

		final OntologyIndex ontologyIndex = new OntologyIndexImpl();
		ComputationExecutor executor = new ComputationExecutor(16, "test");
		final ElkAxiomProcessor inserter = ontologyIndex.getAxiomInserter();

		inserter.visit(objectFactory.getSubClassOfAxiom(a, b));
		inserter.visit(objectFactory.getSubClassOfAxiom(a, c));
		inserter.visit(objectFactory.getSubClassOfAxiom(
				objectFactory.getObjectIntersectionOf(b, c), d));

		IndexedClassExpression A = ontologyIndex.getIndexed(a);
		IndexedClassExpression B = ontologyIndex.getIndexed(b);
		IndexedClassExpression C = ontologyIndex.getIndexed(c);
		IndexedClassExpression D = ontologyIndex.getIndexed(d);
		IndexedClassExpression I = ontologyIndex.getIndexed(objectFactory
				.getObjectIntersectionOf(b, c));

//		assertTrue("A SubClassOf B",
//				A.getToldSuperClassExpressions().contains(B));
//		assertTrue("A SubClassOf C",
//				A.getToldSuperClassExpressions().contains(C));
//		assertFalse("A SubClassOf D", A.getToldSuperClassExpressions()
//				.contains(D));
//		assertTrue("I SubClassOf D",
//				I.getToldSuperClassExpressions().contains(D));

		final TestClassExpressionSaturation<SaturationJob<IndexedClassExpression>> classExpressionSaturation = new TestClassExpressionSaturation<SaturationJob<IndexedClassExpression>>(
				executor, 16, ontologyIndex);

		classExpressionSaturation.start();
		classExpressionSaturation
				.submit(new SaturationJob<IndexedClassExpression>(A));
		classExpressionSaturation.finish();
		Context context = A.getContext();

		assertTrue("A contains A",
				context.getSuperClassExpressions().contains(A));
		assertTrue("A contains B",
				context.getSuperClassExpressions().contains(B));
		assertTrue("A contains C",
				context.getSuperClassExpressions().contains(C));
		assertTrue("A contains I",
				context.getSuperClassExpressions().contains(I));
		assertTrue("A contains D",
				context.getSuperClassExpressions().contains(D));
	}

}
