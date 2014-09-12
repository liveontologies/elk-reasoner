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
import org.semanticweb.elk.reasoner.indexing.hierarchy.ChangeIndexingProcessor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.DirectIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexObjectConverter;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectCache;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectFactory;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.MainAxiomIndexerVisitor;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ModifiableOntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.PlainIndexedAxiomFactory;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;

/**
 * Low-level saturation tests using a high number of workers.
 * 
 * @author Yevgeny Kazakov
 * 
 */
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

		IndexedObjectFactory factory = new PlainIndexedAxiomFactory();
		ModifiableOntologyIndex index = new DirectIndex(new IndexedObjectCache(factory));
		ComputationExecutor executor = new ComputationExecutor(16, "test");

		final ElkAxiomProcessor inserter = new ChangeIndexingProcessor(
				new MainAxiomIndexerVisitor(index, true));
		inserter.visit(objectFactory.getEquivalentClassesAxiom(b, c));
		inserter.visit(objectFactory.getSubClassOfAxiom(a,
				objectFactory.getObjectSomeValuesFrom(r, b)));
		inserter.visit(objectFactory.getSubClassOfAxiom(
				objectFactory.getObjectSomeValuesFrom(r, c), d));
		inserter.visit(objectFactory.getSubObjectPropertyOfAxiom(r, s));

		IndexedObjectCache objectCache = index.getIndexedObjectCache();
		IndexObjectConverter converter = objectCache.getIndexObjectConverter();//new IndexObjectConverter(objectCache,	objectCache);

		IndexedClassExpression A = a.accept(converter);
		IndexedClassExpression D = d.accept(converter);
		IndexedPropertyChain R = r.accept(converter);

		final TestPropertySaturation propertySaturation = new TestPropertySaturation(
				executor, 16);

		SaturationState<?> saturationState = SaturationStateFactory
				.createSaturationState(index);
		final TestClassExpressionSaturation<SaturationJob<IndexedClassExpression>> classExpressionSaturation = new TestClassExpressionSaturation<SaturationJob<IndexedClassExpression>>(
				executor, 16, saturationState);

		propertySaturation.start();
		propertySaturation.submit(R);
		propertySaturation.finish();

		classExpressionSaturation.start();
		classExpressionSaturation
				.submit(new SaturationJob<IndexedClassExpression>(A));
		classExpressionSaturation.finish();

		assertTrue("A contains D", saturationState.getContext(A).getSubsumers()
				.contains(D));

	}

	@Test
	public void testConjunctions() throws InterruptedException,
			ExecutionException {
		ElkClass a = objectFactory.getClass(new ElkFullIri(":A"));
		ElkClass b = objectFactory.getClass(new ElkFullIri(":B"));
		ElkClass c = objectFactory.getClass(new ElkFullIri(":C"));
		ElkClass d = objectFactory.getClass(new ElkFullIri(":D"));

		final ModifiableOntologyIndex index = new DirectIndex(new IndexedObjectCache(new PlainIndexedAxiomFactory()));
		ComputationExecutor executor = new ComputationExecutor(16, "test");
		final ElkAxiomProcessor inserter = new ChangeIndexingProcessor(
				new MainAxiomIndexerVisitor(index, true));

		inserter.visit(objectFactory.getSubClassOfAxiom(a, b));
		inserter.visit(objectFactory.getSubClassOfAxiom(a, c));
		inserter.visit(objectFactory.getSubClassOfAxiom(
				objectFactory.getObjectIntersectionOf(b, c), d));

		IndexedObjectCache objectCache = index.getIndexedObjectCache();
		IndexObjectConverter converter = objectCache.getIndexObjectConverter();//new IndexObjectConverter(objectCache, objectCache);

		IndexedClassExpression A = a.accept(converter);
		IndexedClassExpression B = b.accept(converter);
		IndexedClassExpression C = c.accept(converter);
		IndexedClassExpression D = d.accept(converter);
		IndexedClassExpression I = objectFactory.getObjectIntersectionOf(b, c)
				.accept(converter);

		SaturationState<?> saturationState = SaturationStateFactory
				.createSaturationState(index);
		final TestClassExpressionSaturation<SaturationJob<IndexedClassExpression>> classExpressionSaturation = new TestClassExpressionSaturation<SaturationJob<IndexedClassExpression>>(
				executor, 16, saturationState);

		classExpressionSaturation.start();
		classExpressionSaturation
				.submit(new SaturationJob<IndexedClassExpression>(A));
		classExpressionSaturation.finish();
		Context context = saturationState.getContext(A);

		assertTrue("A contains A", context.getSubsumers().contains(A));
		assertTrue("A contains B", context.getSubsumers().contains(B));
		assertTrue("A contains C", context.getSubsumers().contains(C));
		assertTrue("A contains I", context.getSubsumers().contains(I));
		assertTrue("A contains D", context.getSubsumers().contains(D));
	}

}
