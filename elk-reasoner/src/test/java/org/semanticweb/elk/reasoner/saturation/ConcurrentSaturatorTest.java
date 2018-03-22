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
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.indexing.classes.ChangeIndexingProcessor;
import org.semanticweb.elk.reasoner.indexing.classes.DirectIndex;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkAxiomConverterImpl;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverter;
import org.semanticweb.elk.reasoner.indexing.conversion.ElkPolarityExpressionConverterImpl;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentExecutor;
import org.semanticweb.elk.util.concurrent.computation.ConcurrentExecutors;
import org.semanticweb.elk.util.concurrent.computation.DummyInterruptMonitor;

import junit.framework.TestCase;

/**
 * Low-level saturation tests using a high number of workers.
 * 
 * @author Yevgeny Kazakov
 * 
 */
public class ConcurrentSaturatorTest extends TestCase {

	final ElkObject.Factory objectFactory = new ElkObjectEntityRecyclingFactory();

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

		ModifiableOntologyIndex index = new DirectIndex(objectFactory);
		ConcurrentExecutor executor = ConcurrentExecutors.create("test", 1,
				TimeUnit.NANOSECONDS);

		final ElkAxiomProcessor inserter = new ChangeIndexingProcessor(
				new ElkAxiomConverterImpl(objectFactory, index, 1), 1, index);
		inserter.visit(objectFactory.getEquivalentClassesAxiom(b, c));
		inserter.visit(objectFactory.getSubClassOfAxiom(a,
				objectFactory.getObjectSomeValuesFrom(r, b)));
		inserter.visit(objectFactory.getSubClassOfAxiom(
				objectFactory.getObjectSomeValuesFrom(r, c), d));
		inserter.visit(objectFactory.getSubObjectPropertyOfAxiom(r, s));

		ElkPolarityExpressionConverter converter = new ElkPolarityExpressionConverterImpl(
				objectFactory, index);

		IndexedClassExpression A = a.accept(converter);
		IndexedClassExpression D = d.accept(converter);
		IndexedPropertyChain R = r.accept(converter);

		final TestPropertySaturation propertySaturation = new TestPropertySaturation(
				executor, 16);

		SaturationState<?> saturationState = SaturationStateFactory
				.createSaturationState(index);
		final TestClassExpressionSaturation<SaturationJob<IndexedClassExpression>> classExpressionSaturation = new TestClassExpressionSaturation<SaturationJob<IndexedClassExpression>>(
				DummyInterruptMonitor.INSTANCE, executor, 16, saturationState);

		propertySaturation.start();
		propertySaturation.submit(R);
		propertySaturation.finish();

		classExpressionSaturation.start();
		classExpressionSaturation
				.submit(new SaturationJob<IndexedClassExpression>(A));
		classExpressionSaturation.finish();

		assertTrue("A contains D", saturationState.getContext(A).getComposedSubsumers()
				.contains(D));

	}

	@Test
	public void testConjunctions() throws InterruptedException,
			ExecutionException {
		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));

		final ModifiableOntologyIndex index = new DirectIndex(objectFactory);
		ConcurrentExecutor executor = ConcurrentExecutors.create("test", 1,
				TimeUnit.NANOSECONDS);
		final ElkAxiomProcessor inserter = new ChangeIndexingProcessor(
				new ElkAxiomConverterImpl(objectFactory, index, 1), 1, index);

		inserter.visit(objectFactory.getSubClassOfAxiom(a, b));
		inserter.visit(objectFactory.getSubClassOfAxiom(a, c));
		inserter.visit(objectFactory.getSubClassOfAxiom(
				objectFactory.getObjectIntersectionOf(b, c), d));

		ElkPolarityExpressionConverter converter = new ElkPolarityExpressionConverterImpl(
				objectFactory, index);

		IndexedClassExpression A = a.accept(converter);
		IndexedClassExpression B = b.accept(converter);
		IndexedClassExpression C = c.accept(converter);
		IndexedClassExpression D = d.accept(converter);
		IndexedClassExpression I = objectFactory.getObjectIntersectionOf(b, c)
				.accept(converter);

		SaturationState<?> saturationState = SaturationStateFactory
				.createSaturationState(index);
		final TestClassExpressionSaturation<SaturationJob<IndexedClassExpression>> classExpressionSaturation = new TestClassExpressionSaturation<SaturationJob<IndexedClassExpression>>(
				DummyInterruptMonitor.INSTANCE, executor, 16, saturationState);

		classExpressionSaturation.start();
		classExpressionSaturation
				.submit(new SaturationJob<IndexedClassExpression>(A));
		classExpressionSaturation.finish();
		Context context = saturationState.getContext(A);

		assertTrue("A contains A", context.getComposedSubsumers().contains(A));
		assertTrue("A contains B", context.getComposedSubsumers().contains(B));
		assertTrue("A contains C", context.getComposedSubsumers().contains(C));
		assertTrue("A contains I", context.getComposedSubsumers().contains(I));
		assertTrue("A contains D", context.getComposedSubsumers().contains(D));
	}

}
