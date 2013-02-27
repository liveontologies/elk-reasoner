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

import org.junit.Test;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.DirectIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;

public class ConcurrentSaturatorTest extends TestCase {

	final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

	public ConcurrentSaturatorTest(String testName) {
		super(testName);
	}

	@Test
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

		ModifiableOntologyIndex index = new DirectIndex();
		ComputationExecutor executor = new ComputationExecutor(16, "test");

		final ElkAxiomProcessor inserter = index.getAxiomInserter();
		inserter.visit(objectFactory.getEquivalentClassesAxiom(b, c));
		inserter.visit(objectFactory.getSubClassOfAxiom(a,
				objectFactory.getObjectSomeValuesFrom(r, b)));
		inserter.visit(objectFactory.getSubClassOfAxiom(
				objectFactory.getObjectSomeValuesFrom(r, c), d));
		inserter.visit(objectFactory.getSubObjectPropertyOfAxiom(r, s));

		IndexedClassExpression A = index.getIndexed(a);
		IndexedClassExpression D = index.getIndexed(d);
		IndexedPropertyChain R = index.getIndexed(r);

		final TestPropertySaturation propertySaturation = new TestPropertySaturation(
				executor, 16, index);

		final TestClassExpressionSaturation<SaturationJob<IndexedClassExpression>> classExpressionSaturation = new TestClassExpressionSaturation<SaturationJob<IndexedClassExpression>>(
				executor, 16, index);

		propertySaturation.start();
		propertySaturation.submit(R);
		propertySaturation.finish();

		classExpressionSaturation.start();
		classExpressionSaturation
				.submit(new SaturationJob<IndexedClassExpression>(A));
		classExpressionSaturation.finish();

		assertTrue("A contains D", A.getContext().getSubsumers().contains(D));

	}

	@Test
	public void testConjunctions() throws InterruptedException,
			ExecutionException {
		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));

		final ModifiableOntologyIndex index = new DirectIndex();
		ComputationExecutor executor = new ComputationExecutor(16, "test");
		final ElkAxiomProcessor inserter = index.getAxiomInserter();

		inserter.visit(objectFactory.getSubClassOfAxiom(a, b));
		inserter.visit(objectFactory.getSubClassOfAxiom(a, c));
		inserter.visit(objectFactory.getSubClassOfAxiom(
				objectFactory.getObjectIntersectionOf(b, c), d));

		IndexedClassExpression A = index.getIndexed(a);
		IndexedClassExpression B = index.getIndexed(b);
		IndexedClassExpression C = index.getIndexed(c);
		IndexedClassExpression D = index.getIndexed(d);
		IndexedClassExpression I = index.getIndexed(objectFactory
				.getObjectIntersectionOf(b, c));

		// assertTrue("A SubClassOf B",
		// A.getToldSuperClassExpressions().contains(B));
		// assertTrue("A SubClassOf C",
		// A.getToldSuperClassExpressions().contains(C));
		// assertFalse("A SubClassOf D", A.getToldSuperClassExpressions()
		// .contains(D));
		// assertTrue("I SubClassOf D",
		// I.getToldSuperClassExpressions().contains(D));

		final TestClassExpressionSaturation<SaturationJob<IndexedClassExpression>> classExpressionSaturation = new TestClassExpressionSaturation<SaturationJob<IndexedClassExpression>>(
				executor, 16, index);

		classExpressionSaturation.start();
		classExpressionSaturation
				.submit(new SaturationJob<IndexedClassExpression>(A));
		classExpressionSaturation.finish();
		Context context = A.getContext();

		assertTrue("A contains A", context.getSubsumers().contains(A));
		assertTrue("A contains B", context.getSubsumers().contains(B));
		assertTrue("A contains C", context.getSubsumers().contains(C));
		assertTrue("A contains I", context.getSubsumers().contains(I));
		assertTrue("A contains D", context.getSubsumers().contains(D));
	}

}
