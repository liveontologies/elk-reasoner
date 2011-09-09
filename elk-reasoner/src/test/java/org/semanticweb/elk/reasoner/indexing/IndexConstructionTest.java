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

import junit.framework.TestCase;

import org.semanticweb.elk.owl.ElkAxiomProcessor;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;

public class IndexConstructionTest extends TestCase {
	
	final ElkObjectFactory objectFactory = new ElkObjectFactoryImpl();

	public IndexConstructionTest(String testName) {
		super(testName);
	}

	public void testIndexer() {
		ElkClass a = objectFactory.getClass("A");
		ElkClass b = objectFactory.getClass("B");
		ElkClass c = objectFactory.getClass("C");
		ElkClass d = objectFactory.getClass("D");
		ElkObjectProperty r = objectFactory.getObjectProperty("R");

		OntologyIndex index = new SerialOntologyIndex();
		ElkAxiomProcessor inserter = index.getAxiomInserter();
		ElkAxiomProcessor deleter = index.getAxiomDeleter();

		inserter.process(objectFactory.getSubClassOfAxiom(
				objectFactory.getObjectIntersectionOf(a, b), d));
		inserter.process(objectFactory.getEquivalentClassesAxiom(
				objectFactory.getObjectSomeValuesFrom(r, c), a));

		IndexedClassExpression A = index.getIndexedClassExpression(a);
		IndexedClassExpression B = index.getIndexedClassExpression(b);
		IndexedClassExpression C = index.getIndexedClassExpression(c);
		IndexedObjectProperty R = index.getIndexedObjectProperty(r);
		assertEquals(2, A.negativeOccurrenceNo);
		assertEquals(1, A.positiveOccurrenceNo);
		assertEquals(1, B.negativeOccurrenceNo);
		assertEquals(0, B.positiveOccurrenceNo);
		assertEquals(1, C.negativeOccurrenceNo);
		assertEquals(1, C.positiveOccurrenceNo);
		assertEquals(2, R.occurrenceNo);
		assertTrue(A.getNegConjunctionsByConjunct().containsKey(B));
		assertTrue(C.getNegExistentials().get(0).getRelation() == R);

		deleter.process(objectFactory.getEquivalentClassesAxiom(
				objectFactory.getObjectSomeValuesFrom(r, c), a));

		assertEquals(1, A.negativeOccurrenceNo);
		assertEquals(0, A.positiveOccurrenceNo);
		assertEquals(0, R.occurrenceNo);
		assertNull(C.getNegExistentials());
		assertNull(index.getIndexedEntity(c));
		assertNull(index.getIndexedEntity(r));

		deleter.process(objectFactory.getSubClassOfAxiom(
				objectFactory.getObjectIntersectionOf(a, b), d));
		assertEquals(0, A.negativeOccurrenceNo);
		assertNull(A.getNegConjunctionsByConjunct());
		assertNull(index.getIndexedEntity(a));
	}

	public void testConjunctionSharing() {
		ElkClass a = objectFactory.getClass("A");
		ElkClass b = objectFactory.getClass("B");
		ElkClass c = objectFactory.getClass("C");
		ElkClass d = objectFactory.getClass("D");

		ElkClassExpression x = objectFactory.getObjectIntersectionOf(a, b, c);
		ElkClassExpression y = objectFactory.getObjectIntersectionOf(
				objectFactory.getObjectIntersectionOf(b, a), c);

		OntologyIndex index = new SerialOntologyIndex();
		ElkAxiomProcessor inserter = index.getAxiomInserter();

		inserter.process(objectFactory.getSubClassOfAxiom(x, d));
		inserter.process(objectFactory.getSubClassOfAxiom(y, d));

		assertSame(index.getIndexedClassExpression(x),
				index.getIndexedClassExpression(y));
		assertEquals(2, index.getIndexedClassExpression(x)
				.getToldSuperClassExpressions().size());
	}

}
