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
package org.semanticweb.elk.reasoner.indexing;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import junit.framework.TestCase;

import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.syntax.ElkObjectProperty;
import org.semanticweb.elk.syntax.FutureElkObjectFactory;
import org.semanticweb.elk.syntax.FutureElkObjectFactoryImpl;

public class ConcurrentIndexerTest extends TestCase {

	final FutureElkObjectFactory constructor = new FutureElkObjectFactoryImpl();

	public ConcurrentIndexerTest(String testName) {
		super(testName);
	}

	public void testIndexer() throws InterruptedException, ExecutionException {
		Future<ElkClass> human = constructor.getFutureElkClass("Human");
		Future<ElkObjectProperty> has = constructor
				.getFutureElkObjectProperty("has");
		Future<ElkClass> heart = constructor.getFutureElkClass("Heart");
		Future<ElkClass> organ = constructor.getFutureElkClass("Organ");
		Future<? extends ElkClassExpression> heartAndOrgan = constructor
				.getFutureElkObjectIntersectionOf(heart, organ);
		Future<? extends ElkClassExpression> hasHeartAndOrgan = constructor
				.getFutureElkObjectSomeValuesFrom(has, heartAndOrgan);

		final ExecutorService executor = Executors.newCachedThreadPool();
		Index index = new ConcurrentIndex();
		IndexingManager indexingManager = new IndexingManager(index, executor,
				8);

		indexingManager.submit(constructor.getFutureElkSubClassOfAxiom(human,
				hasHeartAndOrgan));
		indexingManager.waitCompletion();

		assertTrue(index.getIndexed(heartAndOrgan.get()).superClassExpressions
				.contains(index.getIndexed(heart.get())));
		assertTrue(index.getIndexed(heartAndOrgan.get()).superClassExpressions
				.contains(index.getIndexed(organ.get())));
		assertTrue(index.getIndexed(human.get()).superClassExpressions
				.contains(index.getIndexed(hasHeartAndOrgan.get())));
		assertTrue(index.getIndexed(heart.get()).negConjunctionsByConjunct
				.isEmpty());

		indexingManager.submit(constructor.getFutureElkEquivalentClassesAxiom(
				human, hasHeartAndOrgan));
		indexingManager.waitCompletion();

		assertTrue(index.getIndexed(heartAndOrgan.get()).superClassExpressions
				.size() == 2);
		assertFalse(index.getIndexed(heart.get()).negConjunctionsByConjunct
				.isEmpty());
		assertNotSame(index.getIndexed(human.get()),
				index.getIndexed(hasHeartAndOrgan.get()));

		executor.shutdown();
	}

}